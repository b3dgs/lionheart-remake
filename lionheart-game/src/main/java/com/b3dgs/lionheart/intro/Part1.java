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
package com.b3dgs.lionheart.intro;

import java.util.List;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Intro part 1 implementation.
 */
public final class Part1
{
    /** Text large. */
    private static final Text TEXT = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SERIF,
                                                         24,
                                                         TextStyle.NORMAL);
    /** Titles. */
    private static final List<String> TITLES = Util.readLines(Medias.create(Folder.TEXTS, Folder.INTRO, "part1.txt"));

    /** Backgrounds. */
    private final Sprite[] backs = new Sprite[4];
    /** Sceneries. */
    private final Sprite[] sceneries = new Sprite[6];
    /** Title. */
    private final Sprite title = Drawable.loadSprite(Medias.create(Folder.INTRO, "part1", "title.png"));
    /** Camera back. */
    private final Camera cameraBack = new Camera();
    /** Camera scenery. */
    private final Camera cameraScenery = new Camera();
    /** Wide factor. */
    private final double wide;

    /** Text alpha. */
    private double alphaText;

    /**
     * Constructor.
     * 
     * @param wide The wide factor.
     */
    public Part1(double wide)
    {
        super();

        this.wide = wide;
    }

    /**
     * Load part.
     */
    public void load()
    {
        title.load();
        for (int i = 0; i < backs.length; i++)
        {
            backs[i] = Drawable.loadSprite(Medias.create(Folder.INTRO, "part1", "back" + i + ".png"));
            backs[i].load();
            backs[i].prepare();
        }
        for (int i = 0; i < sceneries.length; i++)
        {
            sceneries[i] = Drawable.loadSprite(Medias.create(Folder.INTRO, "part1", "scenery" + i + ".png"));
            sceneries[i].load();
            sceneries[i].prepare();
        }
    }

    /**
     * Update part.
     * 
     * @param seek The current seek.
     * @param extrp The extrapolation value.
     */
    public void update(long seek, double extrp)
    {
        // Text fades
        updateAlphaText(2700, 5350, 5350, 6450, seek, extrp);
        updateAlphaText(7050, 10000, 12000, 14000, seek, extrp);
        updateAlphaText(15200, 18100, 18100, 20200, seek, extrp);
        updateAlphaText(20200, 23100, 23100, 25200, seek, extrp);
        updateAlphaText(25200, 28100, 28100, 30200, seek, extrp);
        updateAlphaText(30200, 33100, 33100, 35200, seek, extrp);

        // Start moving camera until door
        if (seek > 10500)
        {
            cameraBack.moveLocation(extrp, 0.45, 0.0);
            cameraScenery.moveLocation(extrp, 0.77, 0.0);
            final double x = cameraScenery.getX();
            if (x > 1760 - Math.ceil(158.4 * wide))
            {
                cameraScenery.setLocation(1760 - Math.round(158.4 * wide), cameraScenery.getY());
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
     * @param text3 The text 3.
     * @param text4 The text 4.
     * @param width The width.
     * @param height The height.
     * @param seek The current seek.
     * @param g The graphic output.
     */
    private void renderText(int start,
                            int end,
                            int x1,
                            int y1,
                            int x2,
                            int y2,
                            Align align,
                            String text1,
                            String text2,
                            String text3,
                            String text4,
                            int width,
                            int height,
                            long seek,
                            Graphic g)
    {
        if (seek > start && seek < end)
        {
            TEXT.setColor(Constant.ALPHAS_WHITE[(int) alphaText]);
            TEXT.draw(g, width / 2 + x1, height / 2 + y1, align, text1);
            TEXT.draw(g, width / 2 + x2, height / 2 + y2, align, text2);
            if (text3 != null)
            {
                TEXT.draw(g, width / 2 + x2, height / 2 + y2 + 24, align, text3);
            }
            if (text4 != null)
            {
                TEXT.draw(g, width / 2 + x2, height / 2 + y2 + 48, align, text4);
            }
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
    public void render(int width, int height, long seek, Graphic g)
    {
        g.clear(0, 0, width, height);

        // Render backs
        if (seek < 42000)
        {
            for (int i = 0; i < backs.length; i++)
            {
                backs[i].setLocation(Math.floor(cameraBack.getViewpointX(i * backs[i].getWidth())),
                                     height / 2 - backs[i].getHeight() / 2);
                backs[i].render(g);
            }
        }

        // Render sceneries
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

        // Render texts
        renderText(2300,
                   6450,
                   0,
                   -38,
                   0,
                   -16,
                   Align.CENTER,
                   "BYRON 3D GAMES STUDIO",
                   "PRESENTS",
                   null,
                   null,
                   width,
                   height,
                   seek,
                   g);
        if (seek > 6450 && seek < 13100)
        {
            title.setAlpha((int) alphaText);
            title.setLocation(width / 2 - title.getWidth() / 2, height / 2 - title.getHeight() / 2 - 16);
            title.render(g);
        }
        renderText(15200,
                   19200,
                   -115,
                   -60,
                   -100,
                   -35,
                   Align.LEFT,
                   TITLES.get(0),
                   "Erwin Kloibhofer",
                   "Michael Bittner",
                   "Pierre-Alexandre (remake)",
                   width,
                   height,
                   seek,
                   g);

        renderText(20200,
                   24200,
                   -110,
                   -38,
                   -58,
                   -12,
                   Align.LEFT,
                   TITLES.get(1),
                   "Henk Nieborg",
                   null,
                   null,
                   width,
                   height,
                   seek,
                   g);

        renderText(25200,
                   29200,
                   -110,
                   -38,
                   -42,
                   -12,
                   Align.LEFT,
                   TITLES.get(2),
                   "Erik Simon",
                   null,
                   null,
                   width,
                   height,
                   seek,
                   g);

        renderText(30200,
                   34200,
                   -110,
                   -38,
                   -110,
                   -12,
                   Align.LEFT,
                   TITLES.get(3),
                   "Matthias Steinwachs",
                   null,
                   null,
                   width,
                   height,
                   seek,
                   g);
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
    private void updateAlphaText(int start1, int end1, int start2, int end2, long seek, double extrp)
    {
        if (seek >= start1 && seek < end1)
        {
            alphaText += 4.0 * extrp;
        }
        else if (seek >= start2 && seek < end2)
        {
            alphaText -= 4.0 * extrp;
        }
        alphaText = UtilMath.clamp(alphaText, 0.0, 255.0);
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
        sceneries[id].setLocation(Math.floor(cameraScenery.getViewpointX(x)),
                                  height - sceneries[id].getHeight() + (144 - height) / 2.0);
        sceneries[id].render(g);
    }
}
