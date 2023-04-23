/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.landscape;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFolder;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.background.BackgroundAbstract;
import com.b3dgs.lionengine.game.background.BackgroundComponent;
import com.b3dgs.lionengine.game.background.BackgroundElement;
import com.b3dgs.lionengine.game.background.BackgroundElementRastered;
import com.b3dgs.lionengine.game.background.Parallax;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.WorldType;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Lava background implementation.
 */
final class Lava extends BackgroundAbstract
{
    private static final int HEIGHT_MAX = 338;
    private static final int HEIGHT_TOTAL = 85;

    private static final double MOON_SCALE_X = 0.6;
    private static final int MOON_OFFSET_Y = 52;
    private static final int MOUNTAIN_OFFSET_Y = -10;
    private static final int MOUNTAIN2_OFFSET_Y = 48;

    private static final double MOUNTAIN_SPEED_FACTOR = 0.15;
    private static final double MOUNTAIN2_SPEED_FACTOR = 0.25;

    private static final int PARALLAX_W = 60;
    private static final int PARALLAX_H = 100;
    private static final int PARALLAX_Y = 128;
    private static final int PARALLAX_LINES = 96;

    private final Backdrop backdrop;
    private final Parallax parallax;

    private double moonOffsetX;

    /**
     * Constructor.
     * 
     * @param source The resolution source reference.
     * @param scaleH The horizontal factor.
     * @param scaleV The horizontal factor.
     * @param theme The theme name.
     * @param flickering The flickering flag.
     */
    Lava(SourceResolutionProvider source, double scaleH, double scaleV, String theme, boolean flickering)
    {
        super(theme, 0, HEIGHT_MAX);

        totalHeight = HEIGHT_TOTAL;

        final int width = source.getWidth();
        final int halfScreen = source.getWidth() / 3;

        final String path = UtilFolder.getPathSeparator(Medias.getSeparator(),
                                                        Folder.BACKGROUND,
                                                        WorldType.LAVA.getFolder(),
                                                        theme);
        backdrop = new Backdrop(path, flickering, width);
        parallax = new Parallax(source,
                                Medias.create(path, "parallax.png"),
                                PARALLAX_LINES,
                                halfScreen,
                                PARALLAX_Y,
                                PARALLAX_W,
                                PARALLAX_H);
        moonOffsetX = width * MOON_SCALE_X;

        add(backdrop);
        add(parallax);
    }

    @Override
    public void setScreenSize(int width, int height)
    {
        moonOffsetX = width * MOON_SCALE_X;
        setOffsetY(height - Constant.RESOLUTION_GAME.getHeight());
        backdrop.setScreenWidth(width);
        parallax.setScreenSize(width, height);
    }

    /**
     * Backdrop represents the back background plus top background elements.
     */
    private final class Backdrop implements BackgroundComponent
    {
        private final BackgroundElement cloud;
        private final BackgroundElement backcolorA;
        private final BackgroundElement backcolorB;
        private final BackgroundElement mountain;
        private final BackgroundElement mountain2;
        private final BackgroundElementRastered moon;
        private final Sprite mountainSprite;
        private final Sprite mountain2Sprite;
        private final boolean flickering;
        private final int moonOffset;
        private int w;
        private int screenWidth;
        private boolean flicker;

        /**
         * Constructor.
         * 
         * @param path The backdrop path.
         * @param flickering The flickering flag effect.
         * @param screenWidth The screen width.
         */
        Backdrop(String path, boolean flickering, int screenWidth)
        {
            super();

            this.flickering = flickering;
            cloud = createElement(path, "cloud.png", 0, 0);
            if (flickering)
            {
                backcolorA = createElement(path, "backcolor1.png", 0, 1);
                backcolorB = createElement(path, "backcolor2.png", 0, 1);
            }
            else
            {
                backcolorA = createElement(path, "backcolor.png", 0, 1);
                backcolorB = null;
            }
            mountain = createElement(path, "mountain.png", 0, PARALLAX_Y + MOUNTAIN_OFFSET_Y);
            mountain2 = createElement(path, "mountain2.png", 0, PARALLAX_Y + MOUNTAIN2_OFFSET_Y);
            moonOffset = MOON_OFFSET_Y;
            moon = new BackgroundElementRastered(0,
                                                 moonOffset,
                                                 Medias.create(path, "moon.png"),
                                                 Medias.create(path, "palette.png"),
                                                 Medias.create(path, "raster.png"));
            mountainSprite = (Sprite) mountain.getRenderable();
            mountain2Sprite = (Sprite) mountain2.getRenderable();
            this.screenWidth = screenWidth;
            w = (int) Math.ceil(screenWidth / (double) ((Sprite) mountain.getRenderable()).getWidth()) + 1;
        }

        /**
         * Called when the resolution changed.
         * 
         * @param width The new width.
         */
        private void setScreenWidth(int width)
        {
            screenWidth = width;
            w = (int) Math.ceil(screenWidth / (double) ((Sprite) mountain.getRenderable()).getWidth()) + 1;
        }

        /**
         * Render backdrop element.
         * 
         * @param g The graphic output.
         */
        private void renderBackdrop(Graphic g)
        {
            final Sprite sprite;
            if (flicker)
            {
                sprite = (Sprite) backcolorB.getRenderable();
            }
            else
            {
                sprite = (Sprite) backcolorA.getRenderable();
            }
            for (int i = 0; i < Math.ceil(screenWidth / (double) sprite.getWidth()); i++)
            {
                final int x = backcolorA.getMainX() + i * sprite.getWidth();
                final double y = backcolorA.getOffsetY() + backcolorA.getMainY() - 1;
                sprite.setLocation(x, y);
                sprite.render(g);
            }
        }

        /**
         * Render cloud element.
         * 
         * @param g The graphic output.
         */
        private void renderCloud(Graphic g)
        {
            final Sprite cloudSprite = (Sprite) cloud.getRenderable();
            for (int i = 0; i < Math.ceil(screenWidth / (double) cloudSprite.getWidth()); i++)
            {
                final int x = cloud.getMainX() + i * cloudSprite.getWidth();
                final double y = cloud.getOffsetY() + cloud.getMainY();
                cloudSprite.setLocation(x, y);
                cloudSprite.render(g);
            }
        }

        /**
         * Render moon element.
         * 
         * @param g The graphic output.
         */
        private void renderMoon(Graphic g)
        {
            final int id = (int) (mountain.getOffsetY() + (totalHeight - getOffsetY()));
            moon.setRaster(id);
            moon.setLocation(moon.getMainX() + moonOffsetX, moon.getOffsetY() + moon.getMainY());
            moon.render(g);
        }

        /**
         * Render mountains element.
         * 
         * @param g The graphic output.
         */
        private void renderMountains(Graphic g)
        {
            final int oy = (int) (mountain.getOffsetY() + mountain.getMainY());
            final int ox = (int) (-mountain.getOffsetX() + mountain.getMainX());
            final int sx = mountainSprite.getWidth();
            for (int j = 0; j < w; j++)
            {
                mountainSprite.setLocation(ox + sx * j, oy);
                mountainSprite.render(g);
            }
        }

        /**
         * Render mountains element.
         * 
         * @param g The graphic output.
         */
        private void renderMountains2(Graphic g)
        {
            final int oy = (int) (mountain2.getOffsetY() + mountain2.getMainY());
            final int ox = (int) (-mountain2.getOffsetX() + mountain2.getMainX());
            final int sx = mountain2Sprite.getWidth();
            for (int j = 0; j < w; j++)
            {
                mountain2Sprite.setLocation(ox + sx * j, oy);
                mountain2Sprite.render(g);
            }
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            cloud.setOffsetY(y);
            backcolorA.setOffsetY(y);
            moon.setOffsetY(moonOffset - totalHeight + getOffsetY());

            mountain.setOffsetX(UtilMath.wrapDouble(mountain.getOffsetX() + speed * MOUNTAIN_SPEED_FACTOR,
                                                    0.0,
                                                    mountainSprite.getWidth()));
            mountain.setOffsetY(y);

            mountain2.setOffsetX(UtilMath.wrapDouble(mountain2.getOffsetX() + speed * MOUNTAIN2_SPEED_FACTOR,
                                                     0.0,
                                                     mountain2Sprite.getWidth()));
            mountain2.setOffsetY(y);
        }

        @Override
        public void render(Graphic g)
        {
            renderBackdrop(g);
            renderMoon(g);
            renderCloud(g);
            renderMountains(g);
            renderMountains2(g);

            if (flickering)
            {
                flicker = !flicker;
            }
        }
    }
}
