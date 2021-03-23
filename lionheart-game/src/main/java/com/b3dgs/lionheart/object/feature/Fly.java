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
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * Fly feature implementation.
 * <ol>
 * <li>Follow player smoothly.</li>
 * </ol>
 */
@FeatureInterface
public final class Fly extends FeatureModel implements Routine, Recyclable
{
    private static final double SPEED = 1.1;

    private final Force direction = new Force();
    private final Transformable track = services.get(SwordShade.class).getFeature(Transformable.class);

    @FeatureGet private Transformable transformable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Fly(Services services, Setup setup)
    {
        super(services, setup);

        direction.setVelocity(0.04);
        direction.setSensibility(0.01);
    }

    @Override
    public void update(double extrp)
    {
        final double dh = track.getX() - transformable.getOldX();
        final double dv = track.getY() - transformable.getOldY();

        final double nh = Math.abs(dh);
        final double nv = Math.abs(dv);

        final int max = (int) Math.ceil(Math.max(nh, nv));
        final double sx;
        final double sy;

        if (Double.compare(nh, 1.0) >= 0 || Double.compare(nv, 1.0) >= 0)
        {
            sx = dh / max;
            sy = dv / max;
        }
        else
        {
            sx = dh;
            sy = dv;
        }

        direction.setDestination(sx * SPEED, sy * SPEED);
        direction.update(extrp);
        transformable.moveLocation(extrp, direction);
    }

    @Override
    public void recycle()
    {
        direction.setDirection(DirectionNone.INSTANCE);
    }
}
