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
package com.b3dgs.lionheart.narration.intro;

import java.io.Closeable;
import java.util.function.Consumer;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionheart.FadeSide;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.narration.NarrationFactory;

/**
 * Intro part 3 implementation.
 */
public class Part3 implements Closeable
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

    private static final int T_FADE_IN = 93_800;
    private static final int T_VALDYN_MOVE = 95_000;
    private static final int T_VALDYN_RENDER = 95_200;
    private static final int T_DRAGON_HEAD = 96_000;
    private static final int T_CAMERA_MOVE = 96_200;
    private static final int T_VALDYN_HAND = 99_000;
    private static final int T_VALDYN_DRAGON = 100_000;
    private static final int T_DRAGON_EAT = 98_500;
    private static final int T_DRAGON_BACK = 99_900;
    private static final int T_DRAGON_FLY = 101_900;
    private static final int T_DRAGON_RENDER = 101_200;
    private static final int T_FADE_OUT = 108_000;

    private final SpriteAnimated valdyn = Util.get(8, 3, Folder.INTRO, PART3_FOLDER, "valdyn.png");
    private final SpriteAnimated dragon1 = Util.get(6, 3, Folder.INTRO, PART3_FOLDER, "dragon1.png");
    private final SpriteAnimated dragon2 = Util.get(5, 4, Folder.INTRO, PART3_FOLDER, "dragon2.png");
    private final Sprite scene = Util.get(Folder.INTRO, PART3_FOLDER, "scene.png");

    private final Animation valdynWalk = new Animation(Animation.DEFAULT_NAME, 1, 10, ANIM_SPEED, false, true);
    private final Animation valdynPrepare = new Animation(Animation.DEFAULT_NAME, 11, 12, ANIM_SPEED, false, false);
    private final Animation valdynPrepareLoop = new Animation(Animation.DEFAULT_NAME, 13, 14, ANIM_SPEED, false, true);
    private final Animation valdynDragon = new Animation(Animation.DEFAULT_NAME, 15, 24, ANIM_SPEED, false, false);

    private final Animation dragonIdle = new Animation(Animation.DEFAULT_NAME, 1, 15, ANIM_SPEED, false, false);
    private final Animation dragonEat = new Animation(Animation.DEFAULT_NAME, 16, 18, ANIM_SPEED, false, true);
    private final Animation dragonBack = new Animation(Animation.DEFAULT_NAME, 1, 15, ANIM_SPEED, true, false);
    private final Animation dragonFly = new Animation(Animation.DEFAULT_NAME, 1, 20, ANIM_SPEED, true, true);

    private final Coord valdynCoord = new Coord(28.0, -78.0);
    private final Coord dragonCoord = new Coord(176.0, -44.0);
    private final Camera camera = new Camera();
    private final Context context;

    private int width;
    private int height;
    private int bandHeight;

    private double dragonGoDown;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Part3(Context context)
    {
        super();

        this.context = context;
    }

    /**
     * Load part.
     * 
     * @param narration The narration factory.
     * @param source The source resolution.
     */
    public void load(NarrationFactory narration, Consumer<Resolution> source)
    {
        dragon2.setFrameOffsets(DRAGON_OFFSET_X, DRAGON_OFFSET_Y);
        valdyn.play(valdynWalk);

        init(narration, source);
    }

    /**
     * Init resolution.
     * 
     * @param context The context reference.
     * @param source The source resolution.
     */
    public void initResolution(Context context, Consumer<Resolution> source)
    {
        source.accept(Util.getResolution(context, MIN_HEIGHT, MAX_WIDTH, MARGIN_WIDTH));
    }

    /**
     * Called when the resolution changed.
     * 
     * @param width The new screen width.
     * @param height The new screen height.
     */
    public void onResolutionChanged(int width, int height)
    {
        this.width = width;
        this.height = height;
        bandHeight = (int) Math.floor((height - MIN_HEIGHT) / 2.0);
        camera.setView(0, (height - scene.getHeight()) / 2, width, height, height);
    }

    @Override
    public void close()
    {
        valdyn.dispose();
        dragon1.dispose();
        dragon2.dispose();
        scene.dispose();
    }

    private void init(NarrationFactory n, Consumer<Resolution> source)
    {
        n.add(T_FADE_IN, () -> initResolution(context, source));
        n.add(T_FADE_IN, FadeSide.IN, FADE_SPEED);
        n.add(T_FADE_IN, T_VALDYN_RENDER, this::renderValdyn);
        n.add(T_FADE_IN, T_FADE_OUT + 3000, this::renderScene);
        n.add(T_FADE_IN, T_DRAGON_RENDER, this::renderDragon1);
        n.add(T_VALDYN_MOVE, T_VALDYN_DRAGON, this::updateValdynMove);
        n.add(T_VALDYN_RENDER, T_DRAGON_RENDER, this::renderValdyn);
        n.add(T_DRAGON_HEAD, T_VALDYN_DRAGON, this::updateDragonInit);
        n.add(T_CAMERA_MOVE, T_VALDYN_DRAGON, this::updateCameraMove);
        n.add(T_DRAGON_EAT, T_VALDYN_DRAGON, this::updateDragonEat);
        n.add(T_VALDYN_HAND, () -> valdyn.stop());
        n.add(T_DRAGON_BACK, T_VALDYN_DRAGON, this::updateDragonEatDone);
        n.add(T_VALDYN_DRAGON, T_FADE_OUT, this::updateValdynDragon);
        n.add(T_DRAGON_RENDER, T_FADE_OUT, this::renderDragon2);
        n.add(T_DRAGON_FLY, () -> dragon2.play(dragonFly));
        n.add(T_DRAGON_FLY, T_FADE_OUT, this::updateDragonMoveDown);
        n.add(T_FADE_OUT, FadeSide.OUT, FADE_SPEED);
    }

    /**
     * Update valdyn move right until dragon.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateValdynMove(double extrp)
    {
        valdyn.update(extrp);

        if (valdynCoord.getX() < VALDYN_X_MAX)
        {
            valdynCoord.translate(VALDYN_X_SPEED * extrp, 0.0);

            if (valdynCoord.getX() >= VALDYN_X_MAX)
            {
                valdynCoord.setX(VALDYN_X_MAX);
                valdyn.play(valdynPrepare);
            }
        }
        else if (valdyn.getAnimState() == AnimState.FINISHED)
        {
            valdyn.play(valdynPrepareLoop);
        }
    }

    /**
     * Update valdyn dragon animation.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateValdynDragon(double extrp)
    {
        valdyn.update(extrp);
        dragon1.update(extrp);

        if (valdyn.getAnimState() == AnimState.STOPPED)
        {
            valdyn.play(valdynDragon);
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

        final int maxX = MAX_WIDTH - width;
        if (camera.getX() > maxX)
        {
            camera.setLocation(maxX, camera.getY() - camera.getViewY());
        }
    }

    /**
     * Update dragon init time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateDragonInit(double extrp)
    {
        dragon1.update(extrp);

        if (dragon1.getAnimState() == AnimState.STOPPED)
        {
            dragon1.play(dragonIdle);
        }
        else if (dragon1.getAnimState() == AnimState.FINISHED)
        {
            dragon1.play(dragonEat);
        }
    }

    /**
     * Update dragon eat animation.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateDragonEat(double extrp)
    {
        dragon1.stop();
        dragon1.setFrame(dragonBack.lastFrame() + 1);
        dragon1.update(extrp);
    }

    /**
     * Update dragon eat animation.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateDragonEatDone(double extrp)
    {
        dragon1.stop();
        dragon1.play(dragonBack);
        dragon1.setFrame(dragonBack.lastFrame() + 1);
    }

    /**
     * Update dragon movement.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateDragonMoveDown(double extrp)
    {
        dragon2.update(extrp);

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
     * Render valdyn.
     * 
     * @param g The graphic output.
     */
    private void renderValdyn(Graphic g)
    {
        valdyn.setLocation(camera.getViewpointX(valdynCoord.getX()), camera.getViewpointY(valdynCoord.getY()) - height);
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
                            camera.getViewpointY(dragonCoord.getY()) - height);
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
                            camera.getViewpointY(dragonCoord.getY()) - height);
        dragon2.render(g);

        drawBand(g);
    }

    /**
     * Draw horizontal top and bottom black band.
     * 
     * @param g The graphic output.
     */
    private void drawBand(Graphic g)
    {
        g.clear(0, 0, width, bandHeight);
        g.clear(0, height - bandHeight, width, bandHeight);
    }

    private void renderScene(Graphic g)
    {
        g.clear(0, 0, width, height);

        scene.setLocation(camera.getViewpointX(0), camera.getViewpointY(0) - height);
        scene.render(g);
    }
}
