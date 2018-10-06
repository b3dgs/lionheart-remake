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
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;

/**
 * Idle state implementation.
 */
final class StateIdle extends State
{
    private final BorderDetection border = new BorderDetection();
    private final AtomicBoolean ground = new AtomicBoolean();
    private final TileCollidable tileCollidable;
    private final TileCollidableListener listener;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateIdle(EntityModel model, Animation animation)
    {
        super(model, animation);

        final Transformable transformable = model.getFeature(Transformable.class);
        tileCollidable = model.getFeature(TileCollidable.class);

        listener = (tile, category) ->
        {
            border.notifyTileCollided(tile, category);
            if (Axis.Y == category.getAxis())
            {
                if (transformable.getY() < transformable.getOldY())
                {
                    ground.set(true);
                }
            }
        };

        addTransition(StateBorder.class, () -> !isGoingHorizontal() && border.is());
        addTransition(StateWalk.class, this::isGoingHorizontal);
        addTransition(StateCrouch.class, this::isGoingDown);
        addTransition(StateJump.class, this::isGoingUp);
        addTransition(StateAttackPrepare.class, control::isFireButton);
        addTransition(StateFall.class, () -> !ground.get() && transformable.getY() < transformable.getOldY());
    }

    @Override
    public void enter()
    {
        super.enter();

        tileCollidable.addListener(listener);
        border.reset();
        ground.set(false);
    }

    @Override
    public void exit()
    {
        tileCollidable.removeListener(listener);
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        ground.set(false);
    }
}
