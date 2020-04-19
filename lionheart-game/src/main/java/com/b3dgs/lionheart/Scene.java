/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.feature.SequenceGame;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.helper.MapTileHelper;

/**
 * Game scene implementation.
 */
final class Scene extends SequenceGame
{
    private static final String NAME = Constant.PROGRAM_NAME
                                       + com.b3dgs.lionengine.Constant.SPACE
                                       + Constant.PROGRAM_VERSION;
    private static final String ENGINE = com.b3dgs.lionengine.Constant.ENGINE_NAME
                                         + com.b3dgs.lionengine.Constant.SPACE
                                         + com.b3dgs.lionengine.Constant.ENGINE_VERSION;

    /**
     * Set text data.
     * 
     * @param text The text object.
     * @param value The text value.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param align The align used.
     */
    private static void setText(Text text, String value, int x, int y, Align align)
    {
        text.setLocation(x, y);
        text.setAlign(align);
        text.setText(value);
        text.setColor(ColorRgba.GRAY_LIGHT);
    }

    private final Text textName = Graphics.createText(9);
    private final Text textEngine = Graphics.createText(9);
    private final Level level = Level.SWAMP_DEMO;

    /**
     * Create the scene.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    Scene(Context context)
    {
        super(context, Constant.NATIVE_RESOLUTION, World::new);
    }

    @Override
    public void load()
    {
        if (!level.getFile().exists())
        {
            MapTileHelper.importAndSave(level.getRip(), level.getFile());
        }
        world.loadFromFile(level.getFile());

        setText(textEngine, ENGINE, 0, getHeight() - textEngine.getSize(), Align.LEFT);
        setText(textName, NAME, getWidth(), getHeight() - textName.getSize(), Align.RIGHT);
    }

    @Override
    public void render(Graphic g)
    {
        super.render(g);

        textEngine.render(g);
        textName.render(g);
    }
}
