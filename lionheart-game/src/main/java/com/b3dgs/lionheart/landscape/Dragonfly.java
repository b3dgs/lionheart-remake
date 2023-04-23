/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.WorldType;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Dragonfly full background implementation.
 */
final class Dragonfly extends BackgroundAbstract
{
    private static final int HEIGHT_MAX = 400;
    private static final int HEIGHT_TOTAL = 160;

    private static final int OFFSET_Y = 72;

    private final Gradient.Backdrop backdrop;

    /**
     * Constructor.
     * 
     * @param source The resolution source reference.
     * @param scaleH The horizontal factor.
     * @param scaleV The horizontal factor.
     * @param theme The theme name.
     * @param flickering The flickering flag.
     */
    Dragonfly(SourceResolutionProvider source, double scaleH, double scaleV, String theme, boolean flickering)
    {
        super(theme, 0, HEIGHT_MAX);

        final String path = UtilFolder.getPath(Folder.BACKGROUND, WorldType.DRAGONFLY.getFolder(), theme);
        final int width = source.getWidth();
        backdrop = new Gradient.Backdrop(path, flickering, width);
        add(backdrop);
        totalHeight = HEIGHT_TOTAL;
        setScreenSize(source.getWidth(), source.getHeight());
    }

    @Override
    public void setScreenSize(int width, int height)
    {
        setOffsetY(height - Constant.RESOLUTION_GAME.getHeight() + OFFSET_Y);
        backdrop.setScreenWidth(width);
    }
}
