/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Optional;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Game constants.
 */
public final class Constant
{
    /** Application name. */
    public static final String PROGRAM_NAME = "Lionheart Remake";
    /** Application version. */
    public static final Version PROGRAM_VERSION = Version.create(1, 3, 0);
    /** Application website. */
    public static final String PROGRAM_WEBSITE = "https://lionheart.b3dgs.com";

    /** Resolution. */
    public static final Resolution RESOLUTION = new Resolution(320, 240, 50);
    /** Game resolution. */
    public static final Resolution RESOLUTION_GAME = new Resolution(278, 208, RESOLUTION.getRate());
    /** Resolution. */
    public static final Resolution RESOLUTION_OUTPUT = new Resolution(640, 480, RESOLUTION.getRate());

    /** Debug flag. */
    public static final boolean DEBUG = false;
    /** Debug collisions flag. */
    public static final boolean DEBUG_COLLISIONS = false;

    /** Collision group player. */
    public static final Integer COLL_GROUP_PLAYER = Integer.valueOf(1);
    /** Collision group enemies. */
    public static final Integer COLL_GROUP_ENEMIES = Integer.valueOf(2);
    /** Collision group background. */
    public static final Integer COLL_GROUP_BACKGROUND = Integer.valueOf(3);
    /** Collision group projectiles. */
    public static final Integer COLL_GROUP_PROJECTILES = Integer.valueOf(4);

    /** Stats maximum heart. */
    public static final int STATS_MAX_HEART = 8;
    /** Stats maximum health. */
    public static final int STATS_MAX_HEALTH = 99;
    /** Stats maximum talisment. */
    public static final int STATS_MAX_TALISMENT = 99;
    /** Stats maximum life. */
    public static final int STATS_MAX_LIFE = 99;
    /** Stats maximum sword. */
    public static final int STATS_MAX_SWORD = 4;

    /** Default credits. */
    public static final int CREDITS = 7;
    /** Camera horizontal margin. */
    public static final int CAMERA_HORIZONTAL_MARGIN = 16;
    /** Maximum gravity. */
    public static final double GRAVITY = 7.8;
    /** Walk speed. */
    public static final double WALK_SPEED = 2.0;
    /** Minimum speed to start walk. */
    public static final double WALK_MIN_SPEED = 0.75;
    /** Walk velocity on slope decrease. */
    public static final double WALK_VELOCITY_SLOPE_DECREASE = 0.0001;
    /** Walk maximum velocity. */
    public static final double WALK_VELOCITY_MAX = 0.1;
    /** Jump minimum height. */
    public static final double JUMP_MIN = 2.5;
    /** Jump maximum height on hit. */
    public static final double JUMP_HIT = 5.5;
    /** Jump maximum height. */
    public static final Direction JUMP_MAX = new Force(0.0, 5.75);
    /** Jump spider height. */
    public static final Direction JUMP_SPIDER = new Force(0.0, 4.2);

    /** Init stage. */
    public static final Media STAGE1 = Medias.create(Folder.STAGE, Settings.getInstance().getStages(), "stage1.xml");
    /** Init config debug. */
    public static final InitConfig INIT_DEBUG = new InitConfig(STAGE1,
                                                               STATS_MAX_HEART - 1,
                                                               STATS_MAX_TALISMENT - 1,
                                                               STATS_MAX_LIFE - 1,
                                                               STATS_MAX_SWORD - 1,
                                                               Boolean.TRUE,
                                                               CREDITS,
                                                               Difficulty.NORMAL,
                                                               true,
                                                               Optional.empty());

    /** Stage prefix. */
    public static final String STAGE_PREFIX = "stage";
    /** Stage hard suffix. */
    public static final String STAGE_HARD_SUFFIX = "_hard.xml";

    /** Raster tile file. */
    public static final String RASTER_FILE_TILE = "tiles.png";
    /** Raster water file. */
    public static final String RASTER_FILE_WATER = "water.png";
    /** Raster lava file. */
    public static final String RASTER_FILE_LAVA = "lava.png";

    /** Input default. */
    public static final String INPUT_FILE_DEFAULT = "input.xml";
    /** Input cursor. */
    public static final String INPUT_FILE_CURSOR = "input_cursor.xml";

    /** Progress file. */
    public static final String FILE_PROGRESS = "progress.lhr";

    /** Minimum zoom. */
    public static final double ZOOM_MIN = 0.8;
    /** Maximum zoom. */
    public static final double ZOOM_MAX = 1.3;

    /** Alpha black values. */
    public static final ColorRgba[] ALPHAS_BLACK;
    /** Alpha white values. */
    public static final ColorRgba[] ALPHAS_WHITE;

    /**
     * Static init.
     */
    static
    {
        ALPHAS_BLACK = new ColorRgba[com.b3dgs.lionengine.Constant.UNSIGNED_BYTE];
        for (int i = 0; i < com.b3dgs.lionengine.Constant.UNSIGNED_BYTE; i++)
        {
            ALPHAS_BLACK[i] = new ColorRgba(0, 0, 0, i);
        }

        ALPHAS_WHITE = new ColorRgba[com.b3dgs.lionengine.Constant.UNSIGNED_BYTE];
        for (int i = 0; i < ALPHAS_WHITE.length; i++)
        {
            ALPHAS_WHITE[i] = new ColorRgba(255, 255, 255, i);
        }
    }

    /**
     * Private constructor.
     */
    private Constant()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
