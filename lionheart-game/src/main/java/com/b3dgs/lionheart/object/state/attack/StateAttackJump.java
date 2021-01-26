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
package com.b3dgs.lionheart.object.state.attack;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.state.StateFall;

/**
 * Jump attack state implementation.
 */
public final class StateAttackJump extends State
{
    private static final double JUMP_MIN = 2.5;
    private static final double JUMP_MAX = 5.4;

    private final Updatable checkJumpAbort;
    private Updatable check;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateAttackJump(EntityModel model, Animation animation)
    {
        super(model, animation);

        checkJumpAbort = extrp ->
        {
            if (Double.compare(input.getVerticalDirection(), 0.0) <= 0)
            {
                check = UpdatableVoid.getInstance();
                jump.setDirectionMaximum(new Force(0.0,
                                                   UtilMath.clamp(JUMP_MAX - jump.getDirectionVertical(),
                                                                  JUMP_MIN,
                                                                  JUMP_MAX)));
            }
        };

        addTransition(StateFall.class, () -> collideY.get() || is(AnimState.FINISHED));
    }

    @Override
    protected void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        super.onCollideLeg(result, category);

        jump.setDirection(DirectionNone.INSTANCE);
        body.resetGravity();
    }

    @Override
    public void enter()
    {
        super.enter();

        check = checkJumpAbort;
    }

    @Override
    public void exit()
    {
        super.exit();

        jump.setDirectionMaximum(new Force(0.0, JUMP_MAX));
    }

    @Override
    public void update(double extrp)
    {
        check.update(extrp);

        if (Double.compare(jump.getDirectionVertical(), 0.0) > 0
            && Double.compare(transformable.getY(), transformable.getOldY()) >= 0)
        {
            body.resetGravity();
        }

        if (isGoHorizontal())
        {
            movement.setVelocity(Constant.WALK_VELOCITY_MAX);
        }
        else
        {
            movement.setVelocity(0.07);
        }
        movement.setDestination(input.getHorizontalDirection() * Constant.WALK_SPEED, 0.0);
    }
}
