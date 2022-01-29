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

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;

/**
 * Represents a choice in the menu.
 */
final class Choice implements Renderable
{
    private static final int MAX_WIDTH = 130;

    /** Horizontal location. */
    private final int x;
    /** Vertical location. */
    private final int y;
    /** Next menu pointer. */
    private final MenuType next;
    /** Text reference. */
    private final SpriteFont text;
    /** Choice name. */
    private final String name;
    /** Text align. */
    private final Align align;
    /** Current text. */
    private ImageBuffer current;
    /** Buffer. */
    private final ImageBuffer buffer;
    /** Buffer hover. */
    private final ImageBuffer bufferHover;

    /**
     * Constructor.
     * 
     * @param text The text reference.
     * @param textHover The text hover reference.
     * @param name The choice name.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param align The text align.
     */
    Choice(SpriteFont text, SpriteFont textHover, String name, int x, int y, Align align)
    {
        this(text, textHover, name, x, y, align, null);
    }

    /**
     * Constructor.
     * 
     * @param text The text reference.
     * @param textHover The text hover reference.
     * @param name The choice name.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param align The text align.
     * @param next The next menu pointer.
     */
    Choice(SpriteFont text, SpriteFont textHover, String name, int x, int y, Align align, MenuType next)
    {
        super();

        this.text = text;
        this.name = name;
        this.x = x;
        this.y = y;
        this.align = align;
        this.next = next;

        buffer = Graphics.createImageBuffer(text.getTextWidth(name),
                                            text.getTextHeight(name) + 4,
                                            ColorRgba.TRANSPARENT);
        buffer.prepare();
        bufferHover = Graphics.createImageBuffer(buffer.getWidth(), buffer.getHeight() + 4, ColorRgba.TRANSPARENT);
        bufferHover.prepare();

        Graphic g = buffer.createGraphic();
        text.draw(g, 0, 0, Align.LEFT, name);
        g.dispose();
        current = buffer;

        g = bufferHover.createGraphic();
        textHover.draw(g, 0, 0, Align.LEFT, name);
        g.dispose();
    }

    /**
     * Get next menu.
     * 
     * @return The next menu.
     */
    public MenuType getNext()
    {
        return next;
    }

    /**
     * Get y value.
     * 
     * @return The y value.
     */
    public int getY()
    {
        return y;
    }

    /**
     * Check if over.
     * 
     * @param cursor The cursor reference.
     * @return <code>true</code> if over, <code>false</code> else.
     */
    public boolean isOver(Cursor cursor)
    {
        final int x1;
        final int x2;

        if (Align.LEFT == align)
        {
            x1 = x;
            x2 = x + 200;
        }
        else if (Align.RIGHT == align)
        {
            x1 = x - 200;
            x2 = x;
        }
        else
        {
            x1 = x - 100;
            x2 = x + 100;
        }

        return UtilMath.isBetween(cursor.getScreenX(), x1, x2)
               && UtilMath.isBetween(cursor.getScreenY(), y + 5, y + 25);
    }

    /**
     * Set text hover flag.
     * 
     * @param hover <code>true</code> if hover, <code>false</code> else.
     */
    public void setHover(boolean hover)
    {
        current = hover ? bufferHover : buffer;
    }

    @Override
    public void render(Graphic g)
    {
        int offsetX = 0;
        if (Align.LEFT == align)
        {
            offsetX = Math.max(0, text.getTextWidth(name) - MAX_WIDTH);
        }
        else if (Align.CENTER == align)
        {
            offsetX = text.getTextWidth(name) / 2;
        }
        g.drawImage(current, x - offsetX, y);
    }
}
