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
package com.b3dgs.lionheart.constant;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Animation constants.
 */
public final class Anim
{
    /** Animation leg prefix. */
    public static final String LEG = "leg";
    /** Animation attack prefix. */
    public static final String ATTACK = "attack";
    /** Animation body prefix. */
    public static final String BODY = "body";
    /** Animation shade prefix. */
    public static final String SHADE = "shade_";

    /** Animation name idle. */
    public static final String IDLE = "idle";
    /** Animation name walk. */
    public static final String WALK = "walk";
    /** Animation name turn. */
    public static final String TURN = "turn";
    /** Animation name jump. */
    public static final String JUMP = "jump";
    /** Animation name fall. */
    public static final String FALL = "fall";
    /** Animation name land. */
    public static final String LAND = "land";
    /** Animation name attack fall. */
    public static final String ATTACK_FALL = ATTACK + FALL;
    /** Animation name dead prefix. */
    public static final String DEAD = "dead";

    /**
     * Private constructor.
     */
    private Anim()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
