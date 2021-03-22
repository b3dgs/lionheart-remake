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
package com.b3dgs.lionheart.extro;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFolder;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.background.BackgroundAbstract;
import com.b3dgs.lionengine.game.background.BackgroundComponent;
import com.b3dgs.lionengine.game.background.BackgroundElement;
import com.b3dgs.lionengine.game.background.Parallax;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Dragon end background implementation.
 */
final class DragonEnd extends BackgroundAbstract
{
    private static final int PARALLAX_Y = 138;

    private final Backdrop backdrop;
    private final Parallax clouds;
    private final Parallax ground;

    /**
     * Constructor.
     * 
     * @param source The resolution source reference.
     */
    DragonEnd(SourceResolutionProvider source)
    {
        super(null, 0, 234);

        totalHeight = 85;

        final int width = source.getWidth();
        final int halfScreen = source.getWidth() / 3;

        final String path = UtilFolder.getPathSeparator(Medias.getSeparator(), Folder.BACKGROUNDS, "extro");
        backdrop = new Backdrop(path, width);
        clouds = new Parallax(source, Medias.create(path, "clouds.png"), PARALLAX_Y, halfScreen, 22, 50, 100);
        clouds.setInverted(true);

        ground = new Parallax(source, Medias.create(path, "ground.png"), 80, halfScreen, PARALLAX_Y + 44, 50, 100);
        add(backdrop);
        add(clouds);
        add(ground);
    }

    /**
     * Called when the resolution changed.
     * 
     * @param width The new width.
     * @param height The new height.
     */
    public void setScreenSize(int width, int height)
    {
        setOffsetY(height - Constant.RESOLUTION.getHeight());
        backdrop.setScreenWidth(width);
        clouds.setScreenSize(width, height);
        ground.setScreenSize(width, height);
    }

    /**
     * Backdrop represents the back background plus top background elements.
     */
    private final class Backdrop implements BackgroundComponent
    {
        private final BackgroundElement backcolor;
        private final BackgroundElement mountain;
        private final Sprite mountainSprite;
        private int w;
        private int screenWidth;

        /**
         * Constructor.
         * 
         * @param path The backdrop path.
         * @param screenWidth The screen width.
         */
        Backdrop(String path, int screenWidth)
        {
            super();

            backcolor = createElement(path, "backcolor.png", 0, 92);
            mountain = createElement(path, "mountain.png", 0, 230);
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
            final Sprite sprite = (Sprite) backcolor.getRenderable();
            for (int i = 0; i < Math.ceil(screenWidth / (double) sprite.getWidth()); i++)
            {
                final int x = backcolor.getMainX() + i * sprite.getWidth();
                final double y = backcolor.getOffsetY() + backcolor.getMainY();
                sprite.setLocation(x, y);
                sprite.render(g);
            }
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
            backcolor.setOffsetY(y);
            final double mx = mountain.getOffsetX() + speed * 0.24;
            mountain.setOffsetX(UtilMath.wrapDouble(mx, 0.0, mountainSprite.getWidth()));
            mountain.setOffsetY(y);
        }

        @Override
        public void render(Graphic g)
        {
            renderBackdrop(g);
            renderMountains(g);
        }
    }
}
