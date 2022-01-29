/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * Effect liana on destroy feature implementation.
 * <ol>
 * <li>Move down left or right on explode.</li>
 * </ol>
 */
@FeatureInterface
public final class ExplodeLiana extends FeatureModel implements Routine, Recyclable
{
    private static final double INIT_Y = 1.5;

    private int side;
    private double speed;

    @FeatureGet private Transformable transformable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public ExplodeLiana(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    public void update(double extrp)
    {
        transformable.moveLocation(extrp, speed * side, -Math.abs(speed) * 4.0 + INIT_Y);
        speed += 0.09 * extrp;
    }

    @Override
    public void recycle()
    {
        if (UtilRandom.getRandomBoolean())
        {
            side = 1;
        }
        else
        {
            side = -1;
        }
        speed = 0.0;
    }
}
