/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.GameplaySteep;
import com.b3dgs.lionheart.object.State;

/**
 * Slide state implementation.
 */
public class StateSlideBase extends State
{
    private static final double SPEED_JUMP_X = 2.2;
    private static final Direction SPEED_JUMP_Y = new Force(0.0, SPEED_JUMP_X * 1.5);

    private final GameplaySteep steep = new GameplaySteep();
    private final AtomicBoolean abord = new AtomicBoolean();

    private double speed = 0.5;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    protected StateSlideBase(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateIdle.class, () -> !steep.is() || abord.get());
        addTransition(StateJump.class, this::isGoUpOnce);
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

        steep.onCollideLeg(result, category);

        if (result.startWithX(CollisionName.STEEP_LEFT_GROUND) && result.startWithY(CollisionName.STEEP_LEFT_GROUND))
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
        steep.reset();
        abord.set(false);
    }

    @Override
    public void exit()
    {
        super.exit();

        if (isGoUp())
        {
            movement.setDestination(0.0, 0.0);
            movement.setDirection(SPEED_JUMP_X * steep.getSide(), 0.0);
            jump.setDirectionMaximum(SPEED_JUMP_Y);
        }
    }

    @Override
    public void update(double extrp)
    {
        movement.setDestination(speed * steep.getSide(), -speed * 2.0);
    }

    @Override
    protected void postUpdate()
    {
        super.postUpdate();

        if (is(Mirror.NONE) && steep.isLeft())
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else if (is(Mirror.HORIZONTAL) && steep.isRight())
        {
            mirrorable.mirror(Mirror.NONE);
        }

        steep.reset();
    }
}
