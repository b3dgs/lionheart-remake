/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.state.attack.StateAttackDragon;

/**
 * Idle dragon state implementation.
 */
public final class StateIdleDragon extends State
{
    private final AtomicBoolean unlock = new AtomicBoolean();

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateIdleDragon(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateAttackDragon.class, this::isFireOnce);
        addTransition(StateJump.class, () ->
        {
            unlock.set(isFire() && isButtonUpOnce());
            return unlock.get();
        });
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        movement.setDestination(device.getHorizontalDirection() * Constant.WALK_SPEED,
                                device.getVerticalDirection() * Constant.WALK_SPEED);

        body.resetGravity();
    }

    @Override
    public void exit()
    {
        super.exit();

        rasterable.setFrameOffsets(0, 0);
        if (unlock.get())
        {
            jump.setDirection(Constant.JUMP_MAX);
            jump.setDirectionMaximum(Constant.JUMP_MAX);
        }
    }
}
