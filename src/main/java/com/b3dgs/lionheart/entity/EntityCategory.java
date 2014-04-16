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

import java.util.Locale;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilityConversion;
import com.b3dgs.lionheart.entity.player.Valdyn;

/**
 * List of entity categories.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum EntityCategory
{
    /** Item (can be taken). */
    ITEM,
    /** Monster (can be destroyed and attack the player). */
    MONSTER,
    /** Scenery (other objects that are required to complete a level). */
    SCENERY,
    /** Valdyn (player). */
    PLAYER;

    /**
     * Get the race enum from the class type.
     * 
     * @param type The class type.
     * @return The enum race type.
     */
    public static EntityCategory getCategory(Class<? extends Entity> type)
    {
        if (EntityItem.class.isAssignableFrom(type))
        {
            return ITEM;
        }
        else if (EntityMonster.class.isAssignableFrom(type))
        {
            return MONSTER;
        }
        else if (EntityScenery.class.isAssignableFrom(type))
        {
            return SCENERY;
        }
        else if (Valdyn.class.isAssignableFrom(type))
        {
            return PLAYER;
        }
        throw new LionEngineException("Unknown category for ", type.getName());
    }

    /** Count number. */
    private int count;

    /**
     * Get the count number.
     * 
     * @return The count number.
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Increase the count number.
     */
    public void increase()
    {
        count++;
    }

    /**
     * Get the race path.
     * 
     * @return The race path.
     */
    public String getPath()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }

    /*
     * Object
     */

    @Override
    public String toString()
    {
        return UtilityConversion.toTitleCase(name());
    }
}
