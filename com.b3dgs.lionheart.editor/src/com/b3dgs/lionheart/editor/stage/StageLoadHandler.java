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
package com.b3dgs.lionheart.editor.stage;

import java.io.IOException;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.editor.utility.UtilPart;
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
import com.b3dgs.lionheart.editor.checkpoint.CheckpointPart;

/**
 * Load world handler.
 */
public final class StageLoadHandler
{
    /** Element ID. */
    public static final String ID = "menu.file.load";
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StageLoadHandler.class);

    /**
     * Load world.
     * 
     * @param shell The shell reference.
     * @param media The media output.
     */
    private static void load(Shell shell, Media media)
    {
        WorldModel.INSTANCE.getCamera().setLocation(0, 0);

        final MapTile map = WorldModel.INSTANCE.getMap();
        map.clear();

        final MapTilePersister mapPersister = map.getFeature(MapTilePersister.class);

        final StageConfig stage = StageConfig.imports(new Configurer(media));
        UtilPart.getPart(CheckpointPart.ID, CheckpointPart.class).load(stage);
        UtilPart.getPart(StagePart.ID, StagePart.class).load(stage);

        map.loadSheets(Medias.create(Folder.LEVEL,
                                     stage.getBackground().getWorld().getFolder(),
                                     TileSheetsConfig.FILENAME));
        try (FileReading reading = new FileReading(stage.getMapFile()))
        {
            mapPersister.load(reading);
            mapPersister.getFeature(MapTileCollision.class)
                        .loadCollisions(Medias.create(Folder.LEVEL, CollisionFormulaConfig.FILENAME),
                                        Medias.create(Folder.LEVEL, CollisionGroupConfig.FILENAME));
        }
        catch (final IOException exception)
        {
            LOGGER.error("load error", exception);
            UtilDialog.error(shell, Messages.ErrorLoadTitle, Messages.ErrorLoadMessage);
        }

        WorldModel.INSTANCE.getHandler().removeAll();
        stage.getEntities().forEach(StageLoadHandler::createEntity);

        final WorldPart worldPart = WorldModel.INSTANCE.getServices().get(WorldPart.class);
        worldPart.update();
    }

    /**
     * Create entity from configuration.
     * 
     * @param config The entity configuration.
     */
    private static void createEntity(EntityConfig config)
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
    public StageLoadHandler()
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
