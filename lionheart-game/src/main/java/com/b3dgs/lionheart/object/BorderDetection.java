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

import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;

/**
 * Handle borders detection.
 */
public final class BorderDetection implements TileCollidableListener, CollidableListener
{
    private static final String LEG_LEFT = "leg_left";
    private static final String LEG_RIGHT = "leg_right";

    private boolean legLeft;
    private boolean legRight;

    /**
     * Create detection.
     */
    public BorderDetection()
    {
        super();
    }

    /**
     * Reset detection.
     */
    public void reset()
    {
        legLeft = false;
        legRight = false;
    }

    /**
     * Check if is border.
     * 
     * @return <code>true</code> if border, <code>false</code> else.
     */
    public boolean is()
    {
        return isLeft() || isRight();
    }

    /**
     * Check if left border.
     * 
     * @return <code>true</code> if left border, <code>false</code> else.
     */
    public boolean isLeft()
    {
        return legLeft && !legRight;
    }

    /**
     * Check if right border.
     * 
     * @return <code>true</code> if right border, <code>false</code> else.
     */
    public boolean isRight()
    {
        return legRight && !legLeft;
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        final String name = category.getName();
        if (LEG_LEFT.equals(name))
        {
            legLeft = true;
        }
        if (LEG_RIGHT.equals(name))
        {
            legRight = true;
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision collision)
    {
        final String name = collision.getName();
        if (LEG_LEFT.equals(name))
        {
            legLeft = true;
        }
        if (LEG_RIGHT.equals(name))
        {
            legRight = true;
        }
    }
}
