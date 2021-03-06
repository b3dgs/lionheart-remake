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
package com.b3dgs.lionheart.extro;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.CameraTracker;
import com.b3dgs.lionengine.game.feature.ComponentDisplayable;
import com.b3dgs.lionengine.game.feature.ComponentRefreshable;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.helper.MapTileHelper;
import com.b3dgs.lionheart.CheckpointHandler;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.intro.Intro;

/**
 * Extro part 1 implementation.
 */
public final class Part1
{
    private static final int ALPHA_SPEED = 3;
    private static final int SPAWN_EXPLODE_DELAY = 40;
    private static final int SPAWN_EXPLODE_FAST_DELAY = 1;
    private static final double CITADEL_FALL_SPEED = 0.05;

    private final Sprite backcolor = Drawable.loadSprite(Medias.create(Folder.EXTRO, "backcolor.png"));
    private final Sprite clouds = Drawable.loadSprite(Medias.create(Folder.EXTRO, "clouds.png"));
    private final SpriteAnimated citadel = Drawable.loadSpriteAnimated(Medias.create(Folder.EXTRO, "citadel.png"),
                                                                       2,
                                                                       1);
    private final Services services = new Services();
    private final Factory factory = services.create(Factory.class);
    private final Handler handler = services.create(Handler.class);
    private final Spawner spawner = services.add(new Spawner()
    {
        @Override
        public Featurable spawn(Media media, double x, double y)
        {
            final Featurable featurable = factory.create(media);
            featurable.getFeature(Transformable.class).teleport(x, y);
            handler.add(featurable);
            return featurable;
        }
    });
    private final Tick tick = new Tick();

    private int alpha;
    private double citadelY;
    private double citadelYacc;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Part1(Context context)
    {
        super();

        final SourceResolutionProvider source = services.add(new SourceResolutionProvider()
        {
            @Override
            public int getWidth()
            {
                return Constant.NATIVE_RESOLUTION.getWidth();
            }

            @Override
            public int getHeight()
            {
                return Constant.NATIVE_RESOLUTION.getHeight();
            }

            @Override
            public int getRate()
            {
                return Constant.NATIVE_RESOLUTION.getRate();
            }
        });
        final Camera camera = services.create(Camera.class);
        camera.setView(0, 0, source.getWidth(), source.getHeight(), source.getHeight());

        services.add(new CameraTracker(services));
        services.add(new MapTileHelper(services));
        services.add(new CheckpointHandler(services));

        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());
        handler.addListener(factory);
    }

    /**
     * Spawn explode effect.
     */
    private void spawnExplode(int delay)
    {
        if (tick.elapsed(delay))
        {
            tick.restart();
            final int width = citadel.getTileWidth();
            final int height = citadel.getTileHeight();
            spawner.spawn(Medias.create(Folder.EXTRO,
                                        UtilRandom.getRandomBoolean() ? "ExplodeLittle.xml" : "ExplodeBig.xml"),
                          citadel.getX() + UtilRandom.getRandomInteger(width),
                          citadel.getY() + UtilRandom.getRandomInteger((int) (height * 0.75)) + height);
        }
    }

    /**
     * Load part.
     */
    public void load()
    {
        backcolor.load();
        backcolor.prepare();

        clouds.load();
        clouds.prepare();

        citadel.load();
        citadel.prepare();
        citadel.setFrame(1);

        tick.start();
    }

    /**
     * Update part.
     * 
     * @param seek The current seek.
     * @param extrp The extrapolation value.
     */
    public void update(long seek, double extrp)
    {
        handler.update(extrp);
        tick.update(extrp);

        if (seek < 2000 && alpha < 255)
        {
            alpha = UtilMath.clamp(alpha + ALPHA_SPEED, 0, 255);
        }
        else if (seek > 20000 && alpha > 0)
        {
            alpha = UtilMath.clamp(alpha - ALPHA_SPEED, 0, 255);
        }
        if (seek < 16000)
        {
            spawnExplode(SPAWN_EXPLODE_DELAY);
            spawnExplode(SPAWN_EXPLODE_DELAY);
            spawnExplode(SPAWN_EXPLODE_DELAY);
            spawnExplode(SPAWN_EXPLODE_DELAY);
        }
        else if (seek < 16900)
        {
            spawnExplode(SPAWN_EXPLODE_FAST_DELAY);
        }
        if (seek > 17000)
        {
            citadel.setFrame(2);
            citadelY = UtilMath.clamp(citadelY + citadelYacc, 0.0, 500);
            citadelYacc = UtilMath.clamp(citadelYacc + CITADEL_FALL_SPEED, 0.0, 3.0);
        }
        citadel.setLocation(82, 4 + citadelY);
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
        backcolor.render(g);
        clouds.render(g);
        citadel.render(g);
        handler.render(g);

        if (alpha < 255)
        {
            g.setColor(Intro.ALPHAS_BLACK[255 - alpha]);
            g.drawRect(0, 0, width, height, true);
        }
    }
}
