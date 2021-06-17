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
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;

/**
 * Bomb feature implementation.
 * <ol>
 * <li>Explode on proximity.</li>
 * </ol>
 */
@FeatureInterface
public final class Bomb extends FeatureModel implements Routine, Recyclable, CollidableListener
{
    private static final int TRIGGER_DELAY_TICK = 10;
    private static final String COLLISION_NAME = "trigger";
    private static final String COLLISION_NAME_DRAGON = "dragonfly";

    private final Tick tick = new Tick();

    private int count;

    @FeatureGet private Hurtable hurtable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Bomb(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsed(TRIGGER_DELAY_TICK))
        {
            hurtable.hurt();
            count++;
            if (count > 4)
            {
                hurtable.kill(true);
            }
            tick.stop();
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (COLLISION_NAME.equals(with.getName()) && !by.getName().endsWith(COLLISION_NAME_DRAGON))
        {
            tick.start();
        }
    }

    @Override
    public void recycle()
    {
        count = 0;
        tick.stop();
    }
}
