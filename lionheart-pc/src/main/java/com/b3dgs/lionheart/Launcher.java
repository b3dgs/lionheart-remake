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
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.DisplayMode;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

import org.libsdl.SDL;

import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFile;
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
// CHECKSTYLE IGNORE LINE: FanOutComplexity|DataAbstractionCoupling
public final class Launcher
{
    private static final String LANG_DEFAULT = "en";
    private static final String RELEASES_LINK = "https://github.com/b3dgs/lionheart-remake/releases";

    private static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 20);
    private static final Font FONT1 = new Font(Font.MONOSPACED, Font.PLAIN, 16);
    private static final Font FONT2 = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    private static final Integer[] AVAILABLE_SCALE = new Integer[]
    {
        null, Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4),
    };
    private static final String[] AVAILABLE_RATIO = new String[]
    {
        null, "5/4", "4/3", "16/10", "16/9", "21/9"
    };
    private static final double[] RATIO_VALUE = new double[]
    {
        // @formatter:off
        5.0 / 4.0, 4.0 / 3.0, 16.0 / 10.0, 16.0 / 9.0, 21.0 / 9.0
        // @formatter:on
    };
    private static final Map<String, String> LANG_FULL = new HashMap<>();
    private static final Map<String, String> LANG_SHORT = new HashMap<>();

    private static final AtomicInteger WIDTH = new AtomicInteger();
    private static final AtomicInteger HEIGHT = new AtomicInteger();
    private static final AtomicInteger RATE = new AtomicInteger();
    private static final AtomicBoolean WINDOWED = new AtomicBoolean();
    private static final AtomicInteger FLAG_STRATEGY = new AtomicInteger();
    private static final AtomicBoolean FLAG_PARALLEL = new AtomicBoolean();
    private static final AtomicBoolean FLAG_VSYNC = new AtomicBoolean();
    private static final AtomicReference<RasterType> RASTER = new AtomicReference<>();
    private static final AtomicBoolean HUD = new AtomicBoolean();
    private static final AtomicBoolean HUD_SWORD = new AtomicBoolean();
    private static final AtomicInteger ZOOM = new AtomicInteger();
    private static final AtomicInteger MUSIC = new AtomicInteger();
    private static final AtomicInteger SFX = new AtomicInteger();
    private static final AtomicInteger GAMEPAD = new AtomicInteger();
    private static final AtomicReference<String> STAGES = new AtomicReference<>(Folder.ORIGINAL);
    private static final AtomicReference<String> LANG = new AtomicReference<>(LANG_DEFAULT);
    private static final AtomicReference<FilterType> FILTER = new AtomicReference<>(FilterType.NONE);

    private static final String FOLDER_LAUNCHER = "launcher";
    private static final String FILENAME_LANG = "langs.txt";
    private static final String FILENAME_STAGE = "stages.txt";
    private static final String FILENAME_LABELS = "labels.txt";
    private static final String FILENAME_TOOLTIPS = "tooltips.txt";
    private static final String FILENAME_ICON = "icon-256.png";
    private static final String FILENAME_LOGO = "logo.png";
    private static final String FILENAME_LOGO_HOVER = "logo_hover.png";
    private static final String SETTINGS_SEPARATOR = "=";

    private static final String LABEL_LANG = "Lang:";
    private static final String LABEL_FILTER = "Filter:";
    private static final String LABEL_ZOOM = "Zoom:";
    private static final String LABEL_ORIGINAL = "Original";
    private static final String LABEL_REMAKE = "Remake";
    private static final String LABEL_MUSIC = "Music:";
    private static final String LABEL_SFX = "  Sfx:";
    private static final String LABEL_SCALE = "Scale:";
    private static final String LABEL_RESOLUTION = "Resolution:";
    private static final String LABEL_AVAILABLE = "Available:";
    private static final String LABEL_RATIO = "Ratio:";
    private static final String LABEL_WINDOWED = "Windowed:";
    private static final String LABEL_ADVANCED = "Advanced";
    private static final String LABEL_MISC = "Misc";
    private static final String LABEL_FLAG = "Flags";
    private static final String LABEL_FLAG_STRATEGY = "Strategy:";
    private static final String LABEL_FLAG_PARALLEL = "Parallel:";
    private static final String LABEL_FLAG_VSYNC = "VSync:";
    private static final String LABEL_RASTER = "Raster:";
    private static final String LABEL_HUD = "Hud";
    private static final String LABEL_HUD_VISIBLE = "Visible:";
    private static final String LABEL_HUD_SWORD = "Sword:";
    private static final String LABEL_STAGES = "Stages:";
    private static final String LABEL_GAMEPAD = "Gamepad:";
    private static final String LABEL_SETUP_DEVICE = "Setup ";
    private static final String LABEL_PLAY = "Play";
    private static final String LABEL_EDITOR = "Editor";
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

    private static final List<Consumer<String>> LABELS = new ArrayList<>();
    private static final List<JComponent> TIPS = new ArrayList<>();

    /**
     * Main function.
     * 
     * @param args The arguments (none).
     * @throws IOException If error.
     */
    public static void main(String[] args) throws IOException // CHECKSTYLE IGNORE LINE: TrailingComment|UncommentedMain
    {
        UIManager.put("ToolTip.font", FONT1);

        EngineAwt.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, AppLionheart.class);

        loadLangs();

        Settings.load();
        DeviceDialog.prepareInputCustom();

        final Gamepad gamepad = new Gamepad();

        setThemeSystem();
        loadPref();

        final JFrame frame = createFrame();

        final JPanel panel = new JPanel();
        final BorderLayout layout = new BorderLayout();
        panel.setLayout(layout);

        final Box box = Box.createVerticalBox();
        panel.add(box);

        final Box box2 = Box.createHorizontalBox();
        box.add(box2);

        final JLabel splash = new JLabel();
        final ImageIcon logo = new ImageIcon(Launcher.class.getResource(FILENAME_LOGO));
        final ImageIcon logoHover = new ImageIcon(Launcher.class.getResource(FILENAME_LOGO_HOVER));
        splash.setIcon(logo);
        splash.setToolTipText(Constant.PROGRAM_WEBSITE);
        splash.setBorder(BORDER_SPLASH);
        splash.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent event)
            {
                splash.setIcon(logoHover);
                splash.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent event)
            {
                splash.setIcon(logo);
                splash.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseClicked(MouseEvent event)
            {
                try
                {
                    final Desktop desktop = Desktop.getDesktop();
                    final URI oURL = new URI(Constant.PROGRAM_WEBSITE);
                    desktop.browse(oURL);
                }
                catch (final Exception exception) // CHECKSTYLE IGNORE LINE: IllegalCatch|TrailingComment
                {
                    Verbose.exception(exception);
                }
            }
        });
        box2.add(splash);

        createLang(box, frame);
        createScale(box);
        createFilter(box);
        createSlider(box, LABEL_MUSIC, MUSIC, 0, com.b3dgs.lionengine.Constant.HUNDRED);
        createSlider(box, LABEL_SFX, SFX, 0, com.b3dgs.lionengine.Constant.HUNDRED);
        createStages(box);

        final Box advanced = Box.createVerticalBox();
        createFlags(advanced);
        createMiscs(advanced);
        createZoom(advanced);
        advanced.setVisible(false);

        final JButton show = new JButton(LABEL_ADVANCED);
        show.setFont(FONT);
        show.addActionListener(e ->
        {
            advanced.setVisible(true);
            advanced.setBorder(BorderFactory.createTitledBorder(LABEL_ADVANCED));
            show.setVisible(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
        });
        LABELS.add(show::setText);

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;

        final JPanel advancedPanel = new JPanel(new GridBagLayout());
        advancedPanel.setBorder(BORDER);
        advancedPanel.add(show, constraints);

        box.add(advancedPanel);
        box.add(advanced);

        createGamepad(box, gamepad);
        createSetups(box, frame, gamepad);
        createButtons(box, frame, gamepad);
        createCopyright(frame);

        final String lang = Settings.getInstance().getLang();
        loadLabels(lang);
        loadToolTips(lang);

        run(frame, panel);
    }

    private static void loadPref()
    {
        final Settings settings = Settings.getInstance();

        LANG.set(settings.getLang());

        if (Settings.getFile().exists())
        {
            WIDTH.set(settings.getResolution().getWidth());
            HEIGHT.set(settings.getResolution().getHeight());
            RATE.set(settings.getResolution().getRate());
            WINDOWED.set(settings.getResolutionWindowed());
        }
        else
        {
            final DisplayMode desktop = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                           .getDefaultScreenDevice()
                                                           .getDisplayMode();
            WIDTH.set(desktop.getWidth());
            HEIGHT.set(desktop.getHeight());
            RATE.set(desktop.getRefreshRate());
            WINDOWED.set(false);
        }

        FILTER.set(settings.getFilter());
        RASTER.set(settings.getRaster());
        HUD.set(settings.getHudVisible());
        HUD_SWORD.set(settings.getHudSword());
        ZOOM.set(UtilMath.clamp((int) Math.round(settings.getZoom() * 100),
                                (int) (Constant.ZOOM_MIN * 100),
                                (int) (Constant.ZOOM_MAX * 100)));
        MUSIC.set(UtilMath.clamp(settings.getVolumeMusic(), 0, com.b3dgs.lionengine.Constant.HUNDRED));
        SFX.set(UtilMath.clamp(settings.getVolumeSfx(), 0, com.b3dgs.lionengine.Constant.HUNDRED));
        FLAG_STRATEGY.set(UtilMath.clamp(settings.getFlagStrategy(), 0, ImageLoadStrategy.values().length));
        FLAG_PARALLEL.set(settings.getFlagParallel());
        FLAG_VSYNC.set(settings.getFlagVsync());
        STAGES.set(settings.getStages());
    }

    private static void loadLangs()
    {
        for (final String lang : Util.readLines(Medias.create(Folder.TEXT, FILENAME_LANG)))
        {
            final String[] data = lang.split(com.b3dgs.lionengine.Constant.SPACE);
            LANG_FULL.put(data[0], data[1]);
            LANG_SHORT.put(data[1], data[0]);
        }
    }

    private static void loadLabels(String lang)
    {
        Media media = Medias.create(Folder.TEXT, lang, FOLDER_LAUNCHER, FILENAME_LABELS);
        if (!media.exists())
        {
            media = Medias.create(Folder.TEXT, lang, FOLDER_LAUNCHER, FILENAME_LABELS);
        }
        final List<String> labels = Util.readLines(media);
        final int n = Math.min(labels.size(), LABELS.size());
        for (int i = 0; i < n; i++)
        {
            LABELS.get(i).accept(labels.get(i));
        }
    }

    private static void loadToolTips(String lang)
    {
        Media media = Medias.create(Folder.TEXT, lang, FOLDER_LAUNCHER, FILENAME_TOOLTIPS);
        if (!media.exists())
        {
            media = Medias.create(Folder.TEXT, lang, FOLDER_LAUNCHER, FILENAME_TOOLTIPS);
        }
        final List<String> tips = Util.readLines(media);
        final int n = Math.min(tips.size(), TIPS.size());
        for (int i = 0; i < n; i++)
        {
            TIPS.get(i).setToolTipText(tips.get(i));
        }
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

    private static Container createLang(Container parent, JFrame frame)
    {
        final JLabel label = new JLabel(LABEL_LANG);
        label.setFont(FONT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        LABELS.add(label::setText);
        TIPS.add(label);

        final List<String> langs = Medias.create(Folder.TEXT)
                                         .getMedias()
                                         .stream()
                                         .map(s -> LANG_FULL.getOrDefault(s.getName(), s.getName()))
                                         .sorted()
                                         .collect(Collectors.toList());
        final JComboBox<String> combo = new JComboBox<>(langs.toArray(new String[langs.size()]));
        combo.setFont(FONT);
        combo.addItemListener(e ->
        {
            final String k = combo.getItemAt(combo.getSelectedIndex());
            final String lang = LANG_SHORT.getOrDefault(k, k);
            LANG.set(lang);
            loadLabels(lang);
            loadToolTips(lang);
            frame.pack();
            frame.setLocationRelativeTo(null);
        });
        combo.setSelectedItem(LANG_FULL.getOrDefault(LANG.get(), LANG.get()));
        TIPS.add(combo);

        final Box box = Box.createHorizontalBox();
        box.setBorder(BORDER);
        box.add(label);
        box.add(combo);
        parent.add(box);

        return combo;
    }

    private static void updateResolution(JComboBox<Integer> comboScale,
                                         JComboBox<String> comboRatio,
                                         JFormattedTextField width,
                                         JFormattedTextField height)
    {
        if (comboScale.getSelectedIndex() > 0 && comboRatio.getSelectedIndex() > 0)
        {
            int w = Constant.RESOLUTION.getWidth();
            int h = Constant.RESOLUTION.getHeight();

            final int scale = AVAILABLE_SCALE[comboScale.getSelectedIndex()].intValue();
            w *= scale;
            h *= scale;

            final double ratio = RATIO_VALUE[comboRatio.getSelectedIndex() - 1];
            w = (int) Math.round(h * ratio);

            width.setText(String.valueOf(w));
            height.setText(String.valueOf(h));
        }
    }

    private static void createScale(Container parent)
    {
        final JLabel labelScale = new JLabel(LABEL_SCALE);
        labelScale.setFont(FONT);
        labelScale.setHorizontalAlignment(SwingConstants.RIGHT);
        LABELS.add(labelScale::setText);
        TIPS.add(labelScale);

        final JComboBox<Integer> comboScale = new JComboBox<>(AVAILABLE_SCALE);
        comboScale.setFont(FONT);
        comboScale.setEnabled(WINDOWED.get());
        TIPS.add(comboScale);

        final JLabel labelRatio = new JLabel(LABEL_RATIO);
        labelRatio.setFont(FONT);
        labelRatio.setHorizontalAlignment(SwingConstants.RIGHT);
        labelRatio.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        LABELS.add(labelRatio::setText);
        TIPS.add(labelRatio);

        final JComboBox<String> comboRatio = new JComboBox<>(AVAILABLE_RATIO);
        comboRatio.setFont(FONT);
        comboRatio.setEnabled(WINDOWED.get());
        TIPS.add(comboRatio);

        final JLabel labelResolution = new JLabel(LABEL_RESOLUTION);
        labelResolution.setFont(FONT);
        labelResolution.setHorizontalAlignment(SwingConstants.RIGHT);
        LABELS.add(labelResolution::setText);
        TIPS.add(labelResolution);

        final NumberFormat format = NumberFormat.getIntegerInstance(Locale.ENGLISH);
        format.setGroupingUsed(false);
        final NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);

        final JFormattedTextField width = new JFormattedTextField(formatter);
        width.setFont(FONT);
        width.setHorizontalAlignment(SwingConstants.RIGHT);
        width.setText(String.valueOf(WIDTH.get()));
        width.setEditable(WINDOWED.get());
        width.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent event)
            {
                comboScale.setSelectedIndex(0);
                comboRatio.setSelectedIndex(0);
            }
        });
        width.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent event)
            {
                updateField(WIDTH, width);
            }

            @Override
            public void removeUpdate(DocumentEvent event)
            {
                updateField(WIDTH, width);
            }

            @Override
            public void changedUpdate(DocumentEvent event)
            {
                // Nothing to do
            }
        });

        final JLabel labelX = new JLabel(" x ");
        labelX.setFont(FONT);
        TIPS.add(labelX);

        final JFormattedTextField height = new JFormattedTextField(formatter);
        height.setFont(FONT);
        height.setHorizontalAlignment(SwingConstants.LEFT);
        height.setText(String.valueOf(HEIGHT.get()));
        height.setEditable(WINDOWED.get());
        height.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent event)
            {
                comboScale.setSelectedIndex(0);
                comboRatio.setSelectedIndex(0);
            }
        });
        height.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent event)
            {
                updateField(HEIGHT, height);
            }

            @Override
            public void removeUpdate(DocumentEvent event)
            {
                updateField(HEIGHT, height);
            }

            @Override
            public void changedUpdate(DocumentEvent event)
            {
                // Nothing to do
            }
        });

        final JLabel labelA = new JLabel(" @ ");
        labelA.setFont(FONT);
        TIPS.add(labelA);

        final JComboBox<Integer> comboRate = new JComboBox<>();
        comboRate.setFont(FONT);
        comboRate.removeAllItems();
        if (WINDOWED.get())
        {
            getAvailableRates().forEach(comboRate::addItem);
        }
        else
        {
            getNativeRates().forEach(comboRate::addItem);
        }
        comboRate.addItemListener(e ->
        {
            if (comboRate.getSelectedIndex() > -1)
            {
                RATE.set(comboRate.getItemAt(comboRate.getSelectedIndex()).intValue());
            }
        });
        comboRate.setSelectedItem(Integer.valueOf(RATE.get()));
        TIPS.add(comboRate);

        final JLabel labelHz = new JLabel("Hz");
        labelHz.setFont(FONT);

        comboScale.addItemListener(e -> updateResolution(comboScale, comboRatio, width, height));
        comboRatio.addItemListener(e -> updateResolution(comboScale, comboRatio, width, height));

        final Box box = Box.createHorizontalBox();
        box.setBorder(BORDER);
        box.add(labelScale);
        box.add(comboScale);
        box.add(labelRatio);
        box.add(comboRatio);

        final JComboBox<Res> comboRes = createResolutionsAvailable(box, width, height, comboRate);
        comboRes.setSelectedItem(new Res(WIDTH.get(), HEIGHT.get()));
        TIPS.add(comboRes);
        parent.add(box);

        final Box boxResolution = Box.createHorizontalBox();
        boxResolution.setBorder(BORDER);

        boxResolution.add(labelResolution);
        boxResolution.add(width);
        boxResolution.add(labelX);
        boxResolution.add(height);
        boxResolution.add(labelA);
        boxResolution.add(comboRate);
        boxResolution.add(labelHz);
        createWindowed(boxResolution, comboScale, comboRatio, comboRes, comboRate, width, height);

        parent.add(boxResolution);
    }

    private static List<Integer> getNativeRates()
    {
        return Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                .getDefaultScreenDevice()
                                                .getDisplayModes())
                     .stream()
                     .filter(d -> d.getRefreshRate() >= Constant.RESOLUTION.getRate())
                     .map(d -> Integer.valueOf(d.getRefreshRate()))
                     .distinct()
                     .sorted()
                     .collect(Collectors.toList());
    }

    private static List<Integer> getAvailableRates()
    {
        final List<Integer> rates = new ArrayList<>(getNativeRates());
        rates.add(Integer.valueOf(Constant.RESOLUTION.getRate()));
        rates.add(Integer.valueOf(Constant.RESOLUTION.getRate() * 2));
        return rates.stream().distinct().sorted().collect(Collectors.toList());
    }

    private static void updateField(AtomicInteger field, JFormattedTextField text)
    {
        try
        {
            field.set(Integer.parseInt(text.getText()));
        }
        catch (@SuppressWarnings("unused") final NumberFormatException exception)
        {
            // Skip
        }
    }

    private static JComboBox<Res> createResolutionsAvailable(Container parent,
                                                             JFormattedTextField width,
                                                             JFormattedTextField height,
                                                             JComboBox<Integer> comboRate)
    {
        final JLabel labelResolution = new JLabel(LABEL_AVAILABLE);
        labelResolution.setFont(FONT);
        labelResolution.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        labelResolution.setHorizontalAlignment(SwingConstants.RIGHT);
        LABELS.add(labelResolution::setText);
        TIPS.add(labelResolution);

        final List<Res> res = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                               .getDefaultScreenDevice()
                                                               .getDisplayModes())
                                    .stream()
                                    .map(d -> new Res(d.getWidth(), d.getHeight()))
                                    .distinct()
                                    .collect(Collectors.toList());
        Collections.sort(res);

        final JComboBox<Res> comboResolution;
        comboResolution = new JComboBox<>(res.toArray(new Res[res.size()]));
        comboResolution.setFont(FONT);
        TIPS.add(comboResolution);
        comboResolution.addItemListener(e ->
        {
            final Res r = comboResolution.getItemAt(comboResolution.getSelectedIndex());
            width.setText(String.valueOf(r.width));
            height.setText(String.valueOf(r.height));
            RATE.set(((Integer) comboRate.getSelectedItem()).intValue());
        });

        parent.add(labelResolution);
        parent.add(comboResolution);

        return comboResolution;
    }

    private static void createWindowed(Container parent,
                                       JComboBox<Integer> comboScale,
                                       JComboBox<String> comboRatio,
                                       JComboBox<Res> comboRes,
                                       JComboBox<Integer> comboRate,
                                       JFormattedTextField width,
                                       JFormattedTextField height)
    {
        final JCheckBox checkWindowed = new JCheckBox(LABEL_WINDOWED);
        checkWindowed.setFont(FONT);
        checkWindowed.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        checkWindowed.setHorizontalTextPosition(SwingConstants.LEADING);
        checkWindowed.setSelected(WINDOWED.get());
        checkWindowed.addActionListener(e ->
        {
            WINDOWED.set(checkWindowed.isSelected());
            if (!checkWindowed.isSelected())
            {
                final Res res = comboRes.getItemAt(comboRes.getSelectedIndex());
                width.setText(String.valueOf(res.width));
                height.setText(String.valueOf(res.height));
            }
            final Integer custom = comboRate.getItemAt(comboRate.getSelectedIndex());
            comboRate.removeAllItems();
            if (WINDOWED.get())
            {
                getAvailableRates().forEach(comboRate::addItem);
            }
            else
            {
                getNativeRates().forEach(comboRate::addItem);
            }
            comboRate.setSelectedIndex(getClosestRate(comboRate, custom));
            comboScale.setEnabled(checkWindowed.isSelected());
            comboRatio.setEnabled(checkWindowed.isSelected());
            width.setEditable(checkWindowed.isSelected());
            height.setEditable(checkWindowed.isSelected());
        });
        LABELS.add(checkWindowed::setText);
        TIPS.add(checkWindowed);

        parent.add(checkWindowed);
    }

    private static int getClosestRate(JComboBox<Integer> comboRate, Integer custom)
    {
        final int c = comboRate.getItemCount();
        for (int i = 0; i < c; i++)
        {
            final Integer rate = comboRate.getItemAt(i);
            if (rate.compareTo(custom) >= 0)
            {
                return i;
            }
        }
        return 0;
    }

    private static Container createFilter(Container parent)
    {
        final JLabel label = new JLabel(LABEL_FILTER);
        label.setFont(FONT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        LABELS.add(label::setText);
        TIPS.add(label);

        final JComboBox<FilterType> combo = new JComboBox<>(FilterType.values());
        combo.setFont(FONT);
        combo.addItemListener(e -> FILTER.set(combo.getItemAt(combo.getSelectedIndex())));
        combo.setSelectedItem(FILTER.get());
        TIPS.add(combo);

        final Box box = Box.createHorizontalBox();
        box.setBorder(BORDER);
        box.add(label);
        box.add(combo);
        parent.add(box);

        return combo;
    }

    private static void createFlags(Container parent)
    {
        final Box box = Box.createHorizontalBox();
        box.setBorder(BORDER);

        final Box flags = Box.createHorizontalBox();
        flags.setBorder(BorderFactory.createTitledBorder(LABEL_FLAG));

        final JLabel label = new JLabel(LABEL_FLAG_STRATEGY);
        label.setFont(FONT);
        TIPS.add(label);

        final JComboBox<ImageLoadStrategy> combo = new JComboBox<>(ImageLoadStrategy.values());
        combo.setFont(FONT);
        combo.setSelectedIndex(FLAG_STRATEGY.get());
        combo.addItemListener(e -> FLAG_STRATEGY.set(combo.getSelectedIndex()));
        TIPS.add(combo);

        final JCheckBox parallel = new JCheckBox(LABEL_FLAG_PARALLEL);
        parallel.setFont(FONT);
        parallel.setHorizontalTextPosition(SwingConstants.LEADING);
        parallel.setSelected(FLAG_PARALLEL.get());
        parallel.addChangeListener(e -> FLAG_PARALLEL.set(parallel.isSelected()));
        parallel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        TIPS.add(parallel);

        final JCheckBox vsync = new JCheckBox(LABEL_FLAG_VSYNC);
        vsync.setFont(FONT);
        vsync.setHorizontalTextPosition(SwingConstants.LEADING);
        vsync.setSelected(FLAG_VSYNC.get());
        vsync.addChangeListener(e -> FLAG_VSYNC.set(vsync.isSelected()));
        vsync.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        TIPS.add(vsync);

        flags.add(label);
        flags.add(combo);
        flags.add(parallel);
        flags.add(vsync);

        box.add(flags);

        parent.add(box);
    }

    private static void createMiscs(Container parent)
    {
        final JLabel raster = new JLabel(LABEL_RASTER);
        raster.setFont(FONT);
        raster.setHorizontalTextPosition(SwingConstants.LEADING);

        final JComboBox<RasterType> comboRaster;
        comboRaster = new JComboBox<>(RasterType.values());
        comboRaster.setFont(FONT);
        comboRaster.setSelectedItem(RASTER.get());
        comboRaster.addItemListener(e -> RASTER.set(comboRaster.getItemAt(comboRaster.getSelectedIndex())));
        TIPS.add(raster);

        final Box box = Box.createHorizontalBox();
        box.setBorder(BORDER);

        final Box misc = Box.createHorizontalBox();
        misc.setBorder(BorderFactory.createTitledBorder(LABEL_MISC));
        misc.add(raster);
        misc.add(comboRaster);

        box.add(misc);
        createHud(box);

        parent.add(box);
    }

    private static void createHud(Container parent)
    {
        final JCheckBox hud = new JCheckBox(LABEL_HUD_VISIBLE);
        hud.setFont(FONT);
        hud.setHorizontalTextPosition(SwingConstants.LEADING);
        hud.setSelected(HUD.get());
        hud.addChangeListener(e -> HUD.set(hud.isSelected()));
        LABELS.add(hud::setText);
        TIPS.add(hud);

        final JCheckBox sword = new JCheckBox(LABEL_HUD_SWORD);
        sword.setFont(FONT);
        sword.setHorizontalTextPosition(SwingConstants.LEADING);
        sword.setSelected(HUD_SWORD.get());
        sword.addChangeListener(e -> HUD_SWORD.set(sword.isSelected()));
        sword.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        LABELS.add(sword::setText);
        TIPS.add(sword);

        final Box box = Box.createHorizontalBox();
        box.setBorder(BorderFactory.createTitledBorder(LABEL_HUD));
        box.add(hud);
        box.add(sword);

        parent.add(box);
    }

    private static void createZoom(Container parent)
    {
        final Box box = Box.createHorizontalBox();
        box.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        final JButton original = new JButton(LABEL_ORIGINAL);
        original.setFont(FONT);
        TIPS.add(original);

        final JButton remake = new JButton(LABEL_REMAKE);
        remake.setFont(FONT);
        TIPS.add(remake);

        final JSlider slider = createSlider(box,
                                            LABEL_ZOOM,
                                            ZOOM,
                                            (int) (Constant.ZOOM_MIN * 100),
                                            (int) (Constant.ZOOM_MAX * 100));
        TIPS.add(slider);

        original.addActionListener(event ->
        {
            slider.setValue(100);
            ZOOM.set(100);
        });
        remake.addActionListener(event ->
        {
            slider.setValue(115);
            ZOOM.set(115);
        });

        box.add(original);
        box.add(remake);
        parent.add(box);
    }

    private static JSlider createSlider(Container parent, String title, AtomicInteger value, int min, int max)
    {
        final JLabel label = new JLabel(title);
        label.setFont(FONT);
        LABELS.add(label::setText);
        TIPS.add(label);

        final JSlider slider = new JSlider(min, max, value.get());
        slider.setFont(FONT);
        TIPS.add(slider);

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
        LABELS.add(label::setText);
        TIPS.add(label);

        final List<String> stages = Util.readLines(Medias.create(Folder.STAGE, FILENAME_STAGE));

        final JComboBox<String> combo = new JComboBox<>(stages.toArray(new String[stages.size()]));
        combo.setFont(FONT);
        combo.setSelectedItem(STAGES.get());
        combo.addItemListener(e -> STAGES.set(combo.getItemAt(combo.getSelectedIndex())));
        TIPS.add(combo);

        final Box box = Box.createHorizontalBox();
        box.setBorder(BORDER);
        box.add(label);
        box.add(combo);
        parent.add(box);
    }

    private static String getControllerKey(int i)
    {
        return SDL.SDL_JoystickNameForIndex(i) + "#" + i;
    }

    private static void createGamepad(Container parent, Gamepad gamepad)
    {
        final JLabel label = new JLabel(LABEL_GAMEPAD);
        label.setFont(FONT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        LABELS.add(label::setText);
        TIPS.add(label);

        final JComboBox<Object> combo = new JComboBox<>(gamepad.findDevices()
                                                               .values()
                                                               .stream()
                                                               .map(Launcher::getControllerKey)
                                                               .toArray());
        combo.setFont(FONT);
        combo.addItemListener(e -> GAMEPAD.set(combo.getSelectedIndex()));
        TIPS.add(combo);

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
        configs = DeviceControllerConfig.imports(new Services(), Medias.create(Constant.INPUT_FILE_DEFAULT));

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
            LABELS.add(setup::setText);
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
        LABELS.add(play::setText);
        TIPS.add(play);

        final String path = "editor/Lionheart Remake Editor.exe";
        final JButton editor = new JButton(LABEL_EDITOR);
        editor.setFont(FONT);
        editor.addActionListener(event ->
        {
            save();
            if (UtilFile.exists(path))
            {
                try
                {
                    Runtime.getRuntime().exec(path);
                }
                catch (final IOException exception)
                {
                    Verbose.exception(exception);
                }
            }
            else
            {
                try
                {
                    final Desktop desktop = Desktop.getDesktop();
                    final URI oURL = new URI(RELEASES_LINK);
                    desktop.browse(oURL);
                }
                catch (final Exception exception) // CHECKSTYLE IGNORE LINE: IllegalCatch|TrailingComment
                {
                    Verbose.exception(exception);
                }
            }
            window.dispose();
        });
        editor.setVisible(UtilFile.exists(path));
        LABELS.add(editor::setText);
        TIPS.add(editor);

        final JButton exit = new JButton(LABEL_EXIT);
        exit.setFont(FONT);
        exit.addActionListener(event ->
        {
            save();
            gamepad.close();
            window.dispose();
        });
        LABELS.add(exit::setText);
        TIPS.add(exit);

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;

        final JPanel panel = new JPanel(new GridBagLayout());
        parent.add(panel);
        panel.setBorder(BORDER);
        panel.add(play, constraints);
        panel.add(editor, constraints);
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
        try
        {
            SwingUtilities.invokeAndWait(() ->
            {
                frame.add(panel);
                frame.setResizable(false);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
            new Thread(() ->
            {
                try
                {
                    Thread.sleep(250);
                }
                catch (@SuppressWarnings("unused") final InterruptedException exception)
                {
                    Thread.currentThread().interrupt();
                }
                SwingUtilities.invokeLater(() ->
                {
                    frame.toFront();
                });
            }).start();
        }
        catch (InvocationTargetException | InterruptedException exception)
        {
            Verbose.exception(exception);
        }
    }

    private static void prepareSettings()
    {
        try (InputStream input = Medias.create(Settings.FILENAME).getUrl().openStream();
             OutputStream output = Medias.create(Settings.FILENAME).getOutputStream())
        {
            UtilStream.copy(input, output);
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
        }
    }

    private static void save()
    {
        Settings.loadDefault();
        prepareSettings();

        final File file = Medias.create(Settings.FILENAME).getFile();
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
                    else if (line.contains(Settings.RESOLUTION_WINDOWED))
                    {
                        writeFormatted(output, data, WINDOWED.get());
                    }
                    else if (line.contains(Settings.RESOLUTION_WIDTH))
                    {
                        writeFormatted(output, data, WIDTH.get());
                    }
                    else if (line.contains(Settings.RESOLUTION_HEIGHT))
                    {
                        writeFormatted(output, data, HEIGHT.get());
                    }
                    else if (line.contains(Settings.RESOLUTION_RATE))
                    {
                        writeFormatted(output, data, RATE.get());
                    }
                    else if (line.contains(Settings.FILTER))
                    {
                        writeFormatted(output, data, FILTER.get().name());
                    }
                    else if (line.contains(Settings.RASTER_TYPE))
                    {
                        writeFormatted(output, data, RASTER.get().name());
                    }
                    else if (line.contains(Settings.HUD_VISIBLE))
                    {
                        writeFormatted(output, data, HUD.get());
                    }
                    else if (line.contains(Settings.HUD_SWORD))
                    {
                        writeFormatted(output, data, HUD_SWORD.get());
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
                    else if (line.contains(Settings.FLAG_STRATEGY))
                    {
                        writeFormatted(output, data, FLAG_STRATEGY.get());
                    }
                    else if (line.contains(Settings.FLAG_PARALLEL))
                    {
                        writeFormatted(output, data, FLAG_PARALLEL.get());
                    }
                    else if (line.contains(Settings.FLAG_VSYNC))
                    {
                        writeFormatted(output, data, FLAG_VSYNC.get());
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

    private static class Res implements Comparable<Res>
    {
        private final int width;
        private final int height;

        /**
         * Create a resolution.
         * 
         * @param width The width.
         * @param height The height.
         */
        public Res(int width, int height)
        {
            super();

            this.width = width;
            this.height = height;
        }

        @Override
        public int compareTo(Res other)
        {
            final int side;
            if (height < other.height)
            {
                side = -1;
            }
            else if (height > other.height)
            {
                side = 1;
            }
            else
            {
                side = 0;
            }
            return side;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + width;
            result = prime * result + height;
            return result;
        }

        @Override
        public boolean equals(Object object)
        {
            if (this == object)
            {
                return true;
            }
            if (object == null || object.getClass() != getClass())
            {
                return false;
            }
            final Res other = (Res) object;
            return width == other.width && height == other.height;
        }

        @Override
        public String toString()
        {
            return new StringBuilder().append(width).append(" x ").append(height).toString();
        }
    }
}
