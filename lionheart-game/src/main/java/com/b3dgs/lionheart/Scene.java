/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart;

import java.io.IOException;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.game.feature.SequenceGame;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * Game scene implementation.
 */
final class Scene extends SequenceGame
{
    private static final String ERROR_SAVING_MAP = "Error on saving map !";
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

    /**
     * Import the level and save it.
     * 
     * @param level The level to import.
     */
    private static void importLevelAndSave(Level level)
    {
        final Services services = new Services();
        final MapTile map = services.create(MapTileGame.class);
        map.create(level.getRip());
        final MapTilePersister mapPersister = map.addFeatureAndGet(new MapTilePersisterOptimized(services));
        try (FileWriting output = new FileWriting(level.getFile()))
        {
            mapPersister.save(output);
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception, ERROR_SAVING_MAP);
        }
    }

    private final Text textName = Graphics.createText(9);
    private final Text textEngine = Graphics.createText(9);
    /** Current level. */
    private final Level level = Level.SWAMP_1_1;

    /**
     * Create the scene.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, Constant.NATIVE_RESOLUTION, services -> new World(services));
    }

    @Override
    public void load()
    {
        if (!level.getFile().exists())
        {
            importLevelAndSave(level);
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
