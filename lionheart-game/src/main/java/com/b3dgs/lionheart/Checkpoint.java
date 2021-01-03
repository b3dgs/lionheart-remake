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
package com.b3dgs.lionheart;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.geom.Coord;

/**
 * Handle checkpoints.
 */
public class Checkpoint implements Updatable, Listenable<CheckpointListener>
{
    private static final int END_DISTANCE = 32;
    private static final int BOSS_DISTANCE = 32;

    private final List<Coord> respawns = new ArrayList<>();
    private final ListenableModel<CheckpointListener> listenable = new ListenableModel<>();
    private final MapTile map;
    private Updatable checkerEnd;
    private Updatable checkerBoss;
    private Transformable player;
    private int last;
    private int count;
    private Optional<Coord> boss;
    private boolean bossFound;

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
     * @param player The player reference.
     */
    public void load(StageConfig config, Featurable player)
    {
        this.player = player.getFeature(Transformable.class);
        last = 0;
        respawns.add(config.getStart());
        respawns.addAll(config.getRespawns());

        checkerEnd = config.getEnd()
                           .map(e -> UpdatableVoid.wrap(extrp -> updateEnd(toReal(e))))
                           .orElse(UpdatableVoid.getInstance());

        checkerBoss = config.getBoss()
                            .map(b -> UpdatableVoid.wrap(extrp -> updateBoss(toReal(b))))
                            .orElse(UpdatableVoid.getInstance());
        boss = config.getBoss().map(this::toReal);

        count = respawns.size();
        bossFound = false;
    }

    /**
     * Get the current checkpoint.
     * 
     * @param transformable The transformable reference.
     * @return The current checkpoint.
     */
    public Coord getCurrent(Transformable transformable)
    {
        if (bossFound)
        {
            return boss.get();
        }
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
     * Check end reached.
     * 
     * @param end The end location.
     */
    private void updateEnd(Coord end)
    {
        if (UtilMath.getDistance(player, end) < END_DISTANCE)
        {
            final int n = listenable.size();
            for (int i = 0; i < n; i++)
            {
                listenable.get(i).notifyReachedEnd();
            }
            checkerEnd = UpdatableVoid.getInstance();
        }
    }

    /**
     * Check boss reached.
     * 
     * @param boss The boss location.
     */
    private void updateBoss(Coord boss)
    {
        if (UtilMath.getDistance(player, boss) < BOSS_DISTANCE)
        {
            final int n = listenable.size();
            for (int i = 0; i < n; i++)
            {
                listenable.get(i).notifyReachedBoss();
            }
            checkerBoss = UpdatableVoid.getInstance();
            bossFound = true;
        }
    }

    /**
     * To real coord.
     * 
     * @param coord The tile coord.
     * @return The real coord.
     */
    private Coord toReal(Coord coord)
    {
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();

        return new Coord(coord.getX() * tw, coord.getY() * th);
    }

    @Override
    public void addListener(CheckpointListener listener)
    {
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(CheckpointListener listener)
    {
        listenable.removeListener(listener);
    }

    @Override
    public void update(double extrp)
    {
        checkerEnd.update(extrp);
        checkerBoss.update(extrp);
    }
}
