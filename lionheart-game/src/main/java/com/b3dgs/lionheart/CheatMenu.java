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

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Surface;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.Action;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteDigit;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Cheat menu representation.
 */
public class CheatMenu implements Routine, Surface
{
    private static final ColorRgba COLOR = new ColorRgba(128, 128, 128);
    private static final ColorRgba COLOR2 = new ColorRgba(118, 118, 118);
    private static final ColorRgba HOVER = new ColorRgba(170, 170, 170);

    private static final int INT_RADIX = 10;

    private static boolean isInteger(String s)
    {
        return isInteger(s, INT_RADIX);
    }

    private static boolean isInteger(String s, int radix)
    {
        if (s.isEmpty())
        {
            return false;
        }
        for (int i = 0; i < s.length(); i++)
        {
            if (i == 0 && s.charAt(i) == '-')
            {
                if (s.length() == 1)
                {
                    return false;
                }
            }
            if (Character.digit(s.charAt(i), radix) < 0)
            {
                return false;
            }
        }
        return true;
    }

    private final SpriteFont font = Drawable.loadSpriteFont(Medias.create(Folder.SPRITE, "font.png"),
                                                            Medias.create(Folder.SPRITE, "fontdata.xml"),
                                                            12,
                                                            12);
    private final SpriteDigit numberStage;

    private final Rectangle area;
    private final Viewer viewer;
    private final Cursor cursor;
    private final Action action;
    private final List<CheatMenu> menus;
    private final List<CheatMenu> sub;
    private final BooleanSupplier isPressed;

    private boolean active;
    private boolean spawned;
    private boolean hover;
    private boolean hide;

    /**
     * Create menu.
     * 
     * @param services The services reference.
     * @param menus The menus list.
     * @param isPressed The pressed checker.
     * @param width The button width.
     * @param text The text value.
     * @param action The action to run.
     * @param sub The sub menus.
     */
    public CheatMenu(Services services,
                     List<CheatMenu> menus,
                     BooleanSupplier isPressed,
                     int width,
                     String text,
                     Action action,
                     CheatMenu... sub)
    {
        super();

        this.menus = menus;
        this.isPressed = isPressed;
        this.action = action;
        this.sub = Arrays.asList(sub);

        area = new Rectangle(0, 0, width, 20);
        viewer = services.get(Viewer.class);
        cursor = services.get(Cursor.class);

        font.load();
        font.prepare();
        font.setText(text);

        if (isInteger(text))
        {
            final ImageBuffer number = Graphics.getImageBuffer(Medias.create(Folder.SPRITE, "numbers.png"));
            numberStage = Drawable.loadSpriteDigit(number, 8, 16, 2);
            number.prepare();
            numberStage.prepare();
            numberStage.setValue(Integer.parseInt(text));
        }
        else
        {
            numberStage = null;
        }
    }

    /**
     * Spawn menu.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void spawn(double x, double y)
    {
        if (!spawned)
        {
            area.set(x + 1, y + 1, area.getWidth(), area.getHeight());
            if (numberStage != null)
            {
                numberStage.setLocation(x + area.getWidth() / 2 - numberStage.getWidth() / 2 + 1, y + 3);
            }
            else
            {
                font.setAlign(Align.CENTER);
                font.setLocation(x + area.getWidth() / 2 + 1, y + 3);
            }
            spawned = true;
            for (int i = 0; i < sub.size(); i++)
            {
                sub.get(i).hide();
            }
            active = true;
        }
    }

    /**
     * Set inactive flag.
     */
    public void setInactive()
    {
        active = false;
        hover = false;
    }

    /**
     * Hide menu.
     */
    public void hide()
    {
        hide = true;

        for (int i = 0; i < sub.size(); i++)
        {
            sub.get(i).hide();
        }
    }

    /**
     * Check if is hover.
     * 
     * @return <code>true</code> if is hover, <code>false</code> else.
     */
    public boolean isHover()
    {
        return hover;
    }

    /**
     * Check if is hover sub.
     * 
     * @return <code>true</code> if is hover, <code>false</code> else.
     */
    public boolean isHoverSub()
    {
        for (int i = 0; i < sub.size(); i++)
        {
            if (sub.get(i).isHover())
            {
                return true;
            }
        }
        return false;
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
        if (spawned && active)
        {
            hover = false;
            final double cx = cursor.getScreenX();
            final double cy = cursor.getScreenY();
            final boolean inside = area.contains(cx, cy);
            if (inside)
            {
                hover = true;
                if (!sub.isEmpty())
                {
                    for (final CheatMenu menu : menus)
                    {
                        if (menu != this)
                        {
                            for (final CheatMenu s : menu.sub)
                            {
                                s.hide();
                            }
                        }
                    }
                }
                if (sub.isEmpty())
                {
                    if (action != null && isPressed.getAsBoolean())
                    {
                        action.execute();
                    }
                }
                else if (!sub.get(0).spawned)
                {
                    Util.showMenu(viewer,
                                  cursor,
                                  sub,
                                  area.getX() + area.getWidth() - 1 - cx,
                                  area.getY() + area.getHeight() - cy);
                    active = false;
                }
            }
        }
        if (hide)
        {
            hide = false;
            spawned = false;
        }
        active = true;
    }

    /**
     * Update sub menu.
     * 
     * @param extrp The extrapolation value.
     */
    public void updateSub(double extrp)
    {
        for (int i = 0; i < sub.size(); i++)
        {
            sub.get(i).update(extrp);
        }
    }

    /**
     * Draw button border.
     * 
     * @param g The graphic output.
     */
    private void drawBorder(Graphic g)
    {
        final int x1 = (int) area.getX();
        final int y1 = (int) area.getY();
        final int x2 = (int) area.getX() + area.getWidth() - 1;
        final int y2 = (int) area.getY() + area.getHeight() - 1;

        g.setColor(ColorRgba.GRAY_LIGHT);
        g.drawLine(x1, y1, x2, y1);
        g.drawLine(x1, y1, x1, y2);

        g.setColor(ColorRgba.GRAY_DARK);
        g.drawLine(x1, y2, x2, y2);
        g.drawLine(x2, y1, x2, y2);
    }

    /**
     * Draw text content.
     * 
     * @param g The graphic output.
     */
    private void drawText(Graphic g)
    {
        if (numberStage != null)
        {
            numberStage.render(g);
        }
        else
        {
            font.render(g);
        }
    }

    @Override
    public void render(Graphic g)
    {
        if (spawned)
        {
            if (hover)
            {
                g.setColor(HOVER);
            }
            else
            {
                if (numberStage != null)
                {
                    g.setColor(COLOR2);
                }
                else
                {
                    g.setColor(COLOR);
                }
            }
            g.drawRect((int) area.getX(), (int) area.getY(), area.getWidth() - 1, area.getHeight() - 1, true);
            drawBorder(g);
            drawText(g);
        }
        for (int i = 0; i < sub.size(); i++)
        {
            sub.get(i).render(g);
        }
    }
}
