/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.UtilFolder;
import com.b3dgs.lionengine.Version;

/**
 * Game constants.
 */
public final class Constant
{
    /** Application name. */
    public static final String PROGRAM_NAME = "Lionheart Remake";
    /** Application version. */
    public static final Version PROGRAM_VERSION = Version.create(0, 0, 15);

    /** Original display. */
    public static final Resolution NATIVE_RESOLUTION = new Resolution(280, 220, 60);
    /** Default display. */
    public static final Resolution DEFAULT_RESOLUTION = new Resolution(320, 256, NATIVE_RESOLUTION.getRate());

    /** Debug flag (shows collisions). */
    public static final boolean DEBUG = false;

    /** Sprites folder. */
    public static final String FOLDER_SPRITES = "sprite";
    /** Backgrounds folder. */
    public static final String FOLDER_BACKGROUNDS = "background";
    /** Backgrounds folder. */
    public static final String FOLDER_FOREGROUNDS = "foreground";
    /** Entities folder. */
    public static final String FOLDER_ENTITIES = "entity";
    /** Items folder. */
    public static final String FOLDER_ITEMS = UtilFolder.getPath(FOLDER_ENTITIES, "item");
    /** Monsters folder. */
    public static final String FOLDER_MONSTERS = UtilFolder.getPath(FOLDER_ENTITIES, "monster");
    /** Sceneries folder. */
    public static final String FOLDER_SCENERIES = UtilFolder.getPath(FOLDER_ENTITIES, "scenery");
    /** Players folder. */
    public static final String FOLDER_PLAYERS = UtilFolder.getPath(FOLDER_ENTITIES, "player");
    /** Effects folder. */
    public static final String FOLDER_EFFECTS = "effect";
    /** Levels folder. */
    public static final String FOLDER_LEVELS = "levels";
    /** Sounds folder. */
    public static final String FOLDER_SOUNDS = "sfx";
    /** Musics folder. */
    public static final String FOLDER_MUSICS = "music";

    /** Collision group player. */
    public static final Integer COLL_GROUP_PLAYER = Integer.valueOf(1);
    /** Collision group enemies. */
    public static final Integer COLL_GROUP_ENEMIES = Integer.valueOf(2);
    /** Collision group background. */
    public static final Integer COLL_GROUP_BACKGROUND = Integer.valueOf(3);

    /** Animation name take. */
    public static final String ANIM_NAME_TAKE = "take";
    /** Animation name walk. */
    public static final String ANIM_NAME_TURN = "turn";
    /** Animation leg prefix. */
    public static final String ANIM_PREFIX_LEG = "leg";
    /** Animation attack prefix. */
    public static final String ANIM_PREFIX_ATTACK = "attack";
    /** Animation body prefix. */
    public static final String ANIM_PREFIX_BODY = "body";
    /** Animation shade prefix. */
    public static final String ANIM_PREFIX_SHADE = "shade_";

    /** Collision ground prefix. */
    public static final String COLL_PREFIX_GROUND = "ground";
    /** Collision spike prefix. */
    public static final String COLL_PREFIX_SPIKE = "spike";
    /** Collision slope prefix. */
    public static final String COLL_PREFIX_SLOPE = "slope";
    /** Collision slope prefix. */
    public static final String COLL_PREFIX_SLOPE_LEFT = COLL_PREFIX_SLOPE + "_left";
    /** Collision slope prefix. */
    public static final String COLL_PREFIX_SLOPE_RIGHT = COLL_PREFIX_SLOPE + "_right";
    /** Collision steep prefix. */
    public static final String COLL_PREFIX_STEEP = "steep";
    /** Collision steep prefix. */
    public static final String COLL_PREFIX_STEEP_LEFT = COLL_PREFIX_STEEP + "_left";
    /** Collision steep prefix. */
    public static final String COLL_PREFIX_STEEP_RIGHT = COLL_PREFIX_STEEP + "_right";

    /** Levels file extension (with dot). */
    public static final String EXTENSION_LEVEL = ".lrl";
    /** Sounds file extension (with dot). */
    public static final String EXTENSION_SFX = ".wav";
    /** Musics file extension (with dot). */
    public static final String EXTENSION_MUSIC = ".sc68";

    /** Stats maximum health. */
    public static final int STATS_MAX_HEALTH = 99;
    /** Stats maximum talisment. */
    public static final int STATS_MAX_TALISMENT = 99;
    /** Stats maximum life. */
    public static final int STATS_MAX_LIFE = 99;

    /** Jump minimum height. */
    public static final double JUMP_MIN = 2.5;
    /** Jump maximum height on hit. */
    public static final double JUMP_HIT = 3.75;
    /** Jump maximum height. */
    public static final double JUMP_MAX = 5.4;

    /**
     * Private constructor.
     */
    private Constant()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
