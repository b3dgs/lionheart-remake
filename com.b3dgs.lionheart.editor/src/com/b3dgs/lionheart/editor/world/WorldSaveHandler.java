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
package com.b3dgs.lionheart.editor.world;

import java.io.IOException;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.editor.utility.dialog.UtilDialog;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.view.WorldPart;
import com.b3dgs.lionengine.game.feature.HandlerPersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.io.FileWriting;
import com.b3dgs.lionheart.editor.Util;

/**
 * Save world handler.
 */
public final class WorldSaveHandler
{
    /** Element ID. */
    public static final String ID = "menu.file.save";

    /**
     * Save world.
     * 
     * @param shell The shell reference.
     * @param media The media output.
     */
    private static void save(Shell shell, Media media)
    {
        final MapTilePersister mapPersister = WorldModel.INSTANCE.getMap().getFeature(MapTilePersister.class);
        final HandlerPersister handlerPersister = WorldModel.INSTANCE.getHandlerPersister();

        try (final FileWriting writing = new FileWriting(media))
        {
            mapPersister.save(writing);
            handlerPersister.save(writing);
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
            UtilDialog.error(shell, Messages.ErrorSaveTitle, Messages.ErrorSaveMessage);
        }

        final WorldPart worldPart = WorldModel.INSTANCE.getServices().get(WorldPart.class);
        worldPart.update();
    }

    /**
     * Create handler.
     */
    public WorldSaveHandler()
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
        UtilDialog.selectResourceFile(shell, false, Util.getLevelFilter()).ifPresent(media -> save(shell, media));
    }
}
