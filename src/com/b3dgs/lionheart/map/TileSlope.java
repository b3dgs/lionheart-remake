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
 * Tile slope implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class TileSlope
        extends Tile
{
    /**
     * @see Tile#Tile(int, int, Integer, int, TileCollision)
     */
    public TileSlope(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        super(width, height, pattern, number, collision);
    }

    /**
     * Get the slope collision.
     * 
     * @param c The collision type.
     * @param localizable The localizable.
     * @param offset The offset.
     * @return The collision.
     */
    private Double getSlope(TileCollision c, Localizable localizable, int offset)
    {
        return getCollisionY(c.getGroup(), localizable, getTop(), offset, -halfTileHeight, 0, 0);
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
            case SLOPE_RIGHT_1:
                return getSlope(c, localizable, halfTileHeight);
            case SLOPE_RIGHT_2:
                return getSlope(c, localizable, 0);
            case SLOPE_RIGHT_3:
                return getSlope(c, localizable, -halfTileHeight);

            case SLOPE_LEFT_1:
                return getSlope(c, localizable, halfTileHeight);
            case SLOPE_LEFT_2:
                return getSlope(c, localizable, 0);
            case SLOPE_LEFT_3:
                return getSlope(c, localizable, -halfTileHeight);

            default:
                return null;
        }
    }
}
