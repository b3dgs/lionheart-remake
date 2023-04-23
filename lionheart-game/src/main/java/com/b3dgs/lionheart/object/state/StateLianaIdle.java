/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.state.attack.StateAttackLiana;

/**
 * Liana idle state implementation.
 */
public final class StateLianaIdle extends State
{
    private static final int FRAME_OFFSET_Y = 7;

    private double oldVelocity;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateLianaIdle(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateLianaSlide.class, () -> (liana.isLeft() || liana.isRight()) && !isGoDown());
        addTransition(StateLianaWalk.class, this::isGoHorizontal);
        addTransition(StateLianaSoar.class, () -> liana.is() && (isGoUpOnce() || isFire(DeviceMapping.JUMP)));
        addTransition(StateAttackLiana.class, this::isFireOnce);
        addTransition(StateFall.class, () -> !liana.is() || isGoDown());
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

        oldVelocity = movement.getVelocity();
        movement.setVelocity(Constant.WALK_VELOCITY_MAX);
        movement.setDirection(DirectionNone.INSTANCE);
        rasterable.setFrameOffsets(0, FRAME_OFFSET_Y);
    }

    @Override
    public void update(double extrp)
    {
        movement.setDestination(device.getHorizontalDirection() * Constant.WALK_SPEED, 0.1);
    }

    @Override
    public void exit()
    {
        super.exit();

        movement.setVelocity(oldVelocity);
        rasterable.setFrameOffsets(0, 0);
        if (isGoDown())
        {
            transformable.teleportY(transformable.getY() - 1.0);
        }
    }
}
