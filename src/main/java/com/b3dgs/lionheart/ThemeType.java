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

/**
 * List of theme types.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum ThemeType
{
    /** Swamp theme. */
    SWAMP,
    /** Ancient town theme. */
    ANCIENT_TOWN,
    /** None. */
    NONE;

    /**
     * Get the theme enum from the class type.
     * 
     * @param type The class type.
     * @return The enum folder type.
     */
    public static ThemeType getType(Class<? extends Themed> type)
    {
        if (ThemeSwamp.class.isAssignableFrom(type))
        {
            return SWAMP;
        }
        return NONE;
    }

    /**
     * Get the theme path.
     * 
     * @return The theme path.
     */
    public String getPath()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
