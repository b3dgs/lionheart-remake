/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.narration;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Action;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionheart.FadeSide;

/**
 * Narration factory.
 */
public interface NarrationFactory
{
    /**
     * Add narration.
     * 
     * @param startMs The start time.
     * @param side The fade side.
     * @param speed The fade speed.
     */
    void add(int startMs, FadeSide side, double speed);

    /**
     * Add narration.
     * 
     * @param startMs The start time.
     * @param action The action reference.
     */
    void add(int startMs, Action action);

    /**
     * Add narration.
     * 
     * @param startMs The start time.
     * @param endMs The end time.
     * @param step The step reference.
     */
    void add(int startMs, int endMs, Step step);

    /**
     * Add narration.
     * 
     * @param startMs The start time.
     * @param endMs The end time.
     * @param updater The updater reference.
     */
    void add(int startMs, int endMs, Updatable updater);

    /**
     * Add narration.
     * 
     * @param startMs The start time.
     * @param endMs The end time.
     * @param renderer The renderer reference.
     */
    void add(int startMs, int endMs, Renderable renderer);

    /**
     * Add narration.
     * 
     * @param startMs The start time.
     * @param endMs The end time.
     * @param updater The updater reference.
     * @param renderer The renderer reference.
     */
    void add(int startMs, int endMs, Updatable updater, Renderable renderer);

    /**
     * Add narration.
     * 
     * @param timeStartMs The start time.
     * @param timeEndMs The end time.
     * @param font The font reference.
     * @param x1 The first text horizontal location.
     * @param y1 The first text vertical location.
     * @param x2 The other texts horizontal location.
     * @param texts The text lines.
     */
    void add(int timeStartMs, int timeEndMs, SpriteFont font, int x1, int y1, int x2, String... texts);

    /**
     * Add narration.
     * 
     * @param timeStartMs The start time.
     * @param timeEndMs The end time.
     * @param font The font reference.
     * @param x1 The first text horizontal location.
     * @param y1 The first text vertical location.
     * @param x2 The other texts horizontal location.
     * @param align The text align.
     * @param texts The text lines.
     */
    void add(int timeStartMs, int timeEndMs, SpriteFont font, int x1, int y1, int x2, Align align, String... texts);

    /**
     * Add narration.
     * 
     * @param startMs The start time.
     * @param updater The updater reference.
     * @param renderer The renderer reference.
     */
    void add(int startMs, Updatable updater, Renderable renderer);
}
