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
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.WorldType;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Airship background implementation.
 */
final class Airship extends BackgroundAbstract
{
    private static final double FOREST_SPEED = 1.25;
    private static final int FOREST_Y = 180;

    private final Backdrop backdrop;
    private final BackdropForest backdropForest;
    private final BackdropRoad backdropRoad;
    private final Trees trees;

    /**
     * Constructor.
     * 
     * @param source The resolution source reference.
     * @param scaleH The horizontal factor.
     * @param scaleV The horizontal factor.
     * @param theme The theme name.
     * @param flickering The flickering flag.
     */
    Airship(SourceResolutionProvider source, double scaleH, double scaleV, String theme, boolean flickering)
    {
        super(theme, 0, 384);

        totalHeight = 240;

        final int width = source.getWidth();

        final String path = UtilFolder.getPathSeparator(Medias.getSeparator(),
                                                        Folder.BACKGROUNDS,
                                                        WorldType.AIRSHIP.getFolder(),
                                                        theme);
        backdrop = new Backdrop(path, flickering, width);
        trees = new Trees(Medias.create(path, "trees.png"), width, 4);
        backdropForest = new BackdropForest(path, flickering, width);
        backdropRoad = new BackdropRoad(path, flickering, width);

        add(backdrop);
        add(trees);
        add(backdropForest);
        add(backdropRoad);
    }

    /**
     * Called when the resolution changed.
     * 
     * @param width The new width.
     * @param height The new height.
     */
    public void setScreenSize(int width, int height)
    {
        setOffsetY(height - Constant.NATIVE_RESOLUTION.getHeight());
        trees.setScreenWidth(width);
        backdrop.setScreenWidth(width);
        backdropForest.setScreenWidth(width);
        backdropRoad.setScreenWidth(width);
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
            backcolorA = createElement(path, "backcolor1.png", 0, 0);
            if (flickering)
            {
                backcolorB = createElement(path, "backcolor2.png", 0, 0);
            }
            else
            {
                backcolorB = null;
            }
            this.screenWidth = screenWidth;
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

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            backcolorA.setOffsetY(y);

            if (flickering)
            {
                flicker = !flicker;
            }
        }

        @Override
        public void render(Graphic g)
        {
            renderBackdrop(g);
        }
    }

    /**
     * Backdrop represents the back background plus top background elements.
     */
    private final class BackdropForest implements BackgroundComponent
    {
        private final BackgroundElement backcolorA;
        private final BackgroundElement backcolorB;
        private final BackgroundElement forest;
        private final Sprite forestSprite;
        private final boolean flickering;
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
        BackdropForest(String path, boolean flickering, int screenWidth)
        {
            super();

            this.flickering = flickering;
            backcolorA = createElement(path, "backcolorForest1.png", 0, FOREST_Y + 94);
            if (flickering)
            {
                backcolorB = createElement(path, "backcolorForest2.png", 0, FOREST_Y + 94);
            }
            else
            {
                backcolorB = null;
            }
            forest = createElement(path, "forest.png", 0, FOREST_Y);

            forestSprite = (Sprite) forest.getRenderable();
            this.screenWidth = screenWidth;
            w = (int) Math.ceil(screenWidth / (double) ((Sprite) forest.getRenderable()).getWidth()) + 1;
        }

        /**
         * Called when the resolution changed.
         * 
         * @param width The new width.
         */
        private void setScreenWidth(int width)
        {
            screenWidth = width;
            w = (int) Math.ceil(screenWidth / (double) ((Sprite) forest.getRenderable()).getWidth()) + 1;
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
         * Render forest element.
         * 
         * @param g The graphic output.
         */
        private void renderForest(Graphic g)
        {
            final int oy = (int) (forest.getOffsetY() + forest.getMainY());
            final int ox = (int) (-forest.getOffsetX() + forest.getMainX());
            final int sx = forestSprite.getWidth();
            for (int j = 0; j < w; j++)
            {
                forestSprite.setLocation(ox + sx * j, oy);
                forestSprite.render(g);
            }
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            backcolorA.setOffsetY(y);
            forest.setOffsetX(UtilMath.wrapDouble(forest.getOffsetX() + FOREST_SPEED, 0.0, forestSprite.getWidth()));
            forest.setOffsetY(y);

            if (flickering)
            {
                flicker = !flicker;
            }
        }

        @Override
        public void render(Graphic g)
        {
            renderBackdrop(g);
            renderForest(g);
        }
    }

    /**
     * Backdrop represents the back background plus top background elements.
     */
    private final class BackdropRoad implements BackgroundComponent
    {
        private final BackgroundElement road;
        private final Sprite roadSprite;
        private int w;
        private int screenWidth;

        /**
         * Constructor.
         * 
         * @param path The backdrop path.
         * @param flickering The flickering flag effect.
         * @param screenWidth The screen width.
         */
        BackdropRoad(String path, boolean flickering, int screenWidth)
        {
            super();

            road = createElement(path, "road.png", 0, 0);

            roadSprite = (Sprite) road.getRenderable();
            this.screenWidth = screenWidth;
            w = (int) Math.ceil(screenWidth / (double) ((Sprite) road.getRenderable()).getWidth()) + 1;
        }

        /**
         * Called when the resolution changed.
         * 
         * @param width The new width.
         */
        private void setScreenWidth(int width)
        {
            screenWidth = width;
            w = (int) Math.ceil(screenWidth / (double) roadSprite.getWidth()) + 1;
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            road.setOffsetX(UtilMath.wrapDouble(road.getOffsetX() + 1.5, 0.0, roadSprite.getWidth()));
            road.setOffsetY(y + 384);
        }

        @Override
        public void render(Graphic g)
        {
            final int oy = (int) road.getOffsetY();
            final int ox = (int) (-road.getOffsetX() + road.getMainX());
            final int sx = roadSprite.getWidth();
            for (int j = 0; j < w; j++)
            {
                roadSprite.setLocation(ox + sx * j, oy);
                roadSprite.render(g);
            }
        }
    }
}
