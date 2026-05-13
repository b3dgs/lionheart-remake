/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.narration;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;

/**
 * Step description.
 */
interface Step extends Updatable, Renderable
{
    /**
     * Init step.
     */
    default void init()
    {
        // Nothing by default
    }

    @Override
    default void update(double extrp)
    {
        // Nothing by default
    }

    @Override
    default void render(Graphic g)
    {
        // Nothing by default
    }

    /**
     * Get done state.
     * 
     * @return <code>true</code> if done, <code>false</code> else.
     */
    default boolean isDone()
    {
        return false;
    }
}
