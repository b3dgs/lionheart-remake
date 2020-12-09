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
package com.b3dgs.lionheart;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.geom.Coord;

/**
 * Handle checkpoints.
 */
public class Checkpoint
{
    private static final int END_DISTANCE = 32;

    private final List<Coord> respawns = new ArrayList<>();
    private final MapTile map;
    private Localizable end;
    private int last;
    private int count;

    /**
     * Create handler.
     * 
     * @param services The services reference.
     */
    public Checkpoint(Services services)
    {
        super();

        map = services.get(MapTile.class);
    }

    /**
     * Load checkpoints.
     * 
     * @param config The configuration reference.
     */
    public void load(StageConfig config)
    {
        last = 0;
        respawns.add(config.getStart());
        respawns.addAll(config.getRespawns());
        end = new Coord(config.getEnd().getX() * map.getTileWidth(), config.getEnd().getY() * map.getTileHeight());
        count = respawns.size();
    }

    /**
     * Get the current checkpoint.
     * 
     * @param transformable The transformable reference.
     * @return The current checkpoint.
     */
    public Coord getCurrent(Transformable transformable)
    {
        final int start = last + 1;
        for (int i = start; i < count; i++)
        {
            final Coord current = respawns.get(i);
            if (transformable.getX() > current.getX() * map.getTileWidth())
            {
                last = i;
            }
            else
            {
                break;
            }
        }
        return new Coord(respawns.get(last).getX() * map.getTileWidth(),
                         respawns.get(last).getY() * map.getTileHeight());
    }

    /**
     * Check if is on end point.
     * 
     * @param transformable The transformable reference.
     * @return <code>true</code> if on end, <code>false</code> else.
     */
    public boolean isOnEnd(Transformable transformable)
    {
        return UtilMath.getDistance(transformable, end) < END_DISTANCE;
    }
}
