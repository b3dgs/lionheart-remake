/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionheart.object.state;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;

/**
 * Slide state implementation.
 */
public class StateSlideBase extends State
{
    private static final double SPEED_JUMP_X = 1.5;
    private static final Direction SPEED_JUMP_Y = new Force(0.0, 2.5);

    private final AtomicBoolean steep = new AtomicBoolean();
    private final AtomicBoolean steepLeft = new AtomicBoolean();
    private final AtomicBoolean steepRight = new AtomicBoolean();
    private final AtomicBoolean abord = new AtomicBoolean();

    private double speed = 0.5;
    private int side = 1;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateSlideBase(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateIdle.class, () -> !steep.get() || abord.get());
        addTransition(StateJump.class, this::isGoingUp);
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
    protected void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        super.onCollideLeg(result, category);

        tileCollidable.apply(result);

        if (result.startWithY(Constant.COLL_PREFIX_STEEP_LEFT))
        {
            side = -1;
            steep.set(true);
            steepLeft.set(true);
        }
        else if (result.startWithY(Constant.COLL_PREFIX_STEEP_RIGHT))
        {
            side = 1;
            steep.set(true);
            steepRight.set(true);
        }

        if (result.startWithX(Constant.COLL_PREFIX_STEEP_LEFT_GROUND)
            && result.startWithY(Constant.COLL_PREFIX_STEEP_LEFT_GROUND))
        {
            abord.set(true);
        }
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.setDirection(DirectionNone.INSTANCE);
        movement.setDestination(0.0, 0.0);
        steep.set(true);
        steepLeft.set(false);
        steepRight.set(false);
        abord.set(false);
    }

    @Override
    public void exit()
    {
        super.exit();

        if (isGoingUp())
        {
            movement.setDestination(0.0, 0.0);
            movement.setDirection(SPEED_JUMP_X * side, 0.0);
            jump.setDirectionMaximum(SPEED_JUMP_Y);

            if (steepLeft.get())
            {
                transformable.teleportX(transformable.getX() - 2);
            }
            else if (steepRight.get())
            {
                transformable.teleportX(transformable.getX() + 2);
            }
        }
    }

    @Override
    public void update(double extrp)
    {
        movement.setDestination(speed * side, -speed * 2.0);
    }

    @Override
    protected void postUpdate()
    {
        super.postUpdate();

        if (mirrorable.is(Mirror.NONE) && steepLeft.get())
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else if (mirrorable.is(Mirror.HORIZONTAL) && steepRight.get())
        {
            mirrorable.mirror(Mirror.NONE);
        }

        steep.set(false);
        steepLeft.set(false);
        steepRight.set(false);
    }
}
