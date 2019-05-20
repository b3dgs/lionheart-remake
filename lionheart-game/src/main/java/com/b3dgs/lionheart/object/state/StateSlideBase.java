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
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;

/**
 * Slide state implementation.
 */
public class StateSlideBase extends State
{
    private static final double SPEED_JUMP_X = 3.5;
    private static final Direction SPEED_JUMP_Y = new Force(0.0, 2.5);

    private final AtomicBoolean steep = new AtomicBoolean();

    private final TileCollidableListener listenerTileCollidable;

    private double speed = 0.5;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateSlideBase(EntityModel model, Animation animation)
    {
        super(model, animation);

        listenerTileCollidable = (result, category) ->
        {
            if (Axis.Y == category.getAxis())
            {
                tileCollidable.apply(result);
            }
            if (result.startWith(Constant.COLL_PREFIX_STEEP))
            {
                steep.set(true);
            }
        };

        addTransition(StateLand.class, () -> !steep.get());
        addTransition(StateJump.class, this::isGoingUp);
    }

    /**
     * Set the slide speed.
     * 
     * @param speed The slide speed.
     */
    protected final void setSpeed(double speed)
    {
        this.speed = speed;
    }

    @Override
    public void enter()
    {
        super.enter();

        tileCollidable.addListener(listenerTileCollidable);
        steep.set(false);
    }

    @Override
    public void exit()
    {
        tileCollidable.removeListener(listenerTileCollidable);
        if (isGoingUp())
        {
            movement.setDestination(0.0, 0.0);
            movement.setDirection(SPEED_JUMP_X, 0.0);
            jump.setDirectionMaximum(SPEED_JUMP_Y);
        }
    }

    @Override
    public void update(double extrp)
    {
        movement.setDestination(speed, -speed * 2.0);
    }

    @Override
    protected void postUpdate()
    {
        steep.set(false);
    }
}
