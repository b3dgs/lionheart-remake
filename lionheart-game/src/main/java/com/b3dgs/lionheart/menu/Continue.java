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
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.UtilMath;
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
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionengine.io.DevicePointer;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.InitConfig;
import com.b3dgs.lionheart.Scene;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Continue implementation.
 */
// CHECKSTYLE IGNORE LINE: DataAbstractionCoupling
public class Continue extends Sequence
{
    private static final int MIN_HEIGHT = 360;
    private static final int MAX_WIDTH = 640;
    private static final int MARGIN_WIDTH = 0;

    private static final int INDEX_CONTINUE = 0;
    private static final int INDEX_YES = 1;
    private static final int INDEX_NO = 2;
    private static final int INDEX_CREDITS = 3;

    private static final int TEXT_TIME_Y = 96;
    private static final int TEXT_VALUE_Y = 120;
    private static final int TEXT_CREDITS_Y = 244;

    private static final int TIME_MAX_MILLI = 20_000;
    private static final int FADE_OUT_TICK = 70;
    private static final int FADE_SPEED = 8;

    private static final int CENTER_X = 320;
    private static final int MAIN_Y_OFFSET = -336;

    private static final int VALDYN_FRAME_OFFSET_X = -8;
    private static final int VALDYN_OFFSET_Y = 242;

    private static final ColorRgba COLOR_OPTION = new ColorRgba(170, 204, 238);
    private static final ColorRgba COLOR_TITLE = new ColorRgba(255, 255, 255);

    private final Text textTitle = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SERIF, 26, TextStyle.BOLD);
    private final Text text = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SERIF, 26, TextStyle.BOLD);
    private final List<String> continues = Util.readLines(Medias.create(Folder.TEXT,
                                                                        Settings.getInstance().getLang(),
                                                                        Folder.MENU,
                                                                        "continue.txt"));
    private final Sprite back = Drawable.loadSprite(Medias.create(Folder.SPRITE, "menu2.png"));
    private final SpriteAnimated valdyn = Drawable.loadSpriteAnimated(Medias.create(Folder.HERO,
                                                                                    "valdyn",
                                                                                    "Continue.png"),
                                                                      7,
                                                                      1);
    private final Animation animYes = new Animation("yes", 2, 3, 0.1, false, false);
    private final Animation animNo = new Animation("no", 4, 7, 0.12, false, false);

    /** List of menu data with their content. */
    private final Data data;

    private final DeviceController device;
    private final AppInfo info;
    private final Tick tick = new Tick();

    /** Horizontal factor. */
    private final double factorH = getWidth() / 640.0;

    private final int mainY;

    private final Timing timeLeft = new Timing();
    private final Media stage;
    private final InitConfig init;
    private final DeviceController deviceCursor;
    private final Cursor cursor;

    /** Screen mask alpha current value. */
    private double alpha = 255.0;
    /** Current menu transition. */
    private TransitionType transition = TransitionType.IN;
    /** Line choice on. */
    private int choice;

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
        device = services.add(DeviceControllerConfig.create(services,
                                                            Medias.create(Settings.getInstance().getInput())));
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
        cursor.setSync((DevicePointer) getInputDevice(DeviceControllerConfig.imports(services, mediaCursor)
                                                                            .iterator()
                                                                            .next()
                                                                            .getDevice()));
        info = new AppInfo(this::getFps, services);

        mainY = (getHeight() + MAIN_Y_OFFSET) / 2;

        back.setOrigin(Origin.CENTER_TOP);
        back.setLocation(CENTER_X * factorH, mainY);

        valdyn.setOrigin(Origin.CENTER_BOTTOM);
        valdyn.setFrameOffsets(VALDYN_FRAME_OFFSET_X, 0);
        valdyn.setLocation(CENTER_X * factorH, mainY + VALDYN_OFFSET_Y);

        data = create();

        setSystemCursorVisible(true);

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
            new Choice(text, continues.get(INDEX_YES), x - 100, mainY + 188, Align.CENTER, null),
            new Choice(text, continues.get(INDEX_NO), x + 100, mainY + 188, Align.CENTER, null),
        };
        return new Data(text, choices);
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
        alpha -= FADE_SPEED * extrp;

        if (alpha < 0)
        {
            alpha = 0;
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
            alpha += FADE_SPEED * extrp;
        }
        if (alpha > 255)
        {
            alpha = 255;

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

    private int getCursorChoice()
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
     */
    private void updateNavigation()
    {
        final int choiceOld = choice;
        if (device.isFiredOnce(DeviceMapping.LEFT))
        {
            choice--;
            cursor.setVisible(false);
            cursor.setLocation(0, 0);
        }
        if (device.isFiredOnce(DeviceMapping.RIGHT))
        {
            choice++;
            cursor.setVisible(false);
            cursor.setLocation(0, 0);
        }
        if (Double.compare(cursor.getMoveX(), 0.0) != 0 || Double.compare(cursor.getMoveY(), 0.0) != 0)
        {
            cursor.setVisible(true);
        }
        choice = getCursorChoice();
        choice = UtilMath.clamp(choice, 0, data.choiceMax);
        if (choiceOld != choice)
        {
            Sfx.MENU_SELECT.play();
        }
        // Accept choice
        if (device.isFiredOnce(DeviceMapping.CTRL_RIGHT)
            || deviceCursor.isFiredOnce(DeviceMapping.LEFT)
            || timeLeft.elapsed() > TIME_MAX_MILLI)
        {
            if (timeLeft.elapsed() > TIME_MAX_MILLI)
            {
                choice = 1;
            }
            valdyn.play(choice == 0 ? animYes : animNo);
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

        textTitle.setColor(COLOR_TITLE);
        textTitle.draw(g, (int) (CENTER_X * factorH), mainY + TEXT_TIME_Y, Align.CENTER, continues.get(INDEX_CONTINUE));
        if (!tick.isStarted() && timeLeft.elapsed() < TIME_MAX_MILLI)
        {
            textTitle.draw(g, (int) (CENTER_X * factorH), mainY + TEXT_VALUE_Y, Align.CENTER, "(" + formatTime() + ")");
        }

        text.setColor(COLOR_OPTION);
        text.draw(g,
                  (int) (CENTER_X * factorH),
                  mainY + TEXT_CREDITS_Y,
                  Align.CENTER,
                  continues.get(INDEX_CREDITS) + String.valueOf(init.getCredits()));
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
        if (time < com.b3dgs.lionengine.Constant.DECADE)
        {
            return "0" + time;
        }
        return String.valueOf(time);
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
        deviceCursor.update(extrp);
        cursor.update(extrp);
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
        continues.clear();

        if (!hasNextSequence)
        {
            Engine.terminate();
        }
    }
}
