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
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.InitConfig;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.ScenePicture;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.Stage;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.intro.Intro;

/**
 * Menu implementation.
 */
public class Menu extends Sequence
{
    /** Center X. */
    private static final int CENTER_X = 320;
    /** Main Y. */
    private static final int Y = 28;
    /** Text color in menu option. */
    private static final ColorRgba COLOR_OPTION = new ColorRgba(170, 204, 238);
    /** Title text color. */
    private static final ColorRgba COLOR_TITLE = new ColorRgba(255, 255, 255);
    /** Text instance for the title. */
    private static final Text TEXT_TITLE = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SERIF,
                                                               26,
                                                               TextStyle.BOLD);
    /** Text for menu content. */
    private static final Text TEXT = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SERIF, 26, TextStyle.BOLD);
    /** Alpha step speed. */
    private static final double ALPHA_STEP = 8.0;
    /** Main menu. */
    private static final List<String> MAIN = Util.readLines(Medias.create(Folder.TEXTS, Folder.MENU, "main.txt"));
    /** Options menu. */
    private static final List<String> OPTIONS = Util.readLines(Medias.create(Folder.TEXTS, Folder.MENU, "options.txt"));
    /** List of difficulties. */
    private static final List<String> OPTIONS_DIFFICULTY = Util.readLines(Medias.create(Folder.TEXTS,
                                                                                        Folder.MENU,
                                                                                        "difficulties.txt"));
    /** List of joystick. */
    private static final List<String> OPTIONS_JOYSTICK = Util.readLines(Medias.create(Folder.TEXTS,
                                                                                      Folder.MENU,
                                                                                      "joystick.txt"));
    /** List of music. */
    private static final List<String> OPTIONS_MUSIC = Util.readLines(Medias.create(Folder.TEXTS,
                                                                                   Folder.MENU,
                                                                                   "music.txt"));

    /** Level loading text font. */
    private final SpriteFont font = Drawable.loadSpriteFont(Medias.create(Folder.SPRITES, "font_big.png"),
                                                            Medias.create(Folder.SPRITES, "fontdata_big.xml"),
                                                            24,
                                                            24);
    /** Background menus. */
    private final Sprite[] menus = new Sprite[2];
    /** List of menu data with their content. */
    private final Data[] menusData = new Data[menus.length];
    /** Device controller reference. */
    private final DeviceController device;
    /** Horizontal factor. */
    private final double factorH = getWidth() / 640.0;

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
        super(context, Util.getResolution(Constant.RESOLUTION.get2x(), context));

        final Services services = new Services();
        services.add(context);
        device = DeviceControllerConfig.create(services, Medias.create("input.xml"));

        loadMenu();
        menusData[0] = createMain();
        menusData[1] = createOptions();
    }

    /**
     * Load menu background.
     */
    private void loadMenu()
    {
        for (int i = 0; i < menus.length; i++)
        {
            menus[i] = Drawable.loadSprite(Medias.create(Folder.MENU, "menu" + (i + 1) + ".png"));
            menus[i].setOrigin(Origin.CENTER_TOP);
        }

        final int x = (int) (CENTER_X * factorH);
        menus[0].setLocation(x, Y + 64);
        menus[1].setLocation(x, Y + 32);
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
            new Choice(TEXT, MAIN.get(0), x, Y + 152, Align.CENTER, MenuType.NEW),
            new Choice(TEXT, MAIN.get(1), x, Y + 186, Align.CENTER, MenuType.OPTIONS),
            new Choice(TEXT, MAIN.get(2), x, Y + 222, Align.CENTER, MenuType.INTRO),
            new Choice(TEXT, MAIN.get(3), x, Y + 272, Align.CENTER, MenuType.EXIT)
        };
        return new Data(TEXT, choices);
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
            new Choice(TEXT, OPTIONS.get(0), x - 115, Y + 160, Align.LEFT),
            new Choice(TEXT, OPTIONS.get(1), x - 115, Y + 196, Align.LEFT),
            new Choice(TEXT, OPTIONS.get(2), x - 115, Y + 232, Align.LEFT),
            new Choice(TEXT, OPTIONS.get(3), x, Y + 276, Align.CENTER, MenuType.MAIN)
        };
        return new Data(TEXT, choices);
    }

    /**
     * Handle the menu options.
     */
    private void handleOptions()
    {
        if (choice == 0)
        {
            difficulty = changeOption(difficulty, 0, OPTIONS_DIFFICULTY.size() - 1);
        }
        else if (choice == 1)
        {
            joystick = changeOption(joystick, 0, OPTIONS_JOYSTICK.size() - 1);
        }
        else if (choice == 2)
        {
            music = changeOption(music, 0, OPTIONS_MUSIC.size() - 1);
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
                audio = AudioFactory.loadAudio(Music.values()[music - 1].get());
                audio.setVolume(Constant.AUDIO_VOLUME);
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
                end(ScenePicture.class, Stage.STAGE_1.getFile(), new InitConfig(difficulty < 2 ? 4 : 3, 2));
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

        TEXT_TITLE.setColor(COLOR_TITLE);
        TEXT_TITLE.draw(g,
                        (int) (Menu.CENTER_X * factorH),
                        Menu.Y + 128,
                        Align.CENTER,
                        MAIN.get(1).toUpperCase(Locale.ENGLISH));

        TEXT.setColor(COLOR_OPTION);
        drawOptionText(g, 0, OPTIONS_DIFFICULTY.get(difficulty));
        drawOptionText(g, 1, OPTIONS_JOYSTICK.get(joystick));
        drawOptionText(g, 2, OPTIONS_MUSIC.get(music));
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
        TEXT.draw(g, (int) (CENTER_X * factorH) + 10, menusData[1].choices[index].getY(), Align.LEFT, data);
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
        Sfx.cacheStart();

        font.load();
        font.prepare();
        font.setAlpha(0);

        for (final Sprite element : menus)
        {
            element.load();
            element.prepare();
        }

        Sfx.cacheEnd();
        System.gc();
    }

    @Override
    public void update(double extrp)
    {
        device.update(extrp);

        updateTransition(extrp);
        updateMenu(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        renderMenus(g);
        renderTransition(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        stopAudio();
        font.dispose();
        for (final Sprite element : menus)
        {
            element.dispose();
        }
    }
}
