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

import java.io.IOException;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.palette.PalettePart;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.editor.world.WorldViewPart;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.game.purview.Handlable;
import com.b3dgs.lionengine.geom.Point;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionheart.Level;
import com.b3dgs.lionheart.WorldData;
import com.b3dgs.lionheart.WorldType;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.FactoryEntity;
import com.b3dgs.lionheart.entity.HandlerEntity;
import com.b3dgs.lionheart.landscape.LandscapeType;
import com.b3dgs.lionheart.map.Map;

/**
 * Save the world to an external file.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SaveWorldHandler
{
    /** Import level verbose. */
    private static final String VERBOSE_LEVEL_SAVED = "Level saved at: ";
    /** Save level error. */
    private static final String ERROR_SAVE_WORLD = "Error when saving level !";
    /** Error dialog title. */
    private static final String ERROR_DIALOG_TITLE = "Save level error";
    /** Error dialog message. */
    private static final String ERROR_DIALOG_MESSAGE = "An error occured when saving the level !";

    /**
     * Create the level from the editor data.
     * 
     * @param partService The part service reference.
     * @param map The map reference.
     * @return The created level.
     */
    private static Level createLevel(EPartService partService, Map map)
    {
        final WorldViewPart world = UtilEclipse.getPart(partService, WorldViewPart.ID, WorldViewPart.class);
        final com.b3dgs.lionengine.editor.world.HandlerEntity handler = world.getRenderer().getHandler();

        final CameraPlatform camera = new CameraPlatform(0, 0);
        final FactoryEntity factory = new FactoryEntity();

        final HandlerEntity entities = new HandlerEntity(camera, factory);
        for (final Handlable entity : handler.list())
        {
            entities.add((Entity) entity);
        }
        entities.update(1.0);

        return new Level(camera, map, factory, entities);
    }

    /**
     * Fill the level data.
     * 
     * @param partService The part service reference.
     * @param level The level reference.
     * @param map The map reference.
     */
    private static void fillLevel(EPartService partService, Level level, Map map)
    {
        final PalettePart palette = UtilEclipse.getPart(partService, PalettePart.ID, PalettePart.class);
        final CheckpointsView checkpoints = palette.getPaletteView(CheckpointsView.ID, CheckpointsView.class);

        final WorldData worldData = level.worldData;
        final Point start = checkpoints.getStart();
        final Point end = checkpoints.getEnd();

        worldData.setStarting(start.getX(), start.getY());
        worldData.setEnding(end.getX(), end.getY());
        for (final Point point : checkpoints.getCheckpoints())
        {
            worldData.addCheckpoint(point.getX(), point.getY());
        }

        level.setWorld(WorldType.SWAMP);
        level.setLandscape(LandscapeType.SWAMP_DAY);
    }

    /**
     * Save the level to a file.
     * 
     * @param level The level to save.
     * @param media The level media file.
     */
    private static void saveLevel(Level level, Media media)
    {
        try (FileWriting writing = Stream.createFileWriting(media))
        {
            level.save(writing);
        }
        catch (final IOException exception)
        {
            Verbose.exception(SaveWorldHandler.class, "saveLevel", exception, SaveWorldHandler.ERROR_SAVE_WORLD);
            UtilEclipse.showError(SaveWorldHandler.ERROR_DIALOG_TITLE, SaveWorldHandler.ERROR_DIALOG_MESSAGE);
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
        final String file = Tools.selectFile(shell, Project.getActive().getResourcesPath().getAbsolutePath(), false,
                "*." + Level.FILE_FORMAT);
        if (file != null)
        {
            final Map map = (Map) WorldViewModel.INSTANCE.getMap();
            final Level level = SaveWorldHandler.createLevel(partService, map);
            SaveWorldHandler.fillLevel(partService, level, map);

            final Media media = Core.MEDIA.create(file);
            SaveWorldHandler.saveLevel(level, media);

            Verbose.info(SaveWorldHandler.VERBOSE_LEVEL_SAVED, media.getPath());
        }
    }
}
