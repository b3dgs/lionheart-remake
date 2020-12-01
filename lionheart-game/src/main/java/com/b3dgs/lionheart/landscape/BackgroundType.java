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
package com.b3dgs.lionheart.landscape;

import com.b3dgs.lionheart.WorldType;

/**
 * Types of backgrounds.
 */
public enum BackgroundType
{
    /** Swamp dusk. */
    SWAMP_DUSK(WorldType.SWAMP, "dusk"),
    /** Swamp dawn. */
    SWAMP_DAWN(WorldType.SWAMP, "dawn"),
    /** Swamp day. */
    SWAMP_DAY(WorldType.SWAMP, "day");

    /** World type. */
    private final WorldType world;
    /** Theme name. */
    private final String theme;

    /**
     * Constructor.
     * 
     * @param world The world type.
     * @param theme The theme name.
     */
    BackgroundType(WorldType world, String theme)
    {
        this.world = world;
        this.theme = theme;
    }

    /**
     * Get the world type.
     * 
     * @return The world type.
     */
    public WorldType getWorld()
    {
        return world;
    }

    /**
     * Get the theme name.
     * 
     * @return The theme name.
     */
    public String getTheme()
    {
        return theme;
    }
}
