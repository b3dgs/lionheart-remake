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
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;

/**
 * Fall attack state implementation.
 */
final class StateAttackFall extends State implements TileCollidableListener
{
    private final AtomicBoolean ground = new AtomicBoolean();
    private final Transformable transformable;
    private final Body body;
    private final TileCollidable tileCollidable;
    private final Force jump;

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
        transformable = model.getFeature(Transformable.class);
        body = model.getFeature(Body.class);
        tileCollidable = model.getFeature(TileCollidable.class);

        addTransition(StateLand.class, () -> ground.get());
        addTransition(StateFall.class, () -> is(AnimState.FINISHED));
    }

    @Override
    public void enter()
    {
        super.enter();

        tileCollidable.addListener(this);
        jump.setDirection(0.0, 5.0);
        ground.set(false);
    }

    @Override
    public void exit()
    {
        tileCollidable.removeListener(this);
    }

    @Override
    public void notifyTileCollided(Tile tile, CollisionCategory category)
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
    }
}
