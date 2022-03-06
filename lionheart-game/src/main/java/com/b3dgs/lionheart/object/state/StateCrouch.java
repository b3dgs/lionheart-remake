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
package com.b3dgs.lionheart.object.state;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionheart.GameplayType;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.state.attack.StateAttackCrouchHorizontal;
import com.b3dgs.lionheart.object.state.attack.StatePrepareAttackCrouch;

/**
 * Crouch state implementation.
 */
public final class StateCrouch extends State
{
    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateCrouch(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateIdle.class, () -> !isGoDown());
        addTransition(StateFall.class,
                      () -> !collideY.get() && Double.compare(transformable.getY(), transformable.getOldY()) != 0);
        addTransition(StateWin.class, this::hasWin);

        if (Settings.getInstance().getGameplay() == GameplayType.ORIGINAL)
        {
            addTransition(StatePrepareAttackCrouch.class, this::isFire);
        }
        else
        {
            addTransition(StateAttackCrouchHorizontal.class, this::isFire);
        }
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.zero();
        jump.zero();
        body.resetGravity();
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        if (isGoLeft())
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else if (isGoRight())
        {
            mirrorable.mirror(Mirror.NONE);
        }
    }
}
