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

import java.util.Optional;

import com.b3dgs.lionengine.geom.Coord;

/**
 * Listen to checkpoint events.
 */
public interface CheckpointListener
{
    /**
     * Called on next stage reached.
     * 
     * @param next The next stage.
     * @param spawn The next spawn.
     */
    void notifyNextStage(String next, Optional<Coord> spawn);

    /**
     * Called on boss reached.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    void notifyReachedBoss(double x, double y);
}
