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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionheart.object.EntityModel;

/**
 * Guard feature implementation.
 * <p>
 * Attack on close distance, salto on far distance.
 * </p>
 */
@FeatureInterface
public final class Guard extends FeatureModel implements RoutineUpdate, Recyclable
{
    /** Max attack distance. */
    public static final double ATTACK_DISTANCE_MAX = 56.0;
    /** Max move. */
    private static final int MOVE_MAX = 144;
    /** Min move. */
    private static final int MOVE_MIN = 32;

    private final Trackable target = services.get(Trackable.class);

    private final Mirrorable mirrorable;
    private final Transformable transformable;
    private final Rasterable rasterable;
    private final Hurtable hurtable;

    private double startX;
    private boolean started;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param model The model feature.
     * @param mirrorable The mirrorable feature.
     * @param transformable The transformable feature.
     * @param rasterable The rasterable feature.
     * @param hurtable The hurtable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Guard(Services services,
                 SetupSurfaceRastered setup,
                 EntityModel model,
                 Mirrorable mirrorable,
                 Transformable transformable,
                 Rasterable rasterable,
                 Hurtable hurtable)
    {
        super(services, setup);

        this.mirrorable = mirrorable;
        this.transformable = transformable;
        this.rasterable = rasterable;
        this.hurtable = hurtable;

        model.getMovement().setVelocity(1.0);
        mirrorable.update(1.0);
    }

    /**
     * Apply mirror.
     */
    public void applyMirror()
    {
        if (mirrorable.is(Mirror.NONE) && target.getX() > transformable.getX())
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
            rasterable.setFrameOffsets(-22, 0);
            hurtable.setShadeOffset(-22, 0);
        }
        else if (mirrorable.is(Mirror.HORIZONTAL) && target.getX() < transformable.getX())
        {
            mirrorable.mirror(Mirror.NONE);
            rasterable.setFrameOffsets(0, 0);
            hurtable.setShadeOffset(0, 0);
        }
    }

    @Override
    public void update(double extrp)
    {
        if (!started)
        {
            started = true;
            startX = transformable.getX();
        }
        else
        {
            if (transformable.getX() > startX + MOVE_MAX)
            {
                transformable.teleportX(startX + MOVE_MAX);
            }
            else if (transformable.getX() < startX - MOVE_MIN)
            {
                transformable.teleportX(startX - MOVE_MIN);
            }
        }
    }

    @Override
    public void recycle()
    {
        started = false;
    }
}
