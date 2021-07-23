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

import com.b3dgs.lionengine.io.DeviceMapper;

/**
 * Device action mapping.
 */
public enum DeviceMapping implements DeviceMapper
{
    /** Left. */
    LEFT,
    /** Right. */
    RIGHT,
    /** Up. */
    UP,
    /** Down. */
    DOWN,
    /** Action right. */
    CTRL_RIGHT,
    /** Action left. */
    CTRL_LEFT,
    /** Pause. */
    PAUSE,
    /** Quit. */
    QUIT,
    /** Debug. */
    TAB,
    /** Cheats. */
    PAGE_DOWN,
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
    /** Force exit. */
    FORCE_EXIT;

    @Override
    public Integer getIndex()
    {
        return Integer.valueOf(ordinal());
    }
}
