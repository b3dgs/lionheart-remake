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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.state.StateWin;

/**
 * Prepared attack state implementation.
 */
final class StatePreparedAttack extends State
{
    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StatePreparedAttack(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateAttackHorizontal.class, this::canAttackHorizontal);
        addTransition(StateAttackTurning.class, this::canAttackTurning);
        addTransition(StateAttackTop.class, () -> isFire() && isGoUpOnce());
        addTransition(StatePreparedAttackCrouch.class, this::isGoDown);
        addTransition(StateUnprepareAttack.class, () -> !isFire());
        addTransition(StateWin.class, this::hasWin);
    }

    private boolean canAttackHorizontal()
    {
        return isFire() && (is(Mirror.HORIZONTAL) && isGoLeftOnce() || is(Mirror.NONE) && isGoRightOnce());
    }

    private boolean canAttackTurning()
    {
        return isFire() && (is(Mirror.NONE) && isGoLeftOnce() || is(Mirror.HORIZONTAL) && isGoRightOnce());
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.zero();
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        movement.zero();
    }
}
