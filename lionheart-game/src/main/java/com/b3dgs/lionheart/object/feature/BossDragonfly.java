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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionheart.LoadNextStage;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.MusicPlayer;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.object.EntityModel;

/**
 * Boss Dragonfly feature implementation.
 * <ol>
 * <li>Track player with head.</li>
 * <li>Fire laser.</li>
 * <li>Hurt on player neck hit.</li>
 * </ol>
 */
@FeatureInterface
public final class BossDragonfly extends FeatureModel implements Routine, Recyclable
{
    private static final int END_TICK = 500;
    private static final int APPROACH_DELAY_TICK = 40;
    private static final double SPEED = 13.0 / 31.0;
    private static final double SPEED_LEAVE = -2.5;
    private static final int EXPLODE_DELAY = 10;

    private final Tick tick = new Tick();

    private final Trackable target = services.get(Trackable.class);
    private final Spawner spawner = services.get(Spawner.class);
    private final Camera camera = services.get(Camera.class);
    private final MusicPlayer music = services.get(MusicPlayer.class);
    private final LoadNextStage stage = services.get(LoadNextStage.class);

    private Updatable updater;

    private Hurtable head;
    private Hurtable gobelin;
    private Stats stats;
    private int oldHealth;

    @FeatureGet private EntityModel model;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Hurtable hurtable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BossDragonfly(Services services, Setup setup)
    {
        super(services, setup);
    }

    /**
     * Spawn elements.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateSpawn(double extrp)
    {
        head = spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "BossHead.xml"),
                             transformable.getX() - 128,
                             transformable.getY() + 96)
                      .getFeature(Hurtable.class);

        gobelin = spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "BossGobelin.xml"),
                                transformable.getX() - 80,
                                transformable.getY() + 128)
                         .getFeature(Hurtable.class);

        stats = spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "BossNeck.xml"),
                              transformable.getX() - 96,
                              transformable.getY() + 128)
                       .getFeature(Stats.class);

        oldHealth = stats.getHealth();
        updater = this::updateApproach;
        tick.restart();
    }

    /**
     * Update approaching.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateApproach(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsed(APPROACH_DELAY_TICK))
        {
            updater = this::updateAwait;
        }
    }

    /**
     * Update leaving on hurt.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateLeave(double extrp)
    {
        if (camera.getX() < 719 * 16)
        {
            updater = this::updateApproach;
            tick.restart();
        }
        camera.moveLocation(extrp, SPEED_LEAVE, 0.0);
        target.moveLocationX(extrp, SPEED_LEAVE);
    }

    /**
     * Update await.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateAwait(double extrp)
    {
        if (camera.getX() < 741 * 16)
        {
            camera.moveLocation(extrp, SPEED, 0.0);
            target.moveLocationX(extrp, SPEED);
        }

        if (oldHealth != stats.getHealth())
        {
            head.hurt();
            gobelin.hurt();
            hurtable.hurt();
        }
        oldHealth = stats.getHealth();

        if (stats.getHealth() == 0)
        {
            updater = this::updateExplode;
            head.kill(true);
            music.playMusic(Music.BOSS_WIN);
            model.getConfig().getNext().ifPresent(next -> stage.loadNextStage(next, END_TICK));
            tick.restart();
        }
        else if (hurtable.isHurting())
        {
            updater = this::updateLeave;
        }

        collidable.setEnabled(!hurtable.isHurting());
    }

    /**
     * Update death.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateExplode(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsed(EXPLODE_DELAY))
        {
            spawnExplode();
            tick.restart();
        }
    }

    /**
     * Spawn explode.
     */
    private void spawnExplode()
    {
        final int width = transformable.getWidth();
        final int height = transformable.getHeight();

        spawner.spawn(Medias.create(Folder.EFFECT, "dragonfly", "ExplodeBig.xml"),
                      transformable.getX() + UtilRandom.getRandomInteger(width) - width / 2,
                      transformable.getY() + UtilRandom.getRandomInteger(height));
    }

    @Override
    public void update(double extrp)
    {
        updater.update(extrp);
    }

    @Override
    public void recycle()
    {
        updater = this::updateSpawn;
        tick.stop();
    }
}
