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

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.editor.utility.dialog.UtilDialog;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.view.WorldPart;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionheart.EntityConfig;
import com.b3dgs.lionheart.StageConfig;
import com.b3dgs.lionheart.editor.Util;
import com.b3dgs.lionheart.object.XmlSaver;

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
        final Handler handler = WorldModel.INSTANCE.getHandler();
        final MapTile map = WorldModel.INSTANCE.getMap();
        final Xml root = new Xml(StageConfig.NODE_ENTITIES);
        handler.values().forEach(featurable -> root.add(saveFeaturable(featurable, map)));

        root.save(media);

        final WorldPart worldPart = WorldModel.INSTANCE.getServices().get(WorldPart.class);
        worldPart.update();
    }

    /**
     * Save featurable.
     * 
     * @param featurable The featurable to save.
     * @param map The map reference.
     * @return The xml node.
     */
    private static Xml saveFeaturable(Featurable featurable, MapTile map)
    {
        final Xml root = new Xml(EntityConfig.NODE_ENTITY);
        root.writeString(EntityConfig.ATT_FILE, featurable.getMedia().getPath());

        final Transformable transformable = featurable.getFeature(Transformable.class);
        root.writeDouble(EntityConfig.ATT_RESPAWN_TX, transformable.getX() / map.getTileWidth());
        root.writeDouble(EntityConfig.ATT_RESPAWN_TY,
                         transformable.getY() / map.getTileHeight() + map.getInTileHeight(transformable) - 1);

        featurable.getFeatures().forEach(feature ->
        {
            if (feature instanceof XmlSaver)
            {
                ((XmlSaver) feature).save(root);
            }
        });

        return root;
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
