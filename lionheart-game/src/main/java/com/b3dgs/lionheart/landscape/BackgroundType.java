/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
    SWAMP_DAY(WorldType.SWAMP, "day"),
    /** Spider cave 1. */
    SPIDERCAVE1(WorldType.SPIDERCAVE1, null),
    /** Spider cave 2. */
    SPIDERCAVE2(WorldType.SPIDERCAVE2, null),
    /** Ancient Town dusk. */
    ANCIENT_TOWN_DUSK(WorldType.ANCIENT_TOWN, "dusk"),
    /** Ancient Town dawn. */
    ANCIENT_TOWN_DAWN(WorldType.ANCIENT_TOWN, "dawn"),
    /** Ancient Town day. */
    ANCIENT_TOWN_DAY(WorldType.ANCIENT_TOWN, "day"),
    /** Lava. */
    LAVA(WorldType.LAVA, "lava"),
    /** Secret. */
    SECRET(WorldType.SECRET, "secret"),
    /** Airship. */
    AIRSHIP(WorldType.AIRSHIP, "airship"),
    /** Dragonfly. */
    DRAGONFLY(WorldType.DRAGONFLY, "dragonfly"),
    /** Tower. */
    TOWER(WorldType.TOWER, "tower"),
    /** Norka. */
    NORKA(WorldType.NORKA, "norka"),
    /** None. */
    NONE(null, null);

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
