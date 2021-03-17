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

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
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
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.intro.Intro;

/**
 * Menu implementation.
 */
public class Menu extends Sequence
{
    /** Main Y. */
    public static final int Y = 44;
    /** List of difficulties. */
    private static final String[] OPTIONS_DIFFICULTY =
    {
        "Normal", "Hard", "Lionhard"
    };
    /** List of joystick. */
    private static final String[] OPTIONS_JOYSTICK =
    {
        "1 Button", "2 Buttons"
    };
    /** List of music. */
    private static final String[] OPTIONS_MUSIC =
    {
        "Music 00", "Music 01", "Music 02", "Music 03", "Music 04", "Music 05", "Music 06", "Music 07", "Music 08"
    };
    /** Error message. */
    private static final String ERROR_MESSAGE = "Unknown type: ";
    /** Font filename. */
    private static final Media FONT_SPRITE = Medias.create(Folder.SPRITES, "font_big.png");
    /** Font data. */
    private static final Media FONT_DATA = Medias.create(Folder.SPRITES, "fontdata_big.xml");
    /** Text color in menu option. */
    private static final ColorRgba COLOR_OPTION = new ColorRgba(170, 204, 238);
    /** Alpha step speed. */
    private static final double ALPHA_STEP = 8.0;
    /** Cached alpha values. */
    private static final ColorRgba[] ALPHAS;

    /**
     * Static init.
     */
    static
    {
        ALPHAS = new ColorRgba[256];
        for (int i = 0; i < 256; i++)
        {
            ALPHAS[i] = new ColorRgba(0, 0, 0, i);
        }
    }

    /** Text for menu content. */
    private final Text text;
    /** Background menus. */
    private final Sprite[] menus;
    /** Level loading text font. */
    private final SpriteFont font;
    /** List of menu data with their content. */
    private final Data[] menusData;
    /** Horizontal factor. */
    private final double factorH;
    /** Vertical factor. */
    private final double factorV;
    /** Input device reference. */
    private final DeviceController device;
    /** Screen mask alpha current value. */
    private double alpha;
    /** Line choice on */
    private int choice;
    /** Current difficulty index. */
    private int difficulty;
    /** Current joystick value. */
    private int joystick;
    /** Current music test. */
    private int music;
    /** Current */
    private MenuType menu;
    /** Next */
    private MenuType menuNext;
    /** Current menu transition. */
    private TransitionType transition;
    /** Music player. */
    private Audio audio;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Menu(Context context)
    {
        super(context, Constant.MENU_RESOLUTION.get2x());

        final Services services = new Services();
        services.add(context);
        device = DeviceControllerConfig.create(services, Medias.create("input.xml"));

        text = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SERIF, 26, TextStyle.BOLD);
        factorH = getWidth() / (double) com.b3dgs.lionheart.Constant.MENU_RESOLUTION.getWidth();
        factorV = getHeight() / (double) com.b3dgs.lionheart.Constant.MENU_RESOLUTION.getHeight();

        font = Drawable.loadSpriteFont(FONT_SPRITE, FONT_DATA, 24, 24);

        menus = new Sprite[2];
        for (int i = 0; i < menus.length; i++)
        {
            menus[i] = Drawable.loadSprite(Medias.create("menu", "menu" + (i + 1) + ".png"));
            menus[i].setOrigin(Origin.CENTER_TOP);
        }
        menus[0].setLocation(getWidth() / 2, Y + 64 * factorV / 2);
        menus[1].setLocation(getWidth() / 2, Y + 32 * factorV / 2);
        menusData = new Data[2];

        // Main menu
        Choice[] choices = new Choice[]
        {
            new Choice(text, factorH, factorV, "Start game", 213, Y + 54, Align.CENTER, MenuType.NEW),
            new Choice(text, factorH, factorV, "Options", 213, Y + 71, Align.CENTER, MenuType.OPTIONS),
            new Choice(text, factorH, factorV, "Introduction", 213, Y + 89, Align.CENTER, MenuType.INTRO),
            new Choice(text, factorH, factorV, "Quit", 213, Y + 114, Align.CENTER, MenuType.EXIT)
        };
        menusData[0] = new Data(text, factorH, factorV, "Main", false, choices);

        // Options menu
        choices = new Choice[]
        {
            new Choice(text, factorH, factorV, "Difficulty", (int) (192 / factorH), Y + 58, Align.LEFT),
            new Choice(text, factorH, factorV, "Joystick", (int) (192 / factorH), Y + 76, Align.LEFT),
            new Choice(text, factorH, factorV, "Soundtest", (int) (192 / factorH), Y + 94, Align.LEFT),
            new Choice(text, factorH, factorV, "Done", 213, Y + 116, Align.CENTER, MenuType.MAIN)
        };
        menusData[1] = new Data(text, factorH, factorV, "OPTIONS", true, choices);

        menu = MenuType.MAIN;
        transition = TransitionType.IN;
        alpha = 255.0;
        choice = 1;
        difficulty = 0;
        joystick = 0;
        music = 1;
        menuNext = null;
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
        final MenuType next = data.choices[choice].next;
        // Accept choice
        if (next != null && device.isFiredOnce(DeviceMapping.FIRE))
        {
            menuNext = next;
            transition = TransitionType.OUT;
            stopAudio();
        }
    }

    /**
     * Handle the menu options sub
     */
    private void handleMenuOptions()
    {
        if (choice == 0)
        {
            difficulty = changeOption(difficulty, 0, OPTIONS_DIFFICULTY.length - 1);
        }
        else if (choice == 1)
        {
            joystick = changeOption(joystick, 0, OPTIONS_JOYSTICK.length - 1);
        }
        else if (choice == 2)
        {
            music = changeOption(music, 0, OPTIONS_MUSIC.length - 1);
            if (device.isFiredOnce(DeviceMapping.FIRE))
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
    }

    /**
     * Handle the menu states.
     * 
     * @param extrp The extrapolation value.
     */
    private void handleMenu(double extrp)
    {
        switch (menu)
        {
            case MAIN:
                break;
            case NEW:
                end(ScenePicture.class, Stage.STAGE_1.getFile(), new InitConfig(4, 0, 2, 1, false, false));
                break;
            case OPTIONS:
                handleMenuOptions();
                break;
            case INTRO:
                end(Intro.class);
                break;
            case EXIT:
                end();
                break;
            default:
                throw new LionEngineException(ERROR_MESSAGE + menu);
        }
    }

    /**
     * Handle the menu transitions.
     * 
     * @param extrp The extrapolation value.
     */
    private void handleMenuTransition(double extrp)
    {
        switch (transition)
        {
            // Fading in to new menu
            case IN:
                alpha -= ALPHA_STEP * extrp;
                if (alpha < 0.0 - ALPHA_STEP)
                {
                    alpha = 0.0;
                    transition = TransitionType.NONE;
                }
                break;
            // Ready to navigate inside the current menu
            case NONE:
                final int menuId = getMenuId();
                if (menuId > -1)
                {
                    updateMenuNavigation(menuId);
                }
                break;
            // Fading out from current menu
            case OUT:
                alpha += ALPHA_STEP * extrp;
                if (alpha >= 255.0 + ALPHA_STEP)
                {
                    alpha = 255.0;
                    menu = menuNext;
                    transition = TransitionType.IN;
                    choice = 0;
                }
                break;
            default:
                throw new LionEngineException(ERROR_MESSAGE + transition);
        }
    }

    /**
     * Render the menus.
     * 
     * @param g The graphic output.
     * @param id The menu id.
     */
    private void renderMenus(Graphic g, int id)
    {
        switch (menu)
        {
            case MAIN:
            case NEW:
                break;
            case OPTIONS:
                text.setColor(COLOR_OPTION);
                text.draw(g,
                          (int) (219 * factorH),
                          menusData[id].choices[0].y,
                          Align.LEFT,
                          OPTIONS_DIFFICULTY[difficulty]);
                text.draw(g, (int) (219 * factorH), menusData[id].choices[1].y, Align.LEFT, OPTIONS_JOYSTICK[joystick]);
                text.draw(g, (int) (219 * factorH), menusData[id].choices[2].y, Align.LEFT, OPTIONS_MUSIC[music]);
                break;
            case INTRO:
                break;
            case EXIT:
                end();
                break;
            default:
                throw new LionEngineException(ERROR_MESSAGE + menu);
        }
    }

    /**
     * Get the menu id.
     * 
     * @return The menu id.
     */
    private int getMenuId()
    {
        if (menu == MenuType.MAIN)
        {
            return 0;
        }
        else if (menu == MenuType.OPTIONS)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }

    /**
     * Stop audio if exists.
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
        handleMenuTransition(extrp);
        handleMenu(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());
        final int id = getMenuId();
        if (id > -1)
        {
            menus[id].render(g);
            menusData[id].render(g, choice);
        }
        renderMenus(g, id);
        if (transition != TransitionType.NONE)
        {
            final int a = UtilMath.clamp((int) alpha, 0, 255);
            g.setColor(ALPHAS[a]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
        }
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        stopAudio();
    }
}
