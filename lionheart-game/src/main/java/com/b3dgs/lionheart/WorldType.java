/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.io.IOException;
import java.util.Locale;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * List of world types.
 * <p>
 * World title is enum name as title case and underscores replaced by space.
 * </p>
 */
public enum WorldType
{
    /** Swamp world. */
    SWAMP(Music.SWAMP),
    /** Ancient town world. */
    ANCIENT_TOWN(Music.ANCIENT_TOWN);

    /**
     * Load type from its saved format.
     * 
     * @param file The file reading.
     * @return The loaded type.
     * @throws IOException If error.
     */
    public static WorldType load(FileReading file) throws IOException
    {
        return WorldType.valueOf(file.readString());
    }

    /** World music. */
    private final Media music;

    /**
     * Constructor.
     * 
     * @param music The music type.
     */
    WorldType(Music music)
    {
        this.music = music.get();
    }

    /**
     * Save the world type.
     * 
     * @param file The file writing.
     * @throws IOException If error.
     */
    public void save(FileWriting file) throws IOException
    {
        file.writeString(name());
    }

    /**
     * Get the music type.
     * 
     * @return The music type.
     */
    public Media getMusic()
    {
        return music;
    }

    /**
     * Get the world folder.
     * 
     * @return The world folder.
     */
    public String getFolder()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String toString()
    {
        return UtilConversion.toTitleCase(name().replace(Constant.UNDERSCORE, Constant.SPACE));
    }
}
