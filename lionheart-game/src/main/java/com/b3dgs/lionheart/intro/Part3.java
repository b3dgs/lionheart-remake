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
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.menu.Menu;

/**
 * Intro part 3 implementation.
 */
public final class Part3 extends Sequence
{
    private static final int MIN_HEIGHT = 208;
    private static final int MAX_WIDTH = 400;
    private static final int MARGIN_WIDTH = 80;

    /** Scene. */
    private final Sprite scene = Drawable.loadSprite(Medias.create(Folder.INTRO, "part3", "scene.png"));
    /** Scene. */
    private final SpriteAnimated valdyn = Drawable.loadSpriteAnimated(Medias.create(Folder.INTRO,
                                                                                    "part3",
                                                                                    "valdyn.png"),
                                                                      8,
                                                                      3);
    /** Dragon 1. */
    private final SpriteAnimated dragon1 = Drawable.loadSpriteAnimated(Medias.create(Folder.INTRO,
                                                                                     "part3",
                                                                                     "dragon1.png"),
                                                                       6,
                                                                       3);
    /** Dragon 2. */
    private final SpriteAnimated dragon2 = Drawable.loadSpriteAnimated(Medias.create(Folder.INTRO,
                                                                                     "part3",
                                                                                     "dragon2.png"),
                                                                       5,
                                                                       4);
    /** Valdyn walk. */
    private final Animation valdynWalk = new Animation(Animation.DEFAULT_NAME, 1, 10, 0.2f, false, true);
    /** Valdyn prepare. */
    private final Animation valdynPrepare = new Animation(Animation.DEFAULT_NAME, 11, 12, 0.2f, false, false);
    /** Valdyn prepare loop. */
    private final Animation valdynPrepareLoop = new Animation(Animation.DEFAULT_NAME, 13, 14, 0.2f, false, true);
    /** Valdyn dragon. */
    private final Animation valdynDragon = new Animation(Animation.DEFAULT_NAME, 15, 24, 0.2f, false, false);
    /** Dragon idle. */
    private final Animation dragonIdle = new Animation(Animation.DEFAULT_NAME, 1, 15, 0.2f, false, false);
    /** Dragon eat. */
    private final Animation dragonEat = new Animation(Animation.DEFAULT_NAME, 16, 18, 0.2f, false, true);
    /** Dragon back. */
    private final Animation dragonBack = new Animation(Animation.DEFAULT_NAME, 1, 15, 0.2f, true, false);
    /** Dragon back. */
    private final Animation dragonFly = new Animation(Animation.DEFAULT_NAME, 1, 20, 0.2f, true, true);
    /** Dragon location. */
    private final Coord valdynCoord = new Coord(28, -76);
    /** Dragon location. */
    private final Coord dragonCoord = new Coord(176, -44);
    /** Camera back. */
    private final Camera camera = new Camera();
    /** Input device reference. */
    private final DeviceController device;
    /** App info. */
    private final AppInfo info;
    /** Audio. */
    private final Audio audio;
    /** Back alpha. */
    private double alphaBack;
    /** Valdyn state. */
    private int valdynState;
    /** Dragon state. */
    private int dragonState;
    /** Dragon go down. */
    private double dragonGoDown;
    /** Current seek. */
    private long seek;
    /** Skip intro. */
    private boolean skip;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param audio The audio reference.
     */
    public Part3(Context context, Audio audio)
    {
        super(context, Util.getResolution(context, MIN_HEIGHT, MAX_WIDTH, MARGIN_WIDTH));

        this.audio = audio;
        dragon2.setFrameOffsets(3, -33);

        final Services services = new Services();
        services.add(context);
        services.add(new SourceResolutionProvider()
        {
            @Override
            public int getWidth()
            {
                return Part3.this.getWidth();
            }

            @Override
            public int getHeight()
            {
                return Part3.this.getHeight();
            }

            @Override
            public int getRate()
            {
                return Part3.this.getRate();
            }
        });
        device = services.add(DeviceControllerConfig.create(services, Medias.create("input.xml")));
        info = new AppInfo(this::getFps, services);

        load(Part4.class, audio);
    }

    @Override
    public void load()
    {
        scene.load();
        scene.prepare();

        camera.setView(0, (getHeight() - scene.getHeight()) / 2, getWidth(), getHeight(), getHeight());

        valdyn.load();
        valdyn.prepare();

        dragon1.load();
        dragon1.prepare();

        dragon2.load();
        dragon2.prepare();

        valdyn.play(valdynWalk);
    }

    @Override
    public void update(double extrp)
    {
        seek = audio.getTicks();

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
            final int maxX = MAX_WIDTH - getWidth();
            if (camera.getX() > maxX)
            {
                camera.setLocation(maxX, camera.getY() - camera.getViewY());
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
                dragonGoDown -= 0.03;
                if (dragonGoDown < -1.6)
                {
                    dragonGoDown = -1.6;
                }
                dragonCoord.translate(1.3, 1.4 + dragonGoDown);
            }
            if (dragon2.getAnimState() == AnimState.REVERSING && dragon2.getFrameAnim() == 18)
            {
                dragon2.stop();
                dragon2.setFrame(18);
            }
        }

        // First Fade in
        if (seek > 93660 && seek < 96000 && !skip)
        {
            alphaBack += 5.0;
        }

        if (!skip)
        {
            skip = device.isFiredOnce(DeviceMapping.CTRL_RIGHT);
        }

        // First Fade out
        if (seek > 108500 && seek < 110000 || skip)
        {
            alphaBack -= 5.0;
        }
        alphaBack = UtilMath.clamp(alphaBack, 0.0, 255.0);

        if (seek >= 110000 || skip)
        {
            if (skip)
            {
                audio.stop();
                end(Menu.class);
            }
            else
            {
                end();
            }
        }

        info.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        if (seek > 93660 && seek < 95730)
        {
            valdyn.setLocation(camera.getViewpointX(valdynCoord.getX()),
                               camera.getViewpointY(valdynCoord.getY()) - getHeight());
            valdyn.render(g);
        }

        scene.setLocation(camera.getViewpointX(0), camera.getViewpointY(0) - getHeight());
        scene.render(g);

        // Render dragon
        if (seek > 93660 && seek < 101200)
        {
            dragon1.setLocation(camera.getViewpointX(dragonCoord.getX()),
                                camera.getViewpointY(dragonCoord.getY()) - getHeight());
            dragon1.render(g);
        }
        else if (seek >= 101200 && seek < 107000)
        {
            dragon2.setLocation(camera.getViewpointX(dragonCoord.getX()),
                                camera.getViewpointY(dragonCoord.getY()) - getHeight());
            dragon2.render(g);
        }

        // Render valdyn
        if (seek > 95730 && seek < 101200)
        {
            valdyn.setLocation(camera.getViewpointX(valdynCoord.getX()),
                               camera.getViewpointY(valdynCoord.getY()) - getHeight());
            valdyn.render(g);
        }

        // Render fade in
        if (alphaBack < 255)
        {
            g.setColor(Constant.ALPHAS_BLACK[255 - (int) alphaBack]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
        }

        final int bandHeight = (int) (Math.floor(getHeight() - MIN_HEIGHT) / 2.0);
        g.clear(0, 0, getWidth(), bandHeight);
        g.clear(0, getHeight() - bandHeight, getWidth(), bandHeight);

        info.render(g);
    }
}
