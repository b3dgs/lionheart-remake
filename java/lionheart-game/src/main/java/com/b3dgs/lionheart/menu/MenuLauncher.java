/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.AttributesReader;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceControllerListener;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.Difficulty;
import com.b3dgs.lionheart.GameConfig;
import com.b3dgs.lionheart.GameType;
import com.b3dgs.lionheart.InitConfig;
import com.b3dgs.lionheart.Scene;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.menu.MenuLauncher.Type;
import com.b3dgs.lionheart.narration.intro.Intro;

/**
 * Menu launcher implementation.
 */
public final class MenuLauncher extends Menu<Type>
{
    private static final int LAUNCHER_IMAGE_OFFSET_Y = -24;
    private static final int LAUNCHER_TITLE_OFFSET_Y = 60;
    private static final int LAUNCHER_TEXT_OFFSET_X = 5;
    private static final int LAUNCHER_TEXT_TIPS_Y = 292;

    /** Text tip. */
    private final SpriteFont fontTip = Util.loadFont("fonttip.png", "fontdata.xml", 12, 12);
    private final List<String> menu0 = getText("main0.txt");
    private final List<String> options0 = getText("options0.txt");
    private final List<String> game0 = getText("games.txt");
    private final List<String> players0 = getText("players.txt");
    private final List<String> infoGame = getText("infoGame.txt");
    private final List<String> infoStage = getText("infoStage.txt");
    private final List<String> infoPlayer = getText("infoPlayer.txt");
    private final List<String> infoController = getText("infoController.txt");
    private final List<String> difficulty1 = getText("difficulties.txt");
    private final List<List<String>> stages0 = new ArrayList<>();
    private final List<String> controllers0 = new ArrayList<>();

    private final Map<Integer, Integer> controls = new HashMap<>();
    private final ImageBuffer[] bufferTextLauncher;
    private final DeviceControllerListener listener;

    private int totalStages;
    /** Current game index. */
    private int game;
    /** Current players index. */
    private int players;
    /** Current stage index. */
    private int stage;
    /** Current controller index. */
    private int controller;
    /** Current difficulty index. */
    private int difficulty;
    /** Current joystick value. */
    private final int joystick;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param config The config reference (must not be <code>null</code>).
     */
    public MenuLauncher(Context context, GameConfig config)
    {
        super(context, config);

        menus = new Sprite[1];
        menusData = new Data[menus.length];

        game = config.getType().ordinal();
        players = config.getPlayers() - 1;
        joystick = config.isOneButton() ? 0 : 1;

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

        stages0.add(Util.readLines(Medias.create(Folder.STAGE, Folder.STORY, "stages.txt")));
        stages0.add(Util.getStagesTraining());
        stages0.add(Util.getStages(Folder.SPEEDRUN));
        stages0.add(Util.getStages(Folder.BATTLE));
        stages0.add(Util.getStages(Folder.VERSUS));

        menusData[0] = createLauncher();

        final AttributesReader xml = new XmlReader(Medias.create("input.xml"));
        for (final AttributesReader device : xml.getChildren(DeviceControllerConfig.NODE_DEVICE))
        {
            controllers0.add(device.getString(DeviceControllerConfig.ATT_NAME));
        }

        for (int j = 0; j < stages0.size(); j++)
        {
            totalStages += stages0.get(j).size();
        }
        bufferTextLauncher = new ImageBuffer[game0.size()
                                             + players0.size()
                                             + totalStages
                                             + controllers0.size()
                                             + 4
                                             + difficulty1.size()];

        type = Type.LAUNCHER;
    }

    @Override
    public void load()
    {
        final int x = (int) Math.round(CENTER_X * factorH);
        menus[0] = Util.get(Folder.SPRITE, "menu0.png");
        menus[0].setOrigin(Origin.CENTER_TOP);
        menus[0].setLocation(x, mainY + LAUNCHER_IMAGE_OFFSET_Y);

        createCacheTextLauncher();
    }

    /**
     * Create cached texts.
     */
    private void createCacheTextLauncher()
    {
        int i = 0;
        i = Util.cacheText(game0, i, bufferTextLauncher, textBlue);
        i = Util.cacheText(players0, i, bufferTextLauncher, textBlue);
        for (int j = 0; j < stages0.size(); j++)
        {
            final List<String> t = stages0.get(j);
            i = Util.cacheText(t, i, bufferTextLauncher, textBlue);
        }
        i = Util.cacheText(controllers0, i, bufferTextLauncher, textBlue);
        i = Util.cacheText(Arrays.asList("(1)", "(2)", "(3)", "(4)"), i, bufferTextLauncher, textBlue);
        i = Util.cacheText(difficulty1, i, bufferTextLauncher, textBlue);
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
            new Choice(textDark, textWhite, options0.get(5), x - 10, mainY + 268, Align.RIGHT, Type.LAUNCHER_PLAY),
            new Choice(textDark, textWhite, options0.get(6), x + 10, mainY + 268, Align.LEFT, Type.LAUNCHER_EXIT)
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
            final int min = GameType.is(game, GameType.VERSUS) ? 1 : 0;
            final int max = players0.size() - 1;
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
     * Play with current configuration.
     */
    private void play()
    {
        final GameType type = GameType.from(game);
        final Difficulty difficulty = Difficulty.from(this.difficulty);
        final GameConfig c = config.with(type, players + 1, joystick == 0, controls);

        if (GameType.is(game, GameType.STORY))
        {
            end(Intro.class,
                c.with(stages0.get(0).get(stage).toLowerCase(Locale.ENGLISH))
                 .with(new InitConfig(null, 0, 0, difficulty)));
        }
        else if (GameType.is(game, GameType.TRAINING))
        {
            final String[] s = stages0.get(game).get(stage).split("-");
            final String set = s[0].toLowerCase(Locale.ENGLISH);
            final Media media = Util.getStage(set, difficulty, Integer.parseInt(s[1]));
            end(Scene.class, c.with(Util.getInitConfig(difficulty, media)));
        }
        else if (GameType.is(game, GameType.SPEEDRUN))
        {
            end(Scene.class, c.with(new InitConfig(getStage(Folder.SPEEDRUN), 1, 0, difficulty)));
        }
        else if (GameType.is(game, GameType.BATTLE))
        {
            end(Scene.class, c.with(new InitConfig(getStage(Folder.BATTLE), 8, 0, difficulty)));
        }
        else if (GameType.is(game, GameType.VERSUS))
        {
            end(Scene.class, c.with(new InitConfig(getStage(Folder.VERSUS), 8, 0, difficulty)));
        }
    }

    /**
     * Get stage media from type and state index.
     * 
     * @param type The type name.
     * @return The media reference.
     */
    private Media getStage(String type)
    {
        return Medias.create(Folder.STAGE, type, Constant.STAGE_PREFIX + (stage + 1) + ".xml");
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

        final int x = (int) Math.round(CENTER_X * factorH);
        final int y = mainY + LAUNCHER_TITLE_OFFSET_Y;

        textBlue.draw(g, x, y, Align.CENTER, menu0.get(0));
        textDark.draw(g, x, y + 22, Align.CENTER, menu0.get(1));

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
            final int px = x + LAUNCHER_TEXT_OFFSET_X;
            final int py = menusData[0].choices[4].getY();

            g.drawImage(bufferTextLauncher[start + controllers0.size() + controller], px - 8, py);
            g.drawImage(bufferTextLauncher[start + indexDevice], px + 28, py);
        }
        else
        {
            drawLauncherText(g, 4, start, indexDevice);
        }

        if (choice == 0)
        {
            fontTip.draw(g, getWidth() / 2, LAUNCHER_TEXT_TIPS_Y, Align.CENTER, infoGame.get(game));
        }
        else if (choice == 1)
        {
            fontTip.draw(g, getWidth() / 2, LAUNCHER_TEXT_TIPS_Y, Align.CENTER, infoStage.get(0));
        }
        else if (choice == 3)
        {
            fontTip.draw(g, getWidth() / 2, LAUNCHER_TEXT_TIPS_Y, Align.CENTER, infoPlayer.get(0));
        }
        else if (choice == 4)
        {
            fontTip.draw(g, getWidth() / 2, LAUNCHER_TEXT_TIPS_Y, Align.CENTER, infoController.get(0));
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
        g.drawImage(bufferTextLauncher[start + value],
                    menusData[0].choices[index].getX() + LAUNCHER_TEXT_OFFSET_X,
                    menusData[0].choices[index].getY());
    }

    @Override
    protected int getMenuId()
    {
        final int id;
        if (type == Type.LAUNCHER)
        {
            id = 0;
        }
        else
        {
            id = -1;
        }
        return id;
    }

    @Override
    protected void updateMenu(double extrp)
    {
        switch (type)
        {
            case LAUNCHER -> handleLauncher();
            case LAUNCHER_PLAY -> play();
            case LAUNCHER_EXIT -> end();
            default -> throw new LionEngineException(type);
        }
    }

    @Override
    protected void onMenuAccepted(Type type)
    {
        if (type == Type.LAUNCHER_EXIT)
        {
            setSystemCursorVisible(false);
        }
    }

    @Override
    protected boolean filterOption(Type type, int choice)
    {
        return choice < 4;
    }

    @Override
    protected boolean filterChoice(Type type, int choice)
    {
        return choice == 2 && GameType.is(game, GameType.SPEEDRUN, GameType.BATTLE, GameType.VERSUS)
               || choice == 3 && GameType.is(game, GameType.STORY, GameType.TRAINING);
    }

    @Override
    protected boolean filterCursorChoice(Type type, int choice)
    {
        return (choice != 3 || !GameType.is(game, GameType.STORY, GameType.TRAINING))
               && (choice != 2 || !GameType.is(game, GameType.SPEEDRUN, GameType.BATTLE, GameType.VERSUS));
    }

    @Override
    protected void renderMenus(Graphic g)
    {
        switch (type)
        {
            case LAUNCHER:
                renderLauncher(g);
                break;
            case LAUNCHER_PLAY, LAUNCHER_EXIT:
                break;
            default:
                throw new LionEngineException(type);
        }
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        super.onTerminated(hasNextSequence);

        device.removeListener(listener);

        fontTip.dispose();
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

        for (int i = 0; i < bufferTextLauncher.length; i++)
        {
            bufferTextLauncher[i].dispose();
        }
    }

    /**
     * List of menu types.
     */
    enum Type
    {
        /** Launcher menu. */
        LAUNCHER,
        /** Launcher play. */
        LAUNCHER_PLAY,
        /** Launcher exit. */
        LAUNCHER_EXIT;
    }
}
