/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionheart;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.awt.Keyboard;
import com.b3dgs.lionengine.awt.Mouse;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;

/**
 * Profiles dialog.
 */
public class ProfilesDialog extends JDialog
{
    private static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 20);

    private static final int DIALOG_WIDTH = 1024;
    private static final int DIALOG_HEIGHT = 768;

    private static final String LABEL_PROFILES = "Profiles";
    private static final String LABEL_ADD = "Add";
    private static final String LABEL_REMOVE = "Remove";
    private static final String LABEL_EDIT = "Edit";
    private static final String LABEL_RENAME = "Rename";
    private static final String LABEL_UP = "Up";
    private static final String LABEL_DOWN = "Down";
    private static final String LABEL_BACK = "Back";
    private static final String LABEL_SAVE = "Save";

    private final Map<Integer, DeviceControllerConfig> configs = new TreeMap<>();

    /**
     * Create dialog.
     * 
     * @param owner The owner reference.
     * @param gamepad The gamepad reference.
     */
    public ProfilesDialog(Window owner, Gamepad gamepad)
    {
        super(owner, Dialog.ModalityType.DOCUMENT_MODAL);

        final Collection<DeviceControllerConfig> configs;
        configs = DeviceControllerConfig.imports(new Services(), Medias.create(Constant.INPUT_FILE_DEFAULT));
        for (final DeviceControllerConfig config : configs)
        {
            this.configs.put(Integer.valueOf(config.getIndex()), config);
        }

        setResizable(false);
        setLayout(new BorderLayout());

        create(gamepad);

        pack();
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());
    }

    private void create(Gamepad gamepad)
    {
        final DefaultListModel<String> model = new DefaultListModel<>();
        final JList<String> list = new JList<>(model);
        list.setFont(FONT);
        list.getSelectionModel().setSelectionMode(0);
        final AtomicReference<Integer> selection = new AtomicReference<>(Integer.valueOf(-1));
        list.addListSelectionListener(e ->
        {
            if (list.getSelectedIndex() != selection.get().intValue())
            {
                selection.set(Integer.valueOf(list.getSelectedIndex()));
            }
        });

        final JButton add = new JButton(LABEL_ADD);
        add.setFont(FONT);
        add.addActionListener(e -> onAdd(model));

        final JButton remove = new JButton(LABEL_REMOVE);
        remove.setFont(FONT);
        remove.addActionListener(e -> onRemove(model, selection));

        final JButton edit = new JButton(LABEL_EDIT);
        edit.setFont(FONT);
        edit.addActionListener(e -> onEdit(gamepad, selection));

        final JButton rename = new JButton(LABEL_RENAME);
        rename.setFont(FONT);
        rename.addActionListener(e -> onRename(model, selection));

        final JButton up = new JButton(LABEL_UP);
        up.setFont(FONT);
        up.addActionListener(e -> onUp(list, model, selection));

        final JButton down = new JButton(LABEL_DOWN);
        down.setFont(FONT);
        down.addActionListener(e -> onDown(list, model, selection));

        final JButton save = new JButton(LABEL_SAVE);
        save.setFont(FONT);
        save.addActionListener(e -> onSave());

        final JButton back = new JButton(LABEL_BACK);
        back.setFont(FONT);
        back.addActionListener(e -> dispose());

        final JPanel buttons = new JPanel(new GridLayout(4, 2));
        buttons.add(add);
        buttons.add(remove);

        buttons.add(edit);
        buttons.add(rename);

        buttons.add(up);
        buttons.add(down);

        buttons.add(save);
        buttons.add(back);

        final Box box = Box.createHorizontalBox();
        box.setBorder(BorderFactory.createTitledBorder(LABEL_PROFILES));

        final JScrollPane scroll = new JScrollPane(list);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scroll, BorderLayout.CENTER);

        box.add(scroll);
        box.add(buttons);

        for (final DeviceControllerConfig config : configs.values())
        {
            model.addElement(config.getName());
        }

        add(box, BorderLayout.CENTER);
    }

    @SuppressWarnings("unchecked")
    private void onAdd(DefaultListModel<String> model)
    {
        final String name = JOptionPane.showInputDialog(this, "Profile name:");
        if (name != null && !name.isEmpty())
        {
            final Class<? extends InputDevice> device;
            device = (Class<? extends InputDevice>) JOptionPane.showInputDialog(this,
                                                                                "Choice",
                                                                                "Device",
                                                                                JOptionPane.QUESTION_MESSAGE,
                                                                                null,
                                                                                new Class<?>[]
                                                                                {
                                                                                    Keyboard.class, Gamepad.class,
                                                                                    Mouse.class
                                                                                },
                                                                                Keyboard.class);
            int max = 0;
            for (final DeviceControllerConfig c : configs.values())
            {
                max = Math.max(max, c.getIndex());
            }
            final DeviceControllerConfig config = new DeviceControllerConfig(name,
                                                                             model.getSize(),
                                                                             max + 1,
                                                                             device,
                                                                             false,
                                                                             new ArrayList<>(),
                                                                             new ArrayList<>(),
                                                                             new HashMap<>());
            configs.put(Integer.valueOf(config.getIndex()), config);
            model.addElement(config.getName());
        }
    }

    private void onRemove(DefaultListModel<String> model, AtomicReference<Integer> selection)
    {
        final int index = selection.get().intValue();
        if (index > -1 && index < model.size())
        {
            configs.remove(selection.get());

            for (int j = index + 1; j < model.size(); j++)
            {
                final DeviceControllerConfig old = configs.get(Integer.valueOf(j));
                configs.put(Integer.valueOf(j - 1),
                            new DeviceControllerConfig(old.getName(),
                                                       old.getIndex() - 1,
                                                       old.getId(),
                                                       old.getDevice(),
                                                       old.isDisabled(),
                                                       old.getHorizontal(),
                                                       old.getVertical(),
                                                       old.getFire()));
            }
            configs.remove(Integer.valueOf(model.size() - 1));
            model.removeElementAt(index);
        }
    }

    private void onEdit(Gamepad gamepad, AtomicReference<Integer> selection)
    {
        if (selection.get().intValue() > -1)
        {
            final DeviceControllerConfig config = configs.get(selection.get());
            final AssignController assigner = getAssigner(config, gamepad, config.getId());
            final DeviceDialog dialog = new DeviceDialog(this, assigner, config);
            dialog.setResizable(false);
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosed(WindowEvent e)
                {
                    assigner.stop();
                }
            });
            dialog.setVisible(true);

            final DeviceControllerConfig result = dialog.getResult();
            configs.put(Integer.valueOf(result.getIndex()), result);
        }
    }

    private void onRename(DefaultListModel<String> model, AtomicReference<Integer> selection)
    {
        final int index = selection.get().intValue();
        if (index > -1)
        {
            final String name = JOptionPane.showInputDialog(this, "Profile name:");
            if (name != null && !name.isEmpty())
            {
                final DeviceControllerConfig old = configs.get(selection.get());
                final DeviceControllerConfig config = new DeviceControllerConfig(name,
                                                                                 old.getIndex(),
                                                                                 old.getId(),
                                                                                 old.getDevice(),
                                                                                 old.isDisabled(),
                                                                                 old.getHorizontal(),
                                                                                 old.getVertical(),
                                                                                 old.getFire());
                configs.put(Integer.valueOf(config.getIndex()), config);

                model.setElementAt(config.getName(), index);
            }
        }
    }

    private void onUp(JList<String> list, DefaultListModel<String> model, AtomicReference<Integer> selection)
    {
        final int index = selection.get().intValue();
        if (index > 0)
        {
            final DeviceControllerConfig old = configs.get(selection.get());
            final DeviceControllerConfig config = new DeviceControllerConfig(old.getName(),
                                                                             index - 1,
                                                                             old.getId(),
                                                                             old.getDevice(),
                                                                             old.isDisabled(),
                                                                             old.getHorizontal(),
                                                                             old.getVertical(),
                                                                             old.getFire());
            configs.put(Integer.valueOf(config.getIndex()), config);

            final String next = model.getElementAt(index - 1);
            model.setElementAt(model.getElementAt(index), index - 1);
            model.setElementAt(next, index);
            list.setSelectedIndex(index - 1);
        }
    }

    private void onDown(JList<String> list, DefaultListModel<String> model, AtomicReference<Integer> selection)
    {
        final int index = selection.get().intValue();
        if (index > -1 && index < model.size() - 1)
        {
            final DeviceControllerConfig old = configs.get(selection.get());
            final DeviceControllerConfig config = new DeviceControllerConfig(old.getName(),
                                                                             index + 1,
                                                                             old.getId(),
                                                                             old.getDevice(),
                                                                             old.isDisabled(),
                                                                             old.getHorizontal(),
                                                                             old.getVertical(),
                                                                             old.getFire());
            configs.put(Integer.valueOf(config.getIndex()), config);

            final String next = model.getElementAt(index + 1);
            model.setElementAt(model.getElementAt(index), index + 1);
            model.setElementAt(next, index);
            list.setSelectedIndex(index + 1);
        }
    }

    private void onSave()
    {
        DeviceControllerConfig.exports(configs.values(),
                                       Medias.create(Constant.INPUT_FILE_DEFAULT),
                                       DeviceMapping.class,
                                       DeviceMapping::fromIndex);
    }

    private static AssignController getAssigner(DeviceControllerConfig config, Gamepad gamepad, int id)
    {
        final Class<? extends InputDevice> device = config.getDevice();
        if (Keyboard.class.equals(device))
        {
            return new AssignerKeyboard();
        }
        else if (Mouse.class.equals(device))
        {
            return new AssignerMouse();
        }
        else if (Gamepad.class.equals(device))
        {
            return new AssignerGamepad(gamepad, id);
        }
        throw new LionEngineException("Unknown device: " + device);
    }

    @Override
    public void pack()
    {
        super.pack();
        setPreferredSize(new Dimension(Math.min(getWidth() + 32, DIALOG_WIDTH), Math.min(getHeight(), DIALOG_HEIGHT)));
        super.pack();
    }
}
