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
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.state.StateAbstract;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;

/**
 * Fall state implementation.
 */
public class StateFall extends StateAbstract implements TileCollidableListener
{
    private final AtomicBoolean ground = new AtomicBoolean();
    private final Animator animator;
    private final Animation animation;
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
    public StateFall(EntityModel model, Animation animation)
    {
        super();

        this.animation = animation;
        animator = model.getSurface();
        jump= model.getJump();
        transformable = model.getFeature(Transformable.class);
        body = model.getFeature(Body.class);
        tileCollidable = model.getFeature(TileCollidable.class);
        addTransition(StateLand.class, () -> ground.get());
    }

    @Override
    public void enter()
    {
        tileCollidable.addListener(this);
        animator.play(animation);
        jump.setDirection(0.0, 5.0);
        ground.set(false);
    }
    
    @Override
    public void exit()
    {
        tileCollidable.removeListener(this);
    }

    @Override
    public void update(double extrp)
    {
        // Nothing to do
    }
    
    @Override
    public void notifyTileCollided(Tile tile, Axis axis)
    {
        if (Axis.Y == axis)
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
