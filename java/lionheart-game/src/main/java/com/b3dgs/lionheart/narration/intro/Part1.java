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
package com.b3dgs.lionheart.narration.intro;

import java.io.Closeable;
import java.util.List;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionheart.FadeSide;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Time;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.narration.NarrationFactory;
import com.b3dgs.lionheart.narration.TextData;

/**
 * Intro part 1 implementation.
 */
public final class Part1 implements Closeable
{

    private static final int BAND_HEIGHT = 144;

    private static final int SPRITE_BACKS_COUNT = 4;

    private static final double CAMERA_INIT_X = 16.0;
    private static final double CAMERA_SPEED_BACK = 0.5;
    private static final double CAMERA_SPEED_SCENERY = 1.0;

    private static final int FADE_IN = 4;
    private static final int TEXT_ALPHA_SPEED = 6;
    private static final int BACKGROUND_X_MAX = 810;

    private static final String PART1_FOLDER = "part1";
    private static final String PART1_TEXT = "part1.txt";

    private static final String TXT_STUDIO = "BYRON 3D GAMES STUDIO";
    private static final String TXT_PRESENTS = "PRESENTS";
    private static final String TXT_DEV1 = "                Erwin Kloibhofer";
    private static final String TXT_DEV2 = "                Michael Bittner";
    private static final String TXT_DEV3 = "(remake) Pierre-Alexandre";
    private static final String TXT_ARTIST = "Henk Nieborg";
    private static final String TXT_GAMEPLAY = "Erik Simon";
    private static final String TXT_SOUND = "Matthias Steinwachs";

    private static final int T_FADE_IN = 0;
    private static final int T_START_CAMERA_MOVE_MS = 10_100;
    private static final int T_STO_START = 2350;
    private static final int T_STO_END = 5200;
    private static final int T_LGO_START = 7000;
    private static final int T_LGO_END = 12100;
    private static final int T_DEV_START = 15100;
    private static final int T_DEV_END = 17800;
    private static final int T_GFX_START = 20100;
    private static final int T_GFX_END = 22800;
    private static final int T_GPY_START = 25300;
    private static final int T_GPY_END = 27800;
    private static final int T_SFX_START = 30400;
    private static final int T_SFX_END = 32800;
    private static final int T_END = 47_500;

    private final Scenery[] sceneries = createSceneries();
    private final Sprite[] backs = new Sprite[SPRITE_BACKS_COUNT];
    private final Sprite title = Util.get(Folder.INTRO, PART1_FOLDER, "title.png");
    private final Sprite titleShade = Util.get(Folder.INTRO, PART1_FOLDER, "title_shade.png");
    private final SpriteFont font = Util.loadFont("fontintro.png", "fontintro.xml", 24, 28);
    private final List<String> txt = Util.readLines(Medias.create(Folder.TEXT,
                                                                  Settings.getInstance().getLang(),
                                                                  Folder.INTRO,
                                                                  PART1_TEXT));
    private final Camera cameraBack = new Camera();
    private final Camera cameraScenery = new Camera();

    private final Time time;
    private final int width;
    private final int height;
    private final double cameraMax;

    private int alphaTitleOld;
    private double alphaShade;

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

    private Scenery[] createSceneries()
    {
        return new Scenery[]
        {
            new Scenery(0, 32), new Scenery(1, 410), new Scenery(0, 620), new Scenery(1, 745), new Scenery(2, 795),
            new Scenery(0, 850), new Scenery(3, 1000), new Scenery(0, 1090), new Scenery(4, 1510), new Scenery(5, 1830),
        };
    }

    /**
     * Load part.
     * 
     * @param narration The narration factory.
     */
    public void load(NarrationFactory narration)
    {
        for (int i = 0; i < backs.length; i++)
        {
            backs[i] = Util.get(Folder.INTRO, PART1_FOLDER, "back" + i + ".png");
        }
        cameraBack.teleport(CAMERA_INIT_X, 0.0);
        cameraScenery.teleport(CAMERA_INIT_X, 0.0);

        init(narration);
    }

    private void init(NarrationFactory n)
    {
        final TextData title = new TextData(time, font, T_LGO_START, T_LGO_END, 0, 56, 0, Align.CENTER);
        final int hx = width / 2;
        final int hy = height / 2;

        n.add(T_FADE_IN, FadeSide.IN, FADE_IN);
        n.add(T_FADE_IN, T_END, this::renderBackground);

        n.add(T_STO_START, T_STO_END, font, hx, hy - 44, hx, Align.CENTER, TXT_STUDIO, TXT_PRESENTS);
        n.add(T_LGO_START, T_LGO_END + 3000, extrp -> updateTitle(extrp, title), g -> renderTitle(g, title));

        n.add(T_START_CAMERA_MOVE_MS, T_END, this::updateCameraMove);

        n.add(T_DEV_START, T_DEV_END, font, hx - 140, hy - 68, hx - 154, txt.get(0), TXT_DEV1, TXT_DEV2, TXT_DEV3);

        final int x = hx - 112;
        final int y = hy - 46;
        n.add(T_GFX_START, T_GFX_END, font, x, y, hx - 34, txt.get(1), TXT_ARTIST);
        n.add(T_GPY_START, T_GPY_END, font, x, y, hx - 16, txt.get(2), TXT_GAMEPLAY);
        n.add(T_SFX_START, T_SFX_END, font, x, y, hx - 112, txt.get(3), TXT_SOUND);
    }

    @Override
    public void close()
    {
        for (int i = 0; i < sceneries.length; i++)
        {
            sceneries[i].close();
        }
        for (int i = 0; i < backs.length; i++)
        {
            backs[i].dispose();
        }
        title.dispose();
        titleShade.dispose();
        font.dispose();
        txt.clear();
    }

    /**
     * Update camera move right until door.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateCameraMove(double extrp)
    {
        cameraBack.moveLocation(extrp, CAMERA_SPEED_BACK, 0.0);
        cameraScenery.moveLocation(extrp, CAMERA_SPEED_SCENERY, 0.0);

        final double x = cameraScenery.getX();
        if (x > cameraMax)
        {
            cameraScenery.teleport(cameraMax, cameraScenery.getY());
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
            final int y = height / 2 - backs[0].getHeight() / 2;

            for (int i = 0; i < backs.length; i++)
            {
                backs[i].setLocation(i * backs[0].getWidth() - x, y);
                if (UtilMath.isBetween(backs[i].getX(), -backs[i].getWidth(), width))
                {
                    backs[i].render(g);
                }
            }
        }
        for (int i = 0; i < sceneries.length; i++)
        {
            sceneries[i].render(g);
        }
    }

    /**
     * Render title.
     * 
     * @param g The graphic output.
     * @param data The title data.
     */
    private void renderTitle(Graphic g, TextData data)
    {
        final int alpha = data.getAlpha();
        if (alpha > 0)
        {
            if (alpha == 255 || alpha < 255 && Double.compare(alphaShade, 255.0) == 0)
            {
                updateTitleAlpha(alpha);
                title.setLocation(width / 2 - title.getWidth() / 2, height / 2 - data.getY());
                title.render(g);
            }
            if (alphaShade < 255.0)
            {
                updateTitleShadeAlpha(alpha);
                titleShade.setLocation(width / 2 - titleShade.getWidth() / 2, height / 2 - data.getY());
                titleShade.render(g);
            }
        }
    }

    /**
     * Update title shade effect.
     * 
     * @param extrp The extrp value.
     * @param data The title data.
     */
    private void updateTitle(double extrp, TextData data)
    {
        data.update(extrp);

        final int alpha = data.getAlpha();
        if (alpha > 0)
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
    }

    /**
     * Update title shade alpha.
     * 
     * @param alpha The alpha value.
     */
    private void updateTitleShadeAlpha(int alpha)
    {
        final int fade = alpha - (int) Math.floor(alphaShade);
        titleShade.setAlpha(fade);
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
            title.setAlpha(alpha);
        }
    }

    private class Scenery implements Renderable, Closeable
    {
        private final Sprite sprite;
        private final int x;

        /**
         * Create scenery.
         * 
         * @param id The id.
         * @param x The horizontal location.
         */
        Scenery(int id, int x)
        {
            super();

            this.x = x;
            sprite = Util.get(Folder.INTRO, PART1_FOLDER, "scenery" + id + ".png");
        }

        @Override
        public void render(Graphic g)
        {
            sprite.setLocation((int) Math.floor(cameraScenery.getViewpointX(x)),
                               height - sprite.getHeight() + (BAND_HEIGHT - height) / 2);
            if (UtilMath.isBetween(sprite.getX(), -sprite.getWidth(), width))
            {
                sprite.render(g);
            }
        }

        @Override
        public void close()
        {
            sprite.dispose();
        }
    }
}
