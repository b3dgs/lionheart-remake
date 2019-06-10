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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.GameplayLiana;
import com.b3dgs.lionheart.object.GameplaySteep;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Glue;
import com.b3dgs.lionheart.object.feature.Patrol;
import com.b3dgs.lionheart.object.state.attack.StateAttackFall;
import com.b3dgs.lionheart.object.state.attack.StateAttackJump;

/**
 * Fall state implementation.
 */
public final class StateFall extends State
{
    private final GameplaySteep steep = new GameplaySteep();
    private final GameplayLiana liana = new GameplayLiana();

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateFall(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateLand.class, () -> !steep.is() && collideY.get() && !model.hasFeature(Patrol.class));
        addTransition(StatePatrol.class, () -> collideY.get() && model.hasFeature(Patrol.class));
        addTransition(StateSlide.class, steep::is);
        addTransition(StateLianaIdle.class, () -> liana.is() && !liana.isLeft() && !liana.isRight() && !isGoDown());
        addTransition(StateLianaSlide.class, () -> (liana.isLeft() || liana.isRight()) && !isGoDown());
        addTransition(StateAttackJump.class, () -> !collideY.get() && control.isFireButtonOnce() && !isGoDown());
        addTransition(StateAttackFall.class, () -> !collideY.get() && isFire() && isGoDown());
    }

    @Override
    protected void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        super.onCollideLeg(result, category);

        steep.onCollideLeg(result, category);

        if (!result.startWithY(CollisionName.LIANA) && !result.startWithY(CollisionName.SPIKE))
        {
            jump.setDirection(DirectionNone.INSTANCE);
            tileCollidable.apply(result);
            body.resetGravity();
        }
    }

    @Override
    protected void onCollideKnee(CollisionResult result, CollisionCategory category)
    {
        super.onCollideKnee(result, category);

        tileCollidable.apply(result);
    }

    @Override
    protected void onCollideHand(CollisionResult result, CollisionCategory category)
    {
        super.onCollideHand(result, category);

        liana.onCollideHand(result, category);

        if (!isGoDown() && result.startWithY(CollisionName.LIANA))
        {
            tileCollidable.apply(result);
        }
    }

    @Override
    protected void onCollided(Collidable collidable, Collision with, Collision by)
    {
        super.onCollided(collidable, with, by);

        if (collidable.hasFeature(Glue.class) && with.getName().startsWith(Anim.LEG))
        {
            collideY.set(true);
        }
    }

    @Override
    public void enter()
    {
        super.enter();

        steep.reset();
        liana.reset();
    }

    @Override
    public void exit()
    {
        super.exit();

        if (is(Mirror.NONE) && (steep.isLeft() || liana.isLeft()))
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else if (is(Mirror.HORIZONTAL) && (steep.isRight() || liana.isRight()))
        {
            mirrorable.mirror(Mirror.NONE);
        }
    }

    @Override
    public void update(double extrp)
    {
        body.update(extrp);
        movement.setDestination(control.getHorizontalDirection() * Constant.WALK_SPEED, 0.0);
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
