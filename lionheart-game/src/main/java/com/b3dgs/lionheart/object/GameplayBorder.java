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
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;

/**
 * Handle borders detection.
 */
public final class GameplayBorder implements TileCollidableListener, CollidableListener
{
    private static final String LEG_CENTER = Anim.LEG + "_center";
    private static final String LEG_LEFT = Anim.LEG + "_left";
    private static final String LEG_RIGHT = Anim.LEG + "_right";

    private final MapTile map;
    private boolean legLeftGround;
    private boolean legRightGround;

    /**
     * Create detection.
     * 
     * @param map The map tile reference.
     */
    public GameplayBorder(MapTile map)
    {
        super();

        this.map = map;
    }

    /**
     * Reset detection.
     */
    public void reset()
    {
        legLeftGround = false;
        legRightGround = false;
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
        return !legLeftGround && legRightGround;
    }

    /**
     * Check if right border.
     * 
     * @return <code>true</code> if right border, <code>false</code> else.
     */
    public boolean isRight()
    {
        return !legRightGround && legLeftGround;
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        final String name = category.getName();
        if (LEG_CENTER.equals(name))
        {
            legLeftGround = true;
            legRightGround = true;
        }
        if (LEG_LEFT.equals(name)
            && (result.startWithY(CollisionName.SLOPE)
                && map.getTile(result.getTile().getInTileX() + 1, result.getTile().getInTileY()) == null
                || result.startWithY(CollisionName.GROUND)))
        {
            legLeftGround = true;
        }
        if (LEG_RIGHT.equals(name)
            && (result.startWithY(CollisionName.SLOPE)
                && map.getTile(result.getTile().getInTileX() - 1, result.getTile().getInTileY()) == null
                || result.startWithY(CollisionName.GROUND)))
        {
            legRightGround = true;
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        final String name = with.getName();
        if (LEG_LEFT.equals(name))
        {
            legLeftGround = true;
        }
        if (LEG_RIGHT.equals(name))
        {
            legRightGround = true;
        }
    }
}
