/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.editor.world;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;

/**
 * Represents the different standard palette types.
 */
public enum PaletteType
{
    /** Object pointer. Allows to interact with object. */
    POINTER_CHECKPOINT(SWT.CURSOR_ARROW);

    /** The associated cursor. */
    private final Cursor cursor;

    /**
     * Private constructor.
     * 
     * @param cursor The cursor reference.
     */
    PaletteType(int cursor)
    {
        this.cursor = Display.getDefault().getSystemCursor(cursor);
    }

    /**
     * Get the palette cursor.
     * 
     * @return The palette cursor.
     */
    public Cursor getCursor()
    {
        return cursor;
    }
}
