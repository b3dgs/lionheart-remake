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
package com.b3dgs.lionheart.menu;

import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Text;

/**
 * Handle a group of choice, which represents the actions in a menu.
 */
final class Data
{
    /** Text color. */
    private static final ColorRgba COLOR_TEXT = new ColorRgba(136, 136, 136);
    /** Text color on selection. */
    private static final ColorRgba COLOR_OVER = new ColorRgba(255, 255, 255);

    /** Maximum number of choices. */
    final int choiceMax;
    /** Choices list. */
    final Choice[] choices;
    /** Text reference. */
    private final Text text;

    /**
     * Constructor.
     * 
     * @param text The text reference.
     * @param choices The choices list.
     */
    Data(Text text, Choice... choices)
    {
        super();

        this.text = text;
        choiceMax = choices.length - 1;
        this.choices = choices;
    }

    /**
     * Render the menu.
     * 
     * @param g The graphic output.
     * @param choice The current choice.
     */
    void render(Graphic g, int choice)
    {
        for (int i = 0; i <= choiceMax; i++)
        {
            if (choice == i)
            {
                text.setColor(Data.COLOR_OVER);
            }
            else
            {
                text.setColor(Data.COLOR_TEXT);
            }
            choices[i].render(g);
        }
    }
}
