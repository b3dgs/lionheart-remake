/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;

/**
 * Jump state implementation.
 */
public final class StatePrepareJump extends State
{
    private static final long TICK = 50;

    private final Tick prepareTick = new Tick();

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StatePrepareJump(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateJump.class, () -> prepareTick.elapsed(TICK));
    }

    @Override
    public void enter()
    {
        super.enter();

        prepareTick.restart();
    }

    @Override
    public void update(double extrp)
    {
        prepareTick.update(extrp);
        movement.setDirection(DirectionNone.INSTANCE);
        movement.setDestination(0.0, 0.0);
        jump.setDirection(DirectionNone.INSTANCE);
    }

    @Override
    public void exit()
    {
        super.exit();

        prepareTick.stop();
    }
}
