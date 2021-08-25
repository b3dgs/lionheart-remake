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
package com.b3dgs.lionheart;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Surface;
import com.b3dgs.lionengine.game.Action;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;

/**
 * Cheat menu representation.
 */
public class CheatMenu implements Routine, Surface
{
    private static final ColorRgba COLOR = new ColorRgba(128, 128, 128);
    private static final ColorRgba HOVER = new ColorRgba(160, 160, 160);

    private final Rectangle area;
    private final Cursor cursor;
    private final Text text;
    private final Action action;
    private final CheatMenu[] sub;

    private boolean spawned;
    private boolean hover;
    private boolean hide;

    /**
     * Create menu.
     * 
     * @param services The services reference.
     * @param width The button width.
     * @param text The text value.
     * @param action The action to run.
     * @param sub The sub menus.
     */
    public CheatMenu(Services services, int width, String text, Action action, CheatMenu... sub)
    {
        super();

        this.action = action;
        this.sub = sub;

        this.text = Graphics.createText(Constant.FONT_DIALOG, 11, TextStyle.NORMAL);
        this.text.setText(text);

        area = new Rectangle(0, 0, width, 14);
        cursor = services.get(Cursor.class);
    }

    /**
     * Spawn menu.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void spawn(double x, double y)
    {
        area.set(x + 1, y + 1, area.getWidth(), area.getHeight());
        text.setAlign(Align.CENTER);
        text.setLocation(x + area.getWidth() / 2 + 1, y + 4);
        spawned = true;
    }

    /**
     * Hide menu.
     */
    public void hide()
    {
        hide = true;
    }

    @Override
    public int getWidth()
    {
        return area.getWidth();
    }

    @Override
    public int getHeight()
    {
        return area.getHeight();
    }

    @Override
    public void update(double extrp)
    {
        if (spawned)
        {
            hover = false;
            final boolean inside = area.contains(cursor.getScreenX(), cursor.getScreenY());
            if (inside)
            {
                hover = true;
            }
            if (cursor.isPushed(DeviceMapping.LEFT.getIndex()))
            {
                if (inside)
                {
                    if (action != null)
                    {
                        action.execute();
                    }
                    for (int i = 0; i < sub.length; i++)
                    {
                        sub[i].spawn(area.getX() + area.getWidth() + sub[i].getWidth() * i + 1, area.getY() + 1);
                    }
                    hide();
                }
            }
        }
        if (hide)
        {
            hide = false;
            spawned = false;
        }
        for (int i = 0; i < sub.length; i++)
        {
            sub[i].update(extrp);
        }
    }

    @Override
    public void render(Graphic g)
    {
        if (spawned)
        {
            final int x1 = (int) area.getX();
            final int y1 = (int) area.getY();
            final int x2 = (int) area.getX() + area.getWidth();
            final int y2 = (int) area.getY() + area.getHeight();

            g.setColor(ColorRgba.GRAY_LIGHT);
            g.drawLine(x1, y1, x2, y1);
            g.drawLine(x1, y1, x1, y2);

            g.setColor(ColorRgba.GRAY_DARK);
            g.drawLine(x1, y2, x2, y2);
            g.drawLine(x2, y1, x2, y2);

            if (hover)
            {
                g.setColor(HOVER);
                text.setColor(ColorRgba.YELLOW);
            }
            else
            {
                g.setColor(COLOR);
                text.setColor(ColorRgba.WHITE);
            }
            g.drawRect((int) area.getX() + 1, (int) area.getY() + 1, area.getWidth() - 1, area.getHeight() - 1, true);
            text.render(g);
        }
        for (int i = 0; i < sub.length; i++)
        {
            sub[i].render(g);
        }
    }
}
