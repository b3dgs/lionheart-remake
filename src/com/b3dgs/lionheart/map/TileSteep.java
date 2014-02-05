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
package com.b3dgs.lionheart.map;

import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Tile steep implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class TileSteep
        extends Tile
{
    /**
     * @see Tile#Tile(int, int, Integer, int, TileCollision)
     */
    public TileSteep(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        super(width, height, pattern, number, collision);
    }

    /**
     * Get the steep collision.
     * 
     * @param c The collision type.
     * @param localizable The localizable.
     * @param offset The offset.
     * @return The collision.
     */
    private Double getSteep(TileCollision c, Localizable localizable, int offset)
    {
        final int startY = getTop() + 8;
        return getCollisionY(c.getGroup(), localizable, startY, offset, -10, 0, -2);
    }

    /*
     * TilePlatform
     */

    @Override
    public Double getCollisionY(Localizable localizable)
    {
        final TileCollision c = getCollision();
        switch (c)
        {
            case STEEP_RIGHT_1:
                return getSteep(c, localizable, -6);
            case STEEP_RIGHT_2:
                return getSteep(c, localizable, 10);

            case STEEP_LEFT_1:
                return getSteep(c, localizable, -6);
            case STEEP_LEFT_2:
                return getSteep(c, localizable, 10);

            default:
                return null;
        }
    }
}
