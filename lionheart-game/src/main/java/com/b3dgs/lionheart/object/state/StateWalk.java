/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.GameplayType;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.state.attack.StateAttackHorizontal;
import com.b3dgs.lionheart.object.state.attack.StatePrepareAttack;

/**
 * Walk state implementation.
 */
public final class StateWalk extends State
{
    private static final double ANIM_SPEED_DIVISOR = 6.0;
    private static final double WALK_MIN_SPEED = 0.2;
    private static final double SPEED_SLOPE_RISING = -0.3;
    private static final double SPEED_SLOPE_DESCENDING = 0.8;

    private final AtomicBoolean slopeRising = new AtomicBoolean();
    private final AtomicBoolean slopeDescending = new AtomicBoolean();

    private double speedSlope;
    private double factor;

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
        addTransition(StateJump.class, () -> (isGoUpOnce() || isFire(DeviceMapping.UP)));
        addTransition(StateFall.class,
                      () -> model.hasGravity()
                            && Double.compare(movement.getDirectionHorizontal(), 0.0) != 0
                            && !collideY.get());
        addTransition(StateWin.class, this::hasWin);

        if (Settings.getInstance().getGameplay() == GameplayType.ORIGINAL)
        {
            addTransition(StatePrepareAttack.class, this::isFire);
        }
        else
        {
            addTransition(StateAttackHorizontal.class, this::isFire);
        }
    }

    private boolean isWalkingSlowEnough()
    {
        final double speedH = movement.getDirectionHorizontal();
        return UtilMath.isBetween(speedH, -WALK_MIN_SPEED, WALK_MIN_SPEED);
    }

    @Override
    protected void onCollideKnee(CollisionResult result, CollisionCategory category)
    {
        if (!category.getName().startsWith(CollisionName.KNEE + "_1"))
        {
            super.onCollideKnee(result, category);
        }
    }

    @Override
    protected void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        super.onCollideLeg(result, category);

        if (result.startWithY(CollisionName.SLOPE))
        {
            factor = 0.5;

            if (movement.getDirectionHorizontal() > 0 && result.endWithY(CollisionName.LEFT)
                || movement.getDirectionHorizontal() < 0 && result.endWithY(CollisionName.RIGHT))
            {
                slopeRising.set(true);
                speedSlope = SPEED_SLOPE_RISING;
            }
            else if (movement.getDirectionHorizontal() > 0 && result.endWithY(CollisionName.RIGHT)
                     || movement.getDirectionHorizontal() < 0 && result.endWithY(CollisionName.LEFT))
            {
                slopeDescending.set(true);
                speedSlope = SPEED_SLOPE_DESCENDING;
            }
        }
        else if (result.startWithY(CollisionName.INCLINE))
        {
            factor = 2;

            if (movement.getDirectionHorizontal() > 0 && result.endWithY(CollisionName.LEFT)
                || movement.getDirectionHorizontal() < 0 && result.endWithY(CollisionName.RIGHT))
            {
                slopeRising.set(true);
                speedSlope = SPEED_SLOPE_RISING * 1.4;
            }
            else if (movement.getDirectionHorizontal() > 0 && result.endWithY(CollisionName.RIGHT)
                     || movement.getDirectionHorizontal() < 0 && result.endWithY(CollisionName.LEFT))
            {
                slopeDescending.set(true);
                speedSlope = SPEED_SLOPE_DESCENDING * 1.4;
            }
        }
        else
        {
            speedSlope = 0.0;
            factor = 0.0;
        }
    }

    @Override
    public void update(double extrp)
    {
        final double sx = device.getHorizontalDirection() * (Constant.WALK_SPEED + speedSlope);
        movement.setDestination(sx, -Math.abs(factor * sx));
        movement.setDirection(movement.getDirectionHorizontal(), -Math.abs(factor * sx));
        animatable.setAnimSpeed(Math.abs(movement.getDirectionHorizontal()) / ANIM_SPEED_DIVISOR);
    }

    @Override
    public void exit()
    {
        super.exit();

        factor = 0.0;
        speedSlope = 0.0;

        movement.zeroVertical();
    }

    @Override
    protected void postUpdate()
    {
        super.postUpdate();

        if (isGoHorizontal()
            && !(movement.getDirectionHorizontal() < 0 && isGoRight()
                 || movement.getDirectionHorizontal() > 0 && isGoLeft())
            && Math.abs(movement.getDirectionHorizontal()) > Constant.WALK_SPEED
            && movement.isDecreasingHorizontal()
            && !slopeRising.get())
        {
            movement.setVelocity(Constant.WALK_VELOCITY_SLOPE_DECREASE);
        }
        else
        {
            movement.setVelocity(Constant.WALK_VELOCITY_MAX);
        }

        slopeRising.set(false);
        slopeDescending.set(false);
    }
}
