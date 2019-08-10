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
package com.b3dgs.lionheart.object.state.attack;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.GameplaySteep;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Glue;
import com.b3dgs.lionheart.object.feature.Hurtable;
import com.b3dgs.lionheart.object.state.StateCrouch;
import com.b3dgs.lionheart.object.state.StateFall;
import com.b3dgs.lionheart.object.state.StateJump;

/**
 * Fall attack state implementation.
 */
public final class StateAttackFall extends State
{
    private final GameplaySteep steep = new GameplaySteep();

    private final AtomicBoolean collideSword = new AtomicBoolean();

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateAttackFall(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateCrouch.class, () -> !steep.is() && collideY.get() && isGoDown());
        addTransition(StateJump.class, () -> collideSword.get() && jump.getDirectionVertical() > 0);
        addTransition(StateFall.class,
                      () -> steep.is()
                            || !isGoDown() && collideY.get()
                            || !isFire() && Double.compare(jump.getDirectionVertical(), 0.0) <= 0);
    }

    @Override
    protected void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        super.onCollideLeg(result, category);

        steep.onCollideLeg(result, category);

        tileCollidable.apply(result);
        jump.setDirection(DirectionNone.INSTANCE);
        body.resetGravity();
    }

    @Override
    protected void onCollided(Collidable collidable, Collision with, Collision by)
    {
        super.onCollided(collidable, with, by);

        if (collidable.hasFeature(Glue.class) && with.getName().startsWith(Anim.LEG))
        {
            collideY.set(true);
        }
        if (collidable.hasFeature(Hurtable.class) && with.getName().startsWith(Anim.ATTACK_FALL))
        {
            body.resetGravity();
            jump.setDirection(new Force(0, Constant.JUMP_HIT));
            jump.setDirectionMaximum(new Force(0, Constant.JUMP_HIT));
            collideSword.set(true);
        }
    }

    @Override
    public void enter()
    {
        super.enter();

        collideSword.set(false);
        steep.reset();
    }

    @Override
    public void update(double extrp)
    {
        if (Double.compare(jump.getDirectionVertical(), 0.0) <= 0)
        {
            body.update(extrp);
        }
        else
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
        movement.setDestination(control.getHorizontalDirection() * Constant.WALK_SPEED, 0.0);
    }
}
