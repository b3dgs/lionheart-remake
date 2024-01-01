/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionengine.io.DeviceControllerListener;
import com.b3dgs.lionengine.io.DevicePointer;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.Difficulty;
import com.b3dgs.lionheart.GameConfig;
import com.b3dgs.lionheart.GameType;
import com.b3dgs.lionheart.InitConfig;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.Scene;
import com.b3dgs.lionheart.SceneBlack;
import com.b3dgs.lionheart.ScenePicture;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.StageConfig;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Extension;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.intro.Intro;

/**
 * Menu implementation.
 */
// CHECKSTYLE IGNORE LINE: DataAbstractionCoupling|FanOutComplexity
public class Menu extends Sequence
{
    private static final int MOUSE_HIDE_DELAY_MS = 1000;

    private static final int MIN_HEIGHT = 360;
    private static final int FADE_SPEED = 10;
    private static final int CENTER_X = 320;
    private static final int LAUNCHER_IMAGE_OFFSET_Y = -24;
    private static final int LAUNCHER_TITLE_OFFSET_Y = 60;
    private static final int LAUNCHER_TEXT_OFFSET_X = 5;
    private static final int LAUNCHER_TEXT_TIPS_Y = 292;
    private static final int MENU_MAIN_IMAGE_OFFSET_Y = 32;
    private static final int OPTIONS_TITLE_OFFSET_Y = 96;
    private static final int OPTIONS_TEXT_OFFSET_X = 12;
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Menu.class);

    /**
     * Load front.
     * 
     * @param media The font sprite media.
     * @param data The font data media.
     * @param lw The font image letter width.
     * @param lh The font image letter height.
     * @return The loaded font.
     */
    private static SpriteFont loadFont(String media, String data, int lw, int lh)
    {
        return Drawable.loadSpriteFont(Medias.create(Folder.SPRITE, media), Medias.create(Folder.SPRITE, data), lw, lh);
    }

    /**
     * Get all supported training stages.
     * 
     * @return The training stages.
     */
    private static List<String> getStagesTraining()
    {
        final List<String> training = new ArrayList<>();
        for (final String set : Util.readLines(Medias.create(Folder.STAGE, Folder.STORY, "stages.txt")))
        {
            int i = 1;
            while (true)
            {
                if (!Util.getStage(set, null, i).exists())
                {
                    break;
                }
                training.add(set + "-" + i);
                i++;
            }
        }
        return training;
    }

    /**
     * Get all stages in folder.
     * 
     * @param folder The folder name.
     * @return The stages found.
     */
    private static List<String> getStages(String folder)
    {
        final List<String> stages = new ArrayList<>();
        int i = 1;
        while (true)
        {
            final Media media = Medias.create(Folder.STAGE, folder, Constant.STAGE_PREFIX + i + Extension.STAGE);
            if (!media.exists())
            {
                break;
            }
            stages.add(String.valueOf(i));
            i++;
        }
        return stages;
    }

    /**
     * Cache text to image.
     * 
     * @param texts The text lines.
     * @param index The starting index.
     * @param buffers The cached buffer.
     * @param font The font used.
     * @return The next index.
     */
    private static int cacheText(List<String> texts, int index, ImageBuffer[] buffers, SpriteFont font)
    {
        int i;
        for (i = 0; i < texts.size(); i++)
        {
            buffers[i + index] = Graphics.createImageBuffer(160, 40, ColorRgba.TRANSPARENT);
            buffers[i + index].prepare();
            final Graphic g = buffers[i + index].createGraphic();
            font.draw(g, 0, 0, Align.LEFT, texts.get(i));
            g.dispose();
        }
        return index + i;
    }

    /**
     * Get difficulty index if exists.
     * 
     * @param config The current config.
     * @return The difficulty index.
     */
    private static int getDifficultyIndex(GameConfig config)
    {
        if (config.getInit() != null && config.getInit().getDifficulty() != null)
        {
            return config.getInit().getDifficulty().ordinal();
        }
        return 0;
    }

    private final SpriteFont textWhite = loadFont("fontmenu.png", "fontmenu.xml", 26, 30);
    private final SpriteFont textDark = loadFont("fontmenu_dark.png", "fontmenu.xml", 26, 30);
    private final SpriteFont textBlue = loadFont("fontmenu_blue.png", "fontmenu.xml", 26, 30);
    private final SpriteFont font = loadFont("fonttip.png", "fontdata.xml", 12, 12);

    private final Settings settings = Settings.getInstance();

    private final List<String> menu0 = getText("main0.txt");
    private final List<String> options0 = getText("options0.txt");
    private final List<String> game0 = getText("games.txt");
    private final List<String> players0 = getText("players.txt");
    private final List<String> infoGame = getText("infoGame.txt");
    private final List<String> infoStage = getText("infoStage.txt");
    private final List<String> infoPlayer = getText("infoPlayer.txt");
    private final List<String> infoController = getText("infoController.txt");
    private final List<List<String>> stages0 = new ArrayList<>();
    private final List<String> controllers0 = new ArrayList<>();

    private final List<String> menu1 = getText("main.txt");
    private final List<String> options1 = getText("options.txt");
    private final List<String> difficulty1 = getText("difficulties.txt");
    private final List<String> joystick1 = getText("joystick.txt");
    private final List<String> music1 = getText("music.txt");

    private final Map<Integer, Integer> controls = new HashMap<>();
    private final ImageBuffer[] bufferText;

    /** Alpha step speed. */
    int alphaSpeed = FADE_SPEED;
    /** Device controller reference. */
    final DeviceController device;

    /** Background menus. */
    private final Sprite[] menus = new Sprite[3];
    /** List of menu data with their content. */
    private final Data[] menusData = new Data[menus.length];
    /** Application info. */
    private final AppInfo info;
    /** Horizontal factor. */
    private final double factorH = getWidth() / 640.0;
    /** Main Y. */
    private final int mainY;
    private final Tick tickMouse = new Tick();
    private final GameConfig config;
    private final DeviceController deviceCursor;
    private final Cursor cursor;
    private final DevicePointer pointer;
    private final DeviceControllerListener listener;

    /** Current menu transition. */
    protected TransitionType transition = TransitionType.IN;
    /** Screen mask alpha current value. */
    private double alpha = 255.0;
    /** Line choice on. */
    private int choice;
    /** Old choice. */
    private int choiceOld;
    /** Current difficulty index. */
    private int difficulty;
    /** Current joystick value. */
    private int joystick;
    /** Current music test. */
    private int music = 1;
    /** Current game index. */
    private int game;
    /** Current players index. */
    private int players;
    /** Current stage index. */
    private int stage;
    /** Current controller index. */
    private int controller;
    /** Current. */
    private MenuType type;
    /** Next. */
    private MenuType typeNext;
    /** Music player. */
    private Audio audio;
    private boolean movedHorizontal;
    private boolean movedVertical;
    private int totalStages;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param config The config reference (must not be <code>null</code>).
     */
    public Menu(Context context, GameConfig config)
    {
        super(context,
              Util.getResolution(Constant.RESOLUTION, context).get2x(),
              Util.getLoop(context.getConfig().getOutput()));

        this.config = config;
        difficulty = getDifficultyIndex(config);
        joystick = config.isOneButton() ? 0 : 1;
        game = config.getType().ordinal();
        players = config.getPlayers() - 1;
        setSystemCursorVisible(false);

        final Services services = new Services();
        services.add(context);
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_DEFAULT)));
        device.setVisible(false);

        listener = (n, p, c, f) ->
        {
            if (choice == 4 && device.isFired(DeviceMapping.ATTACK))
            {
                controls.put(Integer.valueOf(controller), Integer.valueOf(controllers0.indexOf(n)));
            }
        };
        device.addListener(listener);

        for (int i = 0; i < 4; i++)
        {
            controls.put(Integer.valueOf(i), Integer.valueOf(i));
        }
        for (int i = 0; i < config.getPlayers(); i++)
        {
            try
            {
                controls.put(Integer.valueOf(config.getControl(i)), Integer.valueOf(config.getControl(i)));
            }
            catch (@SuppressWarnings("unused") final NullPointerException exception)
            {
                // Skip
            }
        }

        final Media mediaCursor = Medias.create(Constant.INPUT_FILE_CURSOR);
        deviceCursor = DeviceControllerConfig.create(services, mediaCursor);

        final Camera camera = services.create(Camera.class);
        camera.setView(0, 0, getWidth(), getHeight(), getHeight());

        cursor = services.create(Cursor.class);
        cursor.setViewer(camera);
        cursor.setSensibility(getWidth() / (double) Constant.RESOLUTION.getWidth(),
                              getHeight() / (double) Constant.RESOLUTION.getHeight());
        cursor.setArea(0, 0, getWidth(), getHeight());

        pointer = (DevicePointer) getInputDevice(DeviceControllerConfig.imports(services, mediaCursor)
                                                                       .iterator()
                                                                       .next()
                                                                       .getDevice());
        cursor.setSync(pointer);

        info = new AppInfo(this::getFps, services);

        textWhite.load();
        textWhite.prepare();
        textDark.load();
        textDark.prepare();
        textBlue.load();
        textBlue.prepare();

        mainY = (getHeight() - MIN_HEIGHT) / 2;

        menusData[0] = createLauncher();
        menusData[1] = createMain();
        menusData[2] = createOptions();

        if (config.getInit() == null)
        {
            type = MenuType.LAUNCHER;
            choice = 0;
        }
        else
        {
            type = MenuType.MAIN;
        }

        stages0.add(Util.readLines(Medias.create(Folder.STAGE, Folder.STORY, "stages.txt")));
        stages0.add(getStagesTraining());
        stages0.add(getStages(Folder.SPEEDRUN));
        stages0.add(getStages(Folder.BATTLE));
        stages0.add(getStages(Folder.VERSUS));

        final XmlReader xml = new XmlReader(Medias.create("input.xml"));
        for (final XmlReader device : xml.getChildren(DeviceControllerConfig.NODE_DEVICE))
        {
            controllers0.add(device.getString(DeviceControllerConfig.ATT_NAME));
        }

        for (int j = 0; j < stages0.size(); j++)
        {
            totalStages += stages0.get(j).size();
        }
        bufferText = new ImageBuffer[game0.size()
                                     + players0.size()
                                     + totalStages
                                     + controllers0.size()
                                     + 4
                                     + difficulty1.size()
                                     + joystick1.size()
                                     + music1.size()];
        createCacheText();

        Util.setFilter(this, context, Util.getResolution(Constant.RESOLUTION, context).get2x(), 2);
    }

    /**
     * Get lines from file.
     * 
     * @param file The file to load.
     * @return The lines read.
     */
    private List<String> getText(String file)
    {
        return Util.readLines(Medias.create(Folder.TEXT, settings.getLang(), Folder.MENU, file));
    }

    /**
     * Create cached texts.
     */
    private void createCacheText()
    {
        int i = 0;
        i = cacheText(game0, i, bufferText, textBlue);
        i = cacheText(players0, i, bufferText, textBlue);
        for (int j = 0; j < stages0.size(); j++)
        {
            final List<String> t = stages0.get(j);
            i = cacheText(t, i, bufferText, textBlue);
        }
        i = cacheText(controllers0, i, bufferText, textBlue);
        i = cacheText(Arrays.asList("(1)", "(2)", "(3)", "(4)"), i, bufferText, textBlue);

        i = cacheText(difficulty1, i, bufferText, textBlue);
        i = cacheText(joystick1, i, bufferText, textBlue);
        i = cacheText(music1, i, bufferText, textBlue);
    }

    /**
     * Create options menu.
     * 
     * @return The created data.
     */
    private Data createLauncher()
    {
        final int x = (int) Math.round(CENTER_X * factorH);
        final Choice[] choices =
        {
            new Choice(textDark, textWhite, options0.get(0), x - LAUNCHER_TEXT_OFFSET_X, mainY + 114, Align.RIGHT),
            new Choice(textDark, textWhite, options0.get(1), x - LAUNCHER_TEXT_OFFSET_X, mainY + 141, Align.RIGHT),
            new Choice(textDark, textWhite, options0.get(2), x - LAUNCHER_TEXT_OFFSET_X, mainY + 168, Align.RIGHT),
            new Choice(textDark, textWhite, options0.get(3), x - LAUNCHER_TEXT_OFFSET_X, mainY + 168, Align.RIGHT),
            new Choice(textDark, textWhite, options0.get(4), x - LAUNCHER_TEXT_OFFSET_X, mainY + 196, Align.RIGHT),
            new Choice(textDark, textWhite, options0.get(5), x - 10, mainY + 268, Align.RIGHT, MenuType.LAUNCHER_PLAY),
            new Choice(textDark, textWhite, options0.get(6), x + 10, mainY + 268, Align.LEFT, MenuType.LAUNCHER_EXIT)
        };
        return new Data(choices);
    }

    /**
     * Create main menu.
     * 
     * @return The created data.
     */
    private Data createMain()
    {
        final int x = (int) Math.round(CENTER_X * factorH);
        final Choice[] choices;
        if (config.getStages().isPresent()
            && Medias.create(Folder.STAGE, Folder.STORY, config.getStages().get(), Constant.FILE_PROGRESS).exists())
        {
            choice = 2;
            choices = new Choice[]
            {
                new Choice(textDark, textWhite, menu1.get(0), x, mainY + 100, Align.CENTER, MenuType.NEW),
                new Choice(textDark, textWhite, menu1.get(1), x, mainY + 132, Align.CENTER, MenuType.CONTINUE),
                new Choice(textDark, textWhite, menu1.get(2), x, mainY + 164, Align.CENTER, MenuType.OPTIONS),
                new Choice(textDark, textWhite, menu1.get(3), x, mainY + 194, Align.CENTER, MenuType.INTRO),
                new Choice(textDark, textWhite, menu1.get(4), x, mainY + 235, Align.CENTER, MenuType.LAUNCHER)
            };
        }
        else
        {
            choice = 1;
            choices = new Choice[]
            {
                new Choice(textDark, textWhite, menu1.get(0), x, mainY + 117, Align.CENTER, MenuType.NEW),
                new Choice(textDark, textWhite, menu1.get(2), x, mainY + 151, Align.CENTER, MenuType.OPTIONS),
                new Choice(textDark, textWhite, menu1.get(3), x, mainY + 185, Align.CENTER, MenuType.INTRO),
                new Choice(textDark, textWhite, menu1.get(4), x, mainY + 235, Align.CENTER, MenuType.LAUNCHER)
            };
        }
        return new Data(choices);
    }

    /**
     * Create options menu.
     * 
     * @return The created data.
     */
    private Data createOptions()
    {
        final int x = (int) Math.round(CENTER_X * factorH);
        final Choice[] choices =
        {
            new Choice(textDark, textWhite, options1.get(0), x - 118, mainY + 125, Align.LEFT),
            new Choice(textDark, textWhite, options1.get(1), x - 118, mainY + 161, Align.LEFT),
            new Choice(textDark, textWhite, options1.get(2), x - 118, mainY + 197, Align.LEFT),
            new Choice(textDark, textWhite, options1.get(3), x, mainY + 241, Align.CENTER, MenuType.MAIN)
        };
        return new Data(choices);
    }

    /**
     * Handle the menu launcher.
     */
    private void handleLauncher()
    {
        // Game type
        if (choice == 0)
        {
            final int nextGame = changeOption(game, 0, game0.size() - 1);
            if (nextGame != game)
            {
                game = nextGame;
                if (GameType.is(game, GameType.STORY, GameType.TRAINING) && players > 0)
                {
                    players = 0;
                    controller = 0;
                }
                if (GameType.is(game, GameType.VERSUS) && players < 1)
                {
                    players = 1;
                }
                stage = 0;
            }
        }
        // Stages
        else if (choice == 1)
        {
            stage = changeOption(stage, 0, stages0.get(game).size() - 1);
        }
        // Difficulty
        else if (choice == 2)
        {
            players = 0;
            difficulty = changeOption(difficulty, 0, difficulty1.size() - 1);
        }
        // Players
        else if (choice == 3)
        {
            final int min;
            final int max;
            if (GameType.is(game, GameType.VERSUS))
            {
                min = 1;
            }
            else
            {
                min = 0;
            }
            max = players0.size() - 1;

            final int nextPlayers = changeOption(players, min, max);
            if (nextPlayers != players)
            {
                players = nextPlayers;
                controller = UtilMath.clamp(controller, 0, players);
            }
        }
        // Controller
        else if (choice == 4)
        {
            controller = changeOption(controller, 0, players);
        }
        // Play
        else if (choice == 5)
        {
            choice = changeOption(choice, choice - 1, choice + 1);
        }
        // Quit
        else if (choice == 6)
        {
            choice = changeOption(choice, choice - 1, choice);
        }
    }

    /**
     * Handle the menu options.
     */
    private void handleOptions()
    {
        if (choice == 0)
        {
            difficulty = changeOption(difficulty, 0, difficulty1.size() - 1);
        }
        else if (choice == 1)
        {
            joystick = changeOption(joystick, 0, joystick1.size() - 1);
        }
        else if (choice == 2)
        {
            music = changeOption(music, 0, music1.size() - 1);
            handleOptionMusic();
        }
    }

    /**
     * Handle music option listening.
     */
    private void handleOptionMusic()
    {
        if (device.isFiredOnce(DeviceMapping.ATTACK))
        {
            stopAudio();
            if (music > 0)
            {
                audio = AudioFactory.loadAudio(Music.values()[music - 1]);
                audio.setVolume(settings.getVolumeMusic());
                audio.play();
            }
        }
    }

    /**
     * Stop active music.
     */
    private void stopAudio()
    {
        if (audio != null)
        {
            audio.stop();
            audio = null;
        }
    }

    /**
     * Change an option.
     * 
     * @param option The option.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The new value.
     */
    private int changeOption(int option, int min, int max)
    {
        if (Double.compare(device.getHorizontalDirection(), 0) == 0)
        {
            movedHorizontal = false;
        }

        int value = option;
        if (!movedHorizontal && (device.getHorizontalDirection() < 0 || device.isFiredOnce(DeviceMapping.LEFT)))
        {
            movedHorizontal = true;
            value--;
        }
        if (!movedHorizontal && (device.getHorizontalDirection() > 0 || device.isFiredOnce(DeviceMapping.RIGHT)))
        {
            movedHorizontal = true;
            value++;
        }
        if (choice == choiceOld
            && (type == MenuType.LAUNCHER && choice < 4 || type == MenuType.OPTIONS && choice < 2)
            && deviceCursor.isFiredOnce(DeviceMapping.LEFT)
            && menusData[getMenuId()].choices[choice].isOver(cursor))
        {
            value++;
            if (value > max)
            {
                value = min;
            }
        }
        value = UtilMath.clamp(value, min, max);
        if (value != option)
        {
            Sfx.MENU_SELECT.play();
        }
        return value;
    }

    /**
     * Update the menu transitions.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransition(double extrp)
    {
        switch (transition)
        {
            case IN:
                updateFadeIn(extrp);
                break;
            case OUT:
                updateFadeOut(extrp);
                break;
            case NONE:
                final int menuId = getMenuId();
                if (menuId > -1)
                {
                    updateMenuNavigation(menuId);
                }
                break;
            default:
                throw new LionEngineException(transition);
        }
    }

    /**
     * Update fade in to menu.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeIn(double extrp)
    {
        alpha -= alphaSpeed * extrp;

        if (getAlpha() < 0)
        {
            alpha = 0.0;
            transition = TransitionType.NONE;
        }
    }

    /**
     * Update fade out from menu.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOut(double extrp)
    {
        alpha += alphaSpeed * extrp;

        if (getAlpha() > 255)
        {
            alpha = 255.0;
            type = typeNext;
            transition = TransitionType.IN;
            choice = 0;
        }
    }

    /**
     * Get alpha value.
     * 
     * @return The alpha value.
     */
    private int getAlpha()
    {
        return (int) Math.floor(alpha);
    }

    /**
     * Get the menu id.
     * 
     * @return The menu id.
     */
    private int getMenuId()
    {
        final int id;
        if (type == MenuType.LAUNCHER)
        {
            id = 0;
        }
        else if (type == MenuType.MAIN)
        {
            id = 1;
        }
        else if (type == MenuType.OPTIONS)
        {
            id = 2;
        }
        else
        {
            id = -1;
        }
        return id;
    }

    /**
     * Update the navigation.
     * 
     * @param menuId The menu id.
     */
    private void updateMenuNavigation(int menuId)
    {
        choiceOld = choice;
        if (Double.compare(device.getVerticalDirection(), 0) == 0)
        {
            movedVertical = false;
        }

        if (!movedVertical && (device.getVerticalDirection() > 0 || device.isFiredOnce(DeviceMapping.JUMP)))
        {
            choice--;
            if (type == MenuType.LAUNCHER
                && (choice == 2 && GameType.is(game, GameType.SPEEDRUN, GameType.BATTLE, GameType.VERSUS)
                    || choice == 3 && GameType.is(game, GameType.STORY, GameType.TRAINING)))
            {
                choice--;
            }
            cursor.setVisible(false);
            cursor.setLocation(0, 0);
            movedVertical = true;
        }
        if (!movedVertical && (device.getVerticalDirection() < 0 || device.isFiredOnce(DeviceMapping.DOWN)))
        {
            choice++;
            if (type == MenuType.LAUNCHER
                && (choice == 3 && GameType.is(game, GameType.STORY, GameType.TRAINING)
                    || choice == 2 && GameType.is(game, GameType.SPEEDRUN, GameType.BATTLE, GameType.VERSUS)))
            {
                choice++;
            }
            cursor.setVisible(false);
            cursor.setLocation(0, 0);
            movedVertical = true;
        }
        final Data data = menusData[menuId];
        if (deviceCursor.isFired(DeviceMapping.MOVE)
            || Double.compare(cursor.getMoveX(), 0.0) != 0
            || Double.compare(cursor.getMoveY(), 0.0) != 0)
        {
            choice = getCursorChoice(data);
            cursor.setVisible(true);
        }
        choice = UtilMath.clamp(choice, 0, data.choiceMax);
        if (choiceOld != choice)
        {
            Sfx.MENU_SELECT.play();
        }
        final MenuType next = data.choices[choice].getNext();
        // Accept choice
        if (next != null
            && (device.isFiredOnce(DeviceMapping.ATTACK)
                || deviceCursor.isFiredOnce(DeviceMapping.LEFT) && data.choices[choice].isOver(cursor)))
        {
            typeNext = next;
            transition = TransitionType.OUT;
            stopAudio();
            if (typeNext == MenuType.NEW || typeNext == MenuType.INTRO || typeNext == MenuType.LAUNCHER_EXIT)
            {
                setSystemCursorVisible(false);
            }
        }
    }

    /**
     * Get current choice from cursor.
     * 
     * @param data The current data.
     * @return The choice index.
     */
    private int getCursorChoice(Data data)
    {
        for (int i = 0; i < data.choices.length; i++)
        {
            if (data.choices[i].isOver(cursor)
                && (type != MenuType.LAUNCHER
                    || (i != 3 || !GameType.is(game, GameType.STORY, GameType.TRAINING))
                       && (i != 2 || !GameType.is(game, GameType.SPEEDRUN, GameType.BATTLE, GameType.VERSUS))))
            {
                return i;
            }
        }
        return choice;
    }

    /**
     * Update the menu states.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateMenu(double extrp)
    {
        switch (type)
        {
            case LAUNCHER:
                handleLauncher();
                break;
            case LAUNCHER_PLAY:
                play();
                break;
            case LAUNCHER_EXIT:
                end();
                break;
            case MAIN:
                break;
            case CONTINUE:
                try
                {
                    end(SceneBlack.class, config.with(Util.loadProgress(config)));
                }
                catch (final IOException exception)
                {
                    LOGGER.error("continue error", exception);
                    startNewGame();
                }
                break;
            case NEW:
                startNewGame();
                break;
            case OPTIONS:
                handleOptions();
                break;
            case INTRO:
                end(Intro.class, config);
                break;
            default:
                throw new LionEngineException(type);
        }
    }

    /**
     * Play with current configuration.
     */
    private void play()
    {
        final GameType type = GameType.from(game);
        final Difficulty difficulty = Difficulty.from(this.difficulty);
        final int players = this.players + 1;
        final int stage = this.stage + 1;

        if (GameType.is(game, GameType.STORY))
        {
            end(Intro.class,
                config.with(type, players, joystick == 0, controls)
                      .with(stages0.get(0).get(this.stage).toLowerCase(Locale.ENGLISH))
                      .with(new InitConfig(null, 0, 0, difficulty)));
        }
        else if (GameType.is(game, GameType.TRAINING))
        {
            final String[] s = stages0.get(game).get(this.stage).split("-");
            final String set = s[0].toLowerCase(Locale.ENGLISH);
            final Media media = Util.getStage(set, difficulty, Integer.parseInt(s[1]));
            end(Scene.class, config.with(type, players, joystick == 0, controls).with(getInitConfig(media)));
        }
        else if (GameType.is(game, GameType.SPEEDRUN))
        {
            final Media media = Medias.create(Folder.STAGE, Folder.SPEEDRUN, Constant.STAGE_PREFIX + stage + ".xml");
            end(Scene.class,
                config.with(type, players, joystick == 0, controls).with(new InitConfig(media, 1, 0, difficulty)));
        }
        else if (GameType.is(game, GameType.BATTLE))
        {
            final Media media = Medias.create(Folder.STAGE, Folder.BATTLE, Constant.STAGE_PREFIX + stage + ".xml");
            end(Scene.class,
                config.with(type, players, joystick == 0, controls).with(new InitConfig(media, 8, 0, difficulty)));
        }
        else if (GameType.is(game, GameType.VERSUS))
        {
            final Media media = Medias.create(Folder.STAGE, Folder.VERSUS, Constant.STAGE_PREFIX + stage + ".xml");
            end(Scene.class,
                config.with(type, players, joystick == 0, controls).with(new InitConfig(media, 8, 0, difficulty)));
        }
    }

    /**
     * Start new game from story menu.
     */
    private void startNewGame()
    {
        final boolean hard = difficulty > Difficulty.NORMAL.ordinal();
        final String suffix = hard ? Constant.STAGE_HARD_SUFFIX : com.b3dgs.lionengine.Constant.EMPTY_STRING;
        Media stage = Medias.create(Folder.STAGE, Folder.STORY, config.getStages().get(), "stage1" + suffix + ".xml");
        if (!stage.exists())
        {
            stage = Medias.create(Folder.STAGE, Folder.STORY, config.getStages().get(), "stage1.xml");
        }
        final StageConfig stageConfig = StageConfig.imports(new Configurer(stage));
        end(ScenePicture.class,
            config.with(joystick == 0).with(getInitConfig(stage)),
            stageConfig.getPic().get(),
            stageConfig.getText().get());
    }

    /**
     * Get init config based on difficulty.
     * 
     * @param stage The init stage.
     * @return The init config.
     */
    private InitConfig getInitConfig(Media stage)
    {
        final Difficulty value = Difficulty.from(difficulty);
        return switch (value)
        {
            case BEGINNER -> new InitConfig(stage, 5, 3, Difficulty.NORMAL);
            case NORMAL -> new InitConfig(stage, 4, 2, Difficulty.NORMAL);
            case HARD -> new InitConfig(stage, 3, 2, Difficulty.HARD);
            case LIONHARD -> new InitConfig(stage, 3, 2, Difficulty.LIONHARD);
            default -> throw new LionEngineException(value);
        };
    }

    /**
     * Update move visibility on moved.
     */
    private void updateMoveVisibiltiy()
    {
        if (tickMouse.elapsedTime(getRate(), MOUSE_HIDE_DELAY_MS))
        {
            tickMouse.stop();
            setSystemCursorVisible(false);
        }
        else if (transition == TransitionType.NONE
                 && (Double.compare(cursor.getMoveX(), 0.0) != 0 || Double.compare(cursor.getMoveY(), 0.0) != 0))
        {
            tickMouse.restart();
            setSystemCursorVisible(true);
        }
    }

    /**
     * Render the menus.
     * 
     * @param g The graphic output.
     */
    private void renderMenus(Graphic g)
    {
        switch (type)
        {
            case LAUNCHER:
                renderLauncher(g);
                break;
            case MAIN:
                menus[1].render(g);
                menusData[1].render(g, choice);
                break;
            case OPTIONS:
                renderOptions(g);
                break;
            case LAUNCHER_PLAY:
            case LAUNCHER_EXIT:
            case NEW:
            case CONTINUE:
            case INTRO:
                break;
            default:
                throw new LionEngineException(type);
        }
    }

    /**
     * Render the launcher menu.
     * 
     * @param g The graphic output.
     */
    private void renderLauncher(Graphic g)
    {
        menus[0].render(g);
        if (GameType.is(game, GameType.SPEEDRUN, GameType.BATTLE, GameType.VERSUS))
        {
            menusData[0].render(g, choice, 2);
        }
        else
        {
            menusData[0].render(g, choice, 3);
        }

        textBlue.draw(g,
                      (int) Math.round(CENTER_X * factorH),
                      mainY + LAUNCHER_TITLE_OFFSET_Y,
                      Align.CENTER,
                      menu0.get(0));

        textDark.draw(g,
                      (int) Math.round(CENTER_X * factorH),
                      mainY + LAUNCHER_TITLE_OFFSET_Y + 22,
                      Align.CENTER,
                      menu0.get(1));

        drawLauncherText(g, 0, 0, game);
        drawLauncherText(g, 1, game0.size() + players0.size() + getGameStagesCount(), stage);
        if (GameType.is(game, GameType.SPEEDRUN, GameType.BATTLE, GameType.VERSUS))
        {
            drawLauncherText(g, 3, game0.size(), players);
        }
        else
        {
            drawLauncherText(g, 2, game0.size() + players0.size() + totalStages + controllers0.size() + 4, difficulty);
        }

        final int start = game0.size() + players0.size() + totalStages;
        final int indexDevice = controls.get(Integer.valueOf(controller)).intValue();
        if (players > 0)
        {
            g.drawImage(bufferText[start + controllers0.size() + controller],
                        (int) Math.round(CENTER_X * factorH - 8) + LAUNCHER_TEXT_OFFSET_X,
                        menusData[0].choices[4].getY());

            g.drawImage(bufferText[start + indexDevice],
                        (int) Math.round(CENTER_X * factorH + 28) + LAUNCHER_TEXT_OFFSET_X,
                        menusData[0].choices[4].getY());
        }
        else
        {
            drawLauncherText(g, 4, start, indexDevice);
        }

        if (choice == 0)
        {
            font.draw(g, getWidth() / 2, LAUNCHER_TEXT_TIPS_Y, Align.CENTER, infoGame.get(game));
        }
        else if (choice == 1)
        {
            font.draw(g, getWidth() / 2, LAUNCHER_TEXT_TIPS_Y, Align.CENTER, infoStage.get(0));
        }
        else if (choice == 3)
        {
            font.draw(g, getWidth() / 2, LAUNCHER_TEXT_TIPS_Y, Align.CENTER, infoPlayer.get(0));
        }
        else if (choice == 4)
        {
            font.draw(g, getWidth() / 2, LAUNCHER_TEXT_TIPS_Y, Align.CENTER, infoController.get(0));
        }
    }

    /**
     * Draw text launcher.
     * 
     * @param g The graphic output.
     * @param index The option index.
     * @param start The option start.
     * @param value The option value.
     */
    private void drawLauncherText(Graphic g, int index, int start, int value)
    {
        g.drawImage(bufferText[start + value],
                    menusData[0].choices[index].getX() + LAUNCHER_TEXT_OFFSET_X,
                    menusData[0].choices[index].getY());
    }

    /**
     * Get stages count from current selected game.
     * 
     * @return The stages number in selected game.
     */
    private int getGameStagesCount()
    {
        int count = 0;
        for (int i = 0; i < game; i++)
        {
            count += stages0.get(i).size();
        }
        return count;
    }

    /**
     * Render the options menu.
     * 
     * @param g The graphic output.
     */
    private void renderOptions(Graphic g)
    {
        menus[2].render(g);
        menusData[2].render(g, choice);

        textWhite.draw(g,
                       (int) Math.round(Menu.CENTER_X * factorH),
                       mainY + OPTIONS_TITLE_OFFSET_Y,
                       Align.CENTER,
                       menu1.get(menusData[2].choiceMax == 4 ? 1 : 2).toUpperCase(Locale.ENGLISH));

        drawOptionText(g, 0, 0, difficulty);
        drawOptionText(g, 1, difficulty1.size(), joystick);
        drawOptionText(g, 2, difficulty1.size() + joystick1.size(), music);
    }

    /**
     * Draw text option.
     * 
     * @param g The graphic output.
     * @param index The option index.
     * @param start The option start.
     * @param value The option value.
     */
    private void drawOptionText(Graphic g, int index, int start, int value)
    {
        g.drawImage(bufferText[game0.size() + players0.size() + totalStages + controllers0.size() + 4 + start + value],
                    (int) Math.round(CENTER_X * factorH) + OPTIONS_TEXT_OFFSET_X,
                    menusData[2].choices[index].getY());
    }

    /**
     * Render transition fading.
     * 
     * @param g The graphic output.
     */
    private void renderTransition(Graphic g)
    {
        if (transition != TransitionType.NONE)
        {
            renderFade(g);
        }
    }

    /**
     * Render fade.
     * 
     * @param g The graphic output.
     */
    private void renderFade(Graphic g)
    {
        final int a = getAlpha();
        if (a > 0)
        {
            g.setColor(Constant.ALPHAS_BLACK[a]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
            g.setColor(ColorRgba.BLACK);
        }
    }

    @Override
    public void load()
    {
        for (int i = 0; i < menus.length; i++)
        {
            menus[i] = Drawable.loadSprite(Medias.create(Folder.SPRITE, "menu" + i + ".png"));
            menus[i].setOrigin(Origin.CENTER_TOP);
            menus[i].load();
            menus[i].prepare();
        }

        final int x = (int) Math.round(CENTER_X * factorH);
        menus[0].setLocation(x, mainY + LAUNCHER_IMAGE_OFFSET_Y);
        menus[1].setLocation(x, mainY + MENU_MAIN_IMAGE_OFFSET_Y);
        menus[2].setLocation(x, mainY);

        font.load();
        font.prepare();
    }

    @Override
    public void update(double extrp)
    {
        tickMouse.update(extrp);
        updateMoveVisibiltiy();

        device.update(extrp);
        deviceCursor.update(extrp);
        cursor.update(extrp);
        if (device.isFired())
        {
            cursor.setSync(null);
        }
        if (Double.compare(pointer.getMoveX(), 0.0) != 0 || Double.compare(pointer.getMoveY(), 0.0) != 0)
        {
            cursor.setSync(pointer);
        }

        updateMenu(extrp);
        updateTransition(extrp);

        info.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        renderMenus(g);
        renderTransition(g);

        info.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        super.onTerminated(hasNextSequence);

        device.removeListener(listener);
        stopAudio();

        menu0.clear();
        options0.clear();
        game0.clear();
        players0.clear();
        infoGame.clear();
        infoStage.clear();
        infoPlayer.clear();
        infoController.clear();
        stages0.clear();
        controllers0.clear();
        menu1.clear();
        options1.clear();
        difficulty1.clear();
        joystick1.clear();
        music1.clear();

        textWhite.dispose();
        textDark.dispose();
        textBlue.dispose();
        font.dispose();

        for (int i = 0; i < menus.length; i++)
        {
            menus[i].dispose();
        }
        for (int i = 0; i < bufferText.length; i++)
        {
            bufferText[i].dispose();
        }

        if (!hasNextSequence)
        {
            Engine.terminate();
        }
    }
}
