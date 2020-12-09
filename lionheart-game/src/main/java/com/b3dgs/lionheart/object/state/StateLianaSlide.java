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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.state.attack.StateAttackLianaSlide;

/**
 * Liana slide state implementation.
 */
public class StateLianaSlide extends State
{
    private static final double LIANA_SPEED_FAST = 1.1;
    private static final double LIANA_SPEED_SLOW = 0.6;
    private static final double LIANA_SPEED = 0.8;

    private double speed = LIANA_SPEED;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateLianaSlide(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateAttackLianaSlide.class, () -> liana.is() && isFireOnce());
        addTransition(StateFall.class, () -> !liana.is() || isGoDownOnce());
    }

    @Override
    protected void onCollideHand(CollisionResult result, CollisionCategory category)
    {
        super.onCollideHand(result, category);

        if (result.startWithY(CollisionName.LIANA))
        {
            tileCollidable.apply(result);
            body.resetGravity();
        }
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.zero();
        movement.setVelocity(1.0);
        movement.setSensibility(1.0);
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
            transformable.teleportY(transformable.getY() - Math.ceil(speed) * 3);
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
        movement.setDestination(speed * liana.getSide(), -speed * 2);
    }
}
