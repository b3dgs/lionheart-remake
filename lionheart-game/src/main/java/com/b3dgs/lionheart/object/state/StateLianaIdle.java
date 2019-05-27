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
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;

/**
 * Liana idle state implementation.
 */
public final class StateLianaIdle extends State
{
    private static final double SPEED = 5.0 / 3.0;
    private static final double WALK_MIN_SPEED = 0.75;

    private final AtomicBoolean liana = new AtomicBoolean();

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateLianaIdle(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateLianaWalk.class, () -> isWalkingFastEnough());
        addTransition(StateFall.class, () -> !liana.get() || isGoingDown());
    }

    private boolean isWalkingFastEnough()
    {
        final double speedH = movement.getDirectionHorizontal();
        return isGoingHorizontal() && !UtilMath.isBetween(speedH, -WALK_MIN_SPEED, WALK_MIN_SPEED);
    }

    @Override
    protected void onCollideHand(CollisionResult result, CollisionCategory category)
    {
        if (result.startWith(Constant.COLL_PREFIX_LIANA))
        {
            tileCollidable.apply(result);
            body.resetGravity();
            liana.set(true);
        }
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.setVelocity(0.16);
        movement.setDirection(DirectionNone.INSTANCE);
        liana.set(false);
    }

    @Override
    public void exit()
    {
        super.exit();

        if (isGoingDown())
        {
            transformable.teleportY(transformable.getY() - 1.0);
        }
    }

    @Override
    public void update(double extrp)
    {
        body.update(extrp);
        movement.setDestination(control.getHorizontalDirection() * SPEED, 0.0);
    }

    @Override
    protected void postUpdate()
    {
        super.postUpdate();

        liana.set(false);
    }
}
