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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.state.StateLast;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.feature.Glue;
import com.b3dgs.lionheart.object.feature.Patrol;

/**
 * Turn state implementation.
 */
public final class StateTurn extends State
{
    private final AtomicBoolean collideY = new AtomicBoolean();
    private final TileCollidable tileCollidable;
    private final Collidable collidable;
    private final TileCollidableListener listenerTileCollidable;
    private final CollidableListener listenerCollidable;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateTurn(EntityModel model, Animation animation)
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
        listenerCollidable = (collidable, collision) ->
        {
            if (collidable.hasFeature(Glue.class))
            {
                collideY.set(true);
            }
        };

        addTransition(StateLast.class, () -> is(AnimState.FINISHED));
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.setDestination(0.0, 0.0);
        movement.setDirection(DirectionNone.INSTANCE);

        tileCollidable.addListener(listenerTileCollidable);
        collidable.addListener(listenerCollidable);
    }

    @Override
    public void exit()
    {
        tileCollidable.removeListener(listenerTileCollidable);
        collidable.removeListener(listenerCollidable);
        if (model.hasFeature(Patrol.class))
        {
            model.getFeature(Patrol.class).applyMirror();
        }
    }

    @Override
    protected void postUpdate()
    {
        collideY.set(false);
    }
}
