/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.UtilStream;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.awt.Keyboard;
import com.b3dgs.lionengine.awt.Mouse;
import com.b3dgs.lionengine.awt.graphic.EngineAwt;
import com.b3dgs.lionengine.awt.graphic.ImageLoadStrategy;
import com.b3dgs.lionengine.awt.graphic.ToolsAwt;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Program starts here.
 */
public final class Launcher
{
    private static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 20);
    private static final Font FONT2 = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    private static final Integer[] AVAILABLE_SCALE = new Integer[]
    {
        Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4),
    };
    private static final String[] AVAILABLE_RATIO = new String[]
    {
        "4/3", "16/10", "16/9", "21/9"
    };
    private static final double[] RATIO_VALUE = new double[]
    {
        4.0
      / 3.0, 16.0 / 10.0, 16.0 / 9.0, 21.0 / 9.0
    };
    private static final Map<String, String> LANG_FULL = new HashMap<>();
    private static final Map<String, String> LANG_SHORT = new HashMap<>();

    private static final AtomicInteger SCALE = new AtomicInteger();
    private static final AtomicInteger RATIO = new AtomicInteger();
    private static final AtomicBoolean FULLSCREEN = new AtomicBoolean();
    private static final AtomicInteger FLAG = new AtomicInteger();
    private static final AtomicBoolean RASTER = new AtomicBoolean();
    private static final AtomicInteger ZOOM = new AtomicInteger();
    private static final AtomicInteger MUSIC = new AtomicInteger();
    private static final AtomicInteger SFX = new AtomicInteger();
    private static final AtomicInteger GAMEPAD = new AtomicInteger();
    private static final AtomicReference<String> STAGES = new AtomicReference<>(Folder.ORIGINAL);
    private static final AtomicReference<String> LANG = new AtomicReference<>("en");

    private static final String FILENAME_ICON = "icon-256.png";
    private static final String FILENAME_LOGO = "logo.png";
    private static final String SETTINGS_SEPARATOR = "=";

    private static final String LABEL_LANG = "Lang: ";
    private static final String LABEL_ZOOM = "Zoom: ";
    private static final String LABEL_ORIGINAL = "Original";
    private static final String LABEL_MODERN = "Modern";
    private static final String LABEL_MUSIC = "Music: ";
    private static final String LABEL_SFX = "  Sfx: ";
    private static final String LABEL_SCALE = "Scale: x";
    private static final String LABEL_RATIO = "Ratio: ";
    private static final String LABEL_FULLSCREEN = "Fullscreen:";
    private static final String LABEL_FLAG = "Flag: ";
    private static final String LABEL_RASTER = "Raster:";
    private static final String LABEL_STAGES = "Stages ";
    private static final String LABEL_GAMEPAD = "Gamepad: ";
    private static final String LABEL_SETUP_DEVICE = "Setup ";
    private static final String LABEL_PLAY = "Play";
    private static final String LABEL_EXIT = "Exit";
    private static final String LABEL_MADE = "Made with "
                                             + com.b3dgs.lionengine.Constant.ENGINE_NAME
                                             + com.b3dgs.lionengine.Constant.SPACE
                                             + com.b3dgs.lionengine.Constant.ENGINE_VERSION
                                             + com.b3dgs.lionengine.Constant.SPACE
                                             + com.b3dgs.lionengine.Constant.ENGINE_WEBSITE;

    private static final int DIALOG_WIDTH = 400;
    private static final int DIALOG_HEIGHT = 360;
    private static final Border BORDER_SPLASH = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    private static final Border BORDER = BorderFactory.createEmptyBorder(3, 10, 3, 10);

    static
    {
        LANG_FULL.put("en", "English");
        LANG_FULL.put("fr", "Français");
        LANG_FULL.put("de", "Deutsch");
        LANG_FULL.put("es", "Español");

        LANG_FULL.forEach((k, v) -> LANG_SHORT.put(v, k));
    }

    private static void load()
    {
        final Settings settings = Settings.getInstance();

        LANG.set(settings.getLang());

        final java.awt.DisplayMode display = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                                .getDefaultScreenDevice()
                                                                .getDisplayMode();
        final int width = settings.getResolution().getWidth();
        final int height = settings.getResolution().getHeight();
        final double ratio = width / (double) height;
        SCALE.set(UtilMath.clamp(height / Constant.RESOLUTION.getHeight(), 0, AVAILABLE_SCALE.length - 1));
        for (int i = 0; i < RATIO_VALUE.length; i++)
        {
            if (Math.abs(RATIO_VALUE[i] - ratio) < 0.01)
            {
                RATIO.set(i);
                break;
            }
        }
        FULLSCREEN.set(display.getWidth() == width && display.getHeight() == height);
        RASTER.set(settings.getRaster());
        ZOOM.set(UtilMath.clamp((int) Math.round(settings.getZoom() * 100),
                                (int) (Constant.ZOOM_MIN * 100),
                                (int) (Constant.ZOOM_MAX * 100)));
        MUSIC.set(UtilMath.clamp(settings.getVolumeMusic(), 0, com.b3dgs.lionengine.Constant.HUNDRED));
        SFX.set(UtilMath.clamp(settings.getVolumeSfx(), 0, com.b3dgs.lionengine.Constant.HUNDRED));
        FLAG.set(UtilMath.clamp(settings.getFlag(), 0, ImageLoadStrategy.values().length));
        STAGES.set(settings.getStages());
    }

    /**
     * Main function.
     * 
     * @param args The arguments (none).
     * @throws IOException If error.
     */
    public static void main(String[] args) throws IOException // CHECKSTYLE IGNORE LINE: TrailingComment|UncommentedMain
    {
        EngineAwt.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, AppLionheart.class);
        Settings.load();

        final String input = Settings.getInstance().getInput();
        if (!Medias.create(input).exists())
        {
            final File file = UtilStream.getCopy(Medias.create(Constant.INPUT_FILE_DEFAULT));
            file.renameTo(new File(file.getPath().replace(file.getName(), input)));
        }

        final Gamepad gamepad = new Gamepad();

        setThemeSystem();
        load();

        final JFrame frame = createFrame();

        final JPanel panel = new JPanel();
        final BorderLayout layout = new BorderLayout();
        panel.setLayout(layout);

        final Box box = Box.createVerticalBox();
        panel.add(box);

        final Box box2 = Box.createHorizontalBox();
        box.add(box2);

        final JLabel splash = new JLabel();
        splash.setIcon(new ImageIcon(Launcher.class.getResource(FILENAME_LOGO)));
        splash.setBorder(BORDER_SPLASH);
        box2.add(splash);

        createLang(box);
        final Container scale = createScale(box);
        final Container ratio = createRatio(box);
        createFullscreen(box, scale, ratio);
        createZoom(box);
        createSlider(box, LABEL_MUSIC, MUSIC, 0, com.b3dgs.lionengine.Constant.HUNDRED);
        createSlider(box, LABEL_SFX, SFX, 0, com.b3dgs.lionengine.Constant.HUNDRED);
        createStages(box);
        createGamepad(box, gamepad);
        createSetups(box, frame, gamepad);
        createButtons(box, frame, gamepad);
        createCopyright(frame);

        run(frame, panel);
    }

    private static JFrame createFrame()
    {
        final JFrame frame = new JFrame(Constant.PROGRAM_NAME
                                        + com.b3dgs.lionengine.Constant.SPACE
                                        + Constant.PROGRAM_VERSION);
        try
        {
            frame.setIconImage(ToolsAwt.getImage(Launcher.class.getResourceAsStream(FILENAME_ICON)));
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
        }
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent event)
            {
                Engine.terminate();
            }
        });
        return frame;
    }

    private static Container createLang(Container parent)
    {
        final JLabel label = new JLabel(LABEL_LANG);
        label.setFont(FONT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);

        final List<String> langs = Medias.create(Folder.TEXT)
                                         .getMedias()
                                         .stream()
                                         .map(Media::getName)
                                         .map(f -> LANG_FULL.getOrDefault(f, f))
                                         .sorted()
                                         .collect(Collectors.toList());
        final JComboBox<String> combo = new JComboBox<>(langs.toArray(new String[langs.size()]));
        combo.setFont(FONT);
        combo.addItemListener(e ->
        {
            final String k = combo.getItemAt(combo.getSelectedIndex());
            LANG.set(LANG_SHORT.getOrDefault(k, k));
        });
        combo.setSelectedItem(LANG_FULL.getOrDefault(LANG.get(), LANG.get()));

        final Box box = Box.createHorizontalBox();
        box.setBorder(BORDER);
        box.add(label);
        box.add(combo);
        parent.add(box);

        return combo;
    }

    private static Container createScale(Container parent)
    {
        final JLabel label = new JLabel(LABEL_SCALE);
        label.setFont(FONT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);

        final JComboBox<Integer> combo = new JComboBox<>(AVAILABLE_SCALE);
        combo.setFont(FONT);
        combo.addItemListener(e -> SCALE.set(combo.getSelectedIndex()));
        combo.setEnabled(!FULLSCREEN.get());
        combo.setSelectedIndex(SCALE.get());

        final Box box = Box.createHorizontalBox();
        box.setBorder(BORDER);
        box.add(label);
        box.add(combo);
        parent.add(box);

        return combo;
    }

    private static Container createRatio(Container parent)
    {
        final JLabel label = new JLabel(LABEL_RATIO);
        label.setFont(FONT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);

        final JComboBox<String> combo = new JComboBox<>(AVAILABLE_RATIO);
        combo.setFont(FONT);
        combo.addItemListener(e -> RATIO.set(combo.getSelectedIndex()));
        combo.setEnabled(!FULLSCREEN.get());
        combo.setSelectedIndex(RATIO.get());

        final Box box = Box.createHorizontalBox();
        box.setBorder(BORDER);
        box.add(label);
        box.add(combo);
        parent.add(box);

        return combo;
    }

    private static void createFullscreen(Container parent, Container scale, Container ratio)
    {
        final JCheckBox check = new JCheckBox(LABEL_FULLSCREEN);
        check.setFont(FONT);
        check.setHorizontalTextPosition(SwingConstants.LEADING);
        check.setSelected(FULLSCREEN.get());
        check.addChangeListener(e ->
        {
            FULLSCREEN.set(check.isSelected());
            scale.setEnabled(!check.isSelected());
            ratio.setEnabled(!check.isSelected());
        });

        final JLabel label = new JLabel(LABEL_FLAG);
        label.setFont(FONT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

        final JComboBox<ImageLoadStrategy> combo = new JComboBox<>(ImageLoadStrategy.values());
        combo.setFont(FONT);
        combo.setSelectedIndex(FLAG.get());
        combo.addItemListener(e -> FLAG.set(combo.getSelectedIndex()));

        final JCheckBox raster = new JCheckBox(LABEL_RASTER);
        raster.setFont(FONT);
        raster.setHorizontalTextPosition(SwingConstants.LEADING);
        raster.setSelected(RASTER.get());
        raster.addChangeListener(e -> RASTER.set(raster.isSelected()));
        raster.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

        final Box box = Box.createHorizontalBox();
        box.setBorder(BORDER);
        box.add(check);
        box.add(label);
        box.add(combo);
        box.add(raster);
        parent.add(box);
    }

    private static void createZoom(Container parent)
    {
        final Box box = Box.createHorizontalBox();
        box.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        final JButton original = new JButton(LABEL_ORIGINAL);
        original.setFont(FONT);

        final JButton modern = new JButton(LABEL_MODERN);
        modern.setFont(FONT);

        final JSlider slider = createSlider(box,
                                            LABEL_ZOOM,
                                            ZOOM,
                                            (int) (Constant.ZOOM_MIN * 100),
                                            (int) (Constant.ZOOM_MAX * 100));

        original.addActionListener(event ->
        {
            slider.setValue(100);
            ZOOM.set(100);
        });
        modern.addActionListener(event ->
        {
            slider.setValue(115);
            ZOOM.set(115);
        });

        box.add(original);
        box.add(modern);
        parent.add(box);
    }

    private static JSlider createSlider(Container parent, String title, AtomicInteger value, int min, int max)
    {
        final JLabel label = new JLabel(title);
        label.setFont(FONT);

        final JSlider slider = new JSlider(min, max, value.get());
        slider.setFont(FONT);

        final JLabel result = new JLabel(String.valueOf(getPercent(slider.getValue())));
        result.setFont(FONT);
        result.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        slider.addChangeListener(e ->
        {
            value.set(slider.getValue());
            result.setText(getPercent(value.get()));
        });

        final Box box = Box.createHorizontalBox();
        box.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        box.add(label);
        box.add(slider);
        box.add(result);

        parent.add(box);

        return slider;
    }

    private static String getPercent(int percent)
    {
        if (percent < com.b3dgs.lionengine.Constant.DECADE)
        {
            return "00" + percent;
        }
        else if (percent < com.b3dgs.lionengine.Constant.HUNDRED)
        {
            return "0" + percent;
        }
        return String.valueOf(percent);
    }

    private static void createStages(Container parent)
    {
        final JLabel label = new JLabel(LABEL_STAGES);
        label.setFont(FONT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);

        final List<String> stages = Medias.create(Folder.STAGE)
                                          .getMedias()
                                          .stream()
                                          .filter(m -> m.getFile().isDirectory())
                                          .map(Media::getName)
                                          .sorted()
                                          .collect(Collectors.toList());

        final JComboBox<String> combo = new JComboBox<>(stages.toArray(new String[stages.size()]));
        combo.setFont(FONT);
        combo.setSelectedItem(STAGES.get());
        combo.addItemListener(e -> STAGES.set(combo.getItemAt(combo.getSelectedIndex())));

        final Box box = Box.createHorizontalBox();
        box.setBorder(BORDER);
        box.add(label);
        box.add(combo);
        parent.add(box);
    }

    private static void createGamepad(Container parent, Gamepad gamepad)
    {
        final JLabel label = new JLabel(LABEL_GAMEPAD);
        label.setFont(FONT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);

        final JComboBox<Object> combo = new JComboBox<>(gamepad.findDevices().keySet().toArray());
        combo.setFont(FONT);
        combo.addItemListener(e -> GAMEPAD.set(combo.getSelectedIndex()));

        final Box box = Box.createHorizontalBox();
        box.setBorder(BORDER);
        box.add(label);
        box.add(combo);
        parent.add(box);
    }

    private static void createSetups(Container parent, Window window, Gamepad gamepad)
    {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;

        final JPanel panel = new JPanel(new GridBagLayout());
        parent.add(panel);
        panel.setBorder(BORDER);
        final Collection<DeviceControllerConfig> configs;
        configs = DeviceControllerConfig.imports(new Services(), Medias.create(Settings.getInstance().getInput()));

        for (final DeviceControllerConfig config : configs)
        {
            final JButton setup = new JButton(LABEL_SETUP_DEVICE + config.getDevice().getSimpleName());
            setup.setFont(FONT);
            setup.addActionListener(event ->
            {
                final AssignController assigner = getAssigner(config, gamepad);
                final DeviceDialog dialog = new DeviceDialog(window, assigner);
                dialog.setResizable(false);
                dialog.pack();
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
                dialog.addWindowListener(new WindowAdapter()
                {
                    @Override
                    public void windowClosed(WindowEvent e)
                    {
                        assigner.stop();
                    }
                });
            });
            panel.add(setup, constraints);
        }
    }

    private static AssignController getAssigner(DeviceControllerConfig config, Gamepad gamepad)
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
            return new AssignerGamepad(gamepad);
        }
        throw new LionEngineException("Unknown device: " + device);
    }

    private static void createButtons(Container parent, Window window, Gamepad gamepad)
    {
        final JButton play = new JButton(LABEL_PLAY);
        play.setFont(FONT);
        play.addActionListener(event ->
        {
            gamepad.select(GAMEPAD.get());
            save();
            window.dispose();
            AppLionheart.run(gamepad);
        });

        final JButton exit = new JButton(LABEL_EXIT);
        exit.setFont(FONT);
        exit.addActionListener(event ->
        {
            save();
            gamepad.close();
            window.dispose();
        });

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;

        final JPanel panel = new JPanel(new GridBagLayout());
        parent.add(panel);
        panel.setBorder(BORDER);
        panel.add(play, constraints);
        panel.add(exit, constraints);
    }

    private static void createCopyright(Container parent)
    {
        final JLabel label = new JLabel(LABEL_MADE);
        label.setFont(FONT2);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event)
            {
                try
                {
                    final Desktop desktop = Desktop.getDesktop();
                    final URI oURL = new URI(com.b3dgs.lionengine.Constant.ENGINE_WEBSITE);
                    desktop.browse(oURL);
                }
                catch (final Exception exception) // CHECKSTYLE IGNORE LINE: IllegalCatch|TrailingComment
                {
                    Verbose.exception(exception);
                }
            }
        });
        parent.add(label, BorderLayout.SOUTH);
    }

    /**
     * Use system theme for UI.
     */
    private static void setThemeSystem()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (final ReflectiveOperationException | UnsupportedLookAndFeelException exception)
        {
            Verbose.exception(exception);
        }
    }

    /**
     * Run frame.
     * 
     * @param frame The frame reference.
     * @param panel The panel reference.
     */
    private static void run(JFrame frame, JPanel panel)
    {
        SwingUtilities.invokeLater(() ->
        {
            frame.add(panel);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static int getOutputWidth()
    {
        if (FULLSCREEN.get())
        {
            return GraphicsEnvironment.getLocalGraphicsEnvironment()
                                      .getDefaultScreenDevice()
                                      .getDisplayMode()
                                      .getWidth();
        }
        return AVAILABLE_SCALE[SCALE.get()].intValue() * Constant.RESOLUTION.getWidth();
    }

    private static int getOutputHeight()
    {
        if (FULLSCREEN.get())
        {
            return GraphicsEnvironment.getLocalGraphicsEnvironment()
                                      .getDefaultScreenDevice()
                                      .getDisplayMode()
                                      .getHeight();
        }
        return (int) Math.floor(AVAILABLE_SCALE[SCALE.get()].intValue()
                                * Constant.RESOLUTION.getWidth()
                                / RATIO_VALUE[RATIO.get()]);
    }

    private static void save()
    {
        Settings.loadDefault();
        final File file = UtilStream.getCopy(Medias.create(Settings.FILENAME));
        try
        {
            final List<String> lines = Files.readAllLines(file.toPath());
            try (FileWriter output = new FileWriter(file))
            {
                for (final String line : lines)
                {
                    final String[] data = line.split(SETTINGS_SEPARATOR);
                    if (line.contains(Settings.LANG))
                    {
                        writeFormatted(output, data, LANG.get());
                    }
                    else if (line.contains(Settings.RESOLUTION_WIDTH))
                    {
                        writeFormatted(output, data, getOutputWidth());
                    }
                    else if (line.contains(Settings.RESOLUTION_HEIGHT))
                    {
                        writeFormatted(output, data, getOutputHeight());
                    }
                    else if (line.contains(Settings.RASTER_ENABLED))
                    {
                        writeFormatted(output, data, RASTER.get());
                    }
                    else if (line.contains(Settings.ZOOM))
                    {
                        writeFormatted(output, data, ZOOM.get() / 100.0);
                    }
                    else if (line.contains(Settings.VOLUME_MUSIC))
                    {
                        writeFormatted(output, data, MUSIC.get());
                    }
                    else if (line.contains(Settings.VOLUME_SFX))
                    {
                        writeFormatted(output, data, SFX.get());
                    }
                    else if (line.contains(Settings.FLAG))
                    {
                        writeFormatted(output, data, FLAG.get());
                    }
                    else if (line.contains(Settings.STAGES))
                    {
                        writeFormatted(output, data, STAGES.get());
                    }
                    else
                    {
                        output.write(line);
                    }
                    output.write(System.lineSeparator());
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
        Settings.load();
    }

    private static void writeFormatted(FileWriter output, String[] data, String value) throws IOException
    {
        output.write(data[0] + SETTINGS_SEPARATOR + com.b3dgs.lionengine.Constant.SPACE + value);
    }

    private static void writeFormatted(FileWriter output, String[] data, int value) throws IOException
    {
        output.write(data[0] + SETTINGS_SEPARATOR + com.b3dgs.lionengine.Constant.SPACE + value);
    }

    private static void writeFormatted(FileWriter output, String[] data, double value) throws IOException
    {
        output.write(data[0] + SETTINGS_SEPARATOR + com.b3dgs.lionengine.Constant.SPACE + value);
    }

    private static void writeFormatted(FileWriter output, String[] data, boolean value) throws IOException
    {
        output.write(data[0] + SETTINGS_SEPARATOR + com.b3dgs.lionengine.Constant.SPACE + value);
    }

    /**
     * Private constructor.
     */
    private Launcher()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
