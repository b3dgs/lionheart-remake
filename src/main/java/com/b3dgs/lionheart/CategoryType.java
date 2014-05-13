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
package com.b3dgs.lionheart;

import java.util.Locale;

import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.EntityItem;
import com.b3dgs.lionheart.entity.EntityMonster;
import com.b3dgs.lionheart.entity.EntityScenery;
import com.b3dgs.lionheart.entity.player.Valdyn;

/**
 * List of category types.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum CategoryType
{
    /** Player. */
    PLAYER,
    /** Item. */
    ITEM,
    /** Monster. */
    MONSTER,
    /** Scenery. */
    SCENERY,
    /** None. */
    NONE;

    /**
     * Get the folder enum from the class type.
     * 
     * @param type The class type.
     * @return The enum folder type.
     */
    public static CategoryType getType(Class<? extends Entity> type)
    {
        if (EntityItem.class.isAssignableFrom(type))
        {
            return ITEM;
        }
        if (EntityMonster.class.isAssignableFrom(type))
        {
            return MONSTER;
        }
        if (EntityScenery.class.isAssignableFrom(type))
        {
            return SCENERY;
        }
        if (Valdyn.class.isAssignableFrom(type))
        {
            return PLAYER;
        }
        return NONE;
    }

    /**
     * Get the world path.
     * 
     * @return The world path.
     */
    public String getPath()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
