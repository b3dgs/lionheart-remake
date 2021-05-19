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

    /**
     * Create updater.
     *
     * @param pointer The pointer reference.
     */
    public VirtualKeyboard(Mouse pointer)
    {
        super(pointer);

        addButton(new Rectangle(310, 120, 28, 28), LEFT, "<");
        addButton(new Rectangle(340, 120, 28, 28), RIGHT, ">");
        addButton(new Rectangle(310, 150, 58, 28), DOWN, "\\/");

        addButton(new Rectangle(1, 124, 28, 28), UP, "J");
        addButton(new Rectangle(1, 154, 28, 28), CONTROL, "F");

        addButton(new Rectangle(1, 193, 17, 14), PAUSE, "P");
        addButton(new Rectangle(20, 193, 17, 14), CTRL_LEFT, "C");
        addButton(new Rectangle(39, 193, 17, 14), TAB, "T");
        addButton(new Rectangle(58, 193, 17, 14), PAGE_DOWN, "D");
        addButton(new Rectangle(77, 193, 17, 14), F1, "1");
        addButton(new Rectangle(96, 193, 17, 14), F2, "2");
        addButton(new Rectangle(115, 193, 17, 14), F3, "3");
        addButton(new Rectangle(134, 193, 17, 14), F4, "4");
        addButton(new Rectangle(153, 193, 17, 14), F5, "5");
        addButton(new Rectangle(172, 193, 17, 14), F6, "6");
        addButton(new Rectangle(191, 193, 17, 14), F7, "7");
        addButton(new Rectangle(210, 193, 17, 14), F8, "8");
        addButton(new Rectangle(229, 193, 17, 14), F9, "9");
        addButton(new Rectangle(248, 193, 17, 14), F10, "10");
        addButton(new Rectangle(267, 193, 17, 14), K1, "11");
        addButton(new Rectangle(286, 193, 17, 14), K2, "12");
        addButton(new Rectangle(305, 193, 17, 14), K3, "13");
        addButton(new Rectangle(324, 193, 17, 14), K4, "14");
        addButton(new Rectangle(343, 193, 17, 14), K5, "15");
    }

    @Override
    public boolean isPushed(Integer key)
    {
        final boolean result = super.isPushed(key);
        if (!result && !isVisible())
        {
            if (pointer.getPushed() == 1
                    && (UP.equals(key) && pointer.getY() <= 100 || DOWN.equals(key) && pointer.getY() > 260)
                    || pointer.getPushed() == 2 && CONTROL.equals(key))
            {
                return true;
            }
        }
        return result;
    }
}
