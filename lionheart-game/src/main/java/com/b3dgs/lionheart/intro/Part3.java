/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.intro;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.RenderableVoid;
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
import com.b3dgs.lionheart.GameConfig;
import com.b3dgs.lionheart.Time;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.menu.Menu;

/**
 * Intro part 3 implementation.
 */
public class Part3 extends Sequence
{
    private static final String PART3_FOLDER = "part3";

    private static final int MIN_HEIGHT = 208;
    private static final int MAX_WIDTH = 400;
    private static final int MARGIN_WIDTH = 80;

    private static final int FADE_SPEED = 7;
    private static final double ANIM_SPEED = 0.22;

    private static final int VALDYN_X_MAX = 228;
    private static final double VALDYN_X_SPEED = 1.2;
    private static final double CAMERA_X_SPEED = 1.2;

    private static final int DRAGON_OFFSET_X = 3;
    private static final int DRAGON_OFFSET_Y = -33;
    private static final int DRAGON_MOVE_DOWN_FRAME = 10;
    private static final int DRAGON_FLY_FRAME = 18;
    private static final double DRAGON_MOVE_DOWN_SPEED = 0.035;
    private static final double DRAGON_MOVE_DOWN_MAX = -1.9;
    private static final double DRAGON_MOVE_X = 1.5;
    private static final double DRAGON_MOVE_Y = 1.75;

    private static final int TIME_FADE_IN_MS = 93_800;
    private static final int TIME_VALDYN_MOVE_MS = 95_000;
    private static final int TIME_VALDYN_RENDER_AFTER_MS = 95_200;
    private static final int TIME_DRAGON_HEAD_MS = 96_000;
    private static final int TIME_CAMERA_MOVE_MS = 96_200;
    private static final int TIME_VALDYN_HAND_MS = 99_000;
    private static final int TIME_VALDYN_DRAGON_MS = 100_000;
    private static final int TIME_DRAGON_EAT_MS = 98_500;
    private static final int TIME_DRAGON_BACK_MS = 99_900;
    private static final int TIME_DRAGON_FLY_MS = 101_900;
    private static final int TIME_DRAGON_RENDER_AFTER_MS = 101_200;
    private static final int TIME_FADE_OUT_MS = 108_000;

    /**
     * Get media from filename.
     * 
     * @param filename The filename.
     * @return The media.
     */
    private static Media get(String filename)
    {
        return Medias.create(Folder.INTRO, PART3_FOLDER, filename + ".png");
    }

    private static Animation createAnimation(int start, int end, boolean reverse, boolean repeat)
    {
        return new Animation(Animation.DEFAULT_NAME, start, end, ANIM_SPEED, reverse, repeat);
    }

    /** Device controller reference. */
    final DeviceController device;
    /** Alpha speed. */
    int alphaSpeed = FADE_SPEED;

    private final SpriteAnimated valdyn = Drawable.loadSpriteAnimated(get("valdyn"), 8, 3);
    private final SpriteAnimated dragon1 = Drawable.loadSpriteAnimated(get("dragon1"), 6, 3);
    private final SpriteAnimated dragon2 = Drawable.loadSpriteAnimated(get("dragon2"), 5, 4);
    private final Sprite scene = Drawable.loadSprite(get("scene"));

    private final Animation valdynWalk = createAnimation(1, 10, false, true);
    private final Animation valdynPrepare = createAnimation(11, 12, false, false);
    private final Animation valdynPrepareLoop = createAnimation(13, 14, false, true);
    private final Animation valdynDragon = createAnimation(15, 24, false, false);

    private final Animation dragonIdle = createAnimation(1, 15, false, false);
    private final Animation dragonEat = createAnimation(16, 18, false, true);
    private final Animation dragonBack = createAnimation(1, 15, true, false);
    private final Animation dragonFly = createAnimation(1, 20, true, true);

    private final Coord valdynCoord = new Coord(28.0, -78.0);
    private final Coord dragonCoord = new Coord(176.0, -44.0);

    private final Camera camera = new Camera();
    private final GameConfig config;
    private final AppInfo info;
    private final Time time;
    private final Audio audio;
    private final DeviceController deviceCursor;

    private Updatable updaterValdyn = this::updateValdynInit;
    private Updatable updaterCamera = this::updateCameraInit;
    private Updatable updaterDragon = this::updateDragonInit;
    private Updatable updaterFade = this::updateFadeInit;

    private Renderable rendererFade = this::renderFade;

    private double alpha = 255.0;
    private double dragonGoDown;
    private boolean skip;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param config The config reference (must not be <code>null</code>).
     * @param time The time reference.
     * @param audio The audio reference.
     */
    public Part3(Context context, GameConfig config, Time time, Audio audio)
    {
        super(context,
              Util.getResolution(context, MIN_HEIGHT, MAX_WIDTH, MARGIN_WIDTH),
              Util.getLoop(context.getConfig().getOutput()));

        this.config = config;
        this.time = time;
        this.audio = audio;

        final Services services = new Services();
        services.add(context);
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_DEFAULT)));

        final Media mediaCursor = Medias.create(Constant.INPUT_FILE_CURSOR);
        deviceCursor = DeviceControllerConfig.create(services, mediaCursor);

        info = new AppInfo(this::getFps, services);

        load(Part4.class, config, time, audio);

        camera.setView(0, (getHeight() - scene.getHeight()) / 2, getWidth(), getHeight(), getHeight());

        setSystemCursorVisible(false);
        Util.setFilter(this, context, Util.getResolution(context, MIN_HEIGHT, MAX_WIDTH, MARGIN_WIDTH), 2);
    }

    /**
     * Update valdyn init time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateValdynInit(double extrp)
    {
        if (time.isAfter(TIME_VALDYN_MOVE_MS))
        {
            updaterValdyn = this::updateValdynMove;
        }
    }

    /**
     * Update valdyn move right until dragon.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateValdynMove(double extrp)
    {
        valdynCoord.translate(VALDYN_X_SPEED * extrp, 0.0);

        if (valdynCoord.getX() > VALDYN_X_MAX)
        {
            valdynCoord.setX(VALDYN_X_MAX);
            valdyn.play(valdynPrepare);
            updaterValdyn = this::updateValdynHandPrepare;
        }
    }

    /**
     * Update valdyn hand prepare animation.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateValdynHandPrepare(double extrp)
    {
        if (valdyn.getAnimState() == AnimState.FINISHED)
        {
            valdyn.play(valdynPrepareLoop);
            updaterValdyn = this::updateValdynHandLoop;
        }
    }

    /**
     * Update valdyn hand loop animation.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateValdynHandLoop(double extrp)
    {
        if (time.isAfter(TIME_VALDYN_HAND_MS))
        {
            valdyn.stop();
            updaterValdyn = this::updateValdynDragon;
        }
    }

    /**
     * Update valdyn dragon animation.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateValdynDragon(double extrp)
    {
        if (time.isAfter(TIME_VALDYN_DRAGON_MS))
        {
            valdyn.play(valdynDragon);
            updaterValdyn = UpdatableVoid.getInstance();
        }
    }

    /**
     * Update camera init time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateCameraInit(double extrp)
    {
        if (time.isAfter(TIME_CAMERA_MOVE_MS))
        {
            updaterCamera = this::updateCameraMove;
        }
    }

    /**
     * Update camera move right until limit.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateCameraMove(double extrp)
    {
        camera.moveLocation(extrp, CAMERA_X_SPEED, 0.0);

        final int maxX = MAX_WIDTH - getWidth();
        if (camera.getX() > maxX)
        {
            camera.setLocation(maxX, camera.getY() - camera.getViewY());
            updaterCamera = UpdatableVoid.getInstance();
        }
    }

    /**
     * Update dragon init time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateDragonInit(double extrp)
    {
        if (time.isAfter(TIME_DRAGON_HEAD_MS))
        {
            dragon1.play(dragonIdle);
            updaterDragon = this::updateDragonHead;
        }
    }

    /**
     * Update dragon head animation.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateDragonHead(double extrp)
    {
        if (dragon1.getAnimState() == AnimState.FINISHED)
        {
            dragon1.play(dragonEat);
            updaterDragon = this::updateDragonEat;
        }
    }

    /**
     * Update dragon eat animation.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateDragonEat(double extrp)
    {
        if (time.isAfter(TIME_DRAGON_EAT_MS))
        {
            dragon1.stop();
            dragon1.setFrame(dragonBack.getLast() + 1);
            updaterDragon = this::updateDragonEatDone;
        }
    }

    /**
     * Update dragon eat animation.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateDragonEatDone(double extrp)
    {
        if (time.isAfter(TIME_DRAGON_BACK_MS))
        {
            dragon1.stop();
            dragon1.play(dragonBack);
            dragon1.setFrame(dragonBack.getLast() + 1);
            updaterDragon = this::updateDragonBack;
        }
    }

    /**
     * Update dragon animation back.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateDragonBack(double extrp)
    {
        if (time.isAfter(TIME_DRAGON_FLY_MS))
        {
            dragon2.play(dragonFly);
            updaterDragon = this::updateDragonMoveDown;
        }
    }

    /**
     * Update dragon movement.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateDragonMoveDown(double extrp)
    {
        if (dragon2.getFrame() > DRAGON_MOVE_DOWN_FRAME)
        {
            dragonGoDown -= DRAGON_MOVE_DOWN_SPEED * extrp;
            if (dragonGoDown < DRAGON_MOVE_DOWN_MAX)
            {
                dragonGoDown = DRAGON_MOVE_DOWN_MAX;
            }
            dragonCoord.translate(DRAGON_MOVE_X * extrp, (DRAGON_MOVE_Y + dragonGoDown) * extrp);

            if (dragon2.getAnimState() == AnimState.REVERSING && dragon2.getFrameAnim() <= DRAGON_FLY_FRAME)
            {
                dragon2.stop();
                dragon2.setFrame(DRAGON_FLY_FRAME);
            }
        }
    }

    /**
     * Update fade in start time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeInit(double extrp)
    {
        if (time.isAfter(TIME_FADE_IN_MS))
        {
            updaterFade = this::updateFadeIn;
        }
    }

    /**
     * Update fade in effect.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeIn(double extrp)
    {
        alpha -= alphaSpeed * extrp;

        if (getAlpha() < 0)
        {
            alpha = 0.0;
            updaterFade = this::updateSkip;
            rendererFade = RenderableVoid.getInstance();
        }
    }

    /**
     * Update skip check.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateSkip(double extrp)
    {
        skip = device.isFiredOnce(DeviceMapping.CTRL_RIGHT) || deviceCursor.isFiredOnce(DeviceMapping.LEFT);

        if (time.isAfter(TIME_FADE_OUT_MS) || skip)
        {
            updaterFade = this::updateFadeOut;
            rendererFade = this::renderFade;
        }
    }

    /**
     * Update fade out effect and exit.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOut(double extrp)
    {
        alpha += alphaSpeed * extrp;

        if (getAlpha() > 255)
        {
            alpha = 255.0;

            if (skip)
            {
                audio.stop();
                end(Menu.class, config);
            }
            else
            {
                end();
            }
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
     * Render valdyn.
     * 
     * @param g The graphic output.
     */
    private void renderValdyn(Graphic g)
    {
        valdyn.setLocation(camera.getViewpointX(valdynCoord.getX()),
                           camera.getViewpointY(valdynCoord.getY()) - getHeight());
        valdyn.render(g);
    }

    /**
     * Render dragon 1.
     * 
     * @param g The graphic output.
     */
    private void renderDragon1(Graphic g)
    {
        dragon1.setLocation(camera.getViewpointX(dragonCoord.getX()),
                            camera.getViewpointY(dragonCoord.getY()) - getHeight());
        dragon1.render(g);
    }

    /**
     * Render dragon 2.
     * 
     * @param g The graphic output.
     */
    private void renderDragon2(Graphic g)
    {
        dragon2.setLocation(camera.getViewpointX(dragonCoord.getX()),
                            camera.getViewpointY(dragonCoord.getY()) - getHeight());
        dragon2.render(g);
    }

    /**
     * Draw horizontal top and bottom black band.
     * 
     * @param g The graphic output.
     */
    private void drawBand(Graphic g)
    {
        final int bandHeight = (int) (Math.floor(getHeight() - MIN_HEIGHT) / 2.0);
        g.clear(0, 0, getWidth(), bandHeight);
        g.clear(0, getHeight() - bandHeight, getWidth(), bandHeight);
    }

    @Override
    public void load()
    {
        scene.load();
        scene.prepare();

        dragon1.load();
        dragon1.prepare();

        dragon2.load();
        dragon2.prepare();
        dragon2.setFrameOffsets(DRAGON_OFFSET_X, DRAGON_OFFSET_Y);

        valdyn.load();
        valdyn.prepare();
        valdyn.play(valdynWalk);
    }

    @Override
    public void update(double extrp)
    {
        device.update(extrp);
        deviceCursor.update(extrp);
        time.update(extrp);
        valdyn.update(extrp);
        dragon1.update(extrp);
        dragon2.update(extrp);

        updaterValdyn.update(extrp);
        updaterDragon.update(extrp);
        updaterCamera.update(extrp);
        updaterFade.update(extrp);

        info.update(extrp);

        if (device.isFiredOnce(DeviceMapping.FORCE_EXIT))
        {
            end(null);
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        if (time.isBefore(TIME_VALDYN_RENDER_AFTER_MS))
        {
            renderValdyn(g);
        }

        scene.setLocation(camera.getViewpointX(0), camera.getViewpointY(0) - getHeight());
        scene.render(g);

        if (time.isBefore(TIME_DRAGON_RENDER_AFTER_MS))
        {
            renderDragon1(g);

            if (time.isAfter(TIME_VALDYN_RENDER_AFTER_MS))
            {
                renderValdyn(g);
            }
        }
        else
        {
            renderDragon2(g);
        }

        rendererFade.render(g);
        drawBand(g);

        info.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        super.onTerminated(hasNextSequence);

        if (!hasNextSequence)
        {
            audio.stop();
            Engine.terminate();
        }
    }
}
