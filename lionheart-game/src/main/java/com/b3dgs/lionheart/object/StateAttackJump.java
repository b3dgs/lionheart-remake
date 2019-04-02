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
package com.b3dgs.lionheart.object;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;

/**
 * Jump attack state implementation.
 */
final class StateAttackJump extends State
{
    private static final double SPEED = 5.0 / 3.0;
    private static final double JUMP_MIN = 2.5;
    private static final double JUMP_MAX = 5.4;

    private final AtomicBoolean collideY = new AtomicBoolean();
    private final Transformable transformable;
    private final TileCollidable tileCollidable;
    private final Collidable collidable;
    private final Body body;
    private final Force jump;
    private final TileCollidableListener listener;
    private final CollidableListener listenerCollidable;
    private final Updatable checkJumpStopped;
    private final Updatable checkNone = extrp ->
    {
        // Nothing to do
    };
    private Updatable check;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateAttackJump(EntityModel model, Animation animation)
    {
        super(model, animation);

        transformable = model.getFeature(Transformable.class);
        body = model.getFeature(Body.class);
        tileCollidable = model.getFeature(TileCollidable.class);
        collidable = model.getFeature(Collidable.class);
        jump = model.getJump();

        listener = (result, category) ->
        {
            if (Axis.Y == category.getAxis())
            {
                tileCollidable.apply(result);
                jump.setDirection(DirectionNone.INSTANCE);
                body.resetGravity();
                collideY.set(true);
            }
        };
        listenerCollidable = (collidable, collision) ->
        {
            if (transformable.getY() < transformable.getOldY() && collidable.hasFeature(Sheet.class))
            {
                collideY.set(true);
            }
        };

        checkJumpStopped = extrp ->
        {
            if (Double.compare(control.getVerticalDirection(), 0.0) <= 0)
            {
                check = checkNone;
                jump.setDirectionMaximum(new Force(0.0,
                                                   UtilMath.clamp(JUMP_MAX - jump.getDirectionVertical(),
                                                                  JUMP_MIN,
                                                                  JUMP_MAX)));
            }
        };

        addTransition(StateLand.class, () -> collideY.get());
        addTransition(StateFall.class, () -> is(AnimState.FINISHED));
    }

    @Override
    public void enter()
    {
        super.enter();

        check = checkJumpStopped;

        tileCollidable.addListener(listener);
        collidable.addListener(listenerCollidable);
        collideY.set(false);
    }

    @Override
    public void exit()
    {
        tileCollidable.removeListener(listener);
        collidable.removeListener(listenerCollidable);
        jump.setDirectionMaximum(new Force(0.0, JUMP_MAX));
    }

    @Override
    public void update(double extrp)
    {
        check.update(extrp);
        if (Double.compare(jump.getDirectionVertical(), 0.0) <= 0 || transformable.getY() < transformable.getOldY())
        {
            body.update(extrp);
        }
        else
        {
            body.resetGravity();
        }
        if (isGoingHorizontal())
        {
            movement.setVelocity(0.12);
        }
        else
        {
            movement.setVelocity(0.07);
        }
        movement.setDestination(control.getHorizontalDirection() * SPEED, 0.0);
    }
}
