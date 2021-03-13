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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.audio.Audio;
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
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.helper.MapTileHelper;
import com.b3dgs.lionheart.CheckpointHandler;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.intro.Intro;

/**
 * Extro part 1 implementation.
 */
public final class Part1 extends Sequence
{
    private static final int ALPHA_SPEED = 3;
    private static final int SPAWN_EXPLODE_DELAY = 35;
    private static final int SPAWN_EXPLODE_FAST_DELAY = 1;
    private static final double CITADEL_FALL_SPEED = 0.04;

    private final Sprite backcolor = Drawable.loadSprite(Medias.create(Folder.EXTRO, "part1", "backcolor.png"));
    private final Sprite clouds = Drawable.loadSprite(Medias.create(Folder.EXTRO, "part1", "clouds.png"));
    private final SpriteAnimated citadel = Drawable.loadSpriteAnimated(Medias.create(Folder.EXTRO,
                                                                                     "part1",
                                                                                     "citadel.png"),
                                                                       2,
                                                                       1);

    private final SpriteAnimated valdyn = Drawable.loadSpriteAnimated(Medias.create(Folder.EXTRO,
                                                                                    "part1",
                                                                                    "valdyn.png"),
                                                                      4,
                                                                      3);

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
    private final Tick tickExplode = new Tick();
    private final Audio audio;

    private int alpha;
    private double citadelY;
    private double citadelYacc;
    private double valdynX;
    private double valdynY;
    private double valdynZ;
    private double valdynZacc;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param audio The audio reference.
     */
    public Part1(Context context, Audio audio)
    {
        super(context, Constant.NATIVE_RESOLUTION);

        this.audio = audio;

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
     * 
     * @param delay The next explode delay.
     */
    private void spawnExplode(int delay)
    {
        if (tickExplode.elapsed(delay))
        {
            tickExplode.restart();
            final int width = citadel.getTileWidth();
            final int height = citadel.getTileHeight();
            spawner.spawn(Medias.create(Folder.EXTRO,
                                        "part1",
                                        UtilRandom.getRandomBoolean() ? "ExplodeLittle.xml" : "ExplodeBig.xml"),
                          citadel.getX() + UtilRandom.getRandomInteger(width),
                          citadel.getY() + UtilRandom.getRandomInteger((int) (height * 0.75)) + height);
        }
    }

    @Override
    public void load()
    {
        backcolor.load();
        backcolor.prepare();

        clouds.load();
        clouds.prepare();

        citadel.load();
        citadel.prepare();
        citadel.setFrame(1);

        valdyn.load();
        valdyn.prepare();
        valdyn.play(new Animation(Animation.DEFAULT_NAME, 1, 12, 0.2, false, true));
        valdynX = 100.0;
        valdynY = 50.0;
        valdynZ = 100.0;
        valdynZacc = -0.3;

        tickExplode.start();

        load(Part2.class, audio);
        tick.start();
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);

        handler.update(extrp);
        tickExplode.update(extrp);

        if (valdynX < 220)
        {
            valdyn.update(extrp);
            valdyn.setLocation(valdynX, valdynY);

            valdynZ += valdynZacc;
            valdynZacc = UtilMath.clamp(valdynZacc + 0.0011, -0.3, -0.09);
            final double z = UtilMath.clamp(1000 / valdynZ, 10, 400);
            valdyn.stretch(z, z);

            valdynX += 0.01 + z / 350.0;
            valdynY += 0.01;
        }
        if (tick.elapsed() < 110 && alpha < 255)
        {
            alpha = UtilMath.clamp(alpha + ALPHA_SPEED, 0, 255);
        }
        else if (tick.elapsed() > 1150 && alpha > 0)
        {
            alpha = UtilMath.clamp(alpha - ALPHA_SPEED, 0, 255);
        }
        if (tick.elapsed() < 900)
        {
            spawnExplode(SPAWN_EXPLODE_DELAY);
            spawnExplode(SPAWN_EXPLODE_DELAY);
            spawnExplode(SPAWN_EXPLODE_DELAY);
            spawnExplode(SPAWN_EXPLODE_DELAY);
        }
        else if (tick.elapsed() < 950)
        {
            spawnExplode(SPAWN_EXPLODE_FAST_DELAY);
        }
        if (tick.elapsed() > 950)
        {
            citadel.setFrame(2);
            citadelY = UtilMath.clamp(citadelY + citadelYacc, 0.0, 500);
            citadelYacc = UtilMath.clamp(citadelYacc + CITADEL_FALL_SPEED, 0.0, 3.0);
        }
        citadel.setLocation(82, 4 + citadelY);

        if (tick.elapsed() > 1370)
        {
            end();
        }
    }

    @Override
    public void render(Graphic g)
    {
        backcolor.render(g);
        clouds.render(g);
        citadel.render(g);
        handler.render(g);
        if (valdynX < 220)
        {
            valdyn.render(g);
        }

        if (alpha < 255)
        {
            g.setColor(Intro.ALPHAS_BLACK[255 - alpha]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
        }
    }
}
