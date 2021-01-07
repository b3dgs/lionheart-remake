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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.io.InputDeviceControlVoid;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.state.StateFall;
import com.b3dgs.lionheart.object.state.StatePatrolCeil;

/**
 * Spider feature implementation.
 * <ol>
 * <li>Follow player until defined distance.</li>
 * <li>Fall on player proximity if ceil.</li>
 * </ol>
 */
@FeatureInterface
public final class Spider extends FeatureModel implements Routine
{
    private static final int TRACKED_DISTANCE = 64;
    private static final double TRACK_SPEED = 0.5;

    private final Transformable track = services.get(SwordShade.class).getFeature(Transformable.class);

    private double move;
    private boolean tracked;
    private boolean enabled;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private EntityModel model;
    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private Body body;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Spider(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);
    }

    /**
     * Enable player tracking.
     */
    public void track()
    {
        enabled = true;
        model.setInput(new InputDeviceControlVoid()
        {
            @Override
            public double getHorizontalDirection()
            {
                return move;
            }
        });
    }

    @Override
    public void update(double extrp)
    {
        if (UtilMath.getDistance(track, transformable) < TRACKED_DISTANCE)
        {
            if (stateHandler.isState(StatePatrolCeil.class))
            {
                stateHandler.changeState(StateFall.class);
                body.setGravity(4.5);
                body.setGravityMax(4.5);
            }
            else if (enabled)
            {
                tracked = true;
            }
        }

        if (tracked)
        {
            if (track.getX() > transformable.getX())
            {
                move = TRACK_SPEED;
                mirrorable.mirror(Mirror.NONE);
            }
            else if (track.getX() < transformable.getX())
            {
                move = -TRACK_SPEED;
                mirrorable.mirror(Mirror.HORIZONTAL);
            }
        }
    }
}
