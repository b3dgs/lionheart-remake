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
import java.util.stream.Collectors;

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
import com.b3dgs.lionengine.geom.Point;

/**
 * Handle checkpoints.
 */
public class CheckpointHandler implements Updatable, Listenable<CheckpointListener>
{
    private static final int CHECKPOINT_DISTANCE_TILE = 4;
    private static final int END_DISTANCE_TILE = 2;
    private static final int BOSS_DISTANCE = 128;

    private final List<Checkpoint> checkpoints = new ArrayList<>();
    private final List<Checkpoint> nexts = new ArrayList<>();
    private final ListenableModel<CheckpointListener> listenable = new ListenableModel<>();
    private final MapTile map;
    private int nextsCount;
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
    public CheckpointHandler(Services services)
    {
        super();

        map = services.get(MapTile.class);
    }

    /**
     * Load checkpoints.
     * 
     * @param config The configuration reference.
     * @param player The player reference.
     * @param spawn The spawn tile.
     */
    public void load(StageConfig config, Featurable player, Optional<Point> spawn)
    {
        this.player = player.getFeature(Transformable.class);
        last = 0;
        checkpoints.clear();
        for (final Checkpoint checkpoint : config.getCheckpoints())
        {
            if (!spawn.isPresent()
                || !checkpoint.getNext().isPresent()
                || checkpoint.getTx() != spawn.get().getX()
                || checkpoint.getTy() != spawn.get().getY())
            {
                checkpoints.add(checkpoint);
            }
        }
        count = checkpoints.size();

        nexts.clear();
        nexts.addAll(checkpoints.stream().filter(c -> c.getNext().isPresent()).collect(Collectors.toList()));
        nextsCount = nexts.size();

        checkerBoss = config.getBoss()
                            .map(b -> UpdatableVoid.wrap(extrp -> updateBoss(toReal(b),
                                                                             toReal(config.getBossSpawn().get()))))
                            .orElse(UpdatableVoid.getInstance());
        boss = config.getBoss().map(this::toReal);

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
        return new Coord(checkpoints.get(last).getTx() * map.getTileWidth(),
                         checkpoints.get(last).getTy() * map.getTileHeight());
    }

    /**
     * Check next stage reached.
     */
    private void updateNext()
    {
        for (int i = 0; i < nextsCount; i++)
        {
            final Checkpoint checkpoint = nexts.get(i);
            if (UtilMath.getDistance(map.getInTileX(player),
                                     map.getInTileY(player),
                                     checkpoint.getTx(),
                                     checkpoint.getTy()) < END_DISTANCE_TILE)
            {
                final Optional<String> nextStage = checkpoint.getNext();
                if (nextStage.isPresent())
                {
                    final int n = listenable.size();
                    for (int j = 0; j < n; j++)
                    {
                        listenable.get(j).notifyNextStage(nextStage.get(), checkpoint.getSpawn());
                    }
                }
            }
        }
    }

    /**
     * Check boss reached.
     * 
     * @param boss The boss location.
     * @param spawn The boss spawn location.
     */
    private void updateBoss(Coord boss, Coord spawn)
    {
        if (UtilMath.getDistance(player, boss) < BOSS_DISTANCE)
        {
            final int n = listenable.size();
            for (int i = 0; i < n; i++)
            {
                listenable.get(i).notifyReachedBoss(spawn.getX(), spawn.getY());
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
        updateNext();
        checkerBoss.update(extrp);

        final int start = last + 1;
        for (int i = start; i < count; i++)
        {
            final Checkpoint checkpoint = checkpoints.get(i);
            if (UtilMath.getDistance(map.getInTileX(player),
                                     map.getInTileY(player),
                                     checkpoint.getTx(),
                                     checkpoint.getTy()) < CHECKPOINT_DISTANCE_TILE
                && map.getInTileX(player) > checkpoint.getTx())
            {
                last = i;
            }
        }
    }
}
