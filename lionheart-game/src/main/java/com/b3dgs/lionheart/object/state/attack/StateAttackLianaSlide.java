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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.GameplayLiana;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.state.StateFall;
import com.b3dgs.lionheart.object.state.StateLianaSlide;

/**
 * Liana slide attack state implementation.
 */
public class StateAttackLianaSlide extends State
{
    private static final double LIANA_SPEED_FAST = 1.2;
    private static final double LIANA_SPEED_SLOW = 0.8;
    private static final double LIANA_SPEED = 1.0;

    private final GameplayLiana liana = new GameplayLiana();

    private double speed = 1.0;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateAttackLianaSlide(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateFall.class, () -> !liana.is() || isGoDown());
        addTransition(StateLianaSlide.class,
                      () -> (liana.isLeft() || liana.isRight()) && !isGoDown() && is(AnimState.FINISHED));
    }

    @Override
    protected void onCollideHand(CollisionResult result, CollisionCategory category)
    {
        super.onCollideHand(result, category);

        liana.onCollideHand(result, category);

        if (result.startWithY(Constant.COLL_PREFIX_LIANA))
        {
            tileCollidable.apply(result);
            body.resetGravity();
        }
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.setDirection(DirectionNone.INSTANCE);
        movement.setDestination(0.0, 0.0);
        movement.setVelocity(1.0);
        movement.setSensibility(1.0);

        liana.reset();
    }

    @Override
    public void exit()
    {
        super.exit();

        if (isGoUp())
        {
            movement.setDestination(0.0, 0.0);
        }
        if (isGoDown())
        {
            transformable.teleportY(transformable.getY() - 3.0);
        }
    }

    @Override
    public void update(double extrp)
    {
        body.resetGravity();

        if (is(Mirror.NONE) && isGoRight() || is(Mirror.HORIZONTAL) && isGoLeft())
        {
            speed = LIANA_SPEED_FAST;
        }
        else if (is(Mirror.HORIZONTAL) && isGoRight() || is(Mirror.NONE) && isGoLeft())
        {
            speed = LIANA_SPEED_SLOW;
        }
        else
        {
            speed = LIANA_SPEED;
        }
        movement.setDestination(speed * liana.getSide(), -speed);
    }

    @Override
    protected void postUpdate()
    {
        super.postUpdate();

        liana.reset();
    }
}
