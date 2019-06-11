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
package com.b3dgs.lionheart.constant;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Collisions constants.
 */
public final class CollisionName
{
    /** Category collision leg prefix. */
    public static final String LEG = "leg";
    /** Category collision knee prefix. */
    public static final String KNEE = "knee";
    /** Category collision knee center. */
    public static final String KNEE_CENTER = KNEE + "_center";
    /** Category collision hand prefix. */
    public static final String HAND = "hand";

    /** Collision ground prefix. */
    public static final String GROUND = "ground";
    /** Collision spike prefix. */
    public static final String SPIKE = "spike";
    /** Collision spike left prefix. */
    public static final String SPIKE_LEFT = SPIKE + "_vertical_left";
    /** Collision spike right prefix. */
    public static final String SPIKE_RIGHT = SPIKE + "_vertical_right";
    /** Collision slope prefix. */
    public static final String SLOPE = "slope";
    /** Collision slope left prefix. */
    public static final String SLOPE_LEFT = SLOPE + "_left";
    /** Collision slope right prefix. */
    public static final String SLOPE_RIGHT = SLOPE + "_right";
    /** Collision steep prefix. */
    public static final String STEEP = "steep";
    /** Collision steep left prefix. */
    public static final String STEEP_LEFT = STEEP + "_left";
    /** Collision steep left ground prefix. */
    public static final String STEEP_LEFT_GROUND = STEEP + "_left_ground";
    /** Collision steep right prefix. */
    public static final String STEEP_RIGHT = STEEP + "_right";
    /** Collision steep vertical prefix. */
    public static final String STEEP_VERTICAL = STEEP + "_vertical";
    /** Collision liana prefix. */
    public static final String LIANA = "liana";
    /** Collision liana left prefix. */
    public static final String LIANA_LEFT = LIANA + "_left";
    /** Collision liana right prefix. */
    public static final String LIANA_RIGHT = LIANA + "_right";

    /**
     * Private constructor.
     */
    private CollisionName()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
