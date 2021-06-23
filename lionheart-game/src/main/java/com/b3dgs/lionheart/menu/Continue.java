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
import java.util.Optional;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.InitConfig;
import com.b3dgs.lionheart.Scene;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Continue implementation.
 */
public class Continue extends Sequence
{
    private static final int MIN_HEIGHT = 360;
    private static final int MAX_WIDTH = 640;
    private static final int MARGIN_WIDTH = 0;

    /** Max time. */
    private static final int TIME_MAX_MILLI = 20_000;
    /** Center X. */
    private static final int CENTER_X = 320;
    /** Fade out tick. */
    private static final int FADE_OUT_TICK = 70;
    /** Text color in menu option. */
    private static final ColorRgba COLOR_OPTION = new ColorRgba(170, 204, 238);
    /** Title text color. */
    private static final ColorRgba COLOR_TITLE = new ColorRgba(255, 255, 255);
    /** Text instance for the title. */
    private static final Text TEXT_TITLE = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SERIF,
                                                               26,
                                                               TextStyle.BOLD);
    /** Yes animation. */
    private static final Animation ANIM_YES = new Animation("yes", 2, 3, 0.1, false, false);
    /** No animation. */
    private static final Animation ANIM_NO = new Animation("no", 4, 7, 0.12, false, false);
    /** Text for menu content. */
    private static final Text TEXT = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SERIF, 26, TextStyle.BOLD);
    /** Alpha step speed. */
    private static final double ALPHA_STEP = 8.0;
    /** Continue menu. */
    private static final List<String> CONTINUE = Util.readLines(Medias.create(Folder.TEXT, "menu", "continue.txt"));

    /** Background menu. */
    private final Sprite back = Drawable.loadSprite(Medias.create(Folder.SPRITE, "menu2.png"));
    /** Valdyn sprite. */
    private final SpriteAnimated valdyn = Drawable.loadSpriteAnimated(Medias.create(Folder.HERO,
                                                                                    "valdyn",
                                                                                    "Continue.png"),
                                                                      7,
                                                                      1);
    /** List of menu data with their content. */
    private final Data data;
    /** Device controller reference. */
    private final DeviceController device;
    /** App info. */
    private final AppInfo info;
    /** Tick. */
    private final Tick tick = new Tick();
    /** Horizontal factor. */
    private final double factorH = getWidth() / 640.0;
    /** Main Y. */
    private final int mainY;
    /** Time left. */
    private final Timing timeLeft = new Timing();
    /** Current stage. */
    private final Media stage;
    /** Init config. */
    private final InitConfig init;

    /** Screen mask alpha current value. */
    private double alpha = 255.0;
    /** Current menu transition. */
    private TransitionType transition = TransitionType.IN;
    /** Line choice on */
    private int choice = 0;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param stage The current stage.
     * @param init The init config.
     */
    public Continue(Context context, Media stage, InitConfig init)
    {
        super(context, Util.getResolution(context, MIN_HEIGHT, MAX_WIDTH, MARGIN_WIDTH));

        this.stage = stage;
        this.init = init;

        final Services services = new Services();
        services.add(context);
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        device = services.add(DeviceControllerConfig.create(services, Medias.create("input.xml")));
        device.setVisible(false);
        info = new AppInfo(this::getFps, services);

        mainY = (getHeight() - 336) / 2;

        back.setOrigin(Origin.CENTER_TOP);
        back.setLocation(CENTER_X * factorH, mainY);

        valdyn.setOrigin(Origin.CENTER_BOTTOM);
        valdyn.setFrameOffsets(-8, 0);
        valdyn.setLocation(CENTER_X * factorH, mainY + 242);

        data = create();

        timeLeft.start();
    }

    /**
     * Create menu.
     * 
     * @return The created data.
     */
    private Data create()
    {
        final int x = (int) (CENTER_X * factorH);
        final Choice[] choices = new Choice[]
        {
            new Choice(TEXT, CONTINUE.get(1), x - 100, mainY + 188, Align.CENTER, null),
            new Choice(TEXT, CONTINUE.get(2), x + 100, mainY + 188, Align.CENTER, null),
        };
        return new Data(TEXT, choices);
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
                updateNavigation();
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
        if (tick.elapsed(FADE_OUT_TICK))
        {
            alpha += ALPHA_STEP * extrp;
        }
        if (alpha > 255.0)
        {
            alpha = 255.0;

            if (choice == 0)
            {
                end(Scene.class,
                    stage,
                    new InitConfig(init.getHealthMax(),
                                   0,
                                   2,
                                   init.getSword(),
                                   init.isAmulet(),
                                   init.getCredits(),
                                   init.getDifficulty(),
                                   false,
                                   Optional.empty()));
            }
            else
            {
                end(Menu.class);
            }
        }
    }

    /**
     * Update the navigation.
     */
    private void updateNavigation()
    {
        final int choiceOld = choice;
        if (device.isFiredOnce(DeviceMapping.LEFT))
        {
            choice--;
        }
        if (device.isFiredOnce(DeviceMapping.RIGHT))
        {
            choice++;
        }
        choice = UtilMath.clamp(choice, 0, data.choiceMax);
        if (choiceOld != choice)
        {
            Sfx.MENU_SELECT.play();
        }
        // Accept choice
        if (device.isFiredOnce(DeviceMapping.CTRL_RIGHT) || timeLeft.elapsed() > TIME_MAX_MILLI)
        {
            if (timeLeft.elapsed() > TIME_MAX_MILLI)
            {
                choice = 1;
            }
            valdyn.play(choice == 0 ? ANIM_YES : ANIM_NO);
            transition = TransitionType.OUT;
            tick.start();
        }
    }

    /**
     * Render continue.
     * 
     * @param g The graphic output.
     */
    private void renderContinue(Graphic g)
    {
        back.render(g);
        data.render(g, choice);
        valdyn.render(g);

        TEXT_TITLE.setColor(COLOR_TITLE);
        TEXT_TITLE.draw(g, (int) (CENTER_X * factorH), mainY + 96, Align.CENTER, CONTINUE.get(0));
        if (!tick.isStarted() && timeLeft.elapsed() < TIME_MAX_MILLI)
        {
            TEXT_TITLE.draw(g, (int) (CENTER_X * factorH), mainY + 118, Align.CENTER, formatTime());
        }

        TEXT.setColor(COLOR_OPTION);
        TEXT.draw(g,
                  (int) (CENTER_X * factorH),
                  mainY + 244,
                  Align.CENTER,
                  CONTINUE.get(3) + String.valueOf(init.getCredits()));
    }

    /**
     * Format time left.
     * 
     * @return The time left.
     */
    private String formatTime()
    {
        final long time = (1_000 + TIME_MAX_MILLI - timeLeft.elapsed())
                          / com.b3dgs.lionengine.Constant.ONE_SECOND_IN_MILLI;
        if (time < 10)
        {
            return "(0" + time + ")";
        }
        return "(" + time + ")";
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

    @Override
    public void load()
    {
        back.load();
        back.prepare();

        valdyn.load();
        valdyn.prepare();
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        device.update(extrp);
        valdyn.update(extrp);

        updateTransition(extrp);

        info.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        renderContinue(g);
        renderTransition(g);

        info.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        back.dispose();
        valdyn.dispose();
    }
}
