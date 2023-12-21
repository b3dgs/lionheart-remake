/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceAxis;
import com.b3dgs.lionengine.io.DeviceMapper;

/**
 * Device dialog.
 */
// CHECKSTYLE IGNORE LINE: FanOutComplexity|DataAbstractionCoupling
public class DeviceDialog extends JDialog
{
    private static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 20);

    private static final int DIALOG_WIDTH = 1024;
    private static final int DIALOG_HEIGHT = 768;
    private static final int DEVICE_NAME_SPACE_MAX = 10;

    private static final String LABEL_ASSIGN = "";
    private static final String LABEL_EXIT = "Exit";

    private static String getName(DeviceMapper mapping)
    {
        return String.format("%" + DEVICE_NAME_SPACE_MAX + "s", mapping);
    }

    private final AtomicReference<Integer> positiveX = new AtomicReference<>();
    private final AtomicReference<Integer> negativeX = new AtomicReference<>();
    private final AtomicReference<Integer> positiveY = new AtomicReference<>();
    private final AtomicReference<Integer> negativeY = new AtomicReference<>();
    /** Store by mapping and their code. */
    private final Map<DeviceMapper, Integer> data = new HashMap<>();
    /** Text to code mapper. */
    private final Map<String, Integer> textToCode = new HashMap<>();
    /** Controller. */
    private final AssignController controller;
    /** Config. */
    private final DeviceControllerConfig config;

    /**
     * Create dialog.
     * 
     * @param owner The owner reference.
     * @param controller The controller.
     * @param config The config reference.
     */
    public DeviceDialog(Window owner, AssignController controller, DeviceControllerConfig config)
    {
        super(owner, controller.getName(), Dialog.ModalityType.DOCUMENT_MODAL);

        this.controller = controller;
        this.config = config;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        load(config);

        final JPanel panel = new JPanel(new GridLayout(0, 1));

        createAxis(panel, Axis.Y + " +", positiveY, controller);
        createAxis(panel, Axis.Y + " -", negativeY, controller);
        createAxis(panel, Axis.X + " +", positiveX, controller);
        createAxis(panel, Axis.X + " -", negativeX, controller);

        for (final DeviceMapper mapping : DeviceMapping.values())
        {
            createInput(panel, mapping, controller);
        }

        final JScrollPane scroll = new JScrollPane(panel);
        add(scroll, BorderLayout.CENTER);

        createButtons();
    }

    /**
     * Compute result.
     * 
     * @return The computed result.
     */
    public DeviceControllerConfig getResult()
    {
        final Map<Integer, Set<Integer>> fires = new HashMap<>();
        for (final Entry<DeviceMapper, Integer> entry : data.entrySet())
        {
            final HashSet<Integer> f = new HashSet<>();
            f.add(entry.getValue());
            fires.put(entry.getKey().getIndex(), f);
        }

        config.getVertical().clear();
        if (positiveY.get() != null && negativeY != null)
        {
            config.getVertical().add(new DeviceAxis(positiveY.get(), negativeY.get()));
        }

        config.getHorizontal().clear();
        if (positiveX.get() != null && negativeX.get() != null)
        {
            config.getHorizontal().add(new DeviceAxis(positiveX.get(), negativeX.get()));
        }

        return new DeviceControllerConfig(config.getName(),
                                          config.getIndex(),
                                          config.getId(),
                                          config.getDevice(),
                                          config.isDisabled(),
                                          config.getHorizontal(),
                                          config.getVertical(),
                                          fires);
    }

    private void load(DeviceControllerConfig config)
    {
        final String name = controller.getName();
        if (name.equals(config.getDevice().getSimpleName()))
        {
            if (!config.getVertical().isEmpty())
            {
                positiveY.set(config.getVertical().get(0).getPositive());
                negativeY.set(config.getVertical().get(0).getNegative());
            }
            if (!config.getHorizontal().isEmpty())
            {
                positiveX.set(config.getHorizontal().get(0).getPositive());
                negativeX.set(config.getHorizontal().get(0).getNegative());
            }

            final DeviceMapping[] values = DeviceMapping.values();
            for (final Entry<Integer, Set<Integer>> entry : config.getFire().entrySet())
            {
                if (!entry.getValue().isEmpty())
                {
                    data.put(values[entry.getKey().intValue()], entry.getValue().iterator().next());
                }

                for (final Integer code : entry.getValue())
                {
                    textToCode.put(controller.getText(code.intValue()), code);
                }
            }
        }
    }

    @Override
    public void pack()
    {
        super.pack();
        setPreferredSize(new Dimension(Math.min(Math.max(getWidth(), 256) + 32, DIALOG_WIDTH),
                                       Math.min(getHeight(), DIALOG_HEIGHT)));
        super.pack();
    }

    private JTextField createTextField(Box box, AtomicReference<Integer> axis)
    {
        final JTextField field = new JTextField();
        field.setFont(FONT);
        field.setHorizontalAlignment(SwingConstants.CENTER);
        field.setEditable(false);
        field.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON1)
                {
                    codeAssign(field, axis);
                }
                else
                {
                    axis.set(null);
                }
            }
        });
        box.add(field);
        return field;
    }

    private JTextField createTextField(Box box, DeviceMapper mapping)
    {
        final JTextField field = new JTextField();
        field.setFont(FONT);
        field.setHorizontalAlignment(SwingConstants.CENTER);
        field.setEditable(false);
        field.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON1)
                {
                    codeAssign(field, mapping);
                }
                else
                {
                    data.remove(mapping);
                }
            }
        });
        box.add(field);
        return field;
    }

    private void codeAssign(JTextField field, DeviceMapper mapping)
    {
        final ActionGetter dialog = new ActionGetter(DeviceDialog.this, controller.getName());
        controller.awaitAssign(dialog);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        final int code = dialog.getKey();
        field.setText(controller.getText(code));

        textToCode.put(field.getText(), Integer.valueOf(code));
        data.put(mapping, Integer.valueOf(code));
    }

    private void codeAssign(JTextField field, AtomicReference<Integer> axis)
    {
        final ActionGetter dialog = new ActionGetter(DeviceDialog.this, controller.getName());
        controller.awaitAssign(dialog);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        final int code = dialog.getKey();
        field.setText(controller.getText(code));

        textToCode.put(field.getText(), Integer.valueOf(code));
        axis.set(Integer.valueOf(code));
    }

    private void createAxis(Container parent, String axis, AtomicReference<Integer> code, AssignController controller)
    {
        final Box box = Box.createHorizontalBox();
        parent.add(box);

        final JLabel label = new JLabel(axis);
        label.setFont(FONT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        box.add(label);

        final JTextField field = createTextField(box, code);
        field.setText(code.get() != null ? controller.getText(code.get().intValue()) : LABEL_ASSIGN);
        field.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event)
            {
                field.setText(LABEL_ASSIGN);
                code.set(null);
            }
        });
    }

    private void createInput(Container parent, DeviceMapper mapping, AssignController controller)
    {
        final Box box = Box.createHorizontalBox();
        parent.add(box);

        final JLabel label = new JLabel(getName(mapping));
        label.setFont(FONT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        box.add(label);

        final Integer code = data.get(mapping);

        final JTextField field = createTextField(box, mapping);
        field.setText(code != null ? controller.getText(code.intValue()) : LABEL_ASSIGN);
        field.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event)
            {
                field.setText(LABEL_ASSIGN);
                data.remove(mapping);
            }
        });
    }

    private void createButtons()
    {
        final JButton exit = new JButton(LABEL_EXIT);
        exit.setFont(FONT);
        exit.addActionListener(event ->
        {
            dispose();
        });

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;

        final JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(exit, constraints);

        add(panel, BorderLayout.SOUTH);
    }
}
