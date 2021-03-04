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

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;

/**
 * Handle a group of choice, which represents the actions in a menu.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class Data
{
    /** Title text color. */
    private static final ColorRgba COLOR_TITLE = new ColorRgba(255, 255, 255);
    /** Text color. */
    private static final ColorRgba COLOR_TEXT = new ColorRgba(136, 136, 136);
    /** Text color on selection. */
    private static final ColorRgba COLOR_OVER = new ColorRgba(255, 255, 255);
    /** Text instance for the title. */
    private static final Text TEXT_TITLE = Graphics.createText(Constant.FONT_SERIF, 26, TextStyle.BOLD);
    /** Maximum number of choices. */
    final int choiceMax;
    /** Choices list. */
    final Choice[] choices;
    /** Text reference. */
    private final Text text;
    /** Horizontal factor value. */
    private final double factorH;
    /** Vertical factor value. */
    private final double factorV;
    /** Title name. */
    private final String title;
    /** Show title flag. */
    private final boolean showTitle;

    /**
     * Constructor.
     * 
     * @param text The text reference.
     * @param factorH The horizontal factor value.
     * @param factorV The vertical factor value.
     * @param title The menu title.
     * @param showTitle <code>true</code> to show the title, <code>false</code> else.
     * @param choices The choices list.
     */
    Data(Text text, double factorH, double factorV, String title, boolean showTitle, Choice... choices)
    {
        this.text = text;
        this.factorH = factorH;
        this.factorV = factorV;
        this.title = title;
        choiceMax = choices.length - 1;
        this.showTitle = showTitle;
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
        if (showTitle)
        {
            Data.TEXT_TITLE.setColor(Data.COLOR_TITLE);
            Data.TEXT_TITLE.draw(g, (int) (213 * factorH), (int) (Menu.Y + 64 * factorV), Align.CENTER, title);
        }
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
