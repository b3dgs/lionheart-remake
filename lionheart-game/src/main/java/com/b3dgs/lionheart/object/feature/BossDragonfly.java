/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
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
public final class BossDragonfly extends FeatureModel implements RoutineUpdate, Recyclable
{
    private static final int END_DELAY_MS = 8000;
    private static final int APPROACH_DELAY_MS = 500;
    private static final double SPEED = 0.5;
    private static final double SPEED_LEAVE = -2.0;
    private static final int EXPLODE_DELAY_MS = 160;

    private final Tick tick = new Tick();

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Trackable target = services.get(Trackable.class);
    private final Spawner spawner = services.get(Spawner.class);
    private final Camera camera = services.get(Camera.class);
    private final MusicPlayer music = services.get(MusicPlayer.class);
    private final LoadNextStage stage = services.get(LoadNextStage.class);

    private final EntityModel model;
    private final Transformable transformable;
    private final Collidable collidable;
    private final Hurtable hurtable;

    private Updatable updater;
    private Hurtable head;
    private Hurtable gobelin;

    private boolean leave;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param model The model feature.
     * @param transformable The transformable feature.
     * @param collidable The collidable feature.
     * @param hurtable The hurtable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public BossDragonfly(Services services,
                         Setup setup,
                         EntityModel model,
                         Transformable transformable,
                         Collidable collidable,
                         Hurtable hurtable)
    {
        super(services, setup);

        this.model = model;
        this.transformable = transformable;
        this.collidable = collidable;
        this.hurtable = hurtable;
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

        final Stats stats = spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "BossNeck.xml"),
                                          transformable.getX() - 96,
                                          transformable.getY() + 128)
                                   .getFeature(Stats.class);

        stats.addListener(new StatsHurtListener()
        {
            @Override
            public void notifyHurt(int damages)
            {
                head.hurt();
                gobelin.hurt();
                hurtable.hurt();
            }

            @Override
            public void notifyDead()
            {
                updater = BossDragonfly.this::updateExplode;
                head.kill(true);
                music.playMusic(Music.BOSS_WIN);
                model.getConfig().getNext().ifPresent(next -> stage.loadNextStage(next, END_DELAY_MS));
                tick.restart();
            }
        });

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
        if (tick.elapsedTime(source.getRate(), APPROACH_DELAY_MS))
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
        if (camera.getX() < 736 * 16 - camera.getWidth())
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
        if (camera.getX() < 758 * 16 - camera.getWidth())
        {
            camera.moveLocation(extrp, SPEED, 0.0);
            target.moveLocationX(extrp, SPEED);
        }

        if (hurtable.isHurting())
        {
            leave = true;
        }
        if (leave && !head.isHurting())
        {
            leave = false;
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
        if (tick.elapsedTime(source.getRate(), EXPLODE_DELAY_MS))
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
