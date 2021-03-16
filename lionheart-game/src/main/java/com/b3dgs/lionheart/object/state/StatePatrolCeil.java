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
package com.b3dgs.lionheart.object.state;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;

/**
 * Patrol ceil state implementation.
 */
public final class StatePatrolCeil extends State
{
    private double old;
    private double oldMax;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StatePatrolCeil(EntityModel model, Animation animation)
    {
        super(model, animation);
    }

    @Override
    public void enter()
    {
        super.enter();

        old = body.getGravity();
        oldMax = body.getGravityMax();
        body.setGravity(0.0);
        body.setGravityMax(0.0);
    }

    @Override
    public void update(double extrp)
    {
        movement.setDestination(device.getHorizontalDirection() * Constant.WALK_SPEED,
                                device.getVerticalDirection() * Constant.WALK_SPEED);
        if (Double.compare(movement.getDirectionHorizontal(), 0.0) != 0
            || Double.compare(movement.getDirectionVertical(), 0.0) != 0)
        {
            animatable.setAnimSpeed(Math.abs(movement.getDirectionHorizontal() + movement.getDirectionVertical())
                                    * animation.getSpeed());
        }
    }

    @Override
    public void exit()
    {
        super.exit();

        body.setGravity(old);
        body.setGravityMax(oldMax);
    }
}
