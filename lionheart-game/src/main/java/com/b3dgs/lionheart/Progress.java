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
package com.b3dgs.lionheart;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.game.Bar;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;

/**
 * Progress handling.
 */
public class Progress implements Renderable
{
    private static final double TEXT_HEIGHT_FACTOR = 1.25;
    private static final int BAR_HEIGHT = 12;
    private static final int BAR_WIDTH_MARGIN = 4;

    private final Text text = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_DIALOG, 9, TextStyle.NORMAL);
    private final Bar bar;

    /**
     * Create progress.
     * 
     * @param width The screen width.
     * @param height The screen height.
     */
    public Progress(int width, int height)
    {
        super();

        bar = new Bar(width - BAR_WIDTH_MARGIN, BAR_HEIGHT);

        text.setAlign(Align.CENTER);
        text.setColor(ColorRgba.YELLOW);
        text.setLocation(width / 2, height - text.getSize() * TEXT_HEIGHT_FACTOR);
        text.setText(com.b3dgs.lionengine.Constant.EMPTY_STRING);

        bar.setHorizontalReferential(true);
        bar.setBorderSize(1, 1);
        bar.setColor(ColorRgba.GRAY_DARK, ColorRgba.RED);
        bar.setColorGradient(ColorRgba.RED, ColorRgba.YELLOW);
        bar.setLocation(2, height - bar.getHeight() - 2);
        bar.setWidthPercent(0);

        setPercent(0);
    }

    /**
     * Update percent progress.
     * 
     * @param percent The percent value.
     */
    public void setPercent(int percent)
    {
        text.setText(percent + com.b3dgs.lionengine.Constant.PERCENT);
        bar.setWidthPercent(percent);
    }

    @Override
    public void render(Graphic g)
    {
        bar.render(g);
        text.render(g);
    }
}
