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
import java.awt.Component;
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
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.swing.JOptionPane;
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

import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.awt.graphic.EngineAwt;
import com.b3dgs.lionengine.awt.graphic.ImageLoadStrategy;
import com.b3dgs.lionengine.awt.graphic.ToolsAwt;
import com.b3dgs.lionengine.network.MessageType;
import com.b3dgs.lionengine.network.Network;
import com.b3dgs.lionengine.network.NetworkType;
import com.b3dgs.lionengine.network.UtilNetwork;
import com.b3dgs.lionengine.network.client.InfoGet;
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
    private static final AtomicBoolean FLICKER_BACKGROUND = new AtomicBoolean();
    private static final AtomicBoolean FLICKER_FOREGROUND = new AtomicBoolean();
    private static final AtomicInteger ZOOM = new AtomicInteger();
    private static final AtomicInteger MUSIC = new AtomicInteger();
    private static final AtomicInteger SFX = new AtomicInteger();
    private static final AtomicReference<String> LANG = new AtomicReference<>(LANG_DEFAULT);
    private static final AtomicReference<FilterType> FILTER = new AtomicReference<>(FilterType.NONE);
    private static final AtomicReference<GameplayType> GAMEPLAY = new AtomicReference<>(GameplayType.ORIGINAL);

    private static final String FOLDER_LAUNCHER = "launcher";
    private static final String FILENAME_LANGS = "langs.txt";
    private static final String FILENAME_LABELS = "labels.txt";
    private static final String FILENAME_TOOLTIPS = "tooltips.txt";
    private static final String FILENAME_ICON = "icon-256.png";
    private static final String FILENAME_LOGO = "logo.png";
    private static final String FILENAME_LOGO_HOVER = "logo_hover.png";
    private static final String SETTINGS_SEPARATOR = " = ";

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
    private static final String LABEL_FLAG_STRATEGY = "Strategy:";
    private static final String LABEL_FLAG_PARALLEL = "Parallel:";
    private static final String LABEL_FLAG_VSYNC = "VSync:";
    private static final String LABEL_RASTER = "Raster:";
    private static final String LABEL_HUD = "Hud";
    private static final String LABEL_HUD_VISIBLE = "Visible:";
    private static final String LABEL_HUD_SWORD = "Sword:";
    private static final String LABEL_FLICKER = "Flicker";
    private static final String LABEL_FLICKER_BACKGROUND = "Background:";
    private static final String LABEL_FLICKER_FOREGROUND = "Foreground:";
    private static final String LABEL_GAMEPLAY = "Gameplay:";
    private static final String LABEL_CONTROLS = "Controls";
    private static final String LABEL_PLAY = "Play";
    private static final String LABEL_START_SERVER = "Start Server";
    private static final String LABEL_JOIN_GAME = "Join Game";
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
    private static final Border BORDER_CHECK = BorderFactory.createEmptyBorder(0, 30, 0, 0);
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
        Tools.disableAutoScale();
        UIManager.put("ToolTip.font", FONT1);
        UIManager.put("OptionPane.messageFont", FONT);
        UIManager.put("OptionPane.buttonFont", FONT);
        UIManager.put("ComboBox.font", FONT);
        UIManager.put("TextField.font", FONT);

        if (!Engine.isStarted())
        {
            EngineAwt.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, AppLionheart.class);
        }

        loadLangs();

        Settings.load();
        Tools.prepareInputCustom();

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
        splash.addMouseListener(createSplashListener(splash, logo, logoHover));
        box2.add(splash);

        createLang(box, frame);
        createScale(box);
        createFilter(box);
        createGameplay(box);
        createSlider(box, LABEL_MUSIC, MUSIC, 0, com.b3dgs.lionengine.Constant.HUNDRED);
        createSlider(box, LABEL_SFX, SFX, 0, com.b3dgs.lionengine.Constant.HUNDRED);

        final Box advanced = Box.createVerticalBox();
        createFlags(advanced);
        createMiscs(advanced);
        createZoom(advanced);
        advanced.setVisible(false);

        final Box advancedInner = createBorderBox(box, advanced);

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
        box.add(advancedInner);

        createControls(box, frame, gamepad);
        createButtons(box, frame, gamepad);
        createCopyright(frame);

        final String lang = Settings.getInstance().getLang();
        loadLabels(lang);
        loadToolTips(lang);

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

    private static MouseListener createSplashListener(JLabel splash, ImageIcon logo, ImageIcon logoHover)
    {
        return new MouseAdapter()
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
        };
    }

    private static Container createLang(Container parent, JFrame frame)
    {
        final JLabel label = createLabel(LABEL_LANG, SwingConstants.RIGHT);

        final List<String> langs = LANG_FULL.values().stream().sorted().collect(Collectors.toList());
        final JComboBox<String> combo = createCombo(langs.toArray(new String[langs.size()]),
                                                    LANG_FULL.getOrDefault(LANG.get(), LANG.get()));
        combo.addActionListener(e ->
        {
            final String k = combo.getItemAt(combo.getSelectedIndex());
            final String lang = LANG_SHORT.getOrDefault(k, k);
            LANG.set(lang);
            loadLabels(lang);
            loadToolTips(lang);
            frame.pack();
            frame.setLocationRelativeTo(null);
        });

        createBorderBox(parent, label, combo);

        return combo;
    }

    private static void createScale(Container parent)
    {
        final JLabel labelScale = createLabel(LABEL_SCALE, SwingConstants.RIGHT);

        final JComboBox<Integer> comboScale = createCombo(AVAILABLE_SCALE, null);
        comboScale.setEnabled(WINDOWED.get());

        final JLabel labelRatio = createLabel(LABEL_RATIO, SwingConstants.RIGHT);
        labelRatio.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

        final JComboBox<String> comboRatio = createCombo(AVAILABLE_RATIO, null);
        comboRatio.setEnabled(WINDOWED.get());

        final JLabel labelResolution = createLabel(LABEL_RESOLUTION, SwingConstants.RIGHT);

        final NumberFormat format = NumberFormat.getIntegerInstance(Locale.ENGLISH);
        format.setGroupingUsed(false);
        final NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);

        final JFormattedTextField width = createField(formatter, SwingConstants.RIGHT, WIDTH, comboScale, comboRatio);

        final JLabel labelX = new JLabel(" x ");
        labelX.setFont(FONT);
        TIPS.add(labelX);

        final JFormattedTextField height = createField(formatter, SwingConstants.LEFT, HEIGHT, comboScale, comboRatio);

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
        comboRate.addActionListener(e ->
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

        comboScale.addActionListener(e -> updateResolution(comboScale, comboRatio, width, height));
        comboRatio.addActionListener(e -> updateResolution(comboScale, comboRatio, width, height));

        final Box box = createBorderBox(parent, labelScale, comboScale, labelRatio, comboRatio);

        final JComboBox<Res> comboRes = createResolutionsAvailable(box, width, height, comboRate);
        comboRes.setSelectedItem(new Res(WIDTH.get(), HEIGHT.get()));
        TIPS.add(comboRes);

        final Box boxResolution;
        boxResolution = createBorderBox(parent, labelResolution, width, labelX, height, labelA, comboRate, labelHz);
        createWindowed(boxResolution, comboScale, comboRatio, comboRes, comboRate, width, height);
    }

    private static JComboBox<Res> createResolutionsAvailable(Container parent,
                                                             JFormattedTextField width,
                                                             JFormattedTextField height,
                                                             JComboBox<Integer> comboRate)
    {
        final JLabel labelResolution = createLabel(LABEL_AVAILABLE, SwingConstants.RIGHT);
        labelResolution.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

        final List<Res> res = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                               .getDefaultScreenDevice()
                                                               .getDisplayModes())
                                    .stream()
                                    .map(d -> new Res(d.getWidth(), d.getHeight()))
                                    .distinct()
                                    .collect(Collectors.toList());
        Collections.sort(res);

        final JComboBox<Res> comboResolution = createCombo(res.toArray(new Res[res.size()]), null);
        comboResolution.addActionListener(e ->
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
        final JCheckBox checkWindowed = createCheck(LABEL_WINDOWED, WINDOWED);
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

        parent.add(checkWindowed);
    }

    private static Container createFilter(Container parent)
    {
        final JLabel label = createLabel(LABEL_FILTER, SwingConstants.RIGHT);

        final JComboBox<FilterType> combo = createCombo(FilterType.values(), FILTER.get());
        combo.addActionListener(e -> FILTER.set(combo.getItemAt(combo.getSelectedIndex())));

        createBorderBox(parent, label, combo);

        return combo;
    }

    private static Container createGameplay(Container parent)
    {
        final JLabel label = createLabel(LABEL_GAMEPLAY, SwingConstants.RIGHT);

        final JComboBox<GameplayType> combo = createCombo(GameplayType.values(), GAMEPLAY.get());
        combo.addActionListener(e -> GAMEPLAY.set(combo.getItemAt(combo.getSelectedIndex())));

        createBorderBox(parent, label, combo);

        return combo;
    }

    private static void createFlags(Container parent)
    {
        final JLabel label = createLabel(LABEL_FLAG_STRATEGY, SwingConstants.LEADING);

        final JComboBox<ImageLoadStrategy> combo = createCombo(ImageLoadStrategy.values(), null);
        combo.setSelectedIndex(FLAG_STRATEGY.get());
        combo.addActionListener(e -> FLAG_STRATEGY.set(combo.getSelectedIndex()));

        final JCheckBox parallel = createCheck(LABEL_FLAG_PARALLEL, FLAG_PARALLEL);
        parallel.addChangeListener(e -> FLAG_PARALLEL.set(parallel.isSelected()));

        final JCheckBox vsync = createCheck(LABEL_FLAG_VSYNC, FLAG_VSYNC);
        vsync.addChangeListener(e -> FLAG_VSYNC.set(vsync.isSelected()));

        createBorderBox(parent, label, combo, parallel, vsync);
    }

    private static void createMiscs(Container parent)
    {
        createRaster(parent);

        final Box box = Box.createHorizontalBox();
        createHud(box);
        createFlicker(box);
        parent.add(box);
    }

    private static void createRaster(Container parent)
    {
        final JLabel raster = createLabel(LABEL_RASTER, SwingConstants.LEADING);

        final JComboBox<RasterType> comboRaster = createCombo(RasterType.values(), RASTER.get());
        comboRaster.addActionListener(e -> RASTER.set(comboRaster.getItemAt(comboRaster.getSelectedIndex())));

        createBorderBox(parent, raster, comboRaster);
    }

    private static void createHud(Container parent)
    {
        final JCheckBox hud = createCheck(LABEL_HUD_VISIBLE, HUD);
        hud.addChangeListener(e -> HUD.set(hud.isSelected()));

        final JCheckBox sword = createCheck(LABEL_HUD_SWORD, HUD_SWORD);
        sword.addChangeListener(e -> HUD_SWORD.set(sword.isSelected()));

        final Box box = Box.createHorizontalBox();
        box.setBorder(BorderFactory.createTitledBorder(LABEL_HUD));
        box.add(hud);
        box.add(sword);

        createBorderBox(parent, box);
    }

    private static void createFlicker(Container parent)
    {
        final JCheckBox background = createCheck(LABEL_FLICKER_BACKGROUND, FLICKER_BACKGROUND);
        background.addChangeListener(e -> FLICKER_BACKGROUND.set(background.isSelected()));

        final JCheckBox foreground = createCheck(LABEL_FLICKER_FOREGROUND, FLICKER_FOREGROUND);
        foreground.addChangeListener(e -> FLICKER_FOREGROUND.set(foreground.isSelected()));

        final Box box = Box.createHorizontalBox();
        box.setBorder(BorderFactory.createTitledBorder(LABEL_FLICKER));
        box.add(background);
        box.add(foreground);

        createBorderBox(parent, box);
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

    private static void createControls(Container parent, Window window, Gamepad gamepad)
    {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;

        final JPanel panel = new JPanel(new GridBagLayout());
        parent.add(panel);
        panel.setBorder(BORDER);

        final JButton controls = new JButton(LABEL_CONTROLS);
        controls.setFont(FONT);
        LABELS.add(controls::setText);
        TIPS.add(controls);
        panel.add(controls, constraints);

        controls.addActionListener(event ->
        {
            final ProfilesDialog dialog = new ProfilesDialog(window, gamepad);
            dialog.setTitle(LABEL_CONTROLS);
            dialog.setVisible(true);
        });
    }

    private static void createButtons(Container parent, Window window, Gamepad gamepad)
    {
        final JButton play = createButtonPlay(window, gamepad);
        final JButton startServer = createButtonStartServer(parent, window, gamepad);
        final JButton joinGame = createButtonJoinGame(parent, window, gamepad);
        final JButton editor = createButtonEditor(window);
        final JButton exit = createButtonExit(window, gamepad);

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;

        final JPanel panel = new JPanel(new GridBagLayout());
        parent.add(panel);
        panel.setBorder(BORDER);
        panel.add(play, constraints);
        // TODO panel.add(startServer, constraints);
        // TODO panel.add(joinGame, constraints);
        panel.add(editor, constraints);
        panel.add(exit, constraints);
    }

    private static JButton createButtonPlay(Window window, Gamepad gamepad)
    {
        final JButton play = new JButton(LABEL_PLAY);
        play.setFont(FONT);
        play.addActionListener(event ->
        {
            save();
            window.dispose();
            AppLionheart.run(new GameConfig(), gamepad, false);
        });
        LABELS.add(play::setText);
        TIPS.add(play);

        return play;
    }

    private static JButton createButtonStartServer(Container parent, Window window, Gamepad gamepad)
    {
        final JButton startServer = new JButton(LABEL_START_SERVER);
        startServer.setFont(FONT);
        startServer.addActionListener(event ->
        {
            final MultiplayerDialog dialog = new MultiplayerDialog(window);
            dialog.setVisible(true);

            final NetworkStageData data = new NetworkStageData(dialog.getGameType(),
                                                               dialog.getStage(),
                                                               dialog.getHealth(),
                                                               dialog.getLife());

            final String ip = JOptionPane.showInputDialog(parent,
                                                          "IP",
                                                          "x.x.x.x / None (automatic)",
                                                          JOptionPane.QUESTION_MESSAGE);
            runGame(window, new Network(NetworkType.SERVER, getLocalIp(ip), 1000), gamepad, data);
        });
        LABELS.add(startServer::setText);
        TIPS.add(startServer);

        return startServer;
    }

    private static JButton createButtonJoinGame(Container parent, Window window, Gamepad gamepad)
    {
        final JButton joinGame = new JButton(LABEL_JOIN_GAME);
        joinGame.setFont(FONT);
        joinGame.addActionListener(event ->
        {
            final String ip = JOptionPane.showInputDialog(parent,
                                                          "IP",
                                                          joinGame.getText(),
                                                          JOptionPane.QUESTION_MESSAGE);
            final String name = JOptionPane.showInputDialog(parent,
                                                            "NAME",
                                                            joinGame.getText(),
                                                            JOptionPane.QUESTION_MESSAGE);
            if (ip != null && name != null)
            {
                try
                {
                    runGame(window, new Network(NetworkType.CLIENT, ip, 1000, name), gamepad, getGameType(ip, 1000));
                }
                catch (final IOException exception)
                {
                    Verbose.exception(exception);
                }
            }
        });
        LABELS.add(joinGame::setText);
        TIPS.add(joinGame);

        return joinGame;
    }

    private static JButton createButtonEditor(Window window)
    {
        final String path1 = "editor/lionheart_remake_editor.exe";
        final String path2 = "editor/lionheart_remake_editor";
        final JButton editor = new JButton(LABEL_EDITOR);
        editor.setFont(FONT);
        editor.addActionListener(event ->
        {
            save();
            if (UtilFile.exists(path1))
            {
                try
                {
                    Runtime.getRuntime().exec(path1);
                }
                catch (final IOException exception)
                {
                    Verbose.exception(exception);
                }
            }
            else if (UtilFile.exists(path2))
            {
                try
                {
                    Runtime.getRuntime().exec(path2);
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
        editor.setEnabled(UtilFile.exists(path1) || UtilFile.exists(path2));
        LABELS.add(editor::setText);
        TIPS.add(editor);

        return editor;
    }

    private static JButton createButtonExit(Window window, Gamepad gamepad)
    {
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

        return exit;
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

    private static void loadLangs()
    {
        for (final String lang : Util.readLines(Medias.create(Folder.TEXT, FILENAME_LANGS)))
        {
            for (final String litteral : Util.readLines(Medias.create(Folder.TEXT, lang, Settings.FILE_LANG)))
            {
                LANG_FULL.put(lang, litteral);
                LANG_SHORT.put(litteral, lang);
                break;
            }
        }
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

    private static void loadPref()
    {
        final Settings settings = Settings.getInstance();

        LANG.set(settings.getLang());

        final DisplayMode screen = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                      .getDefaultScreenDevice()
                                                      .getDisplayMode();
        final Resolution desktop = new Resolution(screen.getWidth(), screen.getHeight(), screen.getRefreshRate());

        if (Settings.getFile().exists())
        {
            WIDTH.set(settings.getResolution(desktop).getWidth());
            HEIGHT.set(settings.getResolution(desktop).getHeight());
            RATE.set(settings.getResolution(desktop).getRate());
            WINDOWED.set(settings.getResolutionWindowed());
        }
        else
        {
            WIDTH.set(desktop.getWidth());
            HEIGHT.set(desktop.getHeight());
            RATE.set(desktop.getRate());
            WINDOWED.set(true);
        }

        FILTER.set(settings.getFilter());
        GAMEPLAY.set(settings.getGameplay());
        RASTER.set(settings.getRaster());
        HUD.set(settings.getHudVisible());
        HUD_SWORD.set(settings.getHudSword());
        FLICKER_BACKGROUND.set(settings.getFlickerBackground());
        FLICKER_FOREGROUND.set(settings.getFlickerForeground());
        ZOOM.set(UtilMath.clamp((int) Math.round(settings.getZoom() * 100),
                                (int) (Constant.ZOOM_MIN * 100),
                                (int) (Constant.ZOOM_MAX * 100)));
        MUSIC.set(UtilMath.clamp(settings.getVolumeMusic(), 0, com.b3dgs.lionengine.Constant.HUNDRED));
        SFX.set(UtilMath.clamp(settings.getVolumeSfx(), 0, com.b3dgs.lionengine.Constant.HUNDRED));
        FLAG_STRATEGY.set(UtilMath.clamp(settings.getFlagStrategy(), 0, ImageLoadStrategy.values().length));
        FLAG_PARALLEL.set(settings.getFlagParallel());
        FLAG_VSYNC.set(settings.getFlagVsync());
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

    private static JLabel createLabel(String name, int align)
    {
        final JLabel label = new JLabel(name);
        label.setFont(FONT);
        label.setHorizontalAlignment(align);
        LABELS.add(label::setText);
        TIPS.add(label);
        return label;
    }

    private static <T> JComboBox<T> createCombo(T[] values, T selected)
    {
        final JComboBox<T> combo = new JComboBox<>(values);
        combo.setFont(FONT);
        combo.setSelectedItem(selected);
        TIPS.add(combo);
        return combo;
    }

    private static Box createBorderBox(Container parent, Component... comp)
    {
        final Box box = Box.createHorizontalBox();
        box.setBorder(BORDER);
        for (final Component c : comp)
        {
            box.add(c);
        }
        parent.add(box);
        return box;
    }

    private static JFormattedTextField createField(NumberFormatter formatter,
                                                   int align,
                                                   AtomicInteger field,
                                                   JComboBox<Integer> scale,
                                                   JComboBox<String> ratio)
    {
        final JFormattedTextField tf = new JFormattedTextField(formatter);
        tf.setFont(FONT);
        tf.setHorizontalAlignment(align);
        tf.setText(String.valueOf(field.get()));
        tf.setEditable(WINDOWED.get());
        tf.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent event)
            {
                scale.setSelectedIndex(0);
                ratio.setSelectedIndex(0);
            }
        });
        tf.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent event)
            {
                updateField(field, tf);
            }

            @Override
            public void removeUpdate(DocumentEvent event)
            {
                updateField(field, tf);
            }

            @Override
            public void changedUpdate(DocumentEvent event)
            {
                // Nothing to do
            }
        });
        return tf;
    }

    private static JCheckBox createCheck(String label, AtomicBoolean selected)
    {
        final JCheckBox check = new JCheckBox(label);
        check.setFont(FONT);
        check.setBorder(BORDER_CHECK);
        check.setHorizontalTextPosition(SwingConstants.LEADING);
        check.setSelected(selected.get());
        LABELS.add(check::setText);
        TIPS.add(check);
        return check;
    }

    private static JSlider createSlider(Container parent, String title, AtomicInteger value, int min, int max)
    {
        final JLabel label = createLabel(title, SwingConstants.LEADING);

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

    private static String getLocalIp(String ip)
    {
        if (ip != null)
        {
            return ip;
        }
        try
        {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (@SuppressWarnings("unused") final UnknownHostException exception)
        {
            return "127.0.0.1";
        }
    }

    private static NetworkStageData getGameType(String ip, int port) throws IOException
    {
        try
        {
            final InetAddress address = InetAddress.getByName(ip);
            final DatagramSocket socket = new DatagramSocket();

            final ByteBuffer buffer = ByteBuffer.allocate(1);
            buffer.put(UtilNetwork.toByte(MessageType.INFO));
            final ByteBuffer send = UtilNetwork.createPacket(buffer);
            socket.send(new DatagramPacket(send.array(), send.capacity(), address, port));

            final ByteBuffer info = InfoGet.decode(socket);
            final GameType type = GameType.values()[UtilConversion.toUnsignedByte(info.get())];
            final int size = UtilConversion.toUnsignedByte(info.get());
            final byte[] stageBuffer = new byte[size];
            info.get(stageBuffer);

            final String stage = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(stageBuffer)).toString();
            final int health = UtilConversion.toUnsignedByte(info.get());
            final int life = UtilConversion.toUnsignedByte(info.get());

            return new NetworkStageData(type, Medias.create(stage), health, life);
        }
        catch (final UnknownHostException | SocketException exception)
        {
            throw new IOException(exception);
        }
    }

    private static void runGame(Window window, Network network, Gamepad gamepad, NetworkStageData data)
    {
        save();
        window.dispose();
        AppLionheart.run(gamepad,
                         Scene.class,
                         network,
                         data.type,
                         new InitConfig(data.stage, data.health, data.life, Difficulty.NORMAL));
    }

    private static void save()
    {
        if (new File(Medias.getResourcesDirectory()).isDirectory())
        {
            try (FileWriter output = new FileWriter(prepareSettings()))
            {
                writeFormatted(output, "# undefined = system language");
                writeFormatted(output, Settings.LANG, LANG.get());
                output.write(System.lineSeparator());
                writeFormatted(output, "# undefined = desktop resolution");
                writeFormatted(output, Settings.RESOLUTION_WIDTH, WIDTH.get());
                writeFormatted(output, Settings.RESOLUTION_HEIGHT, HEIGHT.get());
                writeFormatted(output, Settings.RESOLUTION_RATE, RATE.get());
                writeFormatted(output, Settings.RESOLUTION_WINDOWED, WINDOWED.get());
                output.write(System.lineSeparator());
                writeFormatted(output, "# NONE, BLUR, HQ2X, HQ3X, SCANLINE, CRT");
                writeFormatted(output, Settings.FILTER, FILTER.get().name());
                output.write(System.lineSeparator());
                writeFormatted(output, "# [0 - 100]");
                writeFormatted(output, Settings.VOLUME, com.b3dgs.lionengine.Constant.HUNDRED);
                writeFormatted(output, Settings.VOLUME_MUSIC, MUSIC.get());
                writeFormatted(output, Settings.VOLUME_SFX, SFX.get());
                output.write(System.lineSeparator());
                writeFormatted(output, "# ORIGINAL, ALTERNATIVE");
                writeFormatted(output, Settings.GAMEPLAY, GAMEPLAY.get().name());
                output.write(System.lineSeparator());
                writeFormatted(output, "# NONE, DIRECT, CACHE");
                writeFormatted(output, Settings.RASTER_TYPE, RASTER.get().name());
                output.write(System.lineSeparator());
                writeFormatted(output, Settings.HUD_VISIBLE, HUD.get());
                writeFormatted(output, Settings.HUD_SWORD, HUD_SWORD.get());
                output.write(System.lineSeparator());
                writeFormatted(output, Settings.FLICKER_BACKGROUND, FLICKER_BACKGROUND.get());
                writeFormatted(output, Settings.FLICKER_FOREGROUND, FLICKER_FOREGROUND.get());
                output.write(System.lineSeparator());
                writeFormatted(output, "# [0.8 - 1.3]");
                writeFormatted(output, Settings.ZOOM, ZOOM.get() / 100.0);
                output.write(System.lineSeparator());
                writeFormatted(output, "# 0 = FAST_LOADING, 1 = FAST_RENDERING, 2 = LOW_MEMORY");
                writeFormatted(output, Settings.FLAG_STRATEGY, FLAG_STRATEGY.get());
                writeFormatted(output, Settings.FLAG_PARALLEL, FLAG_PARALLEL.get());
                writeFormatted(output, Settings.FLAG_VSYNC, FLAG_VSYNC.get());
                writeFormatted(output, Settings.FLAG_DEBUG, false);

                output.flush();
            }
            catch (final IOException exception)
            {
                Verbose.exception(exception);
            }
            Settings.load();
        }
    }

    private static File prepareSettings() throws IOException
    {
        final File file = new File(Medias.getResourcesDirectory(), Settings.FILENAME);
        if (!file.isFile())
        {
            file.createNewFile();
        }
        return file;
    }

    private static void writeFormatted(FileWriter output, String data) throws IOException
    {
        output.write(data);
        output.write(System.lineSeparator());
    }

    private static void writeFormatted(FileWriter output, String data, String value) throws IOException
    {
        output.write(data + SETTINGS_SEPARATOR + value);
        output.write(System.lineSeparator());
    }

    private static void writeFormatted(FileWriter output, String data, int value) throws IOException
    {
        output.write(data + SETTINGS_SEPARATOR + value);
        output.write(System.lineSeparator());
    }

    private static void writeFormatted(FileWriter output, String data, double value) throws IOException
    {
        output.write(data + SETTINGS_SEPARATOR + value);
        output.write(System.lineSeparator());
    }

    private static void writeFormatted(FileWriter output, String data, boolean value) throws IOException
    {
        output.write(data + SETTINGS_SEPARATOR + value);
        output.write(System.lineSeparator());
    }

    /**
     * Update field.
     * 
     * @param field The field.
     * @param text The text.
     */
    static void updateField(AtomicInteger field, JFormattedTextField text)
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

    private static class NetworkStageData
    {
        private final GameType type;
        private final Media stage;
        private final int health;
        private final int life;

        private NetworkStageData(GameType type, Media stage, int health, int life)
        {
            super();

            this.type = type;
            this.stage = stage;
            this.health = health;
            this.life = life;
        }
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

    /**
     * Private constructor.
     */
    private Launcher()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
