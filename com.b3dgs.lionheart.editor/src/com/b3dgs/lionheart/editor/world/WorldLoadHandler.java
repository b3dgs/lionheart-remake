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
package com.b3dgs.lionheart.editor.world;

import java.io.IOException;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.editor.utility.dialog.UtilDialog;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.view.WorldPart;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormulaConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionGroupConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionheart.EntityConfig;
import com.b3dgs.lionheart.StageConfig;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.editor.Util;

/**
 * Load world handler.
 */
public final class WorldLoadHandler
{
    /** Element ID. */
    public static final String ID = "menu.file.load";

    /**
     * Load world.
     * 
     * @param shell The shell reference.
     * @param media The media output.
     */
    private static void load(Shell shell, Media media)
    {
        final MapTilePersister mapPersister = WorldModel.INSTANCE.getMap().getFeature(MapTilePersister.class);

        final StageConfig stage = StageConfig.imports(new Configurer(media));
        WorldModel.INSTANCE.getMap()
                           .loadSheets(Medias.create(stage.getMapFile().getParentPath(), TileSheetsConfig.FILENAME));
        try (FileReading reading = new FileReading(stage.getMapFile()))
        {
            mapPersister.load(reading);
            mapPersister.getFeature(MapTileCollision.class)
                        .loadCollisions(Medias.create(Folder.LEVEL, CollisionFormulaConfig.FILENAME),
                                        Medias.create(Folder.LEVEL, CollisionGroupConfig.FILENAME));
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
            UtilDialog.error(shell, Messages.ErrorLoadTitle, Messages.ErrorLoadMessage);
        }

        stage.getEntities().forEach(config -> createEntity(stage, config));

        final WorldPart worldPart = WorldModel.INSTANCE.getServices().get(WorldPart.class);
        worldPart.update();
    }

    /**
     * Create entity from configuration.
     * 
     * @param stage The stage configuration.
     * @param config The entity configuration.
     */
    private static void createEntity(StageConfig stage, EntityConfig config)
    {
        final MapTile map = WorldModel.INSTANCE.getMap();
        final Spawner spawner = WorldModel.INSTANCE.getServices().get(Spawner.class);
        com.b3dgs.lionheart.Util.loadEntityFeature(spawner.spawn(config.getMedia(),
                                                                 config.getSpawnX(map),
                                                                 config.getSpawnY(map)),
                                                   config);
    }

    /**
     * Create handler.
     */
    public WorldLoadHandler()
    {
        super();
    }

    /**
     * Execute the handler.
     * 
     * @param shell The shell reference.
     */
    @Execute
    public void execute(Shell shell)
    {
        UtilDialog.selectResourceFile(shell, true, Util.getLevelFilter()).ifPresent(media -> load(shell, media));
    }
}
