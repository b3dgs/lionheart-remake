/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;

/**
 * Patrol state implementation.
 */
public final class StatePatrol extends State
{
    private boolean turn;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StatePatrol(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StatePrepareJump.class, this::isGoUpOnce);
        addTransition(StateTurn.class, () -> turn);
        addTransition(StateFall.class,
                      () -> model.hasGravity()
                            && Double.compare(movement.getDirectionHorizontal(), 0.0) != 0
                            && !collideY.get());
    }

    @Override
    protected void onCollideKnee(CollisionResult result, CollisionCategory category)
    {
        turn = true;
    }

    @Override
    protected void onCollided(Collidable collidable, Collision with, Collision by)
    {
        super.onCollided(collidable, with, by);

        if (by.getName().startsWith(CollisionName.SPIKE))
        {
            turn = true;
        }
    }

    @Override
    public void enter()
    {
        super.enter();

        turn = false;
    }

    @Override
    public void update(double extrp)
    {
        movement.setDestination(input.getHorizontalDirection() * Constant.WALK_SPEED,
                                input.getVerticalDirection() * Constant.WALK_SPEED);
        animatable.setAnimSpeed(Math.abs(movement.getDirectionHorizontal() + movement.getDirectionVertical())
                                * animation.getSpeed());
    }
}
