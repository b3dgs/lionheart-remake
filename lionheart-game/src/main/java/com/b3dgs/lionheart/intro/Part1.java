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
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Renderable;
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
public final class Part1 implements Updatable, Renderable
{
    private static final String PART1_FOLDER = "part1";
    private static final String PART1_TEXT = "part1.txt";

    private static final int BAND_HEIGHT = 144;

    private static final int TEXT_SIZE = 24;
    private static final String TEXT_FONT = com.b3dgs.lionengine.Constant.FONT_SERIF;
    private static final int TEXT_ALPHA_SPEED = 6;
    private static final int TEXT_LINE_HEIGHT = 22;

    private static final int SPRITE_BACKS_COUNT = 4;
    private static final int SPRITE_SCENERIES_COUNT = 6;

    private static final double SPEED_CAMERA_BACK = 0.5;
    private static final double SPEED_CAMERA_SCENERY = 1.0;

    private static final int BACKGROUND_X_MAX = 810;

    private static final int TIME_START_CAMERA_MOVE_MS = 10100;

    /**
     * Get media from filename.
     * 
     * @param filename The filename.
     * @return The media.
     */
    private static Media get(String filename)
    {
        return Medias.create(Folder.INTRO, PART1_FOLDER, filename + ".png");
    }

    private final Text text = Graphics.createText(TEXT_FONT, TEXT_SIZE, TextStyle.BOLD);
    private final List<String> titles = Util.readLines(Medias.create(Folder.TEXT,
                                                                     Settings.getInstance().getLang(),
                                                                     Folder.INTRO,
                                                                     PART1_TEXT));
    // @formatter:off
    private final TextData[] texts = new TextData[]
    {
        new TextData(2350, 5200, 0, -40, 0, Align.CENTER, "BYRON 3D GAMES STUDIO", "PRESENTS"),
        new TextData(7000, 12100, 0, 56, 0, Align.CENTER),
        new TextData(15100, 17800, -112, -64, -154, Align.LEFT, titles.get(0), "              Erwin Kloibhofer",
                     "              Michael Bittner", "(remake) Pierre-Alexandre"),
        new TextData(20100, 22800, -112, -42, -34, Align.LEFT, titles.get(1), "Henk Nieborg"),
        new TextData(25300, 27800, -112, -42, -16, Align.LEFT, titles.get(2), "Erik Simon"),
        new TextData(30400, 32800, -112, -42, -112, Align.LEFT, titles.get(3), "Matthias Steinwachs")
    };
    // @formatter:on
    private final SceneryData[] sceneriesData = new SceneryData[]
    {
        new SceneryData(0, 32), new SceneryData(1, 410), new SceneryData(0, 620), new SceneryData(1, 745),
        new SceneryData(2, 795), new SceneryData(0, 850), new SceneryData(3, 1000), new SceneryData(0, 1090),
        new SceneryData(4, 1510), new SceneryData(5, 1830),
    };
    private final Sprite[] backs = new Sprite[SPRITE_BACKS_COUNT];
    private final Sprite[] sceneries = new Sprite[SPRITE_SCENERIES_COUNT];
    private final Sprite[] titleAlpha = new Sprite[256];
    private final Sprite[] titleShadeAlpha = new Sprite[256];
    private final Sprite[] titleShadeFade = new Sprite[256];
    private final Camera cameraBack = new Camera();
    private final Camera cameraScenery = new Camera();
    private final int width;
    private final int height;
    private final Time time;
    private final double cameraMax;

    private Updatable updaterCamera = this::updateCameraIdle;
    private int alphaTitleOld;
    private double alphaShade;
    private double extrp;

    /** Used to cache text rendering on first pass. */
    private boolean force = true;

    /**
     * Constructor.
     * 
     * @param time The time reference.
     * @param width The screen width.
     * @param height The screen height.
     * @param wide The wide factor.
     */
    public Part1(Time time, int width, int height, double wide)
    {
        super();

        this.time = time;
        this.width = width;
        this.height = height;
        // CHECKSTYLE IGNORE LINE: MagicNumber
        cameraMax = 1941 - Math.ceil(158.4 * wide);
    }

    /**
     * Load part.
     */
    public void load()
    {
        final Sprite title = Drawable.loadSprite(get("title"));
        title.load();
        title.prepare();

        for (int i = 0; i < titleAlpha.length; i++)
        {
            titleAlpha[i] = Drawable.loadSprite(title.getSurface());
            titleAlpha[i].prepare();
            titleAlpha[i].setAlpha(i);
        }

        final Sprite titleShade = Drawable.loadSprite(get("title_shade"));
        titleShade.load();
        titleShade.prepare();
        for (int i = 0; i < titleShadeAlpha.length; i++)
        {
            titleShadeAlpha[i] = Drawable.loadSprite(titleShade.getSurface());
            titleShadeAlpha[i].prepare();
            titleShadeAlpha[i].setAlpha(i);

            titleShadeFade[i] = Drawable.loadSprite(titleShade.getSurface());
            titleShadeFade[i].prepare();
            titleShadeFade[i].setFade(i, i);
        }

        for (int i = 0; i < backs.length; i++)
        {
            backs[i] = Drawable.loadSprite(get("back" + i));
            backs[i].load();
            backs[i].prepare();
        }
        for (int i = 0; i < sceneries.length; i++)
        {
            sceneries[i] = Drawable.loadSprite(get("scenery" + i));
            sceneries[i].load();
            sceneries[i].prepare();
        }

        cameraBack.teleport(16.0, 0.0);
        cameraScenery.teleport(16.0, 0.0);
    }

    /**
     * Update camera idle phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateCameraIdle(double extrp)
    {
        if (time.isAfter(TIME_START_CAMERA_MOVE_MS))
        {
            updaterCamera = this::updateCameraMove;
        }
    }

    /**
     * Update camera move right until door.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateCameraMove(double extrp)
    {
        cameraBack.moveLocation(extrp, SPEED_CAMERA_BACK, 0.0);
        cameraScenery.moveLocation(extrp, SPEED_CAMERA_SCENERY, 0.0);

        final double x = cameraScenery.getX();
        if (x > cameraMax)
        {
            cameraScenery.setLocation(cameraMax, cameraScenery.getY());
            updaterCamera = UpdatableVoid.getInstance();
        }
    }

    /**
     * Render scrolling background.
     * 
     * @param g The graphic output.
     */
    private void renderBackground(Graphic g)
    {
        if (cameraBack.getX() < BACKGROUND_X_MAX)
        {
            final int x = (int) Math.floor(cameraBack.getX());
            final double y = height / 2.0 - backs[0].getHeight() / 2.0;

            for (int i = 0; i < backs.length; i++)
            {
                backs[i].setLocation(i * backs[0].getWidth() - x, y);
                if (UtilMath.isBetween(backs[i].getX(), -backs[i].getWidth(), width))
                {
                    backs[i].render(g);
                }
            }
        }
    }

    /**
     * Render title.
     * 
     * @param g The graphic output.
     */
    private void renderTitle(Graphic g)
    {
        final int alpha = (int) Math.floor(texts[1].getAlpha());
        if (alpha > 0)
        {
            updateTitleShade(alpha);
            if (alpha == 255 || alpha < 255 && Double.compare(alphaShade, 255.0) == 0)
            {
                updateTitleAlpha(alpha);
                titleAlpha[alpha].setLocation(width / 2.0 - titleAlpha[alpha].getWidth() / 2.0,
                                              height / 2.0 - texts[1].getY());
                titleAlpha[alpha].render(g);
            }

            if (alphaShade < 255.0)
            {
                final Sprite titleShade;
                if (alpha == 255)
                {
                    titleShade = titleShadeFade[alpha - (int) Math.floor(alphaShade)];
                }
                else
                {
                    titleShade = titleShadeAlpha[alpha];
                }
                titleShade.setLocation(width / 2.0 - titleShade.getWidth() / 2.0, height / 2.0 - texts[1].getY());
                titleShade.render(g);
            }
        }
    }

    /**
     * Update title shade effect.
     * 
     * @param alpha The alpha value.
     */
    private void updateTitleShade(int alpha)
    {
        if (alphaShade < 255.0 && alphaTitleOld != alpha)
        {
            alphaTitleOld = alpha;
        }
        if (alpha == 255 && alphaShade < 255.0)
        {
            alphaShade += TEXT_ALPHA_SPEED * extrp;
            if (alphaShade > 255.0)
            {
                alphaShade = 255.0;
            }
        }
    }

    /**
     * Update title alpha.
     * 
     * @param alpha The alpha value.
     */
    private void updateTitleAlpha(int alpha)
    {
        if (alphaTitleOld != alpha)
        {
            alphaTitleOld = alpha;
        }
    }

    /**
     * Render a scenery.
     * 
     * @param g The graphic output.
     * @param id The scenery id.
     * @param x The horizontal location.
     */
    private void renderScenery(Graphic g, int id, int x)
    {
        sceneries[id].setLocation(Math.floor(cameraScenery.getViewpointX(x)),
                                  height - sceneries[id].getHeight() + (BAND_HEIGHT - height) / 2.0);
        if (UtilMath.isBetween(sceneries[id].getX(), -sceneries[id].getWidth(), width))
        {
            sceneries[id].render(g);
        }
    }

    @Override
    public void update(double extrp)
    {
        this.extrp = extrp;
        for (int i = 0; i < texts.length; i++)
        {
            texts[i].update(extrp);
        }
        updaterCamera.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, width, height);

        renderBackground(g);

        for (int i = 0; i < sceneriesData.length; i++)
        {
            renderScenery(g, sceneriesData[i].getId(), sceneriesData[i].getX());
        }
        for (int i = 0; i < texts.length; i++)
        {
            texts[i].render(g);
        }

        renderTitle(g);

        force = false;
    }

    /**
     * Scenery data.
     */
    private static final class SceneryData
    {
        private final int id;
        private final int x;

        /**
         * Create scenery.
         * 
         * @param id The id.
         * @param x The horizontal location.
         */
        private SceneryData(int id, int x)
        {
            super();

            this.id = id;
            this.x = x;
        }

        /**
         * Get the id.
         * 
         * @return The id.
         */
        public int getId()
        {
            return id;
        }

        /**
         * Get the horizontal location.
         * 
         * @return The horizontal location.
         */
        public int getX()
        {
            return x;
        }
    }

    /**
     * Text data.
     */
    private final class TextData implements Updatable, Renderable
    {
        private final int timeStartMs;
        private final int timeEndMs;
        private final int x1;
        private final int y1;
        private final int x2;
        private final String[] texts;
        private final Align align;

        private double alpha;
        private double alphaOld;

        /**
         * Create data.
         * 
         * @param timeStartMs The starting time in milliseconds.
         * @param timeEndMs The ending time in milliseconds.
         * @param x1 The first text horizontal location.
         * @param y1 The first text vertical location.
         * @param x2 The other texts horizontal location.
         * @param align The text align.
         * @param texts The text lines.
         */
        private TextData(int timeStartMs, int timeEndMs, int x1, int y1, int x2, Align align, String... texts)
        {
            super();

            this.timeStartMs = timeStartMs;
            this.timeEndMs = timeEndMs;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.align = align;
            this.texts = texts;
        }

        /**
         * Get vertical location.
         * 
         * @return The vertical location.
         */
        public int getY()
        {
            return y1;
        }

        /**
         * Get current alpha.
         * 
         * @return The alpha value.
         */
        public double getAlpha()
        {
            return alpha;
        }

        @Override
        public void update(double extrp)
        {
            alphaOld = alpha;
            if (time.isBetween(timeStartMs, timeEndMs))
            {
                alpha += TEXT_ALPHA_SPEED * extrp;
            }
            else if (time.isAfter(timeEndMs))
            {
                alpha -= TEXT_ALPHA_SPEED * extrp;
            }
            alpha = UtilMath.clamp(alpha, 0.0, 255.0);
        }

        @Override
        public void render(Graphic g)
        {
            if (texts.length > 0 && (force || alpha > 0))
            {
                if (Double.compare(alphaOld, alpha) != 0)
                {
                    text.setColor(Constant.ALPHAS_WHITE[(int) alpha]);
                }
                text.draw(g, width / 2 + x1, height / 2 + y1, align, texts[0]);

                for (int i = 1; i < texts.length; i++)
                {
                    text.draw(g, width / 2 + x2, height / 2 + y1 + i * TEXT_LINE_HEIGHT, align, texts[i]);
                }
            }
        }
    }
}
