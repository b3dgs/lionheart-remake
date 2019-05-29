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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
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
    private static final double SPEED = 5.0 / 3.0;

    private final AtomicBoolean steep = new AtomicBoolean();
    private final AtomicBoolean steepLeft = new AtomicBoolean();
    private final AtomicBoolean steepRight = new AtomicBoolean();

    private final AtomicBoolean liana = new AtomicBoolean();
    private final AtomicBoolean lianaLeft = new AtomicBoolean();
    private final AtomicBoolean lianaRight = new AtomicBoolean();

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateFall(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateLand.class, () -> !steep.get() && collideY.get() && !model.hasFeature(Patrol.class));
        addTransition(StatePatrol.class, () -> collideY.get() && model.hasFeature(Patrol.class));
        addTransition(StateSlide.class, steep::get);
        addTransition(StateLianaIdle.class,
                      () -> liana.get() && !lianaLeft.get() && !lianaRight.get() && !isGoingDown());
        addTransition(StateLianaSlide.class, () -> (lianaLeft.get() || lianaRight.get()) && !isGoingDown());
        addTransition(StateAttackJump.class, () -> !collideY.get() && control.isFireButtonOnce() && !isGoingDown());
        addTransition(StateAttackFall.class, () -> !collideY.get() && control.isFireButton() && isGoingDown());
    }

    @Override
    protected void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        super.onCollideLeg(result, category);

        jump.setDirection(DirectionNone.INSTANCE);
        tileCollidable.apply(result);
        body.resetGravity();

        if (result.startWithY(Constant.COLL_PREFIX_STEEP_LEFT))
        {
            steep.set(true);
            steepLeft.set(true);
        }
        else if (result.startWithY(Constant.COLL_PREFIX_STEEP_RIGHT))
        {
            steep.set(true);
            steepRight.set(true);
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

        if (result.startWithY(Constant.COLL_PREFIX_LIANA_LEFT))
        {
            liana.set(true);
            lianaLeft.set(true);
        }
        else if (result.startWithY(Constant.COLL_PREFIX_LIANA_RIGHT))
        {
            liana.set(true);
            lianaRight.set(true);
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

        steep.set(false);
        steepLeft.set(false);
        steepRight.set(false);

        liana.set(false);
        lianaLeft.set(false);
        lianaRight.set(false);
    }

    @Override
    public void exit()
    {
        super.exit();

        if (mirrorable.is(Mirror.NONE) && (steepLeft.get() || lianaLeft.get()))
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else if (mirrorable.is(Mirror.HORIZONTAL) && (steepRight.get() || lianaRight.get()))
        {
            mirrorable.mirror(Mirror.NONE);
        }
    }

    @Override
    public void update(double extrp)
    {
        body.update(extrp);
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
