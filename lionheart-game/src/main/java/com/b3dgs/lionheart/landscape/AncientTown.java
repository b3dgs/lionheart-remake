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
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.game.background.BackgroundComponent;
import com.b3dgs.lionengine.game.background.BackgroundElement;
import com.b3dgs.lionengine.game.background.BackgroundGame;
import com.b3dgs.lionheart.Scene;

/**
 * Ancient Town full background implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class AncientTown extends BackgroundGame
{
    /** Backdrop. */
    private final Backdrop backdrop;
    /** Flickering flag. */
    private final boolean flickering;
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
    AncientTown(Resolution source, double scaleH, double scaleV, String theme, boolean flickering)
    {
        super(theme, 0, 512);
        this.scaleH = scaleH;
        this.scaleV = scaleV;
        this.flickering = flickering;
        final String path = UtilFile.getPath(Landscape.DIR_BACKGROUNDS, "ancient_town", theme);
        final int width = source.getWidth();
        backdrop = new Backdrop(path, this.flickering, width);
        add(backdrop);
        totalHeight = 120;
        setScreenSize(source.getWidth(), source.getHeight());
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
        /** Flickering flag. */
        private final boolean flickering;
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
            setScreenWidth(screenWidth);
        }

        /**
         * Called when the resolution changed.
         * 
         * @param width The new width.
         */
        void setScreenWidth(int width)
        {
            screenWidth = width;
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            backcolorA.setOffsetY(y);

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
        }
    }
}
