/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
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
package com.b3dgs.lionheart.android;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.android.Mouse;
import com.b3dgs.lionengine.android.VirtualDeviceButton;

/**
 * Entity updating implementation.
 */
public final class VirtualKeyboard extends VirtualDeviceButton
{
    /** CTRL key. */
    private static final Integer CONTROL = 0;
    /** Arrow left key. */
    private static final Integer LEFT = 1;
    /** Arrow right key. */
    private static final Integer RIGHT = 2;
    /** Arrow down key. */
    private static final Integer DOWN = 3;
    /** Arrow up key. */
    private static final Integer UP = 4;

    private static int X = 275;
    private static final int Y = 150;
    private static final int SIZE = 30;
    private static final int SIZE_BUTTON = 24;

    /**
     * Apply horizontal screen ratio.
     * @param ratio The ratio value.
     */
    static void applyWidthRatio(double ratio)
    {
        X = (int) (X * ratio);
    }

    /**
     * Create updater.
     *
     * @param pointer The pointer reference.
     */
    public VirtualKeyboard(Mouse pointer)
    {
        super(pointer);

        addButton(X - SIZE, Y - SIZE, LEFT, Medias.create("button", "up_left.png"));
        addButton(X - SIZE, Y - SIZE, UP, Medias.create("button", "up_left.png"));

        addButton(X, Y - SIZE, UP, Medias.create("button", "up.png"));

        addButton(X + SIZE, Y - SIZE, RIGHT, Medias.create("button", "up_right.png"));
        addButton(X + SIZE, Y - SIZE, UP, Medias.create("button", "up_right.png"));

        addButton(X - SIZE, Y, LEFT, Medias.create("button", "left.png"));

        addButton(X + SIZE, Y, RIGHT, Medias.create("button", "right.png"));

        addButton(X - SIZE, Y + SIZE, LEFT, Medias.create("button", "down_left.png"));
        addButton(X - SIZE, Y + SIZE, DOWN, Medias.create("button", "down_left.png"));

        addButton(X, Y + SIZE, DOWN, Medias.create("button", "down.png"));

        addButton(X + SIZE, Y + SIZE, RIGHT, Medias.create("button", "down_right.png"));
        addButton(X + SIZE, Y + SIZE, DOWN, Medias.create("button", "down_right.png"));

        addButton(1, Y - SIZE_BUTTON, UP, Medias.create("button", "1.png"));
        addButton(1, Y + SIZE_BUTTON, CONTROL, Medias.create("button", "2.png"));
    }
}
