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
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

/**
 * Ghost2 feature implementation.
 * <ol>
 * <li>Move on player.</li>
 * </ol>
 */
@FeatureInterface
public final class Ghost2 extends FeatureModel implements Routine, Recyclable
{
    private static final int TRACK_DELAY_MS = 1500;
    private static final double SPEED = 1.45;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Trackable target = services.get(Trackable.class);

    private final Transformable transformable;
    private final Hurtable hurtable;

    private final Tick tick = new Tick();
    private final Force current = new Force();

    private boolean phase;
    private double startY;
    private double idle;
    private boolean first;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @param hurtable The hurtable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Ghost2(Services services, SetupSurfaceRastered setup, Transformable transformable, Hurtable hurtable)
    {
        super(services, setup);

        this.transformable = transformable;
        this.hurtable = hurtable;
    }

    /**
     * Compute the force vector depending of the target.
     */
    private void computeVector()
    {
        final double sx = transformable.getX();
        final double sy = transformable.getY();

        double dx = target.getX();
        double dy = target.getY();

        final double ray = UtilMath.getDistance(target.getX(), target.getY(), target.getX(), target.getY());
        dx += (int) ((target.getX() - target.getOldX()) * ray);
        dy += (int) ((target.getY() - target.getOldY()) * ray);

        final double dist = Math.max(Math.abs(sx - dx), Math.abs(sy - dy));

        final double vecX = (dx - sx) / dist * SPEED;
        final double vecY = (dy - sy) / dist * SPEED;

        current.setDestination(vecX, vecY);
        current.setDirection(vecX, vecY);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        current.setVelocity(1.0);
        current.setSensibility(0.5);
    }

    @Override
    public void update(double extrp)
    {
        current.update(extrp);

        if (first)
        {
            first = false;
            startY = transformable.getY();
        }

        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), TRACK_DELAY_MS))
        {
            if (!phase)
            {
                computeVector();
            }
            phase = !phase;
            tick.restart();
        }
        else if (phase)
        {
            if (hurtable.isHurting())
            {
                current.zero();
            }
            transformable.moveLocation(extrp, current);
            startY = transformable.getY();
        }
        else
        {
            idle = UtilMath.wrapDouble(idle + 0.15 * extrp, 0, 360);
            transformable.teleportY(startY + Math.sin(idle) * 2.0);
        }
    }

    @Override
    public void recycle()
    {
        first = true;
        phase = false;
        current.zero();
        tick.restart();
    }
}
