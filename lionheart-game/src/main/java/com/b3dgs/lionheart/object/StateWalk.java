/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;

/**
 * Walk state implementation.
 */
final class StateWalk extends State
{
    private static final double SPEED = 5.0 / 3.0;
    private static final double ANIM_SPEED_DIVISOR = 6.0;
    private static final double WALK_MIN_SPEED = 0.005;

    private final AtomicBoolean ground = new AtomicBoolean();
    private final TileCollidable tileCollidable;
    private final TileCollidableListener listener;
    private boolean played;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateWalk(EntityModel model, Animation animation)
    {
        super(model, animation);

        final Transformable transformable = model.getFeature(Transformable.class);
        tileCollidable = model.getFeature(TileCollidable.class);

        listener = (tile, category) ->
        {
            if (Axis.Y == category.getAxis())
            {
                if (transformable.getY() < transformable.getOldY())
                {
                    ground.set(true);
                }
            }
        };

        addTransition(StateIdle.class, this::isWalkingSlowEnough);
        addTransition(StateJump.class, this::isGoingUp);
        addTransition(StateFall.class, () -> !ground.get() && transformable.getY() < transformable.getOldY());
    }

    private boolean isWalkingSlowEnough()
    {
        final double speedH = movement.getDirectionHorizontal();
        return !isGoingHorizontal() && UtilMath.isBetween(speedH, -WALK_MIN_SPEED, WALK_MIN_SPEED);
    }

    @Override
    public void enter()
    {
        super.enter();

        tileCollidable.addListener(listener);
        ground.set(false);
        played = false;
    }

    @Override
    public void exit()
    {
        tileCollidable.removeListener(listener);
    }

    @Override
    public void update(double extrp)
    {
        ground.set(false);
        if (!played && Double.compare(movement.getDirectionHorizontal(), 0.0) != 0)
        {
            animator.play(animation);
            played = true;
        }
        if (isGoingHorizontal())
        {
            movement.setVelocity(0.14);
        }
        else
        {
            movement.setVelocity(0.12);
        }

        movement.setDestination(control.getHorizontalDirection() * SPEED, 0.0);
        animator.setAnimSpeed(Math.abs(movement.getDirectionHorizontal()) / ANIM_SPEED_DIVISOR);
    }
}
