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
package com.b3dgs.lionheart.object.state;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Patrol;

/**
 * Slide state implementation.
 */
public class StateSlideBase extends State
{
    private static final double SPEED_JUMP_X = 2.6;
    private static final Direction SPEED_JUMP_Y = new Force(0.0, SPEED_JUMP_X * 1.8);

    private final AtomicBoolean abord = new AtomicBoolean();

    private double speed = 0.6;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    protected StateSlideBase(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateIdle.class, () -> (!steep.is() || abord.get()) && !model.hasFeature(Patrol.class));
        addTransition(StatePatrol.class, () -> (!steep.is() || abord.get()) && model.hasFeature(Patrol.class));
        addTransition(StateJump.class, this::isButtonUpOnce);
    }

    /**
     * Set the slide speed.
     * 
     * @param speed The slide speed.
     */
    protected final void setSpeed(double speed)
    {
        this.speed = speed;
    }

    @Override
    protected void onCollideKnee(CollisionResult result, CollisionCategory category)
    {
        // Skip
    }

    @Override
    protected void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        super.onCollideLeg(result, category);

        if (result.startWithY(CollisionName.STEEP_LEFT_GROUND))
        {
            abord.set(true);
        }
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.zero();
        abord.set(false);

        if (model.hasFeature(Patrol.class))
        {
            rasterable.setFrameOffsets(0, 20);
        }
    }

    @Override
    public void update(double extrp)
    {
        movement.setDestination(speed * steep.getSide(), -speed * 4);
        movement.setDirection(speed * steep.getSide(), -speed * 4);
        body.resetGravity();
    }

    @Override
    public void exit()
    {
        super.exit();

        movement.zero();

        if (isGoUp() || isFire(DeviceMapping.JUMP))
        {
            movement.setDirection(SPEED_JUMP_X * steep.getSide(), 0.0);
            jump.setDirectionMaximum(SPEED_JUMP_Y);
        }
        else
        {
            movement.setDirection(steep.getSide(), 0.0);
        }

        if (model.hasFeature(Patrol.class))
        {
            rasterable.setFrameOffsets(0, 0);
        }
    }
}
