/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.io.DeviceMapper;

/**
 * Device action mapping.
 */
public enum DeviceMapping implements DeviceMapper
{
    /** Attack. */
    ATTACK,
    /** Jump. */
    JUMP,
    /** Down. */
    DOWN,
    /** Left. */
    LEFT,
    /** Right. */
    RIGHT,
    /** Cheat. */
    CHEAT,
    /** Pause. */
    PAUSE,
    /** Quit. */
    QUIT,
    /** Debug. */
    TAB,
    /** Cheats. */
    PAGE_DOWN,
    /** Zoom in. */
    ZOOM_IN,
    /** Zoom out. */
    ZOOM_OUT,
    /** Stage 1. */
    F1,
    /** Stage 2. */
    F2,
    /** Stage 3. */
    F3,
    /** Stage 4. */
    F4,
    /** Stage 5. */
    F5,
    /** Stage 6. */
    F6,
    /** Stage 7. */
    F7,
    /** Stage 8. */
    F8,
    /** Stage 9. */
    F9,
    /** Stage 11. */
    F10,
    /** Stage 12. */
    K1,
    /** Stage 13. */
    K2,
    /** Stage 14. */
    K3,
    /** Stage 10. */
    K4,
    /** Extro. */
    K5,
    /** Quick save. */
    QUICK_SAVE,
    /** Quick load. */
    QUICK_LOAD,
    /** Force exit. */
    FORCE_EXIT,
    /** Move. */
    MOVE;

    /**
     * Convert from index.
     * 
     * @param index The index.
     * @return The value.
     */
    public static String fromIndex(int index)
    {
        return DeviceMapping.values()[index].name();
    }

    @Override
    public Integer getIndex()
    {
        return Integer.valueOf(ordinal());
    }
}
