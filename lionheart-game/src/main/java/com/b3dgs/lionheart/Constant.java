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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;

/**
 * Game constants.
 */
public final class Constant
{
    /** Application name. */
    public static final String NAME = "Lionheart Remake";
    /** Application version. */
    public static final Version VERSION = Version.create(0, 0, 2);
    /** Original display. */
    public static final Resolution NATIVE = new Resolution(360, 200, 60);

    /** Sprites folder. */
    public static final String FOLDER_SPRITES = "sprite";
    /** Backgrounds folder. */
    public static final String FOLDER_BACKGROUNDS = "background";
    /** Backgrounds folder. */
    public static final String FOLDER_FOREGROUNDS = "foreground";
    /** Entities folder. */
    public static final String FOLDER_ENTITIES = "entity";
    /** Items folder. */
    public static final String FOLDER_ITEMS = "item";
    /** Monsters folder. */
    public static final String FOLDER_MONSTERS = "monster";
    /** Sceneries folder. */
    public static final String FOLDER_SCENERIES = "scenery";
    /** Players folder. */
    public static final String FOLDER_PLAYERS = "player";
    /** Levels folder. */
    public static final String FOLDER_LEVELS = "levels";
    /** Levels file extension (with dot). */
    public static final String LEVEL_EXTENSION = ".lrl";
    /** Musics file extension (with dot). */
    public static final String MUSIC_EXTENSION = ".sc68";

    /**
     * Private constructor.
     */
    private Constant()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
