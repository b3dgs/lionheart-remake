/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.narration;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionheart.Time;

/**
 * Text data.
 */
public final class TextData implements Updatable, Renderable
{
    private static final int TEXT_ALPHA_SPEED = 6;
    private static final int TEXT_LINE_HEIGHT = 22;

    private final Time time;
    private final int timeStartMs;
    private final int timeEndMs;
    private final int[] x;
    private final int[] y;
    private final ImageBuffer[] buffer;

    private double alpha;
    private double alphaOld;

    /**
     * Create data.
     * 
     * @param time The current time.
     * @param font The current font.
     * @param timeStartMs The starting time in milliseconds.
     * @param timeEndMs The ending time in milliseconds.
     * @param x1 The first text horizontal location.
     * @param y1 The first text vertical location.
     * @param x2 The other texts horizontal location.
     * @param align The text align.
     * @param texts The text lines.
     */
    public TextData(Time time,
                    SpriteFont font,
                    int timeStartMs,
                    int timeEndMs,
                    int x1,
                    int y1,
                    int x2,
                    Align align,
                    String... texts)
    {
        super();

        this.time = time;
        this.timeStartMs = timeStartMs;
        this.timeEndMs = timeEndMs;
        x = new int[texts.length];
        y = new int[Math.max(1, texts.length)];
        buffer = new ImageBuffer[texts.length];

        if (texts.length > 0)
        {
            for (int i = 0; i < texts.length; i++)
            {
                final String text = texts[i];
                final int width = font.getTextWidth(text) + 2;
                final int height = font.getTextHeight(text) + 1;

                buffer[i] = Graphics.createImageBufferAlpha(width, height);
                buffer[i].prepare();

                final Graphic g = buffer[i].createGraphic();
                font.draw(g, align == Align.LEFT ? 0 : width / 2, 0, align, text);
                g.dispose();

                final int h = i == 0 ? x1 : x2;
                x[i] = align == Align.LEFT ? h : h - width / 2;
                y[i] = y1 + TEXT_LINE_HEIGHT * i;
            }
        }
        else
        {
            y[0] = y1;
        }
    }

    /**
     * Get starting time in milliseconds.
     * 
     * @return The starting time.
     */
    public int getStartMs()
    {
        return timeStartMs;
    }

    /**
     * Get ending time in milliseconds.
     * 
     * @return The ending time.
     */
    public int getEndMs()
    {
        return timeEndMs;
    }

    /**
     * Get vertical location.
     * 
     * @return The vertical location.
     */
    public int getY()
    {
        return y[0];
    }

    /**
     * Get current alpha.
     * 
     * @return The alpha value.
     */
    public int getAlpha()
    {
        return (int) Math.floor(alpha);
    }

    @Override
    public void update(double extrp)
    {
        alphaOld = alpha;
        if (time.isAfter(timeEndMs))
        {
            alpha -= TEXT_ALPHA_SPEED * extrp;
        }
        else if (time.isAfter(timeStartMs))
        {
            alpha += TEXT_ALPHA_SPEED * extrp;
        }
        alpha = UtilMath.clamp(alpha, 0.0, 255.0);
    }

    @Override
    public void render(Graphic g)
    {
        if (buffer != null && alpha > 0)
        {
            final int old = g.getAlpha();
            if (Double.compare(alphaOld, alpha) != 0)
            {
                g.setAlpha((int) Math.floor(alpha));
            }
            for (int i = 0; i < buffer.length; i++)
            {
                g.drawImage(buffer[i], x[i], y[i]);
            }
            g.setAlpha(old);
        }
    }
}
