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
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.GameplayLiana;
import com.b3dgs.lionheart.object.State;

/**
 * Liana walk state implementation.
 */
final class StateLianaWalk extends State
{
    private static final double SPEED = 2.0 / 3.0;
    private static final double ANIM_SPEED_DIVISOR = 4.0;
    private static final double WALK_MIN_SPEED = 0.005;

    private final GameplayLiana liana = new GameplayLiana();

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateLianaWalk(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateLianaIdle.class, () -> isWalkingSlowEnough());
        addTransition(StateLianaSlide.class, () -> (liana.isLeft() || liana.isRight()) && !isGoDown());
        addTransition(StateFall.class, () -> !liana.is() || isGoDown());
    }

    private boolean isWalkingSlowEnough()
    {
        final double speedH = movement.getDirectionHorizontal();
        return !isGoHorizontal() && UtilMath.isBetween(speedH, -WALK_MIN_SPEED, WALK_MIN_SPEED);
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

        rasterable.setFrameOffsets(0, -2);
        movement.setDirection(DirectionNone.INSTANCE);
        liana.reset();
    }

    @Override
    public void exit()
    {
        super.exit();

        rasterable.setFrameOffsets(0, 0);
        if (isGoDown())
        {
            transformable.teleportY(transformable.getY() - 1.0);
        }
    }

    @Override
    public void update(double extrp)
    {
        movement.setDestination(control.getHorizontalDirection() * SPEED, 0.0);
        animatable.setAnimSpeed(Math.abs(movement.getDirectionHorizontal()) / ANIM_SPEED_DIVISOR);
    }

    @Override
    protected void postUpdate()
    {
        super.postUpdate();

        liana.reset();
    }
}
