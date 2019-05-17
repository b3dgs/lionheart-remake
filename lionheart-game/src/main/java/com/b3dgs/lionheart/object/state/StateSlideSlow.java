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
package com.b3dgs.lionheart.object.state;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionheart.object.EntityModel;

/**
 * Slide slow state implementation.
 */
public final class StateSlideSlow extends StateSlideBase
{
    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateSlideSlow(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateSlide.class, this::isGoingNone);
        addTransition(StateSlideFast.class,
                      () -> mirrorable.getMirror() == Mirror.NONE && isGoingRight()
                            || mirrorable.getMirror() == Mirror.HORIZONTAL && isGoingLeft());
        setSpeed(0.25);
    }
}
