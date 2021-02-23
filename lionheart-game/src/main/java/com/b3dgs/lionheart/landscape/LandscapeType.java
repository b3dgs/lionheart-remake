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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;
import com.b3dgs.lionheart.WorldType;

/**
 * Types of landscapes.
 */
public enum LandscapeType
{
    /** Swamp dusk. */
    SWAMP_DUSK(WorldType.SWAMP, "dusk", "raster1.xml", ForegroundType.WATER),
    /** Swamp dawn. */
    SWAMP_DAWN(WorldType.SWAMP, "dawn", "raster2.xml", ForegroundType.WATER),
    /** Swamp day. */
    SWAMP_DAY(WorldType.SWAMP, "day", "raster3.xml", ForegroundType.WATER),
    /** Ancient Town dusk. */
    ANCIENT_TOWN_DUSK(WorldType.ANCIENT_TOWN, "dusk", "raster1.xml", ForegroundType.WATER),
    /** Ancient Town dawn. */
    ANCIENT_TOWN_DAWN(WorldType.ANCIENT_TOWN, "dawn", "raster2.xml", ForegroundType.WATER),
    /** Ancient Town day. */
    ANCIENT_TOWN_DAY(WorldType.ANCIENT_TOWN, "day", "raster3.xml", ForegroundType.WATER),
    /** Lava. */
    LAVA(WorldType.LAVA, "lava", "raster.xml", ForegroundType.LAVA),
    /** Secret. */
    SECRET(WorldType.SECRET, "secret", "raster.xml", ForegroundType.WATER),
    /** Airship. */
    AIRSHIP(WorldType.AIRSHIP, "airship", "raster.xml", ForegroundType.AIRSHIP),
    /** Dragonfly. */
    DRAGONFLY(WorldType.DRAGONFLY, "dragonfly", "raster.xml", ForegroundType.NONE),
    /** Tower. */
    TOWER(WorldType.TOWER, "tower", "raster3.xml", ForegroundType.WATER);

    /**
     * Get all landscapes related to the world.
     * 
     * @param world The world used as reference.
     * @return The landscapes of this world.
     */
    public static LandscapeType[] getWorldLandscape(WorldType world)
    {
        final Collection<LandscapeType> landscapes = new ArrayList<>(3);
        for (final LandscapeType landscape : LandscapeType.values())
        {
            if (landscape.getWorld() == world)
            {
                landscapes.add(landscape);
            }
        }
        return landscapes.toArray(new LandscapeType[landscapes.size()]);
    }

    /**
     * Load type from its saved format.
     * 
     * @param file The file reading.
     * @return The loaded type.
     * @throws IOException If error.
     */
    public static LandscapeType load(FileReading file) throws IOException
    {
        return LandscapeType.valueOf(file.readString());
    }

    /** World type. */
    private final WorldType world;
    /** Theme name. */
    private final String theme;
    /** Raster name. */
    private final String raster;
    /** The foreground used. */
    private final ForegroundType foreground;

    /**
     * Constructor.
     * 
     * @param world The world type.
     * @param theme The theme name.
     * @param raster The raster name.
     * @param water The water type.
     */
    LandscapeType(WorldType world, String theme, String raster, ForegroundType water)
    {
        this.world = world;
        this.theme = theme;
        this.raster = raster;
        foreground = water;
    }

    /**
     * Save the landscape type.
     * 
     * @param file The file writing.
     * @throws IOException If error.
     */
    public void save(FileWriting file) throws IOException
    {
        file.writeString(name());
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

    /**
     * Get the raster filename.
     * 
     * @return The raster filename.
     */
    public String getRaster()
    {
        return raster;
    }

    /**
     * Get the foreground used.
     * 
     * @return The foreground used.
     */
    public ForegroundType getForeground()
    {
        return foreground;
    }

    @Override
    public String toString()
    {
        final String string = name().toLowerCase(Locale.ENGLISH).replace('_', ' ');
        return Character.toString(string.charAt(0)).toUpperCase(Locale.ENGLISH) + string.substring(1);
    }
}
