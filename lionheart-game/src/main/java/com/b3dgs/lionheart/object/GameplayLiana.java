/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * Liana gameplay checker.
 */
public final class GameplayLiana
{
    private boolean liana;
    private boolean lianaLeft;
    private boolean lianaRight;

    private int side = 1;

    /**
     * Create gameplay.
     */
    public GameplayLiana()
    {
        super();
    }

    /**
     * Reset steep flags.
     */
    public void reset()
    {
        liana = false;
        lianaLeft = false;
        lianaRight = false;
    }

    /**
     * Get liana side.
     * 
     * @return The liana side.
     */
    public int getSide()
    {
        return side;
    }

    /**
     * Get liana flag.
     * 
     * @return The liana flag.
     */
    public boolean is()
    {
        return liana;
    }

    /**
     * Get liana left flag.
     * 
     * @return The liana left flag.
     */
    public boolean isLeft()
    {
        return lianaLeft;
    }

    /**
     * Get liana right flag.
     * 
     * @return The liana right flag.
     */
    public boolean isRight()
    {
        return lianaRight;
    }

    /**
     * Called when a tile collision occurred on vertical axis with hand.
     * 
     * @param result The collided tile.
     * @param category The collided axis.
     */
    public void onCollideHand(CollisionResult result, CollisionCategory category)
    {
        if (result.startWithY(CollisionName.LIANA))
        {
            liana = true;
            if (result.containsY(CollisionName.LEFT))
            {
                side = -1;
                lianaLeft = true;
            }
            else if (result.containsY(CollisionName.RIGHT))
            {
                side = 1;
                lianaRight = true;
            }
        }
    }
}
