/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object.state;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Glue;

/**
 * Patrol state implementation.
 */
public final class StatePatrol extends State
{
    private static final double SPEED = 5.0 / 3.0;
    private static final double ANIM_SPEED_DIVISOR = 3.0;

    private final AtomicBoolean collideY = new AtomicBoolean();
    private final Collidable collidable;
    private final TileCollidable tileCollidable;
    private final TileCollidableListener listenerTileCollidable;
    private final CollidableListener listenerCollidable;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StatePatrol(EntityModel model, Animation animation)
    {
        super(model, animation);

        tileCollidable = model.getFeature(TileCollidable.class);
        collidable = model.getFeature(Collidable.class);

        listenerTileCollidable = (result, category) ->
        {
            if (Axis.Y == category.getAxis())
            {
                tileCollidable.apply(result);
                collideY.set(true);
            }
        };
        listenerCollidable = (collidable, with, by) ->
        {
            if (collidable.hasFeature(Glue.class) && with.getName().startsWith(Constant.ANIM_PREFIX_LEG))
            {
                collideY.set(true);
            }
        };

        addTransition(StateFall.class,
                      () -> model.hasGravity()
                            && Double.compare(movement.getDirectionHorizontal(), 0.0) != 0
                            && !collideY.get());
    }

    @Override
    public void enter()
    {
        super.enter();

        tileCollidable.addListener(listenerTileCollidable);
        collidable.addListener(listenerCollidable);
    }

    @Override
    public void exit()
    {
        tileCollidable.removeListener(listenerTileCollidable);
        collidable.removeListener(listenerCollidable);
    }

    @Override
    public void update(double extrp)
    {
        movement.setDestination(control.getHorizontalDirection() * SPEED, control.getVerticalDirection() * SPEED);
        animator.setAnimSpeed(Math.abs(movement.getDirectionHorizontal() + movement.getDirectionVertical())
                              / ANIM_SPEED_DIVISOR);
    }

    @Override
    protected void postUpdate()
    {
        collideY.set(false);
    }
}
