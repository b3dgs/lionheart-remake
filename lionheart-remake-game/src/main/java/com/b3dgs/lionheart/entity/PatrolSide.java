/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.entity;

/**
 * Represents the patrol sides.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum PatrolSide
{
    /** Patrol to min. */
    MIN(-1),
    /** Patrol to max. */
    MAX(1);

    /** Move value. */
    private final int move;

    /**
     * The movement value.
     * 
     * @param move The movement value.
     */
    private PatrolSide(int move)
    {
        this.move = move;
    }

    /**
     * Get the move value.
     * 
     * @return The move value.
     */
    public int getMove()
    {
        return move;
    }
}
