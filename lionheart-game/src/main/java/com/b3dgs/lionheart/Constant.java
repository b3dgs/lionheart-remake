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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;

/**
 * Game constants.
 */
public final class Constant
{
    /** Application name. */
    public static final String PROGRAM_NAME = "Lionheart Remake";
    /** Application version. */
    public static final Version PROGRAM_VERSION = Version.create(0, 3, 0);

    /** Original display. */
    public static final Resolution NATIVE_RESOLUTION = new Resolution(370, 208, 60); // original = 281 * 208
    /** Default display. */
    public static final Resolution DEFAULT_RESOLUTION = new Resolution(1280, 720, NATIVE_RESOLUTION.getRate());

    /** Debug flag (shows collisions). */
    public static final boolean DEBUG = false;
    /** Disable audio flag. */
    public static final boolean AUDIO_MUTE = false;
    /** Audio volume percent. */
    public static final int AUDIO_VOLUME = 50;

    /** Collision group player. */
    public static final Integer COLL_GROUP_PLAYER = Integer.valueOf(1);
    /** Collision group enemies. */
    public static final Integer COLL_GROUP_ENEMIES = Integer.valueOf(2);
    /** Collision group background. */
    public static final Integer COLL_GROUP_BACKGROUND = Integer.valueOf(3);
    /** Collision group projectiles. */
    public static final Integer COLL_GROUP_PROJECTILES = Integer.valueOf(4);

    /** Stats maximum health. */
    public static final int STATS_MAX_HEALTH = 99;
    /** Stats maximum talisment. */
    public static final int STATS_MAX_TALISMENT = 99;
    /** Stats maximum life. */
    public static final int STATS_MAX_LIFE = 99;
    /** Stats maximum sword. */
    public static final int STATS_MAX_SWORD = 3;

    /** Camera horizontal margin. */
    public static final int CAMERA_HORIZONTAL_MARGIN = 16;
    /** Maximum gravity. */
    public static final double GRAVITY = 6.5;
    /** Walk speed. */
    public static final double WALK_SPEED = 5.0 / 3.0;
    /** Minimum speed to start walk. */
    public static final double WALK_MIN_SPEED = 0.75;
    /** Walk velocity on slope decrease. */
    public static final double WALK_VELOCITY_SLOPE_DECREASE = 0.0001;
    /** Walk maximum velocity. */
    public static final double WALK_VELOCITY_MAX = 0.12;
    /** Jump minimum height. */
    public static final double JUMP_MIN = 2.5;
    /** Jump maximum height on hit. */
    public static final double JUMP_HIT = 5.5;
    /** Jump maximum height. */
    public static final Direction JUMP_MAX = new Force(0.0, 5.4);
    /** Jump spider height. */
    public static final Direction JUMP_SPIDER = new Force(0.0, 3.5);

    /** Raster tile file. */
    public static final String RASTER_FILE_TILE = "tiles.png";
    /** Raster water file. */
    public static final String RASTER_FILE_WATER = "water.png";

    /** Fire button 1. */
    public static final Integer FIRE1 = Integer.valueOf(0);
    /** Fire button 2. */
    public static final Integer FIRE2 = Integer.valueOf(1);

    /**
     * Private constructor.
     */
    private Constant()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
