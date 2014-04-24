/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionheart.intro;

import com.b3dgs.lionengine.UtilityMath;
import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.geom.Geom;

/**
 * Intro part 3 implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
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
    private final CameraGame camera;
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
        scene = Drawable.loadSprite(Core.MEDIA.create("intro", "part3", "scene.png"));
        valdyn = Drawable.loadSpriteAnimated(Core.MEDIA.create("intro", "part3", "valdyn.png"), 8, 3);
        dragon1 = Drawable.loadSpriteAnimated(Core.MEDIA.create("intro", "part3", "dragon1.png"), 6, 3);
        dragon2 = Drawable.loadSpriteAnimated(Core.MEDIA.create("intro", "part3", "dragon2.png"), 5, 4);
        valdynWalk = Anim.createAnimation(1, 10, 0.2f, false, true);
        valdynPrepare = Anim.createAnimation(11, 12, 0.2f, false, false);
        valdynPrepareLoop = Anim.createAnimation(13, 14, 0.1f, false, true);
        valdynDragon = Anim.createAnimation(15, 24, 0.2f, false, false);
        dragonIdle = Anim.createAnimation(1, 15, 0.2f, false, false);
        dragonEat = Anim.createAnimation(16, 18, 0.2f, false, true);
        dragonBack = Anim.createAnimation(1, 15, 0.2f, true, false);
        dragonFly = Anim.createAnimation(1, 20, 0.2f, true, true);
        camera = new CameraGame();
        valdynCoord = Geom.createCoord(28, 90);
        dragonCoord = Geom.createCoord(89, 23);
    }

    /**
     * Load part.
     */
    public void load()
    {
        scene.load(false);
        valdyn.load(false);
        dragon1.load(false);
        dragon2.load(false);
        valdyn.play(valdynWalk);
    }

    /**
     * Update part.
     * 
     * @param seek The current seek.
     * @param extrp The extrapolation value.
     */
    public void update(int seek, double extrp)
    {
        valdyn.updateAnimation(extrp);
        dragon1.updateAnimation(extrp);
        dragon2.updateAnimation(extrp);

        // Move valdyn
        if (seek > 95400 && valdynState == 0)
        {
            valdynCoord.translate(1.2, 0.0);
            if (valdynCoord.getX() > 224)
            {
                valdynCoord.setX(224);
                if (valdyn.getAnimState() == AnimState.PLAYING)
                {
                    valdyn.stopAnimation();
                    valdyn.play(valdynPrepare);
                    valdynState = 1;
                }
            }
        }
        if (valdynState == 1)
        {
            if (valdyn.getAnimState() == AnimState.FINISHED)
            {
                valdyn.stopAnimation();
                valdyn.play(valdynPrepareLoop);
                valdynState = 2;
            }
        }
        if (valdynState == 2 && seek > 98900)
        {
            valdyn.stopAnimation();
        }
        if (valdynState == 2 && seek > 99260)
        {
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
            if (camera.getLocationX() > 80)
            {
                camera.teleportX(80);
            }
        }
        if (dragonState == 0 && seek > 95750 && seek < 97250)
        {
            dragonState = 1;
            dragon1.play(dragonIdle);
        }
        if (dragonState == 1 && dragon1.getAnimState() == AnimState.FINISHED)
        {
            dragon1.play(dragonEat);
            dragonState = 2;
        }
        if (dragonState == 2 && seek > 97720)
        {
            dragonState = 3;
            dragon1.stopAnimation();
            dragon1.setFrame(dragonBack.getLast() + 1);
        }
        if (dragonState == 3 && seek > 99830)
        {
            dragon1.play(dragonBack);
            dragon1.setFrame(dragonBack.getLast() + 1);
            dragonState = 4;
        }
        if (dragonState == 4 && seek > 101600)
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
                dragon2.stopAnimation();
                dragon2.setFrame(18);
            }
        }

        // First Fade in
        if (seek > 93300 && seek < 96000)
        {
            alphaBack += 10.0;
        }

        // First Fade out
        if (seek > 108000 && seek < 120000)
        {
            alphaBack -= 10.0;
        }
        alphaBack = UtilityMath.fixBetween(alphaBack, 0.0, 255.0);
    }

    /**
     * Render part.
     * 
     * @param width The width.
     * @param height The height.
     * @param seek The current seek.
     * @param g The graphic output.
     */
    public void render(int width, int height, int seek, Graphic g)
    {
        g.clear(0, 0, width, height);

        if (seek > 93300 && seek < 95730)
        {
            valdyn.render(g, camera.getViewpointX((int) valdynCoord.getX()),
                    camera.getViewpointY((int) -valdynCoord.getY()));
        }

        scene.render(g, camera.getViewpointX(0), camera.getViewpointY(0) + height / 2 - scene.getHeight() / 2);

        // Render dragon
        if (seek > 93300 && seek < 101600)
        {
            dragon1.render(g, camera.getViewpointX(172), camera.getViewpointY(-56));
        }
        else if (seek >= 101600 && seek < 107400)
        {
            dragon2.render(g, (int) dragonCoord.getX(), (int) dragonCoord.getY());
        }

        // Render valdyn
        if (seek > 95730 && seek < 101600)
        {
            valdyn.render(g, camera.getViewpointX((int) valdynCoord.getX()),
                    camera.getViewpointY((int) -valdynCoord.getY()));
        }

        // Render fade in
        if (alphaBack < 255)
        {
            g.setColor(Intro.ALPHAS_BLACK[255 - (int) alphaBack]);
            g.drawRect(0, 0, width, height, true);
        }
    }
}
