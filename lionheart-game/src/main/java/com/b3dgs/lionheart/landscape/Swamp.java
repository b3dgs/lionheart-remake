/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

/**
 * Swamp background implementation.
 */
final class Swamp extends BackgroundAbstract
{
    private static final int MOON_RASTERS = 20;
    private static final int PARALLAX_LINES = 96;

    private final Backdrop backdrop;
    private final Clouds clouds;
    private final Parallax parallax;
    private double scaleH;
    private double scaleV;

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
        super(theme, 0, 512);

        this.scaleH = scaleH;
        this.scaleV = scaleV;
        totalHeight = 112;

        final int width = source.getWidth();
        final int halfScreen = (int) (source.getWidth() / 3.5);

        final String path = UtilFolder.getPath(Constant.FOLDER_BACKGROUNDS, WorldType.SWAMP.getFolder(), theme);
        backdrop = new Backdrop(path, flickering, width);
        clouds = new Clouds(Medias.create(path, "cloud.png"), width, 4);
        parallax = new Parallax(source, Medias.create(path, "parallax.png"), PARALLAX_LINES, halfScreen, 124, 50, 100);
        add(backdrop);
        add(clouds);
        add(parallax);
    }

    /**
     * Called when the resolution changed.
     * 
     * @param width The new width.
     * @param height The new height.
     */
    public void setScreenSize(int width, int height)
    {
        final double scaleH = width / (double) Constant.NATIVE_RESOLUTION.getWidth();
        final double scaleV = height / (double) Constant.NATIVE_RESOLUTION.getHeight();
        this.scaleH = scaleH;
        this.scaleV = scaleV;
        setOffsetY(height - Constant.NATIVE_RESOLUTION.getHeight() + 40);
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
        private int flickerCount;
        private boolean flickerType;

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
            if (flickering)
            {
                backcolorA = createElement(path, "backcolor_a.png", 0, 0);
                backcolorB = createElement(path, "backcolor_b.png", 0, 0);
                flickerCount = 0;
            }
            else
            {
                backcolorA = createElement(path, "backcolor.png", 0, 0);
                backcolorB = null;
            }
            mountain = createElement(path, "mountain.png", 0, 124);
            final int x = (int) (165 * scaleH);
            moonOffset = 49;
            moon = new BackgroundElementRastered(x,
                                                 moonOffset,
                                                 Medias.create(path, "moon.png"),
                                                 Medias.create(path, "moon.xml"),
                                                 MOON_RASTERS);
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
            if (flickerType || !flickering)
            {
                sprite = (Sprite) backcolorA.getRenderable();
            }
            else
            {
                sprite = (Sprite) backcolorB.getRenderable();
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
            final int id = (int) (mountain.getOffsetY() + (totalHeight - getOffsetY())) / 6;
            final Sprite spriteMoon = moon.getRaster(id);
            spriteMoon.setLocation(moon.getMainX(), moon.getOffsetY() + moon.getMainY());
            spriteMoon.render(g);
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
                flickerCount = (flickerCount + 1) % 2;
                if (flickerCount == 0)
                {
                    flickerType = !flickerType;
                }
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
