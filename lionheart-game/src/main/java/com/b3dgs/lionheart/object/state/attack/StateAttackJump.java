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
package com.b3dgs.lionheart.object.state.attack;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Glue;
import com.b3dgs.lionheart.object.state.StateFall;
import com.b3dgs.lionheart.object.state.StateLand;

/**
 * Jump attack state implementation.
 */
public final class StateAttackJump extends State
{
    private static final double SPEED = 5.0 / 3.0;
    private static final double JUMP_MIN = 2.5;
    private static final double JUMP_MAX = 5.4;

    private final Updatable checkJumpStopped;
    private Updatable check;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateAttackJump(EntityModel model, Animation animation)
    {
        super(model, animation);

        checkJumpStopped = extrp ->
        {
            if (Double.compare(control.getVerticalDirection(), 0.0) <= 0)
            {
                check = UpdatableVoid.getInstance();
                jump.setDirectionMaximum(new Force(0.0,
                                                   UtilMath.clamp(JUMP_MAX - jump.getDirectionVertical(),
                                                                  JUMP_MIN,
                                                                  JUMP_MAX)));
            }
        };

        addTransition(StateLand.class, () -> collideY.get());
        addTransition(StateFall.class, () -> is(AnimState.FINISHED));
    }

    @Override
    protected void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        super.onCollideLeg(result, category);

        tileCollidable.apply(result);
        jump.setDirection(DirectionNone.INSTANCE);
        body.resetGravity();
    }

    @Override
    protected void onCollided(Collidable collidable, Collision with, Collision by)
    {
        super.onCollided(collidable, with, by);

        if (transformable.getY() < transformable.getOldY()
            && collidable.hasFeature(Glue.class)
            && with.getName().startsWith(Constant.ANIM_PREFIX_LEG))
        {
            collideY.set(true);
        }
    }

    @Override
    public void enter()
    {
        super.enter();

        check = checkJumpStopped;
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

        if (Double.compare(jump.getDirectionVertical(), 0.0) <= 0 || transformable.getY() < transformable.getOldY())
        {
            body.update(extrp);
        }
        else
        {
            body.resetGravity();
        }
        if (isGoingHorizontal())
        {
            movement.setVelocity(0.12);
        }
        else
        {
            movement.setVelocity(0.07);
        }
        movement.setDestination(control.getHorizontalDirection() * SPEED, 0.0);
    }
}
