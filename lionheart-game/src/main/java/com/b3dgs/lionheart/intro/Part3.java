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
package com.b3dgs.lionheart.intro;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;

/**
 * Intro part 3 implementation.
 */
public final class Part3
{
    /** Scene. */
    private final Sprite scene;
    /** Scene. */
    private final SpriteAnimated valdyn;
    /** Dragon 1. */
    private final SpriteAnimated dragon1;
    /** Dragon 2. */
    private final SpriteAnimated dragon2;
    /** Valdyn walk. */
    private final Animation valdynWalk;
    /** Valdyn prepare. */
    private final Animation valdynPrepare;
    /** Valdyn prepare loop. */
    private final Animation valdynPrepareLoop;
    /** Valdyn dragon. */
    private final Animation valdynDragon;
    /** Dragon idle. */
    private final Animation dragonIdle;
    /** Dragon eat. */
    private final Animation dragonEat;
    /** Dragon back. */
    private final Animation dragonBack;
    /** Dragon back. */
    private final Animation dragonFly;
    /** Dragon location. */
    private final Coord valdynCoord;
    /** Dragon location. */
    private final Coord dragonCoord;
    /** Camera back. */
    private final Camera camera;
    /** Back alpha. */
    private double alphaBack;
    /** Valdyn state. */
    private int valdynState;
    /** Dragon state. */
    private int dragonState;
    /** Dragon go down. */
    private double dragonGoDown;

    /**
     * Constructor.
     */
    public Part3()
    {
        scene = Drawable.loadSprite(Medias.create("intro", "part3", "scene.png"));
        valdyn = Drawable.loadSpriteAnimated(Medias.create("intro", "part3", "valdyn.png"), 8, 3);
        dragon1 = Drawable.loadSpriteAnimated(Medias.create("intro", "part3", "dragon1.png"), 6, 3);
        dragon2 = Drawable.loadSpriteAnimated(Medias.create("intro", "part3", "dragon2.png"), 5, 4);
        valdynWalk = new Animation(Animation.DEFAULT_NAME, 1, 10, 0.2f, false, true);
        valdynPrepare = new Animation(Animation.DEFAULT_NAME, 11, 12, 0.2f, false, false);
        valdynPrepareLoop = new Animation(Animation.DEFAULT_NAME, 13, 14, 0.2f, false, true);
        valdynDragon = new Animation(Animation.DEFAULT_NAME, 15, 24, 0.2f, false, false);
        dragonIdle = new Animation(Animation.DEFAULT_NAME, 1, 15, 0.2f, false, false);
        dragonEat = new Animation(Animation.DEFAULT_NAME, 16, 18, 0.2f, false, true);
        dragonBack = new Animation(Animation.DEFAULT_NAME, 1, 15, 0.2f, true, false);
        dragonFly = new Animation(Animation.DEFAULT_NAME, 1, 20, 0.2f, true, true);
        camera = new Camera();
        camera.setView(0, 0, 370, 208, 208);
        valdynCoord = new Coord(28, -76);
        dragonCoord = new Coord(176, -44);
        dragon2.setFrameOffsets(3, -33);
    }

    /**
     * Load part.
     */
    public void load()
    {
        scene.load();
        scene.prepare();

        valdyn.load();
        valdyn.prepare();

        dragon1.load();
        dragon1.prepare();

        dragon2.load();
        dragon2.prepare();

        valdyn.play(valdynWalk);
    }

    /**
     * Update part.
     * 
     * @param seek The current seek.
     * @param extrp The extrapolation value.
     */
    public void update(long seek, double extrp)
    {
        valdyn.update(extrp);
        dragon1.update(extrp);
        dragon2.update(extrp);

        // Move valdyn
        if (seek > 95500 && valdynState == 0)
        {
            valdynCoord.translate(1.0, 0.0);
            if (valdynCoord.getX() > 224)
            {
                valdynCoord.setX(224);
                if (valdyn.getAnimState() == AnimState.PLAYING)
                {
                    valdyn.stop();
                    valdyn.play(valdynPrepare);
                    valdynState = 1;
                }
            }
        }
        if (valdynState == 1)
        {
            if (valdyn.getAnimState() == AnimState.FINISHED)
            {
                valdyn.stop();
                valdyn.play(valdynPrepareLoop);
                valdynState = 2;
            }
        }
        if (valdynState == 2 && seek > 99260)
        {
            valdyn.stop();
            valdynState = 3;
        }
        if (valdynState == 3 && seek > 99860)
        {
            valdyn.play(valdynDragon);
            valdynState = 4;
        }

        // Move camera
        if (seek > 96360 && seek < 98300)
        {
            camera.moveLocation(extrp, 1.0, 0.0);
            if (dragonState == 0)
            {
                dragonState = 1;
                dragon1.play(dragonIdle);
            }
            if (camera.getX() > 30)
            {
                camera.setLocation(30, camera.getY());
            }
        }
        if (dragonState == 1 && dragon1.getAnimState() == AnimState.FINISHED)
        {
            dragon1.play(dragonEat);
            dragonState = 2;
        }
        if (dragonState == 2 && seek > 98230)
        {
            dragonState = 3;
            dragon1.stop();
            dragon1.setFrame(dragonBack.getLast() + 1);
        }
        if (dragonState == 3 && seek > 99830)
        {
            dragon1.play(dragonBack);
            dragon1.setFrame(dragonBack.getLast() + 1);
            dragonState = 4;
        }
        if (dragonState == 4 && seek > 101200)
        {
            dragon2.play(dragonFly);
            dragonState = 5;
        }
        if (dragonState == 5)
        {
            if (dragon2.getFrame() > 10)
            {
                dragonGoDown += 0.02;
                if (dragonGoDown > 1.6)
                {
                    dragonGoDown = 1.6;
                }
                dragonCoord.translate(1.3, -1.2 + dragonGoDown);
            }
            if (dragon2.getAnimState() == AnimState.REVERSING && dragon2.getFrameAnim() == 18)
            {
                dragon2.stop();
                dragon2.setFrame(18);
            }
        }

        // First Fade in
        if (seek > 93660 && seek < 96000)
        {
            alphaBack += 5.0;
        }

        // First Fade out
        if (seek > 108500 && seek < 120000)
        {
            alphaBack -= 5.0;
        }
        alphaBack = UtilMath.clamp(alphaBack, 0.0, 255.0);
    }

    /**
     * Render part.
     * 
     * @param width The width.
     * @param height The height.
     * @param seek The current seek.
     * @param g The graphic output.
     */
    public void render(int width, int height, long seek, Graphic g)
    {
        g.clear(0, 0, width, height);

        if (seek > 93660 && seek < 95730)
        {
            valdyn.setLocation(camera.getViewpointX(valdynCoord.getX()),
                               camera.getViewpointY(valdynCoord.getY()) - height);
            valdyn.render(g);
        }

        scene.setLocation(camera.getViewpointX(0), camera.getViewpointY(0) - height);
        scene.render(g);

        // Render dragon
        if (seek > 93660 && seek < 101200)
        {
            dragon1.setLocation(camera.getViewpointX(dragonCoord.getX()),
                                camera.getViewpointY(dragonCoord.getY()) - height);
            dragon1.render(g);
        }
        else if (seek >= 101200 && seek < 107000)
        {
            dragon2.setLocation(camera.getViewpointX(dragonCoord.getX()),
                                camera.getViewpointY(dragonCoord.getY()) - height);
            dragon2.render(g);
        }

        // Render valdyn
        if (seek > 95730 && seek < 101200)
        {
            valdyn.setLocation(camera.getViewpointX(valdynCoord.getX()),
                               camera.getViewpointY(valdynCoord.getY()) - height);
            valdyn.render(g);
        }

        // Render fade in
        if (alphaBack < 255)
        {
            g.setColor(Intro.ALPHAS_BLACK[255 - (int) alphaBack]);
            g.drawRect(0, 0, width, height, true);
        }
    }
}
