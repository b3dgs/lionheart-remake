/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.geom.Coord;

/**
 * Handle checkpoints.
 */
public class CheckpointHandler implements Updatable, Listenable<CheckpointListener>
{
    private static final int CHECKPOINT_DISTANCE_TILE = 4;
    private static final int END_DISTANCE_TILE = 2;
    private static final int BOSS_DISTANCE = 128;

    /**
     * Check if checkpoint is previous to spawn.
     * 
     * @param spawn The spawn reference.
     * @param checkpoint The checkpoint reference.
     * @return <code>true</code> if previous, <code>false</code> else.
     */
    private static boolean isPrevious(Optional<Coord> spawn, Checkpoint checkpoint)
    {
        if (spawn.isPresent())
        {
            return checkpoint.getTx() < spawn.get().getX();
        }
        return false;
    }

    /**
     * Check if coord is not next stage checkpoint.
     * 
     * @param coord The coord to check.
     * @param checkpoint The checkpoint to compare.
     * @return <code>true</code> if not next, <code>false</code> else.
     */
    private static boolean isNotNext(Optional<Coord> coord, Checkpoint checkpoint)
    {
        return !coord.isPresent()
               || !checkpoint.getNext().isPresent()
               || Double.compare(checkpoint.getTx(), coord.get().getX()) != 0
               || Double.compare(checkpoint.getTy(), coord.get().getY()) != 0;
    }

    private final List<Checkpoint> checkpoints = new ArrayList<>();
    private final List<Checkpoint> nexts = new ArrayList<>();
    private final ListenableModel<CheckpointListener> listenable = new ListenableModel<>();
    private final List<Transformable> player = new ArrayList<>();
    private final List<Transformable> toAdd = new ArrayList<>();
    private final List<Transformable> toRemove = new ArrayList<>();
    private final Map<Transformable, Integer> last = new HashMap<>();

    private final MapTile map;
    private final CheatsProvider cheats;

    private int nextsCount;
    private Updatable checkerBoss;
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
        cheats = services.get(CheatsProvider.class);
    }

    /**
     * Register player for checkpoint tracking.
     * 
     * @param transformable The player to register.
     */
    public void register(Transformable transformable)
    {
        last.put(transformable, Integer.valueOf(0));
        toAdd.add(transformable);
    }

    /**
     * Unregister player for checkpoint tracking.
     * 
     * @param transformable The player to unregister.
     */
    public void unregister(Transformable transformable)
    {
        last.remove(transformable);
        toRemove.add(transformable);
    }

    /**
     * Load checkpoints.
     * 
     * @param config The configuration reference.
     * @param spawn The spawn tile.
     */
    public void load(StageConfig config, Optional<Coord> spawn)
    {
        checkpoints.clear();

        spawn.ifPresent(s -> checkpoints.add(new Checkpoint(s.getX(), s.getY(), Optional.empty(), Optional.empty())));

        final List<Checkpoint> list = config.getCheckpoints();
        final int n = list.size();
        for (int i = 0; i < n; i++)
        {
            final Checkpoint checkpoint = list.get(i);
            if (!isPrevious(spawn, checkpoint) && isNotNext(spawn, checkpoint))
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
        return new Coord(checkpoints.get(last.get(transformable).intValue()).getTx() * map.getTileWidth(),
                         checkpoints.get(last.get(transformable).intValue()).getTy() * map.getTileHeight());
    }

    /**
     * Update add remove register.
     */
    private void updateAddRemove()
    {
        if (!toRemove.isEmpty())
        {
            player.removeAll(toRemove);
            toRemove.clear();
        }
        if (!toAdd.isEmpty())
        {
            player.addAll(toAdd);
            toAdd.clear();
        }
    }

    /**
     * Check next stage reached.
     * 
     * @param transformable The transformable reference.
     */
    private void updateNext(Transformable transformable)
    {
        for (int i = 0; i < nextsCount; i++)
        {
            final Checkpoint checkpoint = nexts.get(i);
            if (!cheats.isFly()
                && UtilMath.getDistance(map.getInTileX(transformable),
                                        map.getInTileY(transformable),
                                        checkpoint.getTx(),
                                        checkpoint.getTy()) < END_DISTANCE_TILE)
            {
                final Optional<String> nextStage = checkpoint.getNext();
                if (nextStage.isPresent())
                {
                    final int n = listenable.size();
                    for (int j = 0; j < n; j++)
                    {
                        listenable.get(j).notifyReachStage(nextStage.get(), checkpoint.getSpawn());
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
        final int n = player.size();
        for (int i = 0; i < n; i++)
        {
            final Transformable transformable = player.get(i);
            if (!cheats.isFly() && UtilMath.getDistance(transformable, boss) < BOSS_DISTANCE)
            {
                final int k = listenable.size();
                for (int j = 0; j < k; j++)
                {
                    listenable.get(j).notifyReachBoss(spawn.getX(), spawn.getY());
                }
                checkerBoss = UpdatableVoid.getInstance();
                bossFound = true;
            }
        }
    }

    private void update(Transformable transformable)
    {
        final int start = player.size() == 1 ? last.get(transformable).intValue() + 1 : 0;
        for (int i = start; i < count; i++)
        {
            final Checkpoint checkpoint = checkpoints.get(i);
            if (UtilMath.getDistance(map.getInTileX(transformable),
                                     map.getInTileY(transformable),
                                     checkpoint.getTx(),
                                     checkpoint.getTy()) < CHECKPOINT_DISTANCE_TILE
                && map.getInTileX(transformable) > checkpoint.getTx())
            {
                last.put(transformable, Integer.valueOf(i));

                final int n = listenable.size();
                for (int j = 0; j < n; j++)
                {
                    listenable.get(j).notifyReachCheckpoint(transformable, checkpoint, i);
                }
            }
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
        updateAddRemove();

        if (checkerBoss != null)
        {
            checkerBoss.update(extrp);
        }
        final int n = player.size();
        for (int i = 0; i < n; i++)
        {
            final Transformable transformable = player.get(i);
            updateNext(transformable);
            update(transformable);
        }
    }
}
