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
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.state.StateGripIdle;

/**
 * Grip attack state implementation.
 */
public final class StateAttackGrip extends State
{
    private static final int FRAME_OFFSET_Y = 10;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateAttackGrip(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateGripIdle.class, () -> is(AnimState.FINISHED));
    }

    @Override
    protected void onCollideHand(CollisionResult result, CollisionCategory category)
    {
        super.onCollideHand(result, category);

        if (result.startWithY(CollisionName.GRIP))
        {
            tileCollidable.apply(result);
            body.resetGravity();
        }
    }

    @Override
    public void enter()
    {
        super.enter();

        rasterable.setFrameOffsets(0, FRAME_OFFSET_Y);
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        body.resetGravity();
    }

    @Override
    public void exit()
    {
        super.exit();

        rasterable.setFrameOffsets(0, 0);
    }
}
