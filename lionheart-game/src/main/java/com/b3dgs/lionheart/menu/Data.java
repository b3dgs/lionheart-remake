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
package com.b3dgs.lionheart.menu;

import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Handle a group of choice, which represents the actions in a menu.
 */
final class Data
{
    /** Maximum number of choices. */
    final int choiceMax;
    /** Choices list. */
    final Choice[] choices;

    /**
     * Constructor.
     * 
     * @param choices The choices list.
     */
    Data(Choice... choices)
    {
        super();

        choiceMax = choices.length - 1;
        this.choices = choices;
    }

    /**
     * Render the menu.
     * 
     * @param g The graphic output.
     * @param choice The current choice.
     * @param skip The items to skip.
     */
    void render(Graphic g, int choice, int... skip)
    {
        for (int i = 0; i <= choiceMax; i++)
        {
            if (!isSkip(i, skip))
            {
                choices[i].setHover(choice == i);
                choices[i].render(g);
            }
        }
    }

    private static boolean isSkip(int cur, int... skip)
    {
        for (int i = 0; i < skip.length; i++)
        {
            if (cur == skip[i])
            {
                return true;
            }
        }
        return false;
    }
}
