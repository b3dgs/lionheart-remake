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
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Norka full background implementation.
 */
final class Norka extends BackgroundAbstract
{
    private final Backdrop backdrop;

    /**
     * Constructor.
     * 
     * @param source The resolution source reference.
     * @param scaleH The horizontal factor.
     * @param scaleV The horizontal factor.
     * @param theme The theme name.
     * @param flickering The flickering flag.
     */
    Norka(SourceResolutionProvider source, double scaleH, double scaleV, String theme, boolean flickering)
    {
        super(theme, 0, 0);

        final String path = UtilFolder.getPath(Folder.BACKGROUND, "norka", theme);
        final int width = source.getWidth();
        backdrop = new Backdrop(path, flickering, width, source);
        add(backdrop);
        totalHeight = 0;
        setScreenSize(source.getWidth(), source.getHeight());
    }

    @Override
    public void setScreenSize(int width, int height)
    {
        setOffsetY(height - Constant.RESOLUTION_GAME.getHeight());
    }

    /**
     * Backdrop represents the back background plus top background elements.
     */
    private final class Backdrop implements BackgroundComponent
    {
        private final int[][] data =
        {
            {
                0, 144
            },
            {
                64, 112
            },
            {
                128, 96
            },
            {
                160, 96
            },
            {
                256, 96
            },
            {
                304, 112
            },
            {
                368, 144
            },
        };
        private final SpriteAnimated[] back = new SpriteAnimated[7];
        private final SourceResolutionProvider source;

        private double frame;

        /**
         * Constructor.
         * 
         * @param path The backdrop path.
         * @param flickering The flickering flag effect.
         * @param screenWidth The screen width.
         * @param source The resolution source reference.
         */
        Backdrop(String path, boolean flickering, int screenWidth, SourceResolutionProvider source)
        {
            super();

            this.source = source;
            for (int i = 0; i < back.length; i++)
            {
                back[i] = Drawable.loadSpriteAnimated(Medias.create(path, "back" + (i + 1) + ".png"), 4, 1);
                back[i].load();
                back[i].prepare();
                back[i].setFrame(4);
            }
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            frame = UtilMath.wrapAngleDouble(frame + 5.0);
            final int id = 1 + (int) Math.round(UtilMath.sin(frame * 0.5) * 2.75);

            for (int i = 0; i < back.length; i++)
            {
                back[i].setFrame(id);
                back[i].setLocation(data[i][0] - x, y - data[i][1]);
            }
        }

        @Override
        public void render(Graphic g)
        {
            g.clear(0, 0, source.getWidth(), source.getHeight());
            for (final SpriteAnimated element : back)
            {
                element.render(g);
            }
        }
    }
}
