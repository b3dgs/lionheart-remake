/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.game.background.BackgroundAbstract;
import com.b3dgs.lionengine.game.background.BackgroundComponent;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

/**
 * No background implementation.
 */
final class Black extends BackgroundAbstract
{
    private int width;
    private int height;

    /**
     * Constructor.
     * 
     * @param source The source reference.
     */
    Black(SourceResolutionProvider source)
    {
        super(null, 0, 0);

        width = source.getWidth();
        height = source.getHeight();

        add(new BackgroundComponent()
        {
            @Override
            public void update(double extrp, int x, int y, double speed)
            {
                // Nothing to do
            }

            @Override
            public void render(Graphic g)
            {
                g.clear(0, 0, width, height);
            }
        });
    }

    @Override
    public void setScreenSize(int width, int height)
    {
        super.setScreenSize(width, height);

        this.width = width;
        this.height = height;
    }
}
