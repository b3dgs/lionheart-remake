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
package com.b3dgs.lionheart;

import java.util.Optional;

import com.b3dgs.lionengine.geom.Coord;

/**
 * Load next stage.
 */
public interface LoadNextStage
{
    /**
     * Reload current stage.
     */
    void reloadStage();

    /**
     * Load next stage.
     * 
     * @param next The next stage.
     * @param delayMs The delay before load stage in milli.
     * @param spawn The next spawn.
     */
    void loadNextStage(String next, int delayMs, Optional<Coord> spawn);

    /**
     * Load next stage.
     * 
     * @param next The next stage.
     * @param delayMs The delay before load stage in milli.
     */
    default void loadNextStage(String next, int delayMs)
    {
        loadNextStage(next, delayMs, Optional.empty());
    }

    /**
     * Load next stage now.
     * 
     * @param next The next stage.
     */
    default void loadNextStage(String next)
    {
        loadNextStage(next, 0, Optional.empty());
    }
}
