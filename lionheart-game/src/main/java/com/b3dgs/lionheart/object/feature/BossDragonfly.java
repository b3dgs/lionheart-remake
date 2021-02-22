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
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
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
import com.b3dgs.lionheart.constant.Folder;

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
    private static final int EXPLODE_DELAY = 10;

    private final Tick tick = new Tick();

    private final Transformable player = services.get(SwordShade.class).getFeature(Transformable.class);
    private final Spawner spawner = services.get(Spawner.class);

    private Updatable updater;

    private Hurtable head;
    private Hurtable gobelin;
    private Stats stats;
    private int oldHealth;

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

        final AnimationConfig config = AnimationConfig.imports(setup);
    }

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
        updater = this::updateAwait;
    }

    private void updateAwait(double extrp)
    {
        collidable.setEnabled(!hurtable.isHurting());

        if (oldHealth != stats.getHealth())
        {
            head.hurt();
            gobelin.hurt();
            hurtable.hurt();
        }
        oldHealth = stats.getHealth();
        if (stats.getHealth() == 0)
        {
            if (!tick.isStarted())
            {
                tick.start();
                head.kill();
            }

            tick.update(extrp);
            if (tick.elapsed(EXPLODE_DELAY))
            {
                spawnExplode();

                tick.restart();
            }
        }
    }

    /**
     * Spawn explode.
     */
    private void spawnExplode()
    {
        final int width = transformable.getWidth();
        final int height = transformable.getHeight();

        spawner.spawn(Medias.create(Folder.EFFECTS, "dragonfly", "ExplodeBig.xml"),
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
