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
package com.b3dgs.lionheart.narration.extro;

import java.io.Closeable;

import com.b3dgs.lionengine.Animation;
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
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.MapTileHelper;
import com.b3dgs.lionheart.CheatsProvider;
import com.b3dgs.lionheart.CheckpointHandler;
import com.b3dgs.lionheart.FadeSide;
import com.b3dgs.lionheart.GameConfig;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.narration.NarrationFactory;

/**
 * Extro part 1 implementation.
 */
// CHECKSTYLE IGNORE LINE: FanOutComplexity|DataAbstractionCoupling
public final class Part1 implements Closeable
{
    private static final int SPAWN_EXPLODE_DELAY_MS = 500;
    private static final int SPAWN_EXPLODE_MEDIUM_DELAY_MS = 80;
    private static final int SPAWN_EXPLODE_FAST_DELAY_MS = 15;
    private static final int FADE_SPEED = 5;

    private static final String PART1_FOLDER = "part1";
    private static final String EXPLODE_LITTLE = "ExplodeLittle.xml";
    private static final String EXPLODE_BIG = "ExplodeBig.xml";

    private static final int VALDYN_X = 100;
    private static final int VALDYN_Y = 65;
    private static final int VALDYN_Z = 100;
    private static final int VALDYN_X_MAX = 140;
    private static final double VALDYN_Z_SPEED = 0.00125;
    private static final double VALDYN_Z_ACC = -0.35;
    private static final double VALDYN_Z_ACC_MIN = -0.35;
    private static final double VALDYN_Z_ACC_MAX = -0.10;
    private static final int VALDYN_SCALE_MIN = 10;
    private static final int VALDYN_SCALE_MAX = 400;
    private static final double VALDYN_MOVE_X = -0.05;
    private static final double VALDYN_MOVE_Y = 0.01;
    private static final int VALDYN_MOVE_X_SCALE_DIVISOR = -500;

    private static final int CITADEL_X = 82;
    private static final int CITADEL_Y = 4;
    private static final double CITADEL_FALL_SPEED = 0.05;
    private static final double CITADEL_ACC_MAX = 3.5;

    private static final int EXPLODE_Y_OFFSET = 12;
    private static final double EXPLODE_Y_SCALE = 0.6;

    private static final int T_EXPLODE_LOT = 15_300;
    private static final int T_CITADEL_DESTROYED = 16_000;
    private static final int T_CITADEL_FALL = 16_600;
    private static final int T_EXPLODE_END = 18_000;
    private static final int T_FADE_OUT = 19_300;

    private final Sprite backcolor = Util.get(Folder.EXTRO, PART1_FOLDER, "backcolor.png");
    private final Sprite clouds = Util.get(Folder.EXTRO, PART1_FOLDER, "clouds.png");
    private final SpriteAnimated citadel = Util.get(2, 1, Folder.EXTRO, PART1_FOLDER, "citadel.png");
    private final SpriteAnimated valdyn = Util.get(4, 3, Folder.EXTRO, PART1_FOLDER, "valdyn.png");

    private final Services services = new Services();
    private final Factory factory = services.create(Factory.class);
    private final Handler handler = services.create(Handler.class);
    private final Spawner spawner = services.add((Spawner) (media, x, y) ->
    {
        final Featurable featurable = factory.create(media);
        featurable.getFeature(Transformable.class).teleport(x, y);
        handler.add(featurable);
        return featurable;
    });
    private final Tick tickExplode = new Tick();
    private final Animation valdynAnim = new Animation(Animation.DEFAULT_NAME, 1, 12, 0.25, false, true);

    private final int width;
    private final int height;
    private final int rate;
    private final int bandHeight;

    private double citadelY = CITADEL_Y;
    private double citadelYacc;
    private double valdynX = VALDYN_X;
    private double valdynY = VALDYN_Y;
    private double valdynZ = VALDYN_Z;
    private double valdynZacc = VALDYN_Z_ACC;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param width The screen width.
     * @param height The screen height.
     * @param rate The rate value.
     */
    public Part1(Context context, int width, int height, int rate)
    {
        super();

        this.width = width;
        this.height = height;
        this.rate = rate;

        bandHeight = (int) Math.floor((height - 208) / 2.0);

        services.add(new SourceResolutionDelegate(() -> width, () -> height, () -> rate));
        services.add(new GameConfig());
        final Camera camera = services.create(Camera.class);
        camera.setView(0, 0, width, height, height);

        services.add(context);
        services.add(new CameraTracker(services));
        services.add(new MapTileHelper(services));
        services.add((CheatsProvider) () -> false);
        services.add(new CheckpointHandler(services));

        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());
        handler.addListener(factory);
    }

    /**
     * Load part.
     * 
     * @param narration The narration factory.
     */
    public void load(NarrationFactory narration)
    {
        backcolor.setLocation(0, bandHeight);

        clouds.setLocation(0, bandHeight);

        citadel.setFrame(1);
        citadel.setLocation(CITADEL_X, bandHeight + citadelY);

        valdyn.play(valdynAnim);

        tickExplode.start();

        init(narration);
    }

    private void init(NarrationFactory n)
    {
        n.add(0, T_FADE_OUT + 3000, this::update, this::render);
        n.add(0, FadeSide.IN, FADE_SPEED);
        n.add(0, T_EXPLODE_LOT, this::updateExplodeFew);
        n.add(T_EXPLODE_LOT, T_CITADEL_FALL, this::updateExplodeLot);
        n.add(T_CITADEL_DESTROYED, () -> citadel.setFrame(2));
        n.add(T_CITADEL_FALL, T_FADE_OUT, this::updateCitadelFall);
        n.add(T_CITADEL_FALL, T_EXPLODE_END, this::updateExplodeMedium);
        n.add(T_FADE_OUT, FadeSide.OUT, FADE_SPEED);
    }

    /**
     * Spawn explode effect.
     * 
     * @param delay The next explode delay.
     */
    private void spawnExplode(int delay)
    {
        if (tickExplode.elapsedTime(rate, delay))
        {
            tickExplode.restart();

            final int width = citadel.getTileWidth();
            final int height = citadel.getTileHeight();
            final Media media = Medias.create(Folder.EXTRO,
                                              PART1_FOLDER,
                                              UtilRandom.getRandomBoolean() ? EXPLODE_LITTLE : EXPLODE_BIG);
            final double x = UtilRandom.getRandomInteger(width);
            final double y = (double) UtilRandom.getRandomInteger((int) (height * EXPLODE_Y_SCALE))
                             + height
                             + bandHeight;
            final int citadelOffsetY = citadel.getHeight() / 3;
            spawner.spawn(media, citadel.getX() + x, y - citadel.getY() + citadelOffsetY - EXPLODE_Y_OFFSET);
        }
    }

    /**
     * Update citadel fall routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateCitadelFall(double extrp)
    {
        citadel.setFrame(2);
        citadelY = UtilMath.clamp(citadelY + citadelYacc * extrp, 0.0, height + (double) citadel.getHeight());
        citadelYacc = UtilMath.clamp(citadelYacc + CITADEL_FALL_SPEED * extrp, 0.0, CITADEL_ACC_MAX);
        citadel.setLocation(CITADEL_X, bandHeight + citadelY);
    }

    /**
     * Update valdyn fly and scale.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateValdyn(double extrp)
    {
        if (valdynX < VALDYN_X_MAX)
        {
            valdyn.update(extrp);
            valdyn.setLocation(valdynX, valdynY);

            valdynZ += valdynZacc * extrp;
            valdynZacc = UtilMath.clamp(valdynZacc + VALDYN_Z_SPEED * extrp, VALDYN_Z_ACC_MIN, VALDYN_Z_ACC_MAX);

            final double scale = UtilMath.clamp(1000 / valdynZ, VALDYN_SCALE_MIN, VALDYN_SCALE_MAX);
            valdyn.stretch(scale, scale);

            valdynX += (VALDYN_MOVE_X - scale / VALDYN_MOVE_X_SCALE_DIVISOR) * extrp;
            valdynY += VALDYN_MOVE_Y * extrp;

            if (valdynX > VALDYN_X_MAX)
            {
                valdynX = VALDYN_X_MAX;
            }
        }
    }

    /**
     * Update explode few routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateExplodeFew(double extrp)
    {
        tickExplode.update(extrp);

        spawnExplode(SPAWN_EXPLODE_DELAY_MS);
        spawnExplode(SPAWN_EXPLODE_DELAY_MS);
        spawnExplode(SPAWN_EXPLODE_DELAY_MS);
        spawnExplode(SPAWN_EXPLODE_DELAY_MS);
    }

    /**
     * Update explode lot routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateExplodeLot(double extrp)
    {
        tickExplode.update(extrp);

        spawnExplode(SPAWN_EXPLODE_FAST_DELAY_MS);
    }

    /**
     * Update explode medium routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateExplodeMedium(double extrp)
    {
        tickExplode.update(extrp);

        spawnExplode(SPAWN_EXPLODE_MEDIUM_DELAY_MS);
        spawnExplode(SPAWN_EXPLODE_MEDIUM_DELAY_MS);
        spawnExplode(SPAWN_EXPLODE_MEDIUM_DELAY_MS);
        spawnExplode(SPAWN_EXPLODE_MEDIUM_DELAY_MS);
        spawnExplode(SPAWN_EXPLODE_MEDIUM_DELAY_MS);
        spawnExplode(SPAWN_EXPLODE_MEDIUM_DELAY_MS);
    }

    /**
     * Draw band effect.
     * 
     * @param g The graphic output.
     */
    private void drawBand(Graphic g)
    {
        g.clear(0, 0, width, bandHeight);
        g.clear(0, height - bandHeight, width, bandHeight);
    }

    private void update(double extrp)
    {
        handler.update(extrp);
        updateValdyn(extrp);
    }

    private void render(Graphic g)
    {
        backcolor.render(g);
        clouds.render(g);
        citadel.render(g);
        handler.render(g);
        if (valdynX < VALDYN_X_MAX)
        {
            valdyn.render(g);
        }

        drawBand(g);
    }

    @Override
    public void close()
    {
        backcolor.dispose();
        clouds.dispose();
        citadel.dispose();
        valdyn.dispose();
    }
}
