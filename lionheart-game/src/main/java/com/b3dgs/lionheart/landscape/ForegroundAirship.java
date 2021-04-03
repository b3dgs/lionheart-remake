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
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Airship foreground implementation.
 */
final class ForegroundAirship extends BackgroundAbstract implements Foreground
{
    /** Primary. */
    private final Secondary secondary;
    /** Screen width. */
    private int screenWidth;
    /** Screen height. */
    private int screenHeight;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param source The resolution source reference.
     * @param theme The theme name.
     * @param config The configuration.
     */
    ForegroundAirship(Services services, SourceResolutionProvider source, String theme, ForegroundConfig config)
    {
        super(theme, 0, 0);

        final String path = UtilFolder.getPathSeparator(Medias.getSeparator(), Folder.FOREGROUND, theme);
        secondary = new Secondary(path);

        setScreenSize(source.getWidth(), source.getHeight());
        add(secondary);
    }

    @Override
    public void renderFront(Graphic g)
    {
        renderComponent(0, g);
    }

    @Override
    public void setScreenSize(int width, int height)
    {
        screenWidth = width;
        screenHeight = height;
    }

    /**
     * Second front component.
     */
    private final class Secondary implements BackgroundComponent
    {
        /** Water element. */
        private final BackgroundElement foreground1;
        /** Water element. */
        private final BackgroundElement foreground2;

        /**
         * Constructor.
         * 
         * @param path The primary surface path.
         */
        Secondary(String path)
        {
            super();

            final Sprite sprite1 = Drawable.loadSprite(Medias.create(path, "foreground1.png"));
            sprite1.load();
            sprite1.prepare();
            foreground1 = new BackgroundElement(0, 0, sprite1);

            final Sprite sprite2 = Drawable.loadSprite(Medias.create(path, "foreground2.png"));
            sprite2.load();
            sprite2.prepare();
            foreground2 = new BackgroundElement(0, 0, sprite2);
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            foreground1.setOffsetY(y - 49);
            foreground2.setOffsetY(y - 59);

            foreground1.setOffsetX(UtilMath.wrapDouble(foreground1.getOffsetX() - 2.8,
                                                       0.0,
                                                       foreground1.getRenderable().getWidth()));
            foreground2.setOffsetX(UtilMath.wrapDouble(foreground2.getOffsetX() - 3.75,
                                                       0.0,
                                                       foreground2.getRenderable().getWidth()));
        }

        @Override
        public void render(Graphic g)
        {
            final Sprite sprite1 = (Sprite) foreground1.getRenderable();
            final int w1 = (int) Math.ceil(screenWidth / (double) sprite1.getWidth());
            final int y1 = (int) (screenHeight + foreground1.getOffsetY());

            if (y1 >= -sprite1.getHeight() && y1 < screenHeight)
            {
                for (int j = 0; j <= w1; j++)
                {
                    sprite1.setLocation(sprite1.getWidth() * j + foreground1.getOffsetX() - sprite1.getWidth(), y1);
                    sprite1.render(g);
                }
            }

            final Sprite sprite2 = (Sprite) foreground2.getRenderable();
            final int w2 = (int) Math.ceil(screenWidth / (double) sprite2.getWidth());
            final int y2 = (int) (screenHeight + foreground2.getOffsetY());

            if (y2 >= -sprite2.getHeight() && y2 < screenHeight)
            {
                for (int j = 0; j <= w2; j++)
                {
                    sprite2.setLocation(sprite2.getWidth() * j + foreground2.getOffsetX() - sprite2.getWidth(), y2);
                    sprite2.render(g);
                }
            }
        }
    }
}
