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
package com.b3dgs.lionheart.editor.world.updater;

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.editor.utility.UtilWorld;
import com.b3dgs.lionengine.editor.world.PaletteModel;
import com.b3dgs.lionengine.editor.world.updater.WorldMouseClickListener;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.geom.Point;
import com.b3dgs.lionheart.Checkpoint;
import com.b3dgs.lionheart.editor.checkpoint.CheckpointPart;
import com.b3dgs.lionheart.editor.checkpoint.Checkpoints;
import com.b3dgs.lionheart.editor.world.PaletteType;

/**
 * Handle the checkpoint updating.
 */
public class CheckpointUpdater implements WorldMouseClickListener
{
    private final MapTile map;
    private final Camera camera;
    private final PaletteModel palette;
    private final Checkpoints checkpoints;

    /**
     * Create checkpoints renderer.
     * 
     * @param services The services reference.
     */
    public CheckpointUpdater(Services services)
    {
        super();

        map = services.get(MapTile.class);
        camera = services.get(Camera.class);
        palette = services.get(PaletteModel.class);
        checkpoints = services.get(Checkpoints.class);
    }

    @Override
    public void onMousePressed(int click, int mx, int my)
    {
        if (palette.isPalette(PaletteType.POINTER_CHECKPOINT))
        {
            final Point point = UtilWorld.getPoint(camera, mx, my);
            final int x = UtilMath.getRounded(point.getX(), map.isCreated() ? map.getTileWidth() : 1)
                          / map.getTileWidth();
            final int y = UtilMath.getRounded(point.getY(), map.isCreated() ? map.getTileHeight() : 1)
                          / map.getTileHeight();

            int i = 0;
            for (final Checkpoint checkpoint : checkpoints)
            {
                if (Double.compare(Math.round(checkpoint.getTx()), x) == 0
                    && Double.compare(Math.round(checkpoint.getTy()), y) == 0)
                {
                    checkpoints.select(i);
                    UtilPart.getPart(CheckpointPart.ID, CheckpointPart.class).select(i);
                    break;
                }
                i++;
            }
        }
    }

    @Override
    public void onMouseReleased(int click, int mx, int my)
    {
        if (palette.isPalette(PaletteType.POINTER_CHECKPOINT))
        {
            checkpoints.getSelection().ifPresent(i ->
            {
                final Point point = UtilWorld.getPoint(camera, mx, my);
                final int x = UtilMath.getRounded(point.getX(), map.isCreated() ? map.getTileWidth() : 1)
                              / map.getTileWidth();
                final int y = UtilMath.getRounded(point.getY(), map.isCreated() ? map.getTileHeight() : 1)
                              / map.getTileHeight();

                final Checkpoint checkpoint = checkpoints.get(i);
                checkpoints.set(i, new Checkpoint(x, y, checkpoint.getNext(), checkpoint.getSpawn()));

                UtilPart.getPart(CheckpointPart.ID, CheckpointPart.class).updateIndexes();
            });
        }
    }
}
