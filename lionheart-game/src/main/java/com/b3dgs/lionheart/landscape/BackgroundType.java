/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
    SWAMP_DUSK(WorldType.SWAMP, ThemeType.DUSK),
    /** Swamp dawn. */
    SWAMP_DAWN(WorldType.SWAMP, ThemeType.DAWN),
    /** Swamp day. */
    SWAMP_DAY(WorldType.SWAMP, ThemeType.DAY),
    /** Swamp cold. */
    SWAMP_COLD(WorldType.SWAMP, ThemeType.COLD),
    /** Underworld. */
    UNDERWORLD(WorldType.UNDERWORLD, ThemeType.UNDERWORLD),
    /** Spider cave 1. */
    SPIDERCAVE1(WorldType.SPIDERCAVE1, null),
    /** Spider cave 2. */
    SPIDERCAVE2(WorldType.SPIDERCAVE2, null),
    /** Spider cave 3. */
    SPIDERCAVE3(WorldType.SPIDERCAVE3, null),
    /** Ancient Town dusk. */
    ANCIENTTOWN_DUSK(WorldType.ANCIENTTOWN, ThemeType.DUSK),
    /** Ancient Town dawn. */
    ANCIENTTOWN_DAWN(WorldType.ANCIENTTOWN, ThemeType.DAWN),
    /** Ancient Town day. */
    ANCIENTTOWN_DAY(WorldType.ANCIENTTOWN, ThemeType.DAY),
    /** Ancient Town cold. */
    ANCIENTTOWN_COLD(WorldType.ANCIENTTOWN, ThemeType.COLD),
    /** Lava. */
    LAVA(WorldType.LAVA, ThemeType.LAVA),
    /** Lava. */
    LAVA_HOT(WorldType.LAVA, ThemeType.HOT),
    /** Secret. */
    SECRET(WorldType.SECRET, ThemeType.SECRET),
    /** Secret cold. */
    SECRET_COLD(WorldType.SECRET, ThemeType.COLD),
    /** Airship. */
    AIRSHIP(WorldType.AIRSHIP, ThemeType.AIRSHIP),
    /** Airship cold. */
    AIRSHIP_COLD(WorldType.AIRSHIP, ThemeType.COLD),
    /** Dragonfly. */
    DRAGONFLY(WorldType.DRAGONFLY, ThemeType.DRAGONFLY),
    /** Dragonfly cold. */
    DRAGONFLY_COLD(WorldType.DRAGONFLY, ThemeType.COLD),
    /** Tower. */
    TOWER(WorldType.TOWER, ThemeType.TOWER),
    /** Tower cold. */
    TOWER_COLD(WorldType.TOWER, ThemeType.COLD),
    /** Norka. */
    NORKA(WorldType.NORKA, ThemeType.NORKA),
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
    BackgroundType(WorldType world, ThemeType theme)
    {
        this.world = world;
        this.theme = theme != null ? theme.getName() : null;
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
