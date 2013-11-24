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
    /** Title. */
    private final Sprite title;
    /** Sceneries. */
    private final Sprite[] sceneries;
    /** Camera. */
    private final CameraGame camera;
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
        camera = new CameraGame();
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
        if (seek > 0 && seek < 2500)
        {
            alphaBack += 4.0;
        }
        updateAlphaText(2600, 5150, 5150, 7050, seek, extrp);
        updateAlphaText(7050, 10000, 11000, 13000, seek, extrp);
        updateAlphaText(7050, 12050, 12050, 15200, seek, extrp);
        updateAlphaText(15200, 18100, 18100, 20200, seek, extrp);
        updateAlphaText(20200, 23100, 23100, 25200, seek, extrp);
        updateAlphaText(25200, 28100, 28100, 30200, seek, extrp);
        updateAlphaText(30200, 33100, 33100, 35200, seek, extrp);

        if (seek > 10500)
        {
            camera.moveLocation(extrp, 0.45, 0.0);
            cameraScenery.moveLocation(extrp, 0.76, 0.0);
            if (cameraScenery.getLocationX() > 1602.0)
            {
                cameraScenery.teleportX(1602.0);
            }
        }
        alphaBack = UtilityMath.fixBetween(alphaBack, 0.0, 255.0);
        alphaText = UtilityMath.fixBetween(alphaText, 0.0, 255.0);
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
        if (seek < 42000)
        {
            for (int i = 0; i < backs.length; i++)
            {
                backs[i].render(g, camera.getViewpointX(i * backs[i].getWidth()), height / 2 - backs[i].getHeight() / 2);
            }
        }
        renderScenery(height, 0, 32, g);
        renderScenery(height, 1, 420, g);
        renderScenery(height, 0, 570, g);
        renderScenery(height, 1, 670, g);
        renderScenery(height, 2, 730, g);
        renderScenery(height, 0, 790, g);
        renderScenery(height, 3, 910, g);
        renderScenery(height, 0, 980, g);
        renderScenery(height, 4, 1350, g);
        renderScenery(height, 5, 1650, g);

        if (seek > 2600 && seek < 7050)
        {
            textLarge.setColor(Intro.ALPHAS_WHITE[(int) alphaText]);
            textLarge.draw(g, width / 2, height / 2 - 38, Align.CENTER, "BYRON 3D GAMES STUDIO");
            textLarge.draw(g, width / 2, height / 2 - 16, Align.CENTER, "PRESENTS");
        }
        if (seek > 7050 && seek < 13000)
        {
            title.setAlpha((int) alphaText);
            title.render(g, width / 2 - title.getWidth() / 2, height / 2 - title.getHeight() / 2 - 16);
        }
        if (seek > 15200 && seek < 19200)
        {
            textLarge.setColor(Intro.ALPHAS_WHITE[(int) alphaText]);
            textLarge.draw(g, width / 2 - 110, height / 2 - 60, Align.LEFT, "PROGRAMMING");
            textLarge.draw(g, width / 2 - 86, height / 2 - 35, Align.LEFT, "Pierre-Alexandre");
        }
        if (seek > 20200 && seek < 24200)
        {
            textLarge.setColor(Intro.ALPHAS_WHITE[(int) alphaText]);
            textLarge.draw(g, width / 2 - 110, height / 2 - 38, Align.LEFT, "GRAPHICS");
            textLarge.draw(g, width / 2 - 58, height / 2 - 12, Align.LEFT, "Henk Nieborg");
        }
        if (seek > 25200 && seek < 29200)
        {
            textLarge.setColor(Intro.ALPHAS_WHITE[(int) alphaText]);
            textLarge.draw(g, width / 2 - 110, height / 2 - 38, Align.LEFT, "GAMEDESIGN");
            textLarge.draw(g, width / 2 - 42, height / 2 - 12, Align.LEFT, "Erik Simon");
        }
        if (seek > 30200 && seek < 34200)
        {
            textLarge.setColor(Intro.ALPHAS_WHITE[(int) alphaText]);
            textLarge.draw(g, width / 2 - 110, height / 2 - 38, Align.LEFT, "MUSIC & SFX");
            textLarge.draw(g, width / 2 - 110, height / 2 - 12, Align.LEFT, "Matthias Steinwachs");
        }
        g.setColor(Intro.ALPHAS_BLACK[255 - (int) alphaBack]);
        g.drawRect(0, 0, width, height, true);
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
            alphaText += 4.0 * extrp;
        }
        else if (seek >= start2 && seek < end2)
        {
            alphaText -= 4.0 * extrp;
        }
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
