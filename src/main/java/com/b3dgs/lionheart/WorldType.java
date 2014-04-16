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

import java.io.IOException;
import java.util.Locale;

import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;

/**
 * List of world types.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum WorldType
{
    /** Swamp world. */
    SWAMP("Swamp", Music.SWAMP),
    /** Ancient town world. */
    ANCIENT_TOWN("Ancient Town", Music.ANCIENT_TOWN);

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

    /** Title displayed. */
    private final String title;
    /** World music. */
    private final Music music;

    /**
     * Constructor.
     * 
     * @param title The displayed title.
     * @param music The music type.
     */
    private WorldType(String title, Music music)
    {
        this.title = title;
        this.music = music;
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
    public Music getMusic()
    {
        return music;
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

    /*
     * Object
     */

    @Override
    public String toString()
    {
        return title;
    }
}
