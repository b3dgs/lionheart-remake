/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionheart.object;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;

/**
 * Idle state implementation.
 */
final class StateIdle extends State implements TileCollidableListener
{
    private final AtomicBoolean ground = new AtomicBoolean();
    private final Transformable transformable;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateIdle(EntityModel model, Animation animation)
    {
        super(model, animation);

        transformable = model.getFeature(Transformable.class);

        addTransition(StateWalk.class, this::isGoingHorizontal);
        addTransition(StateCrouch.class, this::isGoingDown);
        addTransition(StateJump.class, this::isGoingUp);
        addTransition(StateAttackPrepare.class, control::isFireButton);
        addTransition(StateFall.class, () -> !ground.get() && transformable.getY() < transformable.getOldY());
    }

    @Override
    public void notifyTileCollided(Tile tile, Axis axis)
    {
        if (Axis.Y == axis)
        {
            if (transformable.getY() < transformable.getOldY())
            {
                ground.set(true);
            }
        }
    }
}
