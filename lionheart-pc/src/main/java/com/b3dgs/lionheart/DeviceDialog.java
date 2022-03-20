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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilStream;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
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
    private static final String STR_EQUAL_QUOTE = "=\"";

    private static final String LABEL_ADD = "Add";
    private static final String LABEL_REMOVE = "Remove";
    private static final String LABEL_ASSIGN = "assign...";
    private static final String LABEL_SAVE = "Save";
    private static final String LABEL_EXIT = "Exit";

    /**
     * Prepare custom input file.
     */
    public static void prepareInputCustom()
    {
        try (InputStream input = Medias.create(Constant.INPUT_FILE_DEFAULT).getUrl().openStream();
             OutputStream output = Medias.create(Constant.INPUT_FILE_DEFAULT).getOutputStream())
        {
            UtilStream.copy(input, output);
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
        }
    }

    private static String getName(DeviceMapper mapping)
    {
        return String.format("%" + DEVICE_NAME_SPACE_MAX + "s", mapping);
    }

    /** Stored by device, by mapping and their codes. */
    private final Map<DeviceMapper, Set<Integer>> data = new HashMap<>();
    /** Text to code mapper. */
    private final Map<String, Integer> textToCode = new HashMap<>();
    /** Custom input. */
    private final Media inputCustom = Medias.create(Constant.INPUT_FILE_DEFAULT);
    /** Controller. */
    private final AssignController controller;

    /**
     * Create dialog.
     * 
     * @param owner The owner reference.
     * @param controller The controller.
     */
    public DeviceDialog(Window owner, AssignController controller)
    {
        super(owner, controller.getName(), Dialog.ModalityType.DOCUMENT_MODAL);

        this.controller = controller;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        load();

        final JPanel panel = new JPanel(new GridLayout(0, 1));
        for (final DeviceMapper mapping : DeviceMapping.values())
        {
            createInput(panel, mapping, controller);
        }

        final JScrollPane scroll = new JScrollPane(panel);
        add(scroll, BorderLayout.CENTER);

        createButtons();
    }

    @Override
    public void pack()
    {
        super.pack();
        setPreferredSize(new Dimension(Math.min(getWidth() + 32, DIALOG_WIDTH), Math.min(getHeight(), DIALOG_HEIGHT)));
        super.pack();
    }

    private JTextField createTextField(Box box, DeviceMapper mapping, AtomicBoolean removeEnabled)
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
                if (!removeEnabled.get())
                {
                    final Set<Integer> codes = data.computeIfAbsent(mapping, m -> new HashSet<>());
                    if (e.getButton() == MouseEvent.BUTTON1)
                    {
                        codeAssign(field, codes);
                    }
                    else
                    {
                        codeRemove(box, mapping, field, codes);
                    }
                }
            }
        });
        box.add(field);
        return field;
    }

    private void codeAssign(JTextField field, Set<Integer> codes)
    {
        final ActionGetter dialog = new ActionGetter(DeviceDialog.this, controller.getName());
        controller.awaitAssign(dialog);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        final int code = dialog.getKey();
        field.setText(controller.getText(code));

        textToCode.put(field.getText(), Integer.valueOf(code));
        codes.add(Integer.valueOf(code));
    }

    private void codeRemove(Box box, DeviceMapper mapping, JTextField field, Set<Integer> codes)
    {
        codes.remove(textToCode.get(field.getText()));
        if (codes.isEmpty())
        {
            data.remove(mapping);
        }
        box.remove(field);
        box.revalidate();
        box.repaint();
    }

    private void createInput(Container parent, DeviceMapper mapping, AssignController controller)
    {
        final Box box = Box.createHorizontalBox();
        parent.add(box);

        final JLabel label = new JLabel(getName(mapping));
        label.setFont(FONT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        box.add(label);

        final AtomicBoolean removeEnabled = new AtomicBoolean();
        final JToggleButton remove = new JToggleButton(LABEL_REMOVE);
        remove.setFont(FONT);
        remove.addActionListener(e ->
        {
            removeEnabled.set(!removeEnabled.get());
        });

        final JButton add = new JButton(LABEL_ADD);
        add.setFont(FONT);
        add.addActionListener(e ->
        {
            remove.setSelected(false);
            removeEnabled.set(false);

            final JTextField field = createTextField(box, mapping, removeEnabled);
            field.setText(LABEL_ASSIGN);
            box.revalidate();
        });
        box.add(add);

        box.add(remove);

        for (final Integer code : Optional.ofNullable(data.get(mapping)).orElse(Collections.emptySet()))
        {
            final JTextField field = createTextField(box, mapping, removeEnabled);
            field.setText(controller.getText(code.intValue()));
            field.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseClicked(MouseEvent event)
                {
                    if (removeEnabled.get())
                    {
                        removeEnabled.set(false);
                        remove.setSelected(false);
                        codeRemove(box, mapping, field, data.computeIfAbsent(mapping, m -> new HashSet<>()));
                    }
                }
            });
        }
    }

    private void createButtons()
    {
        final JButton save = new JButton(LABEL_SAVE);
        save.setFont(FONT);
        save.addActionListener(event ->
        {
            save();
            dispose();
        });

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
        panel.add(save, constraints);
        panel.add(exit, constraints);

        add(panel, BorderLayout.SOUTH);
    }

    private void load()
    {
        if (!inputCustom.exists())
        {
            prepareInputCustom();
        }
        final Collection<DeviceControllerConfig> configs = DeviceControllerConfig.imports(new Services(), inputCustom);
        for (final DeviceControllerConfig config : configs)
        {
            final String name = controller.getName();
            if (name.equals(config.getDevice().getSimpleName()))
            {
                final DeviceMapping[] values = DeviceMapping.values();
                for (final Entry<Integer, Set<Integer>> entry : config.getFire().entrySet())
                {
                    data.put(values[entry.getKey().intValue()], entry.getValue());

                    for (final Integer code : entry.getValue())
                    {
                        textToCode.put(controller.getText(code.intValue()), code);
                    }
                }
            }
        }
    }

    private void save()
    {
        final Map<DeviceMapper, Set<Integer>> written = new HashMap<>();
        prepareInputCustom();

        final File file = inputCustom.getFile();
        try
        {
            boolean started = false;
            final List<String> lines = Files.readAllLines(file.toPath());
            try (FileWriter output = new FileWriter(file))
            {
                for (final String line : lines)
                {
                    if (started)
                    {
                        if (line.contains(DeviceControllerConfig.NODE_FIRE))
                        {
                            final DeviceMapper mapping = readMapping(line);
                            if (mapping != null)
                            {
                                final Set<Integer> codes = data.get(mapping);
                                if (codes != null)
                                {
                                    for (final Integer code : codes)
                                    {
                                        if (written.computeIfAbsent(mapping, m -> new HashSet<>()).add(code))
                                        {
                                            writeCode(output, mapping, code);
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            if (line.contains(DeviceControllerConfig.NODE_DEVICE))
                            {
                                started = false;
                            }
                            output.write(line);
                            output.write(System.lineSeparator());
                        }
                    }
                    else
                    {
                        if (line.contains(DeviceControllerConfig.NODE_DEVICE) && line.contains(controller.getName()))
                        {
                            started = true;
                        }
                        output.write(line);
                        output.write(System.lineSeparator());
                    }
                }
                output.flush();
            }
            catch (final IOException exception)
            {
                Verbose.exception(exception);
            }
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
        }
        written.clear();
    }

    private DeviceMapping readMapping(String line)
    {
        final String index = "index=\"";
        final int start = line.indexOf(index) + index.length();
        return DeviceMapping.valueOf(line.substring(start, line.indexOf('\"', start)));
    }

    private static void writeCode(FileWriter output, DeviceMapper mapping, Integer code) throws IOException
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("        <")
               .append(DeviceControllerConfig.NODE_FIRE)
               .append(com.b3dgs.lionengine.Constant.SPACE)
               .append(DeviceControllerConfig.ATT_INDEX)
               .append(STR_EQUAL_QUOTE)
               .append(mapping)
               .append("\"")
               .append(com.b3dgs.lionengine.Constant.SPACE)
               .append(DeviceControllerConfig.ATT_POSITIVE)
               .append(STR_EQUAL_QUOTE)
               .append(code)
               .append("\"/>");
        output.write(builder.toString());
        output.write(System.lineSeparator());
    }
}
