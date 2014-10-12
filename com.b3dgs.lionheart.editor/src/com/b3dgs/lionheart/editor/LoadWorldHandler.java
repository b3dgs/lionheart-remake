/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.editor;

import java.io.File;
import java.io.IOException;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.core.swt.UtilityMedia;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.factory.FactoryView;
import com.b3dgs.lionengine.editor.palette.PalettePart;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.editor.world.WorldViewPart;
import com.b3dgs.lionengine.game.CoordTile;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionheart.Level;
import com.b3dgs.lionheart.WorldData;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.FactoryEntity;
import com.b3dgs.lionheart.entity.HandlerEntity;
import com.b3dgs.lionheart.map.Map;

/**
 * Load the world from an external file.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class LoadWorldHandler
{
    /** Import level verbose. */
    private static final String VERBOSE_LEVEL_LOADED = "Level loaded from: ";
    /** Load level error. */
    private static final String ERROR_LOAD_WORLD = "Error when loading level !";
    /** Error dialog title. */
    private static final String ERROR_DIALOG_TITLE = "Load level error";
    /** Error dialog message. */
    private static final String ERROR_DIALOG_MESSAGE = "An error occured when loading the level !";

    /**
     * Create the level for the editor data.
     * 
     * @param map The map reference.
     * @return The created level.
     */
    private static Level createLevel(Map map)
    {
        final CameraPlatform camera = new CameraPlatform(0, 0);
        final FactoryEntity factoryEntity = new FactoryEntity();
        final HandlerEntity entities = new HandlerEntity(camera, factoryEntity);
        final Level level = new Level(camera, map, factoryEntity, entities);

        final FactoryObjectGame<?> factory = level.factoryEntity;
        factory.setClassLoader(Project.getActive().getClassLoader());
        factory.setPrepareEnabled(false);
        WorldViewModel.INSTANCE.setFactory(factory);

        return level;
    }

    /**
     * Fill the world data from loaded level.
     * 
     * @param partService The part service reference.
     * @param level The level reference.
     * @param map The map reference.
     */
    private static void fillWorld(EPartService partService, Level level, Map map)
    {
        final PalettePart palette = UtilEclipse.getPart(partService, PalettePart.ID, PalettePart.class);
        final CheckpointsView checkpoints = palette.getPaletteView(CheckpointsView.ID, CheckpointsView.class);

        final FactoryView factoryView = new FactoryView(partService);
        factoryView.setFactory(WorldViewModel.INSTANCE.getFactory());
        palette.addPalette("Factory", factoryView);

        final WorldData worldData = level.worldData;
        checkpoints.setStart(Geom.createPoint(worldData.getStartX(), worldData.getStartY()));
        checkpoints.setEnd(Geom.createPoint(worldData.getEndX(), worldData.getEndY()));
        for (final CoordTile tile : worldData.getCheckpoints())
        {
            checkpoints.addCheckpoint(Geom.createPoint(tile.getX(), tile.getY()));
        }

        final WorldViewPart world = UtilEclipse.getPart(partService, WorldViewPart.ID, WorldViewPart.class);
        final com.b3dgs.lionengine.editor.world.HandlerEntity handler = world.getRenderer().getHandler();
        for (final Entity entity : level.handlerEntity.list())
        {
            handler.add(entity);
        }
        handler.update(1.0);

        world.update();
    }

    /**
     * Load the level from a file.
     * 
     * @param level The level to load.
     * @param media The level media file.
     */
    private static void loadLevel(Level level, Media media)
    {
        try (FileReading reading = Stream.createFileReading(media))
        {
            level.load(reading);
            level.handlerEntity.update(1.0);
        }
        catch (final IOException exception)
        {
            Verbose.exception(LoadWorldHandler.class, "loadLevel", exception, LoadWorldHandler.ERROR_LOAD_WORLD);
            UtilEclipse.showError(LoadWorldHandler.ERROR_DIALOG_TITLE, LoadWorldHandler.ERROR_DIALOG_MESSAGE);
        }
    }

    /**
     * Execute the handler.
     * 
     * @param partService The part service reference.
     * @param shell The shell parent.
     */
    @Execute
    public void execute(EPartService partService, Shell shell)
    {
        final File file = Tools.selectResourceFile(shell, true, new String[]
        {
            "Level file  (*." + Level.FILE_FORMAT + ")"
        }, new String[]
        {
            "*." + Level.FILE_FORMAT
        });
        if (file != null)
        {
            final Map map = (Map) WorldViewModel.INSTANCE.getMap();
            final Level level = LoadWorldHandler.createLevel(map);
            final Media media = UtilityMedia.get(file);
            LoadWorldHandler.loadLevel(level, media);
            LoadWorldHandler.fillWorld(partService, level, map);
            map.createCollisionDraw();

            Verbose.info(LoadWorldHandler.VERBOSE_LEVEL_LOADED, media.getPath());
        }
    }
}
