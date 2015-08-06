/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.game.background.BackgroundComponent;
import com.b3dgs.lionengine.game.background.BackgroundElement;
import com.b3dgs.lionengine.game.background.BackgroundGame;
import com.b3dgs.lionengine.game.background.Parallax;
import com.b3dgs.lionheart.Scene;

/**
 * Swamp background implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Swamp extends BackgroundGame
{
    /** Moon rasters. */
    private static final int MOON_RASTERS = 20;

    /**
     * Create a rastered element.
     * 
     * @param path The surface path.
     * @param name The element name.
     * @param x The location x.
     * @param y The location y.
     * @param rastersNumber The number of rasters to use.
     * @return The created element.
     */
    static ElementRastered createElementRastered(String path, String name, int x, int y, int rastersNumber)
    {
        final Sprite sprite = Drawable.loadSprite(Medias.create(path, name));
        sprite.load();
        sprite.prepare();
        return new ElementRastered(x, y, sprite, rastersNumber);
    }

    /** Backdrop. */
    private final Backdrop backdrop;
    /** Clouds. */
    private final Clouds clouds;
    /** Parallax. */
    private final Parallax parallax;
    /** Number of parallax lines. */
    private final int parallaxsNumber = 96;
    /** The horizontal factor. */
    double scaleH;
    /** The vertical factor. */
    double scaleV;

    /**
     * Constructor.
     * 
     * @param source The resolution source reference.
     * @param scaleH The horizontal factor.
     * @param scaleV The horizontal factor.
     * @param theme The theme name.
     * @param flickering The flickering flag.
     */
    public Swamp(Resolution source, double scaleH, double scaleV, String theme, boolean flickering)
    {
        super(theme, 0, 512);
        this.scaleH = scaleH;
        this.scaleV = scaleV;
        final int width = source.getWidth();
        final int halfScreen = (int) (source.getWidth() / 3.5);
        this.scaleH = scaleH;
        this.scaleV = scaleV;
        setOffsetY(source.getHeight() - Scene.RESOLUTION_ORIGINAL.getHeight() + 72);

        final String path = UtilFile.getPath(Landscape.DIR_BACKGROUNDS, WorldType.SWAMP.getFolder(), theme);
        backdrop = new Backdrop(path, flickering, width);
        clouds = new Clouds(Medias.create(path, "cloud.png"), width, 4);
        parallax = new Parallax(source, Medias.create(path, "parallax.png"), parallaxsNumber, halfScreen, 124, 50, 100);
        add(backdrop);
        add(clouds);
        add(parallax);
        totalHeight = 120;
    }

    /**
     * Called when the resolution changed.
     * 
     * @param width The new width.
     * @param height The new height.
     */
    public void setScreenSize(int width, int height)
    {
        final double scaleH = width / (double) Scene.RESOLUTION.getWidth();
        final double scaleV = height / (double) Scene.RESOLUTION.getHeight();
        this.scaleH = scaleH;
        this.scaleV = scaleV;
        setOffsetY(height - Scene.RESOLUTION.getHeight() + 72);
        backdrop.setScreenWidth(width);
        clouds.setScreenWidth(width);
        parallax.setScreenSize(width, height);
    }

    /**
     * Backdrop represents the back background plus top background elements.
     * 
     * @author Pierre-Alexandre (contact@b3dgs.com)
     */
    private final class Backdrop implements BackgroundComponent
    {
        /** Backdrop color A. */
        private final BackgroundElement backcolorA;
        /** Backdrop color B. */
        private final BackgroundElement backcolorB;
        /** Mountain element. */
        private final BackgroundElement mountain;
        /** Moon element. */
        private final ElementRastered moon;
        /** Mountain sprite. */
        private final Sprite mountainSprite;
        /** Flickering flag. */
        private final boolean flickering;
        /** Original offset. */
        private final int moonOffset;
        /** Screen wide value. */
        private int w;
        /** Screen width. */
        int screenWidth;
        /** Flickering counter. */
        private int flickerCount;
        /** Flickering type. */
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
            this.flickering = flickering;
            if (flickering)
            {
                backcolorA = createElement(path, "backcolor_a.png", 0, 0, false);
                backcolorB = createElement(path, "backcolor_b.png", 0, 0, false);
                flickerCount = 0;
            }
            else
            {
                backcolorA = createElement(path, "backcolor.png", 0, 0, false);
                backcolorB = null;
            }
            mountain = createElement(path, "mountain.png", 0, 124, false);
            final int x = (int) (208 * scaleH);
            moonOffset = 40;
            moon = createElementRastered(path, "moon.png", x, moonOffset, MOON_RASTERS);
            mountainSprite = (Sprite) mountain.getRenderable();
            this.screenWidth = screenWidth;
            w = (int) Math.ceil(screenWidth / (double) ((Sprite) mountain.getRenderable()).getWidth()) + 1;
        }

        /**
         * Called when the resolution changed.
         * 
         * @param width The new width.
         */
        void setScreenWidth(int width)
        {
            screenWidth = width;
            w = (int) Math.ceil(screenWidth / (double) ((Sprite) mountain.getRenderable()).getWidth()) + 1;
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            backcolorA.setOffsetY(y);
            moon.setOffsetY(-20 - moonOffset + getOffsetY());
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
            // Render back background first
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
            // Render moon
            final int id = (int) ((mountain.getOffsetY() + (moonOffset - getOffsetY())) / 4 + MOON_RASTERS);
            final Sprite spriteMoon = moon.getRaster(id);
            spriteMoon.setLocation(moon.getMainX(), moon.getOffsetY() + moon.getMainY());
            spriteMoon.render(g);

            // Render mountains
            final int oy = (int) (mountain.getOffsetY() + mountain.getMainY());
            final int ox = (int) (-mountain.getOffsetX() + mountain.getMainX());
            final int sx = mountainSprite.getWidth();
            for (int j = 0; j < w; j++)
            {
                mountainSprite.setLocation(ox + sx * j, oy);
                mountainSprite.render(g);
            }
        }
    }
}
