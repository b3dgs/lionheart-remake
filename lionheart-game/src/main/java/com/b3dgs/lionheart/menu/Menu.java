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
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Camera;
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
import com.b3dgs.lionengine.io.DevicePointer;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.Difficulty;
import com.b3dgs.lionheart.InitConfig;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.ScenePicture;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.StageConfig;
import com.b3dgs.lionheart.Util;
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
    private static final int MENU_MAIN_IMAGE_OFFSET_Y = 32;
    private static final int OPTIONS_TITLE_OFFSET_Y = 96;
    private static final int OPTIONS_TEXT_OFFSET_X = 12;

    private static List<String> getText(Settings settings, String file)
    {
        return Util.readLines(Medias.create(Folder.TEXT, settings.getLang(), Folder.MENU, file));
    }

    private final ColorRgba colorOption = new ColorRgba(170, 204, 238);
    private final ColorRgba colorTitle = new ColorRgba(255, 255, 255);
    private final Text textTitle = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SERIF, 26, TextStyle.BOLD);
    private final Text text = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SERIF, 26, TextStyle.BOLD);
    private final Settings settings = Settings.getInstance();

    private final List<String> main = getText(settings, "main.txt");
    private final List<String> options = getText(settings, "options.txt");
    private final List<String> optionsDifficulty = getText(settings, "difficulties.txt");
    private final List<String> optionsJoystick = getText(settings, "joystick.txt");
    private final List<String> optionsMusic = getText(settings, "music.txt");

    /** Alpha step speed. */
    int alphaSpeed = FADE_SPEED;
    /** Device controller reference. */
    final DeviceController device;

    /** Background menus. */
    private final Sprite[] menus = new Sprite[2];
    /** List of menu data with their content. */
    private final Data[] menusData = new Data[menus.length];
    /** Application info. */
    private final AppInfo info;
    /** Horizontal factor. */
    private final double factorH = getWidth() / 640.0;
    /** Main Y. */
    private final int mainY;
    private final Tick tickMouse = new Tick();
    private final DeviceController deviceCursor;
    private final Cursor cursor;
    private final DevicePointer pointer;

    /** Screen mask alpha current value. */
    private double alpha = 255.0;
    /** Current menu transition. */
    private TransitionType transition = TransitionType.IN;
    /** Line choice on. */
    private int choice = 1;
    /** Current difficulty index. */
    private int difficulty;
    /** Current joystick value. */
    private int joystick;
    /** Current music test. */
    private int music = 1;
    /** Current. */
    private MenuType menu = MenuType.MAIN;
    /** Next. */
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
        super(context, Util.getResolution(Constant.RESOLUTION.get2x(), context), Util.getLoop());

        setSystemCursorVisible(false);

        final Services services = new Services();
        services.add(context);
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        device = services.add(DeviceControllerConfig.create(services, Medias.create(settings.getInput())));
        device.setVisible(false);

        final Media mediaCursor = Medias.create(Constant.INPUT_FILE_CUSTOR);
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

        mainY = (getHeight() - MIN_HEIGHT) / 2;

        menusData[0] = createMain();
        menusData[1] = createOptions();
    }

    /**
     * Create main menu.
     * 
     * @return The created data.
     */
    private Data createMain()
    {
        final int x = (int) Math.round(CENTER_X * factorH);
        final Choice[] choices = new Choice[]
        {
            new Choice(text, main.get(0), x, mainY + 120, Align.CENTER, MenuType.NEW),
            new Choice(text, main.get(1), x, mainY + 154, Align.CENTER, MenuType.OPTIONS),
            new Choice(text, main.get(2), x, mainY + 188, Align.CENTER, MenuType.INTRO),
            new Choice(text, main.get(3), x, mainY + 238, Align.CENTER, MenuType.EXIT)
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
        final int x = (int) Math.round(CENTER_X * factorH);
        final Choice[] choices = new Choice[]
        {
            new Choice(text, options.get(0), x - 118, mainY + 128, Align.LEFT),
            new Choice(text, options.get(1), x - 118, mainY + 164, Align.LEFT),
            new Choice(text, options.get(2), x - 118, mainY + 200, Align.LEFT),
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
                audio.setVolume(settings.getVolumeMusic());
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

    private int getCursorChoice(Data data)
    {
        for (int i = 0; i < data.choices.length; i++)
        {
            if (data.choices[i].isOver(cursor))
            {
                return i;
            }
        }
        return choice;
    }

    /**
     * Update the navigation.
     * 
     * @param menuId The menu id.
     */
    private void updateMenuNavigation(int menuId)
    {
        final int choiceOld = choice;
        if (device.isFiredOnce(DeviceMapping.UP))
        {
            choice--;
            cursor.setVisible(false);
            cursor.setLocation(0, 0);
        }
        if (device.isFiredOnce(DeviceMapping.DOWN))
        {
            choice++;
            cursor.setVisible(false);
            cursor.setLocation(0, 0);
        }
        if (Double.compare(cursor.getMoveX(), 0.0) != 0 || Double.compare(cursor.getMoveY(), 0.0) != 0)
        {
            cursor.setVisible(true);
        }
        final Data data = menusData[menuId];
        choice = getCursorChoice(data);
        choice = UtilMath.clamp(choice, 0, data.choiceMax);
        if (choiceOld != choice)
        {
            Sfx.MENU_SELECT.play();
        }
        final MenuType next = data.choices[choice].getNext();
        // Accept choice
        if (next != null
            && (device.isFiredOnce(DeviceMapping.CTRL_RIGHT) || deviceCursor.isFiredOnce(DeviceMapping.LEFT)))
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
                final String suffix = difficulty > 0 ? "_hard" : com.b3dgs.lionengine.Constant.EMPTY_STRING;
                Media stage = Medias.create(Folder.STAGE, settings.getStages(), "stage1" + suffix + ".xml");
                if (!stage.exists())
                {
                    stage = Medias.create(Folder.STAGE, settings.getStages(), "stage1.xml");
                }
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
     * Get init config based on difficulty.
     * 
     * @return The init config.
     */
    private InitConfig getInitConfig()
    {
        final InitConfig init;
        final Difficulty value = Difficulty.from(difficulty);
        switch (value)
        {
            case NORMAL:
                init = Constant.INIT_STANDARD;
                break;
            case HARD:
                init = Constant.INIT_HARD;
                break;
            case LIONHARD:
                init = Constant.INIT_LIONHARD;
                break;
            default:
                throw new LionEngineException(value);
        }
        return init;
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
        else
        {
            if (Double.compare(cursor.getMoveX(), 0.0) != 0 || Double.compare(cursor.getMoveY(), 0.0) != 0)
            {
                tickMouse.restart();
                setSystemCursorVisible(true);
            }
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

        textTitle.setColor(colorTitle);
        textTitle.draw(g,
                       (int) Math.round(Menu.CENTER_X * factorH),
                       mainY + OPTIONS_TITLE_OFFSET_Y,
                       Align.CENTER,
                       main.get(1).toUpperCase(Locale.ENGLISH));

        text.setColor(colorOption);
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
        text.draw(g,
                  (int) Math.round(CENTER_X * factorH) + OPTIONS_TEXT_OFFSET_X,
                  menusData[1].choices[index].getY(),
                  Align.LEFT,
                  data);
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
        final int x = (int) Math.round(CENTER_X * factorH);
        menus[0].setLocation(x, mainY + MENU_MAIN_IMAGE_OFFSET_Y);
        menus[1].setLocation(x, mainY);
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
