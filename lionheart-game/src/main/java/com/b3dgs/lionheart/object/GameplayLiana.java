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
 * Liana gameplay checker.
 */
public final class GameplayLiana
{
    private final AtomicBoolean liana = new AtomicBoolean();
    private final AtomicBoolean lianaLeft = new AtomicBoolean();
    private final AtomicBoolean lianaRight = new AtomicBoolean();

    private int side = 1;

    /**
     * Reset steep flags.
     */
    public void reset()
    {
        liana.set(false);
        lianaLeft.set(false);
        lianaRight.set(false);
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
        return liana.get();
    }

    /**
     * Get liana left flag.
     * 
     * @return The liana left flag.
     */
    public boolean isLeft()
    {
        return lianaLeft.get();
    }

    /**
     * Get liana right flag.
     * 
     * @return The liana right flag.
     */
    public boolean isRight()
    {
        return lianaRight.get();
    }

    /**
     * Called when a tile collision occurred on vertical axis with hand.
     * 
     * @param result The collided tile.
     * @param category The collided axis.
     */
    public void onCollideHand(CollisionResult result, CollisionCategory category)
    {
        if (result.startWithY(Constant.COLL_PREFIX_LIANA))
        {
            liana.set(true);
        }
        if (result.startWithY(Constant.COLL_PREFIX_LIANA_LEFT))
        {
            side = -1;
            lianaLeft.set(true);
        }
        else if (result.startWithY(Constant.COLL_PREFIX_LIANA_RIGHT))
        {
            side = 1;
            lianaRight.set(true);
        }
    }
}
