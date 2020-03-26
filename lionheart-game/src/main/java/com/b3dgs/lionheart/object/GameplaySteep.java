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
package com.b3dgs.lionheart.object;

import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.constant.CollisionName;

/**
 * Steep gameplay checker.
 */
public final class GameplaySteep
{
    private boolean steep;
    private boolean steepLeft;
    private boolean steepRight;

    private int side = 1;

    /**
     * Reset steep flags.
     */
    public void reset()
    {
        steep = false;
        steepLeft = false;
        steepRight = false;
    }

    /**
     * Get steep side.
     * 
     * @return The steep side.
     */
    public int getSide()
    {
        return side;
    }

    /**
     * Get steep flag.
     * 
     * @return The steep flag.
     */
    public boolean is()
    {
        return steep;
    }

    /**
     * Get steep left flag.
     * 
     * @return The steep left flag.
     */
    public boolean isLeft()
    {
        return steepLeft;
    }

    /**
     * Get steep right flag.
     * 
     * @return The steep right flag.
     */
    public boolean isRight()
    {
        return steepRight;
    }

    /**
     * Called when a tile collision occurred on vertical axis with leg.
     * 
     * @param result The collided tile.
     * @param category The collided axis.
     */
    public void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        if (result.startWithY(CollisionName.STEEP_LEFT))
        {
            side = -1;
            steep = true;
            steepLeft = true;
        }
        else if (result.startWithY(CollisionName.STEEP_RIGHT))
        {
            side = 1;
            steep = true;
            steepRight = true;
        }
    }

    /**
     * Called when a tile collision occurred on horizontal axis with knee.
     * 
     * @param result The collided tile.
     * @param category The collided axis.
     */
    public void onCollideKnee(CollisionResult result, CollisionCategory category)
    {
        if (result.startWithX(CollisionName.STEEP_LEFT))
        {
            steep = true;
            steepLeft = true;
        }
        else if (result.startWithX(CollisionName.STEEP_RIGHT))
        {
            steep = true;
            steepRight = true;
        }
    }
}
