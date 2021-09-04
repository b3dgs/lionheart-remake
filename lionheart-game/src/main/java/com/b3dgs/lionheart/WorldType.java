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
package com.b3dgs.lionheart;

import java.util.Locale;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.UtilConversion;

/**
 * List of world types.
 * <p>
 * World title is enum name as title case and underscores replaced by space.
 * </p>
 */
public enum WorldType
{
    /** Swamp world. */
    SWAMP,
    /** Spider cave 1 world. */
    SPIDERCAVE1,
    /** Spider cave 2 world. */
    SPIDERCAVE2,
    /** Spider cave 3 world. */
    SPIDERCAVE3,
    /** Ancient town world. */
    ANCIENTTOWN,
    /** Lava world. */
    LAVA,
    /** Secret world. */
    SECRET,
    /** Airship world. */
    AIRSHIP,
    /** Dragonfly world. */
    DRAGONFLY,
    /** Tower world. */
    TOWER,
    /** Norka world. */
    NORKA;

    /** Associated folder. */
    private final String folder;
    /** To string. */
    private final String toString;

    /**
     * Constructor.
     */
    WorldType()
    {
        folder = name().toLowerCase(Locale.ENGLISH);
        toString = UtilConversion.toTitleCase(name().replace(Constant.UNDERSCORE, Constant.SPACE));
    }

    /**
     * Get the world folder.
     * 
     * @return The world folder.
     */
    public String getFolder()
    {
        return folder;
    }

    @Override
    public String toString()
    {
        return toString;
    }
}
