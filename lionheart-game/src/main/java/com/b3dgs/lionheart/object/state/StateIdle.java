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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.BorderDetection;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Glue;
import com.b3dgs.lionheart.object.state.attack.StateAttackPrepare;

/**
 * Idle state implementation.
 */
public final class StateIdle extends State
{
    private static final double SPEED = 5.0 / 3.0;
    private static final double WALK_MIN_SPEED = 0.75;

    private final AtomicBoolean collideX = new AtomicBoolean();
    private final AtomicBoolean collideY = new AtomicBoolean();
    private final BorderDetection border;
    private final TileCollidable tileCollidable;
    private final Collidable collidable;
    private final TileCollidableListener listenerTileCollidable;
    private final CollidableListener listenerCollidable;
    private final Body body;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateIdle(EntityModel model, Animation animation)
    {
        super(model, animation);

        border = new BorderDetection(model.getMap());
        tileCollidable = model.getFeature(TileCollidable.class);
        collidable = model.getFeature(Collidable.class);
        body = model.getFeature(Body.class);

        listenerTileCollidable = (result, category) ->
        {
            border.notifyTileCollided(result, category);

            if (Axis.X == category.getAxis())
            {
                if (isGoingLeft() && result.startWith(Constant.COLL_PREFIX_STEEP_RIGHT)
                    || isGoingRight() && result.startWith(Constant.COLL_PREFIX_STEEP_LEFT))
                {
                    tileCollidable.apply(result);
                    movement.setDirection(DirectionNone.INSTANCE);
                }
                collideX.set(true);
            }
            if (Axis.Y == category.getAxis())
            {
                tileCollidable.apply(result);
                body.resetGravity();
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
        collidable.addListener(border);

        final Transformable transformable = model.getFeature(Transformable.class);

        addTransition(StateBorder.class, () -> collideY.get() && !isGoingHorizontal() && border.is());
        addTransition(StateWalk.class, () -> !collideX.get() && isWalkingFastEnough());
        addTransition(StateCrouch.class, this::isGoingDown);
        addTransition(StateJump.class, this::isGoingUp);
        addTransition(StateAttackPrepare.class, control::isFireButton);
        addTransition(StateFall.class,
                      () -> model.hasGravity()
                            && !collideY.get()
                            && Double.compare(transformable.getY(), transformable.getOldY()) != 0);
    }

    private boolean isWalkingFastEnough()
    {
        final double speedH = movement.getDirectionHorizontal();
        return isGoingHorizontal() && !UtilMath.isBetween(speedH, -WALK_MIN_SPEED, WALK_MIN_SPEED);
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.setVelocity(0.16);
        tileCollidable.addListener(listenerTileCollidable);
        collidable.addListener(listenerCollidable);
        border.reset();
        collideX.set(false);
        collideY.set(false);
    }

    @Override
    public void exit()
    {
        tileCollidable.removeListener(listenerTileCollidable);
        collidable.removeListener(listenerCollidable);

        if (border.isLeft())
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else if (border.isRight())
        {
            mirrorable.mirror(Mirror.NONE);
        }
    }

    @Override
    public void update(double extrp)
    {
        body.update(extrp);
        movement.setDestination(control.getHorizontalDirection() * SPEED, 0.0);
    }

    @Override
    protected void postUpdate()
    {
        collideX.set(false);
        collideY.set(false);
        border.reset();
    }
}
