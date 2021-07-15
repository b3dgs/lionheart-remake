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
package com.b3dgs.lionheart.menu;

import java.util.List;
import java.util.Locale;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.Difficulty;
import com.b3dgs.lionheart.InitConfig;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.ScenePicture;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.Stage;
import com.b3dgs.lionheart.StageConfig;
import com.b3dgs.lionheart.StageHard;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.intro.Intro;

/**
 * Menu implementation.
 */
public class Menu extends Sequence
{
    private static final int MIN_HEIGHT = 360;
    private static final int MAX_WIDTH = 640;
    private static final int MARGIN_WIDTH = 0;

    private static final int CENTER_X = 320;
    /** Text color in menu option. */
    private static final ColorRgba COLOR_OPTION = new ColorRgba(170, 204, 238);
    /** Title text color. */
    private static final ColorRgba COLOR_TITLE = new ColorRgba(255, 255, 255);
    /** Alpha step speed. */
    private static final double ALPHA_STEP = 8.0;

    private static List<String> getText(String file)
    {
        return Util.readLines(Medias.create(Folder.TEXT, Settings.getInstance().getLang(), Folder.MENU, file));
    }

    private final Text textTitle = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SANS_SERIF,
                                                       25,
                                                       TextStyle.BOLD);
    private final Text text = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SANS_SERIF, 22, TextStyle.BOLD);

    private final List<String> main = getText("main.txt");
    private final List<String> options = getText("options.txt");
    private final List<String> optionsDifficulty = getText("difficulties.txt");
    private final List<String> optionsJoystick = getText("joystick.txt");
    private final List<String> optionsMusic = getText("music.txt");

    /** Background menus. */
    private final Sprite[] menus = new Sprite[2];
    /** List of menu data with their content. */
    private final Data[] menusData = new Data[menus.length];
    /** Device controller reference. */
    private final DeviceController device;
    /** Application info. */
    private final AppInfo info;
    /** Horizontal factor. */
    private final double factorH = getWidth() / 640.0;
    /** Main Y. */
    private final int mainY;

    /** Screen mask alpha current value. */
    private double alpha = 255.0;
    /** Current menu transition. */
    private TransitionType transition = TransitionType.IN;
    /** Line choice on */
    private int choice = 1;
    /** Current difficulty index. */
    private int difficulty;
    /** Current joystick value. */
    private int joystick;
    /** Current music test. */
    private int music = 1;
    /** Current */
    private MenuType menu = MenuType.MAIN;
    /** Next */
    private MenuType menuNext;
    /** Music player. */
    private Audio audio;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Menu(Context context)
    {
        super(context, Util.getResolution(context, MIN_HEIGHT, MAX_WIDTH, MARGIN_WIDTH));

        final Services services = new Services();
        services.add(context);
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_CUSTOM)));
        device.setVisible(false);
        info = new AppInfo(this::getFps, services);

        mainY = (getHeight() - 360) / 2;

        menusData[0] = createMain();
        menusData[1] = createOptions();

        setSystemCursorVisible(false);
    }

    /**
     * Create main menu.
     * 
     * @return The created data.
     */
    private Data createMain()
    {
        final int x = (int) (CENTER_X * factorH);
        final Choice[] choices = new Choice[]
        {
            new Choice(text, main.get(0), x, mainY + 120, Align.CENTER, MenuType.NEW),
            new Choice(text, main.get(1), x, mainY + 154, Align.CENTER, MenuType.OPTIONS),
            new Choice(text, main.get(2), x, mainY + 190, Align.CENTER, MenuType.INTRO),
            new Choice(text, main.get(3), x, mainY + 240, Align.CENTER, MenuType.EXIT)
        };
        return new Data(text, choices);
    }

    /**
     * Create options menu.
     * 
     * @return The created data.
     */
    private Data createOptions()
    {
        final int x = (int) (CENTER_X * factorH);
        final Choice[] choices = new Choice[]
        {
            new Choice(text, options.get(0), x - 120, mainY + 128, Align.LEFT),
            new Choice(text, options.get(1), x - 120, mainY + 164, Align.LEFT),
            new Choice(text, options.get(2), x - 120, mainY + 200, Align.LEFT),
            new Choice(text, options.get(3), x, mainY + 244, Align.CENTER, MenuType.MAIN)
        };
        return new Data(text, choices);
    }

    /**
     * Handle the menu options.
     */
    private void handleOptions()
    {
        if (choice == 0)
        {
            difficulty = changeOption(difficulty, 0, optionsDifficulty.size() - 1);
        }
        else if (choice == 1)
        {
            joystick = changeOption(joystick, 0, optionsJoystick.size() - 1);
        }
        else if (choice == 2)
        {
            music = changeOption(music, 0, optionsMusic.size() - 1);
            handleOptionMusic();
        }
    }

    /**
     * Handle music option listening.
     */
    private void handleOptionMusic()
    {
        if (device.isFiredOnce(DeviceMapping.CTRL_RIGHT))
        {
            stopAudio();
            if (music > 0)
            {
                audio = AudioFactory.loadAudio(Music.values()[music - 1]);
                audio.setVolume(Settings.getInstance().getVolumeMusic());
                audio.play();
            }
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
        int value = option;
        if (device.isFiredOnce(DeviceMapping.LEFT))
        {
            value--;
        }
        if (device.isFiredOnce(DeviceMapping.RIGHT))
        {
            value++;
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
        alpha -= ALPHA_STEP * extrp;
        if (alpha < 0.0)
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
        alpha += ALPHA_STEP * extrp;
        if (alpha > 255.0)
        {
            alpha = 255.0;
            menu = menuNext;
            transition = TransitionType.IN;
            choice = 0;
        }
    }

    /**
     * Get the menu id.
     * 
     * @return The menu id.
     */
    private int getMenuId()
    {
        final int id;
        if (menu == MenuType.MAIN)
        {
            id = 0;
        }
        else if (menu == MenuType.OPTIONS)
        {
            id = 1;
        }
        else
        {
            id = -1;
        }
        return id;
    }

    /**
     * Update the navigation against the
     * 
     * @param menuId The menu id.
     */
    private void updateMenuNavigation(int menuId)
    {
        final int choiceOld = choice;
        if (device.isFiredOnce(DeviceMapping.UP))
        {
            choice--;
        }
        if (device.isFiredOnce(DeviceMapping.DOWN))
        {
            choice++;
        }
        final Data data = menusData[menuId];
        choice = UtilMath.clamp(choice, 0, data.choiceMax);
        if (choiceOld != choice)
        {
            Sfx.MENU_SELECT.play();
        }
        final MenuType next = data.choices[choice].getNext();
        // Accept choice
        if (next != null && device.isFiredOnce(DeviceMapping.CTRL_RIGHT))
        {
            menuNext = next;
            transition = TransitionType.OUT;
            stopAudio();
        }
    }

    /**
     * Update the menu states.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateMenu(double extrp)
    {
        switch (menu)
        {
            case MAIN:
                break;
            case NEW:
                final Media stage = difficulty > 0 ? StageHard.STAGE1 : Stage.STAGE1;
                final StageConfig config = StageConfig.imports(new Configurer(stage));
                end(ScenePicture.class, stage, getInitConfig(), config.getPic().get(), config.getText().get());
                break;
            case OPTIONS:
                handleOptions();
                break;
            case INTRO:
                end(Intro.class);
                break;
            case EXIT:
                end();
                break;
            default:
                throw new LionEngineException(menu);
        }
    }

    /**
     * Get init config.
     * 
     * @return The init config.
     */
    private InitConfig getInitConfig()
    {
        final Difficulty value = Difficulty.from(difficulty);
        switch (value)
        {
            case NORMAL:
                return Constant.INIT_STANDARD;
            case HARD:
                return Constant.INIT_HARD;
            case LIONHARD:
                return Constant.INIT_LIONHARD;
            default:
                throw new LionEngineException(value);
        }
    }

    /**
     * Render the menus.
     * 
     * @param g The graphic output.
     */
    private void renderMenus(Graphic g)
    {
        switch (menu)
        {
            case MAIN:
                menus[0].render(g);
                menusData[0].render(g, choice);
                break;
            case OPTIONS:
                renderOptions(g);
                break;
            case NEW:
            case INTRO:
            case EXIT:
                break;
            default:
                throw new LionEngineException(menu);
        }
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

        textTitle.setColor(COLOR_TITLE);
        textTitle.draw(g,
                       (int) (Menu.CENTER_X * factorH),
                       mainY + 96,
                       Align.CENTER,
                       main.get(1).toUpperCase(Locale.ENGLISH));

        text.setColor(COLOR_OPTION);
        drawOptionText(g, 0, optionsDifficulty.get(difficulty));
        drawOptionText(g, 1, optionsJoystick.get(joystick));
        drawOptionText(g, 2, optionsMusic.get(music));
    }

    /**
     * Draw text option.
     * 
     * @param g The graphic output.
     * @param index The option index.
     * @param data The option text.
     */
    private void drawOptionText(Graphic g, int index, String data)
    {
        text.draw(g, (int) (CENTER_X * factorH) + 20, menusData[1].choices[index].getY(), Align.LEFT, data);
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
            final int a = UtilMath.clamp((int) Math.floor(alpha), 0, 255);
            g.setColor(Constant.ALPHAS_BLACK[a]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
        }
        g.setColor(ColorRgba.BLACK);
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

    @Override
    public void load()
    {
        for (int i = 0; i < menus.length; i++)
        {
            menus[i] = Drawable.loadSprite(Medias.create(Folder.SPRITE, "menu" + (i + 1) + ".png"));
            menus[i].setOrigin(Origin.CENTER_TOP);
            menus[i].load();
            menus[i].prepare();
        }
        final int x = (int) (CENTER_X * factorH);
        menus[0].setLocation(x, mainY + 32);
        menus[1].setLocation(x, mainY);
    }

    @Override
    public void update(double extrp)
    {
        device.update(extrp);

        updateTransition(extrp);
        updateMenu(extrp);

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
        stopAudio();
        for (final Sprite element : menus)
        {
            element.dispose();
        }
        if (!hasNextSequence)
        {
            Engine.terminate();
        }
    }
}
