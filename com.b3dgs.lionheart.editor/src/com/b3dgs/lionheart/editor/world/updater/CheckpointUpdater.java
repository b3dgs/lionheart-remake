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
package com.b3dgs.lionheart.editor.world.updater;

import java.util.List;

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.editor.utility.UtilWorld;
import com.b3dgs.lionengine.editor.world.updater.WorldMouseClickListener;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.geom.Point;
import com.b3dgs.lionheart.Checkpoint;
import com.b3dgs.lionheart.editor.checkpoint.CheckpointPart;

/**
 * Handle the checkpoint updating.
 */
public class CheckpointUpdater implements WorldMouseClickListener
{
    private final MapTile map;
    private final Camera camera;

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
    }

    @Override
    public void onMousePressed(int click, int mx, int my)
    {
        final CheckpointPart part = UtilPart.getPart(CheckpointPart.ID, CheckpointPart.class);
        final Point point = UtilWorld.getPoint(camera, mx, my);
        final int x = UtilMath.getRounded(point.getX(), map.isCreated() ? map.getTileWidth() : 1) / map.getTileWidth();
        final int y = UtilMath.getRounded(point.getY(), map.isCreated() ? map.getTileHeight() : 1)
                      / map.getTileHeight();
        final List<Checkpoint> list = part.get();

        for (int i = 0; i < list.size(); i++)
        {
            final Checkpoint checkpoint = list.get(i);
            if (Double.compare(Math.round(checkpoint.getTx()), x) == 0
                && Double.compare(Math.round(checkpoint.getTy()), y) == 0)
            {
                part.select(i);
                break;
            }
        }
    }

    @Override
    public void onMouseReleased(int click, int mx, int my)
    {
        final CheckpointPart part = UtilPart.getPart(CheckpointPart.ID, CheckpointPart.class);
        part.getSelection().ifPresent(i ->
        {
            final List<Checkpoint> list = part.get();
            final Point point = UtilWorld.getPoint(camera, mx, my);
            final int x = UtilMath.getRounded(point.getX(), map.isCreated() ? map.getTileWidth() : 1)
                          / map.getTileWidth();
            final int y = UtilMath.getRounded(point.getY(), map.isCreated() ? map.getTileHeight() : 1)
                          / map.getTileHeight();

            part.set(i, new Checkpoint(x, y, list.get(i).getNext(), list.get(i).getSpawn()));
        });
    }
}
