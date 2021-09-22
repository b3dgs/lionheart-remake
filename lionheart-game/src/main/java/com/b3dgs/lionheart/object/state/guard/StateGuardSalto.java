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
package com.b3dgs.lionheart.object.state.guard;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;

/**
 * Attack guard state implementation.
 */
public final class StateGuardSalto extends State
{
    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateGuardSalto(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateGuardAttackPrepare.class, () -> collideY.get());
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.zero();
        movement.setVelocity(1);

        int side;
        if (mirrorable.getMirror() == Mirror.HORIZONTAL)
        {
            side = 1;
        }
        else
        {
            side = -1;
        }
        movement.setDirection(side * 1.0, 0.0);
        movement.setDestination(side * 1.0, 0.0);
        jump.setDirection(0.0, 4.0);
        jump.setDestination(0.0, 0.0);
        jump.setVelocity(0.05);
        body.resetGravity();
    }
}
