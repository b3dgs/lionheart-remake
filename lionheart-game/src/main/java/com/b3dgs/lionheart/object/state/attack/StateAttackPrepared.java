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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;

/**
 * Prepared attack state implementation.
 */
final class StateAttackPrepared extends State
{
    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateAttackPrepared(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateAttackHorizontal.class, () -> control.isFireButton() && canAttackHorizontal());
        addTransition(StateAttackTurning.class, () -> control.isFireButton() && canAttackTurning());
        addTransition(StateAttackTop.class, () -> control.isFireButton() && isGoingUpOnce());
        addTransition(StateAttackCrouchPrepared.class, () -> isGoingDown());
        addTransition(StateAttackUnprepare.class, () -> !control.isFireButton());
    }

    private boolean canAttackHorizontal()
    {
        return mirrorable.is(Mirror.HORIZONTAL) && isGoingLeftOnce()
               || mirrorable.is(Mirror.NONE) && isGoingRightOnce();
    }

    private boolean canAttackTurning()
    {
        return mirrorable.is(Mirror.NONE) && isGoingLeftOnce()
               || mirrorable.is(Mirror.HORIZONTAL) && isGoingRightOnce();
    }
}
