/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionheart.intro;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.UtilityImage;
import com.b3dgs.lionengine.core.UtilityMath;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.game.CameraGame;

/**
 * Intro part 1 implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Part1
{
    /** Text large. */
    private final Text textLarge;
    /** Backgrounds. */
    private final Sprite[] backs;
    /** Sceneries. */
    private final Sprite[] sceneries;
    /** Title. */
    private final Sprite title;
    /** Camera back. */
    private final CameraGame cameraBack;
    /** Camera scenery. */
    private final CameraGame cameraScenery;
    /** Back alpha. */
    private double alphaBack;
    /** Text alpha. */
    private double alphaText;

    /**
     * Constructor.
     */
    public Part1()
    {
        textLarge = UtilityImage.createText(Text.SANS_SERIF, 24, TextStyle.NORMAL);
        title = Drawable.loadSprite(UtilityMedia.get("intro", "part1", "title.png"));
        backs = new Sprite[4];
        sceneries = new Sprite[6];
        cameraBack = new CameraGame();
        cameraScenery = new CameraGame();
    }

    /**
     * Load part.
     */
    public void load()
    {
        title.load(true);
        for (int i = 0; i < backs.length; i++)
        {
            backs[i] = Drawable.loadSprite(UtilityMedia.get("intro", "part1", "back" + i + ".png"));
            backs[i].load(false);
        }
        for (int i = 0; i < sceneries.length; i++)
        {
            sceneries[i] = Drawable.loadSprite(UtilityMedia.get("intro", "part1", "scenery" + i + ".png"));
            sceneries[i].load(false);
        }
    }

    /**
     * Update part.
     * 
     * @param seek The current seek.
     * @param extrp The extrapolation value.
     */
    public void update(int seek, double extrp)
    {
        // First Fade in
        if (seek > 0 && seek < 2500)
        {
            alphaBack += 5.0;
            alphaBack = UtilityMath.fixBetween(alphaBack, 0.0, 255.0);
        }

        // Text fades
        updateAlphaText(2600, 5650, 5650, 7050, seek, extrp);
        updateAlphaText(7550, 10000, 12000, 14000, seek, extrp);
        updateAlphaText(14900, 17900, 17900, 20000, seek, extrp);
        updateAlphaText(19900, 22900, 22900, 25000, seek, extrp);
        updateAlphaText(24900, 27900, 27900, 30000, seek, extrp);
        updateAlphaText(29900, 32900, 32900, 35000, seek, extrp);

        // Start moving camera until door
        if (seek > 10200)
        {
            cameraBack.moveLocation(extrp, 0.45, 0.0);
            cameraScenery.moveLocation(extrp, 0.76, 0.0);
            if (cameraScenery.getLocationX() > 1602.0)
            {
                cameraScenery.teleportX(1602.0);
            }
        }
    }

    /**
     * Render text.
     * 
     * @param start The starting time.
     * @param end The ending time.
     * @param x1 The text1 x.
     * @param y1 The text1 y.
     * @param x2 The text2 x.
     * @param y2 The text2 y.
     * @param align The text align.
     * @param text1 The text 1.
     * @param text2 The text 2.
     * @param width The width.
     * @param height The height.
     * @param seek The current seek.
     * @param g The graphic output.
     */
    private void renderText(int start, int end, int x1, int y1, int x2, int y2, Align align, String text1,
            String text2, int width, int height, int seek, Graphic g)
    {
        if (seek > start && seek < end)
        {
            textLarge.setColor(Intro.ALPHAS_WHITE[(int) alphaText]);
            textLarge.draw(g, width / 2 + x1, height / 2 + y1, align, text1);
            textLarge.draw(g, width / 2 + x2, height / 2 + y2, align, text2);
        }
    }

    /**
     * Render part.
     * 
     * @param width The width.
     * @param height The height.
     * @param seek The current seek.
     * @param g The graphic output.
     */
    public void render(int width, int height, int seek, Graphic g)
    {
        g.clear(0, 0, width, height);

        // Render backs
        if (seek < 42000)
        {
            for (int i = 0; i < backs.length; i++)
            {
                backs[i].render(g, cameraBack.getViewpointX(i * backs[i].getWidth()), height / 2 - backs[i].getHeight()
                        / 2);
            }
        }

        // Render sceneries
        renderScenery(height, 0, 32, g);
        renderScenery(height, 1, 420, g);
        renderScenery(height, 0, 580, g);
        renderScenery(height, 1, 690, g);
        renderScenery(height, 2, 750, g);
        renderScenery(height, 0, 810, g);
        renderScenery(height, 3, 920, g);
        renderScenery(height, 0, 980, g);
        renderScenery(height, 4, 1350, g);
        renderScenery(height, 5, 1650, g);

        // Render texts
        renderText(2600, 7050, 0, -38, 0, -16, Align.CENTER, "BYRON 3D GAMES STUDIO", "PRESENTS", width, height, seek,
                g);
        if (seek > 7050 && seek < 13000)
        {
            title.setAlpha((int) alphaText);
            title.render(g, width / 2 - title.getWidth() / 2, height / 2 - title.getHeight() / 2 - 24);
        }
        renderText(14900, 19000, -110, -60, -86, -35, Align.LEFT, "PROGRAMMING", "Pierre-Alexandre", width, height,
                seek, g);
        renderText(19900, 24000, -110, -38, -58, -12, Align.LEFT, "GRAPHICS", "Henk Nieborg", width, height, seek, g);
        renderText(24900, 29000, -110, -38, -42, -12, Align.LEFT, "GAMEDESIGN", "Erik Simon", width, height, seek, g);
        renderText(29900, 34000, -110, -38, -110, -12, Align.LEFT, "MUSIC & SFX", "Matthias Steinwachs", width, height,
                seek, g);

        // Render fade in
        if (alphaBack < 255)
        {
            g.setColor(Intro.ALPHAS_BLACK[255 - (int) alphaBack]);
            g.drawRect(0, 0, width, height, true);
        }
    }

    /**
     * Update alpha text.
     * 
     * @param start1 The entering start.
     * @param end1 The entering end.
     * @param start2 The ending start.
     * @param end2 The ending end.
     * @param seek The current seek.
     * @param extrp The extrapolation value.
     */
    private void updateAlphaText(int start1, int end1, int start2, int end2, int seek, double extrp)
    {
        if (seek >= start1 && seek < end1)
        {
            alphaText += 5.0 * extrp;
        }
        else if (seek >= start2 && seek < end2)
        {
            alphaText -= 5.0 * extrp;
        }
        alphaText = UtilityMath.fixBetween(alphaText, 0.0, 255.0);
    }

    /**
     * Render a scenery.
     * 
     * @param height The height.
     * @param id The scenery id.
     * @param x The horizontal location.
     * @param g The graphic output.
     */
    private void renderScenery(int height, int id, int x, Graphic g)
    {
        sceneries[id].render(g, cameraScenery.getViewpointX(x), height - sceneries[id].getHeight() - 48);
    }
}
