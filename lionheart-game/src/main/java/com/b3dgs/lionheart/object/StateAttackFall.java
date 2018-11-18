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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;

/**
 * Fall attack state implementation.
 */
final class StateAttackFall extends State
{
    private static final double SPEED = 5.0 / 3.0;

    private final AtomicBoolean ground = new AtomicBoolean();
    private final TileCollidable tileCollidable;
    private final Force jump;
    private final TileCollidableListener listener;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateAttackFall(EntityModel model, Animation animation)
    {
        super(model, animation);

        jump = model.getJump();
        final Transformable transformable = model.getFeature(Transformable.class);
        final Body body = model.getFeature(Body.class);
        tileCollidable = model.getFeature(TileCollidable.class);

        listener = (tile, category) ->
        {
            if (Axis.Y == category.getAxis())
            {
                jump.setDirection(DirectionNone.INSTANCE);
                body.resetGravity();
                if (transformable.getY() < transformable.getOldY())
                {
                    ground.set(true);
                }
            }
        };

        addTransition(StateLand.class, () -> ground.get());
        addTransition(StateFall.class, () -> is(AnimState.FINISHED));
    }

    @Override
    public void enter()
    {
        super.enter();

        tileCollidable.setEnabled(true);
        tileCollidable.addListener(listener);
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
        movement.setDestination(control.getHorizontalDirection() * SPEED, 0.0);
    }
}
