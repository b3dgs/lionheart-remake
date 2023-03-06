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

import com.b3dgs.lionengine.UtilMath;

/**
 * List of game types.
 */
public enum GameType
{
    /** Linked stages from intro to credits. */
    STORY,
    /** Custom startup on a single chosen stage. */
    TRAINING,
    /** Stage without destroyable objects. Reach end first to win. */
    SPEEDRUN,
    /** Stage with incoming monsters from any sides. Kill all waves to win. */
    BATTLE,
    /** Players fight on fixed area. Custom health count but no life. Last stand win. */
    VERSUS;

    private static final GameType[] VALUES = GameType.values();

    /**
     * Get game type from index.
     * 
     * @param index The index value.
     * @return The game type.
     */
    public static GameType from(int index)
    {
        return VALUES[UtilMath.clamp(index, 0, VALUES.length - 1)];
    }

    /**
     * Check if index is type of.
     * 
     * @param index The index to check.
     * @param types The types to check.
     * @return <code>true</code> if is type, <code>false</code> if none.
     */
    public static boolean is(int index, GameType... types)
    {
        return is(from(index), types);
    }

    /**
     * Check if type is type of.
     * 
     * @param type The type to check.
     * @param types The types to check.
     * @return <code>true</code> if is type, <code>false</code> if none.
     */
    private static boolean is(GameType type, GameType... types)
    {
        final int n = types.length;
        for (int i = 0; i < n; i++)
        {
            if (type == types[i])
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if is type of.
     * 
     * @param types The types to check.
     * @return <code>true</code> if is type, <code>false</code> if none.
     */
    public boolean is(GameType... types)
    {
        return is(this, types);
    }
}
