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
package com.b3dgs.lionheart;

import java.util.Optional;

/**
 * Checkpoint data.
 */
public final class Checkpoint
{
    /** Horizontal tile. */
    private final int tx;
    /** Vertical tile. */
    private final int ty;
    /** Next stage. */
    private final Optional<String> next;

    /**
     * Create checkpoint.
     * 
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     * @param next The next stage.
     */
    public Checkpoint(int tx, int ty, Optional<String> next)
    {
        super();

        this.tx = tx;
        this.ty = ty;
        this.next = next;
    }

    /**
     * Get the horizontal tile.
     * 
     * @return The horizontal tile.
     */
    public int getTx()
    {
        return tx;
    }

    /**
     * Get the vertical tile.
     * 
     * @return The vertical tile.
     */
    public int getTy()
    {
        return ty;
    }

    /**
     * Get the next stage.
     * 
     * @return The next stage.
     */
    public Optional<String> getNext()
    {
        return next;
    }
}
