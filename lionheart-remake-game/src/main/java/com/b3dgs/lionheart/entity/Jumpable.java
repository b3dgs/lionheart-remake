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
 * Jumpable ability for an entity.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Jumpable
{
    /**
     * Set the jumpable flag.
     * 
     * @param jumpable The jumpable flag.
     */
    void setJumpable(boolean jumpable);

    /**
     * Set the jump force speed.
     * 
     * @param jumpForceSpeed The jump force speed.
     */
    void setJumpForceSpeed(int jumpForceSpeed);

    /**
     * Get the jump force.
     * 
     * @return The jump force.
     */
    int getJumpForceSpeed();

    /**
     * Check if crawling is jumpable.
     * 
     * @return <code>true</code> if jumpable, <code>false</code> else.
     */
    boolean isJumpable();
}
