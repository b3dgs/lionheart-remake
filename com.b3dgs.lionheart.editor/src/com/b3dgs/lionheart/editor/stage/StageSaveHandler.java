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

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.editor.utility.dialog.UtilDialog;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.view.WorldPart;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionheart.EntityConfig;
import com.b3dgs.lionheart.StageConfig;
import com.b3dgs.lionheart.editor.Util;
import com.b3dgs.lionheart.editor.checkpoint.Checkpoints;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Save world handler.
 */
public final class StageSaveHandler
{
    /** Element ID. */
    public static final String ID = "menu.file.save";

    /**
     * Save world.
     * 
     * @param media The media output.
     */
    private static void save(Media media)
    {
        final Handler handler = WorldModel.INSTANCE.getHandler();
        final Xml root = new Xml(StageConfig.NODE_STAGE);

        UtilPart.getPart(StagePart.ID, StagePart.class).save(root);

        final Xml checkpoints = root.createChild(StageConfig.NODE_CHECKPOINTS);
        WorldModel.INSTANCE.getServices().get(Checkpoints.class).forEach(c ->
        {
            final Xml checkpoint = checkpoints.createChild(StageConfig.NODE_CHECKPOINT);
            checkpoint.writeDouble(StageConfig.ATT_CHECKPOINT_TX, c.getTx());
            checkpoint.writeDouble(StageConfig.ATT_CHECKPOINT_TY, c.getTy());
            c.getNext().ifPresent(n -> checkpoint.writeString(StageConfig.ATT_CHECKPOINT_NEXT, n));
            c.getSpawn().ifPresent(s ->
            {
                checkpoint.writeDouble(StageConfig.ATT_SPAWN_TX, s.getX());
                checkpoint.writeDouble(StageConfig.ATT_SPAWN_TY, s.getY());
            });
        });

        final Xml entities = root.createChild(StageConfig.NODE_ENTITIES);
        handler.values().forEach(featurable -> entities.add(saveFeaturable(featurable)));

        root.save(media);

        final WorldPart worldPart = WorldModel.INSTANCE.getServices().get(WorldPart.class);
        worldPart.update();
    }

    /**
     * Save featurable.
     * 
     * @param featurable The featurable to save.
     * @return The xml node.
     */
    private static Xml saveFeaturable(Featurable featurable)
    {
        final Xml root = new Xml(EntityConfig.NODE_ENTITY);
        featurable.getFeatures().forEach(feature ->
        {
            if (feature instanceof final XmlSaver saver)
            {
                saver.save(root);
            }
        });
        return root;
    }

    /**
     * Create handler.
     */
    public StageSaveHandler()
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
        UtilDialog.selectResourceFile(shell, false, Util.getLevelFilter()).ifPresent(StageSaveHandler::save);
    }
}
