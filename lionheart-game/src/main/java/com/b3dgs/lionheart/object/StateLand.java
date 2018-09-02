/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Tick;

/**
 * Land state implementation.
 */
final class StateLand extends State
{
    private static final long LAND_TICK = 10L;

    private final Tick landed = new Tick();

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateLand(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateIdle.class, () -> !this.isGoingDown() && landed.elapsed(LAND_TICK));
        addTransition(StateJump.class, this::isGoingUp);
        addTransition(StateCrouch.class, this::isGoingDown);
    }

    @Override
    public void enter()
    {
        super.enter();

        landed.restart();
    }

    @Override
    public void update(double extrp)
    {
        landed.update(extrp);

        final double side = control.getHorizontalDirection();
        movement.setDestination(side * 3.0, 0.0);

        if (mirrorable.getMirror() == Mirror.NONE && movement.getDirectionHorizontal() < 0.0)
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else if (mirrorable.getMirror() == Mirror.HORIZONTAL && movement.getDirectionHorizontal() > 0.0)
        {
            mirrorable.mirror(Mirror.NONE);
        }
    }
}
