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
package com.b3dgs.lionheart.editor.world.renderer;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.editor.world.renderer.WorldRenderListener;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionheart.Checkpoint;
import com.b3dgs.lionheart.editor.checkpoint.Checkpoints;

/**
 * Handle the checkpoint rendering.
 */
public class CheckpointRenderer implements WorldRenderListener
{
    private static final ColorRgba COLOR_CHECKPOINT = ColorRgba.YELLOW;

    private final MapTile map;
    private final Camera camera;
    private final Checkpoints checkpoints;

    /**
     * Create checkpoints renderer.
     * 
     * @param services The services reference.
     */
    public CheckpointRenderer(Services services)
    {
        super();

        map = services.get(MapTile.class);
        camera = services.get(Camera.class);
        checkpoints = services.get(Checkpoints.class);
    }

    @Override
    public void onRender(Graphic g, int width, int height, double scale, int ctw, int cth)
    {
        final ColorRgba old = g.getColor();
        g.setColor(COLOR_CHECKPOINT);

        for (final Checkpoint checkpoint : checkpoints)
        {
            g.drawRect(camera,
                       Origin.BOTTOM_LEFT,
                       checkpoint.getTx() * map.getTileWidth(),
                       checkpoint.getTy() * map.getTileHeight(),
                       map.getTileWidth(),
                       map.getTileHeight(),
                       true);
        }
        g.setColor(old);
    }
}
