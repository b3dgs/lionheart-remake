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

import java.util.List;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionengine.io.DevicePointer;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.GameConfig;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Menu implementation.
 * 
 * @param <T> The menu enum type.
 */
// CHECKSTYLE IGNORE LINE: DataAbstractionCoupling|FanOutComplexity
abstract class Menu<T extends Enum<T>> extends Sequence
{
    /** Horizontal center. */
    protected static final int CENTER_X = 320;

    private static final int MOUSE_HIDE_DELAY_MS = 1000;
    private static final int MIN_HEIGHT = 360;
    private static final int FADE_SPEED = 10;

    /** Alpha step speed. */
    int alphaSpeed = FADE_SPEED;
    /** Device controller reference. */
    final DeviceController device;

    /** Text white. */
    protected final SpriteFont textWhite = Util.loadFont("fontmenu.png", "fontmenu.xml", 26, 30);
    /** Text dark. */
    protected final SpriteFont textDark = Util.loadFont("fontmenu_dark.png", "fontmenu.xml", 26, 30);
    /** Text blue. */
    protected final SpriteFont textBlue = Util.loadFont("fontmenu_blue.png", "fontmenu.xml", 26, 30);
    /** Horizontal factor. */
    protected final double factorH = getWidth() / 640.0;
    /** Game config reference. */
    protected final GameConfig config;

    /** Main Y. */
    protected int mainY = (getHeight() - MIN_HEIGHT) / 2;
    /** Current menu transition. */
    protected TransitionType transition = TransitionType.IN;
    /** Background menus. */
    protected Sprite[] menus = {};
    /** List of menu data with their content. */
    protected Data[] menusData = {};
    /** Current. */
    protected T type;
    /** Line choice on. */
    protected int choice;

    /** Language. */
    private final String lang = Settings.getInstance().getLang();
    /** Tick mouse. */
    private final Tick tickMouse = new Tick();
    /** Cursor reference. */
    private final Cursor cursor;
    /** Device cursor. */
    private final DeviceController deviceCursor;
    /** Device pointer. */
    private final DevicePointer pointer;
    /** Application info. */
    private final AppInfo info;

    /** Screen mask alpha current value. */
    private double alpha = 255.0;
    /** Old choice. */
    private int choiceOld;
    /** Horizontal moved flag. */
    private boolean movedHorizontal;
    /** Vertical moved flag. */
    private boolean movedVertical;
    /** Next. */
    private T typeNext;

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

        final Services services = new Services();
        services.add(context);
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_DEFAULT)));
        device.setVisible(false);

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

        setSystemCursorVisible(false);
        Util.setFilter(this, context, Util.getResolution(Constant.RESOLUTION, context).get2x(), 2);
    }

    /**
     * Update the menu states.
     * 
     * @param extrp The extrapolation value.
     */
    protected abstract void updateMenu(double extrp);

    /**
     * Called on menu accepted.
     * 
     * @param type The accepted type.
     */
    protected abstract void onMenuAccepted(T type);

    /**
     * Render the menus.
     * 
     * @param g The graphic output.
     */
    protected abstract void renderMenus(Graphic g);

    /**
     * Get the menu id.
     * 
     * @return The menu id.
     */
    protected int getMenuId()
    {
        return 0;
    }

    /**
     * Get lines from file.
     * 
     * @param file The file to load.
     * @return The lines read.
     */
    protected List<String> getText(String file)
    {
        return Util.readLines(Medias.create(Folder.TEXT, lang, Folder.MENU, file));
    }

    /**
     * Change an option.
     * 
     * @param option The option.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The new value.
     */
    protected int changeOption(int option, int min, int max)
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
            && filterOption(type, choice)
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
     * Filter option depending on choice.
     * 
     * @param type The menu type.
     * @param choice The choice menu.
     * @return <code>true</code> if accepted, <code>false</code> else.
     */
    protected boolean filterOption(T type, int choice)
    {
        return true;
    }

    /**
     * Additional accept condition on menu.
     * 
     * @return <code>true</code> to accept, <code>false</code> else for default condition.
     */
    protected boolean checkAcceptCondition()
    {
        return false;
    }

    /**
     * Filter choice to skip.
     * 
     * @param type The menu type.
     * @param choice The choice menu.
     * @return <code>true</code> if skipped, <code>false</code> else.
     */
    protected boolean filterChoice(T type, int choice)
    {
        return false;
    }

    /**
     * Check if bottom down pressed.
     * 
     * @return <code>true</code> if down, <code>false</code> else.
     */
    protected boolean checkButtonDown()
    {
        return device.getVerticalDirection() < 0 || device.isFiredOnce(DeviceMapping.DOWN);
    }

    /**
     * Check if bottom down pressed.
     * 
     * @return <code>true</code> if down, <code>false</code> else.
     */
    protected boolean checkButtonUp()
    {
        return device.getVerticalDirection() > 0 || device.isFiredOnce(DeviceMapping.JUMP);
    }

    /**
     * Filter choice cursor.
     * 
     * @param type The menu type.
     * @param choice The choice menu.
     * @return <code>true</code> if accepted, <code>false</code> else.
     */
    protected boolean filterCursorChoice(T type, int choice)
    {
        return true;
    }

    /**
     * Update fade out from menu.
     * 
     * @param extrp The extrapolation value.
     */
    protected void updateFadeOut(double extrp)
    {
        alpha += alphaSpeed * extrp;

        if (getAlpha() > 255)
        {
            alpha = 255.0;
            type = typeNext;
            transition = TransitionType.IN;
            onFadedOut();
            choice = 0;
        }
    }

    /**
     * Called on faded out. Does nothing by default.
     */
    protected void onFadedOut()
    {
        // Nothing by default
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

        if (!movedVertical && checkButtonUp())
        {
            choice--;
            if (filterChoice(type, choice))
            {
                choice--;
            }
            cursor.setVisible(false);
            cursor.setLocation(0, 0);
            movedVertical = true;
        }
        if (!movedVertical && checkButtonDown())
        {
            choice++;
            if (filterChoice(type, choice))
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
        final T next = data.choices[choice].getNext();
        // Accept choice
        if (next != null
            && (device.isFiredOnce(DeviceMapping.ATTACK)
                || deviceCursor.isFiredOnce(DeviceMapping.LEFT) && data.choices[choice].isOver(cursor))
            || checkAcceptCondition())
        {
            typeNext = next;
            transition = TransitionType.OUT;
            onMenuAccepted(next);
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
            if (data.choices[i].isOver(cursor) && filterCursorChoice(type, i))
            {
                return i;
            }
        }
        return choice;
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

    /**
     * Get alpha value.
     * 
     * @return The alpha value.
     */
    private int getAlpha()
    {
        return (int) Math.floor(alpha);
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

        textWhite.dispose();
        textDark.dispose();
        textBlue.dispose();

        for (int i = 0; i < menus.length; i++)
        {
            menus[i].dispose();
        }

        if (!hasNextSequence)
        {
            Engine.terminate();
        }
    }

    /**
     * List of transition types.
     */
    enum TransitionType
    {
        /** Entering menu. */
        IN,
        /** Exiting menu. */
        OUT,
        /** No transition. */
        NONE;
    }
}
