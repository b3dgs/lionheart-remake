/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionheart.landscape.WorldType;

/**
 * List of levels with their file.
 */
public enum Level
{
    /** First swamp stage. */
    SWAMP_1_1(WorldType.SWAMP, "level1-1"),
    /** First swamp stage. */
    SWAMP_1_3(WorldType.SWAMP, "level1-3"),
    /** First swamp stage. */
    SWAMP_1_5(WorldType.SWAMP, "level1-5");

    /** Levels folder. */
    public static final String DIR = "levels";
    /** Levels file extension. */
    public static final String EXTENSION = ".lrl";

    /** Level file. */
    private final Media level;
    /** Level rip image. */
    private final Media rip;

    /**
     * Create the level.
     * 
     * @param world The level world.
     * @param level The level file name.
     */
    Level(WorldType world, String level)
    {
        this.level = Medias.create(DIR, world.getFolder(), level + EXTENSION);
        rip = Medias.create(DIR, world.getFolder(), level + ".png");
    }

    /**
     * Get the level data file.
     * 
     * @return The level data file.
     */
    public Media getFile()
    {
        return level;
    }

    /**
     * Get the associated level rip.
     * 
     * @return The level rip image.
     */
    public Media getRip()
    {
        return rip;
    }
}
