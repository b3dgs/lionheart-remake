/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.game.feature.SequenceGame;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * Game scene implementation.
 */
final class Scene extends SequenceGame
{
    private static final String ERROR_SAVING_MAP = "Error on saving map !";
    
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
        final MapTilePersister mapPersister = map.addFeatureAndGet(new MapTilePersisterModel(services));
        try (FileWriting output = new FileWriting(level.getFile()))
        {
            mapPersister.save(output);
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception, ERROR_SAVING_MAP);
        }
    }

    /** Current level. */
    private final Level level = Level.SWAMP_1_1;

    /**
     * Create the scene.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, Constant.NATIVE, services -> new World(services));
    }

    @Override
    public void load()
    {
        if (!level.getFile().exists())
        {
            importLevelAndSave(level);
        }
        world.loadFromFile(level.getFile());
    }
}
