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

import java.util.Optional;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.geom.Coord;

/**
 * Checkpoint data.
 */
public final class Checkpoint
{
    /** Horizontal tile. */
    private final double tx;
    /** Vertical tile. */
    private final double ty;
    /** Next stage. */
    private final Optional<String> next;
    /** Next spawn. */
    private final Optional<Coord> spawn;

    /**
     * Create checkpoint.
     * 
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     * @param next The next stage.
     * @param spawn The spawn tile.
     */
    public Checkpoint(double tx, double ty, Optional<String> next, Optional<Coord> spawn)
    {
        super();

        this.tx = tx;
        this.ty = ty;
        this.next = next;
        this.spawn = spawn;
    }

    /**
     * Get the horizontal tile.
     * 
     * @return The horizontal tile.
     */
    public double getTx()
    {
        return tx;
    }

    /**
     * Get the vertical tile.
     * 
     * @return The vertical tile.
     */
    public double getTy()
    {
        return ty;
    }

    /**
     * Get the next stage.
     * 
     * @return The next stage.
     */
    public Optional<String> getNext()
    {
        return next;
    }

    /**
     * Get the next spawn.
     * 
     * @return The next spawn.
     */
    public Optional<Coord> getSpawn()
    {
        return spawn;
    }

    private static void add(StringBuilder builder, String name, double value)
    {
        builder.append(name).append(Constant.DOUBLE_DOT).append(value).append(Constant.SPACE);
    }

    private static void add(StringBuilder builder, String name, Optional<String> value)
    {
        value.ifPresent(v -> builder.append(name).append(Constant.DOUBLE_DOT).append(v).append(Constant.SPACE));
    }

    private static void addCoord(StringBuilder builder, Optional<Coord> value)
    {
        value.ifPresent(v -> builder.append(StageConfig.ATT_SPAWN_TX)
                                    .append(Constant.DOUBLE_DOT)
                                    .append(v.getX())
                                    .append(StageConfig.ATT_SPAWN_TY)
                                    .append(Constant.DOUBLE_DOT)
                                    .append(v.getY())
                                    .append(Constant.SPACE));
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Checkpoint [ ");
        add(builder, StageConfig.ATT_CHECKPOINT_TX, tx);
        add(builder, StageConfig.ATT_CHECKPOINT_TY, ty);
        add(builder, StageConfig.ATT_CHECKPOINT_NEXT, next);
        addCoord(builder, spawn);
        builder.append("]");
        return builder.toString();
    }
}
