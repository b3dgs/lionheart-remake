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
import com.b3dgs.lionengine.Engine;
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
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.Time;
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

    private static SpriteAnimated loadSpriteAnimated(String name, int fh, int fv)
    {
        return Drawable.loadSpriteAnimated(Medias.create(Folder.INTRO, "part3", name), fh, fv);
    }

    private static Animation createAnimation(int start, int end, boolean reverse, boolean repeat)
    {
        return new Animation(Animation.DEFAULT_NAME, start, end, 0.2, reverse, repeat);
    }

    private final SpriteAnimated valdyn = loadSpriteAnimated("valdyn.png", 8, 3);
    private final SpriteAnimated dragon1 = loadSpriteAnimated("dragon1.png", 6, 3);
    private final SpriteAnimated dragon2 = loadSpriteAnimated("dragon2.png", 5, 4);
    private final Sprite scene = Drawable.loadSprite(Medias.create(Folder.INTRO, "part3", "scene.png"));

    private final Animation valdynWalk = createAnimation(1, 10, false, true);
    private final Animation valdynPrepare = createAnimation(11, 12, false, false);
    private final Animation valdynPrepareLoop = createAnimation(13, 14, false, true);
    private final Animation valdynDragon = createAnimation(15, 24, false, false);

    private final Animation dragonIdle = createAnimation(1, 15, false, false);
    private final Animation dragonEat = createAnimation(16, 18, false, true);
    private final Animation dragonBack = createAnimation(1, 15, true, false);
    private final Animation dragonFly = createAnimation(1, 20, true, true);

    private final Coord valdynCoord = new Coord(28, -76);
    private final Coord dragonCoord = new Coord(176, -44);

    private final Camera camera = new Camera();
    private final DeviceController device;
    private final AppInfo info;
    private final Time time;
    private final Audio audio;

    private double alphaBack;
    private int valdynState;
    private int dragonState;
    private double dragonGoDown;
    private boolean skip;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param time The time reference.
     * @param audio The audio reference.
     */
    public Part3(Context context, Time time, Audio audio)
    {
        super(context, Util.getResolution(context, MIN_HEIGHT, MAX_WIDTH, MARGIN_WIDTH));

        this.time = time;
        this.audio = audio;

        final Services services = new Services();
        services.add(context);
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_CUSTOM)));
        info = new AppInfo(this::getFps, services);

        load(Part4.class, time, audio);

        dragon2.setFrameOffsets(3, -33);

        camera.setView(0, (getHeight() - scene.getHeight()) / 2, getWidth(), getHeight(), getHeight());

        setSystemCursorVisible(false);
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

        valdyn.load();
        valdyn.prepare();
        valdyn.play(valdynWalk);
    }

    @Override
    public void update(double extrp)
    {
        time.update(extrp);

        valdyn.update(extrp);
        dragon1.update(extrp);
        dragon2.update(extrp);

        // Move valdyn
        if (time.isAfter(95500) && valdynState == 0)
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
        if (valdynState == 2 && time.isAfter(99260))
        {
            valdyn.stop();
            valdynState = 3;
        }
        if (valdynState == 3 && time.isAfter(99860))
        {
            valdyn.play(valdynDragon);
            valdynState = 4;
        }

        // Move camera
        if (time.isBetween(96360, 98300))
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
        if (dragonState == 2 && time.isAfter(98230))
        {
            dragonState = 3;
            dragon1.stop();
            dragon1.setFrame(dragonBack.getLast() + 1);
        }
        if (dragonState == 3 && time.isAfter(99830))
        {
            dragon1.play(dragonBack);
            dragon1.setFrame(dragonBack.getLast() + 1);
            dragonState = 4;
        }
        if (dragonState == 4 && time.isAfter(101200))
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
        if (time.isBetween(93660, 96000) && !skip)
        {
            alphaBack += 5.0;
        }

        if (!skip)
        {
            skip = device.isFiredOnce(DeviceMapping.CTRL_RIGHT);
        }

        // First Fade out
        if (time.isBetween(108500, 110000) || skip)
        {
            alphaBack -= 5.0;
        }
        alphaBack = UtilMath.clamp(alphaBack, 0.0, 255.0);

        if (time.isAfter(110000) || skip)
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

        if (time.isBetween(93660, 95730))
        {
            valdyn.setLocation(camera.getViewpointX(valdynCoord.getX()),
                               camera.getViewpointY(valdynCoord.getY()) - getHeight());
            valdyn.render(g);
        }

        scene.setLocation(camera.getViewpointX(0), camera.getViewpointY(0) - getHeight());
        scene.render(g);

        // Render dragon
        if (time.isBetween(93660, 101200))
        {
            dragon1.setLocation(camera.getViewpointX(dragonCoord.getX()),
                                camera.getViewpointY(dragonCoord.getY()) - getHeight());
            dragon1.render(g);
        }
        else if (time.isBetween(101200, 107000))
        {
            dragon2.setLocation(camera.getViewpointX(dragonCoord.getX()),
                                camera.getViewpointY(dragonCoord.getY()) - getHeight());
            dragon2.render(g);
        }

        // Render valdyn
        if (time.isBetween(95730, 101200))
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

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        if (!hasNextSequence)
        {
            audio.stop();
            Engine.terminate();
        }
    }
}
