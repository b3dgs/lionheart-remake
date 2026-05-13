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
import java.util.Optional;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.GameConfig;
import com.b3dgs.lionheart.InitConfig;
import com.b3dgs.lionheart.Scene;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.menu.MenuContinue.Type;

/**
 * Menu continue implementation.
 */
public final class MenuContinue extends Menu<Type>
{
    private static final int INDEX_CONTINUE = 0;
    private static final int INDEX_YES = 1;
    private static final int INDEX_NO = 2;
    private static final int INDEX_CREDITS = 3;

    private static final int TEXT_TIME_Y = 96;
    private static final int TEXT_VALUE_Y = 120;
    private static final int TEXT_CREDITS_Y = 244;

    private static final int TIME_MAX_MS = 20_000;

    private static final int MAIN_Y_OFFSET = -336;

    private static final int VALDYN_FRAME_OFFSET_X = -8;
    private static final double VALDYN_OFFSET_Y = 242.0;

    private final List<String> continues = getText("continue.txt");

    private final SpriteAnimated valdyn = Util.get(7, 1, Folder.HERO, "valdyn", "Continue.png");
    private final Animation animYes = new Animation("yes", 2, 3, 0.1, false, false);
    private final Animation animNo = new Animation("no", 4, 7, 0.12, false, false);
    private final Tick timeLeft = new Tick();

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param config The config reference (must not be <code>null</code>).
     */
    public MenuContinue(Context context, GameConfig config)
    {
        super(context, config);

        menus = new Sprite[1];
        menusData = new Data[menus.length];
        menusData[0] = create();

        type = Type.CONTINUE;

        mainY = (getHeight() + MAIN_Y_OFFSET) / 2;

        timeLeft.start();
    }

    /**
     * Create menu.
     * 
     * @return The created data.
     */
    private Data create()
    {
        final int x = (int) Math.round(CENTER_X * factorH);
        final Choice[] choices =
        {
            new Choice(textDark,
                       textWhite,
                       continues.get(INDEX_YES),
                       x - 100,
                       mainY + 188,
                       Align.CENTER,
                       Type.CONTINUE),
            new Choice(textDark, textWhite, continues.get(INDEX_NO), x + 100, mainY + 188, Align.CENTER, Type.MENU),
        };
        return new Data(choices);
    }

    /**
     * Format time left.
     * 
     * @return The time left.
     */
    private String formatTime()
    {
        final long time = (1_000 + TIME_MAX_MS - timeLeft.elapsedTime(getRate()))
                          / com.b3dgs.lionengine.Constant.ONE_SECOND_IN_MILLI;
        if (time < com.b3dgs.lionengine.Constant.DECADE)
        {
            return "0" + time;
        }
        return String.valueOf(time);
    }

    @Override
    public void load()
    {
        menus[0] = Util.get(Folder.SPRITE, "menu2.png");
        menus[0].setOrigin(Origin.CENTER_TOP);
        menus[0].setLocation(CENTER_X * factorH, mainY);

        valdyn.setOrigin(Origin.CENTER_BOTTOM);
        valdyn.setFrameOffsets(VALDYN_FRAME_OFFSET_X, 0);
        valdyn.setLocation(CENTER_X * factorH, mainY + VALDYN_OFFSET_Y);
    }

    @Override
    protected boolean checkButtonUp()
    {
        return device.getHorizontalDirection() < 0 || device.isFiredOnce(DeviceMapping.LEFT);
    }

    @Override
    protected boolean checkButtonDown()
    {
        return device.getHorizontalDirection() > 0 || device.isFiredOnce(DeviceMapping.RIGHT);
    }

    @Override
    protected boolean checkAcceptCondition()
    {
        return timeLeft.elapsedTime(getRate()) > TIME_MAX_MS;
    }

    @Override
    protected void updateMenu(double extrp)
    {
        timeLeft.update(extrp);
        valdyn.update(extrp);
    }

    @Override
    protected void onMenuAccepted(Type type)
    {
        valdyn.play(choice == 0 ? animYes : animNo);
    }

    @Override
    protected void renderMenus(Graphic g)
    {
        menus[0].render(g);
        menusData[0].render(g, choice);

        valdyn.render(g);

        textWhite.draw(g,
                       (int) Math.round(CENTER_X * factorH),
                       mainY + TEXT_TIME_Y,
                       Align.CENTER,
                       continues.get(INDEX_CONTINUE));
        if (valdyn.getAnimState() == AnimState.STOPPED && timeLeft.elapsedTime(getRate()) < TIME_MAX_MS)
        {
            textWhite.draw(g,
                           (int) Math.round(CENTER_X * factorH),
                           mainY + TEXT_VALUE_Y,
                           Align.CENTER,
                           "(" + formatTime() + ")");
        }

        textBlue.draw(g,
                      (int) Math.round(CENTER_X * factorH),
                      mainY + TEXT_CREDITS_Y,
                      Align.CENTER,
                      continues.get(INDEX_CREDITS) + String.valueOf(/* config.getInit().getCredits() */5));
    }

    @Override
    protected void updateFadeOut(double extrp)
    {
        if (valdyn.getAnimState() == AnimState.FINISHED)
        {
            super.updateFadeOut(extrp);
        }
    }

    @Override
    protected void onFadedOut()
    {
        if (choice == 0)
        {
            final InitConfig init = config.getInit();
            final InitConfig initNext = new InitConfig(init.getStage(),
                                                       init.getHealthMax(),
                                                       0,
                                                       2,
                                                       init.getSword(),
                                                       init.isAmulet(),
                                                       init.getCredits(),
                                                       init.getDifficulty(),
                                                       false,
                                                       Optional.empty());
            end(Scene.class, config.with(initNext));
        }
        else
        {
            end(MenuGame.class, config);
        }
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        valdyn.dispose();
        continues.clear();

        super.onTerminated(hasNextSequence);
    }

    /**
     * List of menu types.
     */
    enum Type
    {
        /** Continue. */
        CONTINUE,
        /** Menu. */
        MENU;
    }
}
