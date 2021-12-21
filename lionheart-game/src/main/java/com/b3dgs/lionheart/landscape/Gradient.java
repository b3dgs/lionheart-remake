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

import com.b3dgs.lionengine.UtilFolder;
import com.b3dgs.lionengine.game.background.BackgroundAbstract;
import com.b3dgs.lionengine.game.background.BackgroundComponent;
import com.b3dgs.lionengine.game.background.BackgroundElement;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Gradient color background implementation.
 */
final class Gradient extends BackgroundAbstract
{
    private static final int OFFSET_Y = 82;

    private final Backdrop backdrop;

    /**
     * Constructor.
     * 
     * @param maxHeight The max height.
     * @param totalHeight The total height.
     * @param source The resolution source reference.
     * @param type The background type.
     * @param flickering The flickering flag.
     */
    Gradient(int maxHeight, int totalHeight, SourceResolutionProvider source, BackgroundType type, boolean flickering)
    {
        super(type.getTheme(), 0, maxHeight);

        final String path = UtilFolder.getPath(Folder.BACKGROUND, type.getWorld().getFolder(), type.getTheme());
        final int width = source.getWidth();
        backdrop = new Backdrop(path, flickering, width);
        add(backdrop);
        this.totalHeight = totalHeight;
        setScreenSize(source.getWidth(), source.getHeight());
    }

    @Override
    public void setScreenSize(int width, int height)
    {
        setOffsetY(height - Constant.RESOLUTION_GAME.getHeight() + OFFSET_Y);
        backdrop.setScreenWidth(width);
    }

    /**
     * Backdrop represents the back background plus top background elements.
     */
    private final class Backdrop implements BackgroundComponent
    {
        private final BackgroundElement backcolorA;
        private final BackgroundElement backcolorB;
        private final boolean flickering;
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

            if (flickering)
            {
                backcolorA = createElement(path, "backcolor1.png", 0, 0);
                backcolorB = createElement(path, "backcolor2.png", 0, 0);
            }
            else
            {
                backcolorA = createElement(path, "backcolor.png", 0, 0);
                backcolorB = null;
            }
            setScreenWidth(screenWidth);
        }

        /**
         * Called when the resolution changed.
         * 
         * @param width The new width.
         */
        private void setScreenWidth(int width)
        {
            screenWidth = width;
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            backcolorA.setOffsetY(y);
        }

        @Override
        public void render(Graphic g)
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
            if (flickering)
            {
                flicker = !flicker;
            }
        }
    }
}
