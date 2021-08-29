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
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * Entity updating implementation.
 */
public final class VirtualKeyboard extends VirtualDeviceButton
{
    /** CTRL key. */
    private static final Integer CONTROL = Integer.valueOf(0);
    /** Arrow left key. */
    private static final Integer LEFT = Integer.valueOf(1);
    /** Arrow right key. */
    private static final Integer RIGHT = Integer.valueOf(2);
    /** Arrow down key. */
    private static final Integer DOWN = Integer.valueOf(3);
    /** Arrow up key. */
    private static final Integer UP = Integer.valueOf(4);
    /** Pause key. */
    private static final Integer PAUSE = Integer.valueOf(5);
    /** Ctrl left key. */
    private static final Integer CTRL_LEFT = Integer.valueOf(6);
    /** Tab key. */
    private static final Integer TAB = Integer.valueOf(7);
    /** Page down key. */
    private static final Integer PAGE_DOWN = Integer.valueOf(8);
    /** F1 key. */
    private static final Integer F1 = Integer.valueOf(9);
    /** F2 key. */
    private static final Integer F2 = Integer.valueOf(10);
    /** F3 key. */
    private static final Integer F3 = Integer.valueOf(11);
    /** F4 key. */
    private static final Integer F4 = Integer.valueOf(12);
    /** F5 key. */
    private static final Integer F5 = Integer.valueOf(13);
    /** F6 key. */
    private static final Integer F6 = Integer.valueOf(14);
    /** F7 key. */
    private static final Integer F7 = Integer.valueOf(15);
    /** F8 key. */
    private static final Integer F8 = Integer.valueOf(16);
    /** F9 key. */
    private static final Integer F9 = Integer.valueOf(17);
    /** F10 key. */
    private static final Integer F10 = Integer.valueOf(18);
    /** K1 key. */
    private static final Integer K1 = Integer.valueOf(19);
    /** K2 key. */
    private static final Integer K2 = Integer.valueOf(20);
    /** K3 key. */
    private static final Integer K3 = Integer.valueOf(21);
    /** K4 key. */
    private static final Integer K4 = Integer.valueOf(22);
    /** K5 key. */
    private static final Integer K5 = Integer.valueOf(23);

    private static final int X = 310;
    private static final int Y = 130;
    private static final int SIZE = 30;

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

        addButton(1, Y - SIZE / 2, UP, Medias.create("button", "1.png"));
        addButton(1, Y + SIZE / 2, CONTROL, Medias.create("button", "2.png"));
    }
}
