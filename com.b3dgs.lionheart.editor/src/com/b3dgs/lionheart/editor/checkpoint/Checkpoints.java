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
package com.b3dgs.lionheart.editor.checkpoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;

import com.b3dgs.lionheart.Checkpoint;

/**
 * Handle checkpoints.
 */
public class Checkpoints implements Iterable<Checkpoint>
{
    private final List<Checkpoint> checkpoints = new ArrayList<>();

    private OptionalInt selection = OptionalInt.empty();

    /**
     * Select checkpoint.
     * 
     * @param index The checkpoint index to select.
     */
    public void select(int index)
    {
        selection = OptionalInt.of(index);
    }

    /**
     * Get selected checkpoint.
     * 
     * @return The selected checkpoint.
     */
    public OptionalInt getSelection()
    {
        return selection;
    }

    /**
     * Add a checkpoint.
     * 
     * @param checkpoint The checkpoint to add.
     */
    public void add(Checkpoint checkpoint)
    {
        checkpoints.add(checkpoint);
    }

    /**
     * Get checkpoint at index.
     * 
     * @param index The index number.
     * @return The checkpoint found.
     */
    public Checkpoint get(int index)
    {
        return checkpoints.get(index);
    }

    /**
     * Set checkpoint.
     * 
     * @param index The index number.
     * @param checkpoint The checkpoint reference.
     */
    public void set(int index, Checkpoint checkpoint)
    {
        checkpoints.set(index, checkpoint);
    }

    /**
     * Remove checkpoint.
     * 
     * @param index The index number.
     */
    public void remove(int index)
    {
        if (selection.isPresent() && selection.getAsInt() == index)
        {
            selection = OptionalInt.empty();
        }
        checkpoints.remove(index);
    }

    /**
     * Remove all checkpoint.
     */
    public void removeAll()
    {
        checkpoints.clear();
    }

    @Override
    public Iterator<Checkpoint> iterator()
    {
        return checkpoints.iterator();
    }
}
