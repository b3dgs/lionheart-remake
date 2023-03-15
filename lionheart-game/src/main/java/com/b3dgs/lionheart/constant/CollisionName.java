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
package com.b3dgs.lionheart.constant;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Collisions constants.
 */
public final class CollisionName
{
    /** Collision ground prefix. */
    public static final String GROUND = "ground";
    /** Collision animal prefix. */
    public static final String ANIMAL = "animal";
    /** Center collision. */
    public static final String CENTER = "center";
    /** Horizontal collision. */
    public static final String HORIZONTAL = "horizontal";
    /** Vertical collision. */
    public static final String VERTICAL = "vertical";
    /** Left collision. */
    public static final String LEFT = "left";
    /** Right collision. */
    public static final String RIGHT = "right";
    /** Vertical left collision. */
    public static final String LEFT_VERTICAL = LEFT + Constant.UNDERSCORE + VERTICAL;
    /** Vertical right collision. */
    public static final String RIGHT_VERTICAL = RIGHT + Constant.UNDERSCORE + VERTICAL;

    /** Category collision leg prefix. */
    public static final String LEG = Anim.LEG;
    /** Collision leg center. */
    public static final String LEG_CENTER = Anim.LEG + Constant.UNDERSCORE + CENTER;
    /** Collision leg left. */
    public static final String LEG_LEFT = Anim.LEG + Constant.UNDERSCORE + LEFT;
    /** Collision leg right. */
    public static final String LEG_RIGHT = Anim.LEG + Constant.UNDERSCORE + RIGHT;
    /** Collision body. */
    public static final String BODY = Anim.BODY;
    /** Collision take. */
    public static final String TAKE = "take";
    /** Collision bite. */
    public static final String BITE = "bite";

    /** Category collision knee prefix. */
    public static final String KNEE = "knee";
    /** Category collision knee center. */
    public static final String KNEE_CENTER = KNEE + Constant.UNDERSCORE + CENTER;
    /** Category collision knee x center. */
    public static final String KNEE_X_CENTER = KNEE + "_x_" + CENTER;

    /** Category collision head prefix. */
    public static final String HEAD = "head";
    /** Category collision hand prefix. */
    public static final String HAND = "hand";

    /** Collision spike prefix. */
    public static final String SPIKE = "spike";
    /** Collision spike left prefix. */
    public static final String SPIKE_LEFT = SPIKE + Constant.UNDERSCORE + LEFT_VERTICAL;
    /** Collision spike right prefix. */
    public static final String SPIKE_RIGHT = SPIKE + Constant.UNDERSCORE + RIGHT_VERTICAL;

    /** Collision slope prefix. */
    public static final String SLOPE = "slope";
    /** Collision slope left prefix. */
    public static final String SLOPE_LEFT = SLOPE + Constant.UNDERSCORE + LEFT;
    /** Collision slope right prefix. */
    public static final String SLOPE_RIGHT = SLOPE + Constant.UNDERSCORE + RIGHT;

    /** Collision incline prefix. */
    public static final String INCLINE = "incline";
    /** Collision incline left prefix. */
    public static final String INCLINE_LEFT = INCLINE + Constant.UNDERSCORE + LEFT;
    /** Collision incline right prefix. */
    public static final String INCLINE_RIGHT = INCLINE + Constant.UNDERSCORE + RIGHT;

    /** Collision steep prefix. */
    public static final String STEEP = "steep";
    /** Collision steep left prefix. */
    public static final String STEEP_LEFT = STEEP + Constant.UNDERSCORE + LEFT;
    /** Collision steep left ground prefix. */
    public static final String STEEP_LEFT_GROUND = STEEP + Constant.UNDERSCORE + LEFT + Constant.UNDERSCORE + GROUND;
    /** Collision steep right prefix. */
    public static final String STEEP_RIGHT = STEEP + Constant.UNDERSCORE + RIGHT;

    /** Collision grip prefix. */
    public static final String GRIP = "grip";

    /** Collision liana prefix. */
    public static final String LIANA = "liana";
    /** Collision liana left prefix. */
    public static final String LIANA_LEFT = LIANA + Constant.UNDERSCORE + LEFT;
    /** Collision liana right prefix. */
    public static final String LIANA_RIGHT = LIANA + Constant.UNDERSCORE + RIGHT;
    /** Collision liana full prefix. */
    public static final String LIANA_FULL = LIANA + "_full";
    /** Collision liana top prefix. */
    public static final String LIANA_TOP = LIANA + Constant.UNDERSCORE + GROUND + "_top";

    /** Collision block prefix. */
    public static final String BLOCK = "block";
    /** Collision trigger prefix. */
    public static final String TRIGGER = "trigger";

    /** Collision sigh. */
    public static final String COLL_SIGH = "sigh";

    /**
     * Private constructor.
     */
    private CollisionName()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
