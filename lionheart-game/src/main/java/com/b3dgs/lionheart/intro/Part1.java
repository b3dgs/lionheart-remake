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
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Time;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Intro part 1 implementation.
 */
public final class Part1 implements Updatable
{
    private final Text text = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SANS_SERIF, 20, TextStyle.NORMAL);
    private final List<String> titles = Util.readLines(Medias.create(Folder.TEXT,
                                                                     Settings.getInstance().getLang(),
                                                                     Folder.INTRO,
                                                                     "part1.txt"));

    private final Sprite[] backs = new Sprite[4];
    private final Sprite[] sceneries = new Sprite[6];
    private final Sprite title = Drawable.loadSprite(Medias.create(Folder.INTRO, "part1", "title.png"));

    private final Camera cameraBack = new Camera();
    private final Camera cameraScenery = new Camera();

    private final Time time;
    private final double wide;

    private double alphaText;
    private double alphaTextOld;

    /** Used to cache text rendering on first pass. */
    private boolean force = true;

    /**
     * Constructor.
     * 
     * @param time The time reference.
     * @param wide The wide factor.
     */
    public Part1(Time time, double wide)
    {
        super();

        this.time = time;
        this.wide = wide;
    }

    /**
     * Load part.
     */
    public void load()
    {
        title.load();
        title.prepare();
        title.setAlpha(0);

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

    @Override
    public void update(double extrp)
    {
        alphaTextOld = alphaText;

        // Text fades
        updateAlphaText(extrp, 2700, 5350, 5350, 7050);
        updateAlphaText(extrp, 7050, 10000, 12000, 15200);
        updateAlphaText(extrp, 15200, 18100, 18100, 20200);
        updateAlphaText(extrp, 20200, 23100, 23100, 25200);
        updateAlphaText(extrp, 25200, 28100, 28100, 30200);
        updateAlphaText(extrp, 30200, 33100, 33100, 35200);

        // Start moving camera until door
        if (time.isAfter(10500))
        {
            cameraBack.moveLocation(extrp, 0.45, 0.0);
            cameraScenery.moveLocation(extrp, 0.77, 0.0);
            final double x = cameraScenery.getX();
            if (x > 1760 - Math.ceil(158.4 * wide))
            {
                cameraScenery.setLocation(1760 - Math.floor(158.4 * wide), cameraScenery.getY());
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
                            Graphic g)
    {
        if (force || time.isBetween(start, end))
        {
            if (force || alphaText > 0)
            {
                if (Double.compare(alphaTextOld, alphaText) != 0)
                {
                    text.setColor(Constant.ALPHAS_WHITE[(int) alphaText]);
                }
                text.draw(g, width / 2 + x1, height / 2 + y1, align, text1);
                text.draw(g, width / 2 + x2, height / 2 + y2, align, text2);
                if (text3 != null)
                {
                    text.draw(g, width / 2 + x2, height / 2 + y2 + 24, align, text3);
                }
                if (text4 != null)
                {
                    text.draw(g, width / 2 + x2, height / 2 + y2 + 48, align, text4);
                }
            }
        }
    }

    /**
     * Render part.
     * 
     * @param g The graphic output.
     * @param width The width.
     * @param height The height.
     */
    public void render(Graphic g, int width, int height)
    {
        g.clear(0, 0, width, height);

        // Render backs
        if (time.isBefore(42000))
        {
            for (int i = 0; i < backs.length; i++)
            {
                backs[i].setLocation(Math.floor(cameraBack.getViewpointX(i * backs[i].getWidth())),
                                     height / 2 - backs[i].getHeight() / 2);
                if (UtilMath.isBetween(backs[i].getX(), -backs[i].getWidth(), width))
                {
                    backs[i].render(g);
                }
            }
        }

        // Render sceneries
        renderScenery(g, width, height, 0, 32);
        renderScenery(g, width, height, 1, 420);
        renderScenery(g, width, height, 0, 570);
        renderScenery(g, width, height, 1, 670);
        renderScenery(g, width, height, 2, 730);
        renderScenery(g, width, height, 0, 790);
        renderScenery(g, width, height, 3, 910);
        renderScenery(g, width, height, 0, 980);
        renderScenery(g, width, height, 4, 1350);
        renderScenery(g, width, height, 5, 1650);

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
                   g);
        if (time.isBetween(6450, 13100))
        {
            if (alphaText > 0)
            {
                if (Double.compare(alphaTextOld, alphaText) != 0)
                {
                    title.setAlpha((int) alphaText);
                }
                title.setLocation(width / 2 - title.getWidth() / 2, height / 2 - title.getHeight() / 2 - 16);
                title.render(g);
            }
        }
        renderText(15200,
                   19200,
                   -120,
                   -60,
                   -110,
                   -35,
                   Align.LEFT,
                   titles.get(0),
                   "Erwin Kloibhofer",
                   "Michael Bittner",
                   "Pierre-Alexandre (remake)",
                   width,
                   height,
                   g);

        renderText(20200,
                   24200,
                   -120,
                   -38,
                   -58,
                   -12,
                   Align.LEFT,
                   titles.get(1),
                   "Henk Nieborg",
                   null,
                   null,
                   width,
                   height,
                   g);

        renderText(25200,
                   29200,
                   -120,
                   -38,
                   -42,
                   -12,
                   Align.LEFT,
                   titles.get(2),
                   "Erik Simon",
                   null,
                   null,
                   width,
                   height,
                   g);

        renderText(30200,
                   34200,
                   -110,
                   -38,
                   -110,
                   -12,
                   Align.LEFT,
                   titles.get(3),
                   "Matthias Steinwachs",
                   null,
                   null,
                   width,
                   height,
                   g);

        force = false;
    }

    /**
     * Update alpha text.
     * 
     * @param extrp The extrapolation value.
     * @param start1 The entering start.
     * @param end1 The entering end.
     * @param start2 The ending start.
     * @param end2 The ending end.
     */
    private void updateAlphaText(double extrp, int start1, int end1, int start2, int end2)
    {
        if (time.isBetween(start1, end1))
        {
            alphaText += 4.0 * extrp;
        }
        else if (time.isBetween(start2, end2))
        {
            alphaText -= 4.0 * extrp;
        }
        alphaText = UtilMath.clamp(alphaText, 0.0, 255.0);
    }

    /**
     * Render a scenery.
     * 
     * @param g The graphic output.
     * @param width The width.
     * @param height The height.
     * @param id The scenery id.
     * @param x The horizontal location.
     */
    private void renderScenery(Graphic g, int width, int height, int id, int x)
    {
        sceneries[id].setLocation(Math.floor(cameraScenery.getViewpointX(x)),
                                  height - sceneries[id].getHeight() + (144 - height) / 2.0);
        if (UtilMath.isBetween(sceneries[id].getX(), -sceneries[id].getWidth(), width))
        {
            sceneries[id].render(g);
        }
    }
}
