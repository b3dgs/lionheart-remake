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
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.state.attack.StateAttackFall;
import com.b3dgs.lionheart.object.state.attack.StateAttackJump;

/**
 * Jump state implementation.
 */
public final class StateJump extends State
{
    /** Check for jump interruption during jumping. */
    private final Updatable checkJumpStopped;

    private Updatable check;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateJump(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateFall.class,
                      () -> (Double.compare(jump.getDirectionVertical(), 0.0) <= 0
                             || transformable.getY() < transformable.getOldY()));
        addTransition(StateAttackJump.class, () -> isFireOnce() && !isGoDown());
        addTransition(StateAttackFall.class, () -> isFire() && isGoDown());

        checkJumpStopped = extrp ->
        {
            if (!isFire() && Double.compare(input.getVerticalDirection(), 0.0) <= 0)
            {
                check = UpdatableVoid.getInstance();
                jump.setDirectionMaximum(new Force(0.0,
                                                   UtilMath.clamp(Constant.JUMP_MAX - jump.getDirectionVertical(),
                                                                  Constant.JUMP_MIN,
                                                                  Constant.JUMP_MAX)));
            }
        };
    }

    @Override
    protected void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        if (result.startWithY(CollisionName.STEEP))
        {
            body.resetGravity();
        }
    }

    @Override
    protected void onCollideKnee(CollisionResult result, CollisionCategory category)
    {
        super.onCollideKnee(result, category);

        if (movement.getDirectionHorizontal() > 0 && result.startWithX(CollisionName.STEEP_LEFT)
            || movement.getDirectionHorizontal() < 0 && result.startWithX(CollisionName.STEEP_RIGHT)
            || result.startWithX(CollisionName.STEEP_VERTICAL))
        {
            tileCollidable.apply(result);
        }
    }

    @Override
    public void enter()
    {
        super.enter();

        check = checkJumpStopped;

        jump.setDirection(0.0, Constant.JUMP_MAX);
        jump.setDirectionMaximum(new Force(0.0, Constant.JUMP_MAX));
    }

    @Override
    public void exit()
    {
        super.exit();

        jump.setDirectionMaximum(new Force(0.0, Constant.JUMP_MAX));
    }

    @Override
    public void update(double extrp)
    {
        check.update(extrp);
        body.resetGravity();
        movement.setDestination(input.getHorizontalDirection() * Constant.WALK_SPEED, 0.0);
    }

    @Override
    protected void postUpdate()
    {
        super.postUpdate();

        if (isGoHorizontal()
            && !(movement.getDirectionHorizontal() < 0 && isGoRight()
                 || movement.getDirectionHorizontal() > 0 && isGoLeft())
            && Math.abs(movement.getDirectionHorizontal()) > Constant.WALK_SPEED
            && movement.isDecreasingHorizontal())
        {
            movement.setVelocity(Constant.WALK_VELOCITY_SLOPE_DECREASE);
        }
        else
        {
            movement.setVelocity(Constant.WALK_VELOCITY_MAX);
        }
    }
}
