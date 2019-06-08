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
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Glue;
import com.b3dgs.lionheart.object.state.attack.StateAttackPrepare;

/**
 * Walk state implementation.
 */
final class StateWalk extends State
{
    private static final double SPEED = 5.0 / 3.0;
    private static final double ANIM_SPEED_DIVISOR = 6.0;
    private static final double WALK_MIN_SPEED = 0.005;
    private static final double SPEED_SLOPE_RISING = -0.3;
    private static final double SPEED_SLOPE_DESCENDING = 0.6;

    private final AtomicBoolean slopeRising = new AtomicBoolean();
    private final AtomicBoolean slopeDescending = new AtomicBoolean();

    private double speedSlope = 0.0;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateWalk(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateIdle.class,
                      () -> isGoRight() && collideXright.get()
                            || isGoLeft() && collideXleft.get()
                            || isWalkingSlowEnough());
        addTransition(StateCrouch.class, this::isGoDown);
        addTransition(StateJump.class, this::isGoUp);
        addTransition(StateAttackPrepare.class, control::isFireButton);
        addTransition(StateFall.class,
                      () -> model.hasGravity()
                            && Double.compare(movement.getDirectionHorizontal(), 0.0) != 0
                            && !collideY.get());
    }

    private boolean isWalkingSlowEnough()
    {
        final double speedH = movement.getDirectionHorizontal();
        return !isGoHorizontal() && UtilMath.isBetween(speedH, -WALK_MIN_SPEED, WALK_MIN_SPEED);
    }

    @Override
    protected void onCollideKnee(CollisionResult result, CollisionCategory category)
    {
        super.onCollideKnee(result, category);

        if (movement.getDirectionHorizontal() < 0 && result.startWithX(Constant.COLL_PREFIX_STEEP_RIGHT)
            || movement.getDirectionHorizontal() > 0 && result.startWithX(Constant.COLL_PREFIX_STEEP_LEFT))
        {
            tileCollidable.apply(result);
            movement.setDirection(DirectionNone.INSTANCE);
            movement.setDestination(0.0, 0.0);
        }
    }

    @Override
    protected void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        super.onCollideLeg(result, category);

        tileCollidable.apply(result);

        if (movement.getDirectionHorizontal() > 0 && result.startWithY(Constant.COLL_PREFIX_SLOPE_LEFT)
            || movement.getDirectionHorizontal() < 0 && result.startWithY(Constant.COLL_PREFIX_SLOPE_RIGHT))
        {
            slopeRising.set(true);
            speedSlope = SPEED_SLOPE_RISING;
        }
        else if (movement.getDirectionHorizontal() > 0 && result.startWithY(Constant.COLL_PREFIX_SLOPE_RIGHT)
                 || movement.getDirectionHorizontal() < 0 && result.startWithY(Constant.COLL_PREFIX_SLOPE_LEFT))
        {
            slopeDescending.set(true);
            speedSlope = SPEED_SLOPE_DESCENDING;
        }
        else
        {
            speedSlope = 0.0;
        }
    }

    @Override
    protected void onCollided(Collidable collidable, Collision with, Collision by)
    {
        super.onCollided(collidable, with, by);

        if (collidable.hasFeature(Glue.class) && with.getName().startsWith(Constant.ANIM_PREFIX_LEG))
        {
            collideY.set(true);
        }
    }

    @Override
    public void enter()
    {
        super.enter();

        speedSlope = 0.0;
    }

    @Override
    public void update(double extrp)
    {
        movement.setDestination(control.getHorizontalDirection() * (SPEED + speedSlope), 0.0);
        animatable.setAnimSpeed(Math.abs(movement.getDirectionHorizontal()) / ANIM_SPEED_DIVISOR);
    }

    @Override
    protected void postUpdate()
    {
        super.postUpdate();

        if (isGoHorizontal()
            && !(movement.getDirectionHorizontal() < 0 && isGoRight()
                 || movement.getDirectionHorizontal() > 0 && isGoLeft())
            && Math.abs(movement.getDirectionHorizontal()) > SPEED
            && movement.isDecreasingHorizontal())
        {
            movement.setVelocity(0.0001);
        }
        else
        {
            movement.setVelocity(0.12);
        }

        slopeRising.set(false);
        slopeDescending.set(false);
    }
}
