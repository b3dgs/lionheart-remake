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
 * Swamp background implementation.
 */
final class Swamp extends BackgroundAbstract
{
    private static final int HEIGHT_MAX = 338;
    private static final int HEIGHT_TOTAL = 82;

    private static final int CLOUD_Y = 4;

    private static final int MOON_OFFSET_X = -56;
    private static final int MOON_OFFSET_Y = 50;

    private static final int PARALLAX_W = 50;
    private static final int PARALLAX_H = 100;
    private static final int PARALLAX_Y = 127;
    private static final int PARALLAX_LINES = 96;

    private final Backdrop backdrop;
    private final Clouds clouds;
    private final Parallax parallax;
    private double scaleH;

    /**
     * Constructor.
     * 
     * @param source The resolution source reference.
     * @param scaleH The horizontal factor.
     * @param scaleV The horizontal factor.
     * @param theme The theme name.
     * @param flickering The flickering flag.
     */
    Swamp(SourceResolutionProvider source, double scaleH, double scaleV, String theme, boolean flickering)
    {
        super(theme, 0, HEIGHT_MAX);

        this.scaleH = scaleH;
        totalHeight = HEIGHT_TOTAL;

        final int width = source.getWidth();
        final int halfScreen = source.getWidth() / 3;

        final String path = UtilFolder.getPathSeparator(Medias.getSeparator(),
                                                        Folder.BACKGROUND,
                                                        WorldType.SWAMP.getFolder(),
                                                        theme);
        backdrop = new Backdrop(path, flickering, width);
        clouds = new Clouds(Medias.create(path, "cloud.png"), width, CLOUD_Y);
        parallax = new Parallax(source,
                                Medias.create(path, "parallax.png"),
                                PARALLAX_LINES,
                                halfScreen,
                                PARALLAX_Y,
                                PARALLAX_W,
                                PARALLAX_H);
        add(backdrop);
        add(clouds);
        add(parallax);
    }

    @Override
    public void setScreenSize(int width, int height)
    {
        scaleH = width / (double) Constant.RESOLUTION_GAME.getWidth();
        setOffsetY(height - Constant.RESOLUTION_GAME.getHeight());
        backdrop.setScreenWidth(width);
        clouds.setScreenWidth(width);
        parallax.setScreenSize(width, height);
    }

    /**
     * Backdrop represents the back background plus top background elements.
     */
    private final class Backdrop implements BackgroundComponent
    {
        private final BackgroundElement backcolorA;
        private final BackgroundElement backcolorB;
        private final BackgroundElement mountain;
        private final BackgroundElementRastered moon;
        private final Sprite mountainSprite;
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
            backcolorA = createElement(path, "backcolor1.png", 0, 0);
            if (flickering)
            {
                backcolorB = createElement(path, "backcolor2.png", 0, 0);
            }
            else
            {
                backcolorB = null;
            }
            mountain = createElement(path, "mountain.png", 0, PARALLAX_Y);
            moonOffset = MOON_OFFSET_Y;
            final int x = (int) (224 * scaleH);
            moon = new BackgroundElementRastered(x,
                                                 moonOffset,
                                                 Medias.create(path, "moon.png"),
                                                 Medias.create(path, "palette.png"),
                                                 Medias.create(path, "raster.png"));
            mountainSprite = (Sprite) mountain.getRenderable();
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
                final double y = backcolorA.getOffsetY() + backcolorA.getMainY();
                sprite.setLocation(x, y);
                sprite.render(g);
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
            moon.setLocation(moon.getMainX() - MOON_OFFSET_X, moon.getOffsetY() + moon.getMainY());
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

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            backcolorA.setOffsetY(y);
            moon.setOffsetY(moonOffset - totalHeight + getOffsetY());
            final double mx = mountain.getOffsetX() + speed * 0.24;
            mountain.setOffsetX(UtilMath.wrapDouble(mx, 0.0, mountainSprite.getWidth()));
            mountain.setOffsetY(y);

            if (flickering)
            {
                flicker = !flicker;
            }
        }

        @Override
        public void render(Graphic g)
        {
            renderBackdrop(g);
            renderMoon(g);
            renderMountains(g);
        }
    }
}
