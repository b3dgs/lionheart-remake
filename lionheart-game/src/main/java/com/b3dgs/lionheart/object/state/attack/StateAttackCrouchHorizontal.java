/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;

/**
 * Crouch horizontal attack state implementation.
 */
final class StateAttackCrouchHorizontal extends State
{
    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateAttackCrouchHorizontal(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StatePreparedAttackCrouch.class, () -> is(AnimState.FINISHED));
    }

    @Override
    public void enter()
    {
        super.enter();

        if (device.getHorizontalDirection() < 0 && is(Mirror.NONE))
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else if (device.getHorizontalDirection() > 0 && is(Mirror.HORIZONTAL))
        {
            mirrorable.mirror(Mirror.NONE);
        }

        movement.zero();

        Sfx.VALDYN_SWORD.play();
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        movement.zero();
    }
}
