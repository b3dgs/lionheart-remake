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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.RenderableVoid;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.Difficulty;
import com.b3dgs.lionheart.GameConfig;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.SceneBlack;
import com.b3dgs.lionheart.ScenePicture;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.StageConfig;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.menu.MenuGame.Type;
import com.b3dgs.lionheart.narration.intro.Intro;

/**
 * Menu game implementation.
 */
public class MenuGame extends Menu<Type>
{
    private static final int MENU_MAIN_IMAGE_OFFSET_Y = 32;
    private static final int OPTIONS_TITLE_OFFSET_Y = 96;
    private static final int OPTIONS_TEXT_OFFSET_X = 12;
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuGame.class);

    private final Settings settings = Settings.getInstance();

    private final List<String> menu1 = getText("main.txt");
    private final List<String> options1 = getText("options.txt");
    private final List<String> difficulty1 = getText("difficulties.txt");
    private final List<String> joystick1 = getText("joystick.txt");
    private final List<String> music1 = getText("music.txt");

    private final ImageBuffer[] bufferTextOptions;

    /** Current difficulty index. */
    private int difficulty;
    /** Current joystick value. */
    private int joystick;
    /** Current music test. */
    private int music = 1;
    /** Music player. */
    private Audio audio;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param config The config reference (must not be <code>null</code>).
     */
    public MenuGame(Context context, GameConfig config)
    {
        super(context, config);

        menus = new Sprite[2];
        menusData = new Data[menus.length];

        difficulty = Util.getDifficultyIndex(config);
        joystick = config.isOneButton() ? 0 : 1;

        menusData[0] = createMain();
        menusData[1] = createOptions();

        type = Type.MAIN;

        bufferTextOptions = new ImageBuffer[difficulty1.size() + joystick1.size() + music1.size()];
    }

    /**
     * Create cached texts.
     */
    private void createCacheTextOptions()
    {
        int i = 0;
        i = Util.cacheText(difficulty1, i, bufferTextOptions, textBlue);
        i = Util.cacheText(joystick1, i, bufferTextOptions, textBlue);
        i = Util.cacheText(music1, i, bufferTextOptions, textBlue);
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
                new Choice(textDark, textWhite, menu1.get(0), x, mainY + 100, Align.CENTER, Type.NEW),
                new Choice(textDark, textWhite, menu1.get(1), x, mainY + 132, Align.CENTER, Type.CONTINUE),
                new Choice(textDark, textWhite, menu1.get(2), x, mainY + 164, Align.CENTER, Type.OPTIONS),
                new Choice(textDark, textWhite, menu1.get(3), x, mainY + 194, Align.CENTER, Type.INTRO),
                new Choice(textDark, textWhite, menu1.get(4), x, mainY + 235, Align.CENTER, Type.LAUNCHER)
            };
        }
        else
        {
            choice = 1;
            choices = new Choice[]
            {
                new Choice(textDark, textWhite, menu1.get(0), x, mainY + 117, Align.CENTER, Type.NEW),
                new Choice(textDark, textWhite, menu1.get(2), x, mainY + 151, Align.CENTER, Type.OPTIONS),
                new Choice(textDark, textWhite, menu1.get(3), x, mainY + 185, Align.CENTER, Type.INTRO),
                new Choice(textDark, textWhite, menu1.get(4), x, mainY + 235, Align.CENTER, Type.LAUNCHER)
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
            new Choice(textDark, textWhite, options1.get(3), x, mainY + 241, Align.CENTER, Type.MAIN)
        };
        return new Data(choices);
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
            config.with(joystick == 0).with(Util.getInitConfig(Difficulty.from(difficulty), stage)),
            stageConfig.getPic().get(),
            stageConfig.getText().get());
    }

    private void renderMain(Graphic g)
    {
        menus[0].render(g);
        menusData[0].render(g, choice);
    }

    /**
     * Render the options menu.
     * 
     * @param g The graphic output.
     */
    private void renderOptions(Graphic g)
    {
        menus[1].render(g);
        menusData[1].render(g, choice);

        textWhite.draw(g,
                       (int) Math.round(CENTER_X * factorH),
                       mainY + OPTIONS_TITLE_OFFSET_Y,
                       Align.CENTER,
                       menu1.get(menusData[1].choiceMax == 4 ? 1 : 2).toUpperCase(Locale.ENGLISH));

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
        g.drawImage(bufferTextOptions[start + value],
                    (int) Math.round(CENTER_X * factorH) + OPTIONS_TEXT_OFFSET_X,
                    menusData[1].choices[index].getY());
    }

    @Override
    public void load()
    {
        for (int i = 0; i < menus.length; i++)
        {
            menus[i] = Util.get(Folder.SPRITE, "menu" + (i + 1) + ".png");
            menus[i].setOrigin(Origin.CENTER_TOP);
        }

        final int x = (int) Math.round(CENTER_X * factorH);
        menus[0].setLocation(x, mainY + MENU_MAIN_IMAGE_OFFSET_Y);
        menus[1].setLocation(x, mainY);

        createCacheTextOptions();
    }

    @Override
    protected int getMenuId()
    {
        final int id;
        if (type == Type.MAIN)
        {
            id = 0;
        }
        else if (type == Type.OPTIONS)
        {
            id = 1;
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
            case LAUNCHER:
                goToLauncher();
                break;
            default:
                throw new LionEngineException(type);
        }
    }

    /**
     * Go to launcher menu.
     */
    protected void goToLauncher()
    {
        end(MenuLauncher.class, config);
    }

    @Override
    protected void onMenuAccepted(Type type)
    {
        stopAudio();
        if (type == Type.NEW || type == Type.INTRO)
        {
            setSystemCursorVisible(false);
        }
    }

    @Override
    protected boolean filterOption(Type type, int choice)
    {
        return type == Type.OPTIONS && choice < 2;
    }

    @Override
    protected void renderMenus(Graphic g)
    {
        switch (type)
        {
            case MAIN -> renderMain(g);
            case OPTIONS -> renderOptions(g);
            case NEW, CONTINUE, INTRO, LAUNCHER -> RenderableVoid.getInstance().render(g);
            default -> throw new LionEngineException(type);
        }
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        super.onTerminated(hasNextSequence);

        stopAudio();

        menu1.clear();
        options1.clear();
        difficulty1.clear();
        joystick1.clear();
        music1.clear();

        for (int i = 0; i < bufferTextOptions.length; i++)
        {
            bufferTextOptions[i].dispose();
        }
    }

    /**
     * List of menu types.
     */
    enum Type
    {
        /** Main menu. */
        MAIN,
        /** New game. */
        NEW,
        /** Continue game. */
        CONTINUE,
        /** Options menu. */
        OPTIONS,
        /** Intro. */
        INTRO,
        /** Launcher. */
        LAUNCHER;
    }
}
