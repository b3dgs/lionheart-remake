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
package com.b3dgs.lionheart.object;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;

/**
 * Steep gameplay checker.
 */
public final class GameplaySteep
{
    private final AtomicBoolean steep = new AtomicBoolean();
    private final AtomicBoolean steepLeft = new AtomicBoolean();
    private final AtomicBoolean steepRight = new AtomicBoolean();

    private int side = 1;

    /**
     * Reset steep flags.
     */
    public void reset()
    {
        steep.set(false);
        steepLeft.set(false);
        steepRight.set(false);
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
        return steep.get();
    }

    /**
     * Get steep left flag.
     * 
     * @return The steep left flag.
     */
    public boolean isLeft()
    {
        return steepLeft.get();
    }

    /**
     * Get steep right flag.
     * 
     * @return The steep right flag.
     */
    public boolean isRight()
    {
        return steepRight.get();
    }

    /**
     * Called when a tile collision occurred on vertical axis with leg.
     * 
     * @param result The collided tile.
     * @param category The collided axis.
     */
    public void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        if (result.startWithY(Constant.COLL_PREFIX_STEEP_LEFT))
        {
            side = -1;
            steep.set(true);
            steepLeft.set(true);
        }
        else if (result.startWithY(Constant.COLL_PREFIX_STEEP_RIGHT))
        {
            side = 1;
            steep.set(true);
            steepRight.set(true);
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
        if (result.startWithX(Constant.COLL_PREFIX_STEEP_LEFT))
        {
            steep.set(true);
            steepLeft.set(true);
        }
        else if (result.startWithX(Constant.COLL_PREFIX_STEEP_RIGHT))
        {
            steep.set(true);
            steepRight.set(true);
        }
    }
}
