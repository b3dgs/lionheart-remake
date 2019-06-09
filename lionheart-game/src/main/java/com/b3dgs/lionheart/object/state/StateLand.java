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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.GameplaySteep;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Glue;
import com.b3dgs.lionheart.object.state.attack.StateAttackPrepare;

/**
 * Land state implementation.
 */
public final class StateLand extends State
{
    private static final long LAND_TICK = 10L;

    private final Tick landed = new Tick();
    private final GameplaySteep steep = new GameplaySteep();

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateLand(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateIdle.class, () -> !isGoDown() && landed.elapsed(LAND_TICK));
        addTransition(StateJump.class, this::isGoUpOnce);
        addTransition(StateCrouch.class, this::isGoDown);
        addTransition(StateAttackPrepare.class, control::isFireButton);
        addTransition(StateFall.class,
                      () -> !collideY.get() && Double.compare(movement.getDirectionHorizontal(), 0.0) != 0);
    }

    @Override
    protected void onCollideKnee(CollisionResult result, CollisionCategory category)
    {
        super.onCollideKnee(result, category);

        steep.onCollideKnee(result, category);
        tileCollidable.apply(result);

        if (movement.getDirectionHorizontal() < 0 && result.startWithX(Constant.COLL_PREFIX_STEEP_RIGHT)
            || movement.getDirectionHorizontal() > 0 && result.startWithX(Constant.COLL_PREFIX_STEEP_LEFT))
        {
            movement.setDirection(DirectionNone.INSTANCE);
            movement.setDestination(0.0, 0.0);
        }
    }

    @Override
    protected void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        super.onCollideLeg(result, category);

        steep.onCollideLeg(result, category);
        tileCollidable.apply(result);
    }

    @Override
    protected void onCollided(Collidable collidable, Collision with, Collision by)
    {
        super.onCollided(collidable, with, by);

        if (collidable.hasFeature(Glue.class) && with.getName().startsWith(Constant.ANIM_PREFIX_LEG))
        {
            collideY.set(true);
        }
    }

    @Override
    public void enter()
    {
        super.enter();

        landed.restart();
    }

    @Override
    public void update(double extrp)
    {
        landed.update(extrp);
    }

    @Override
    protected void postUpdate()
    {
        super.postUpdate();

        if (!(steep.isLeft() && isGoRight() || steep.isRight() && isGoLeft()))
        {
            movement.setDestination(control.getHorizontalDirection() * Constant.WALK_SPEED, 0.0);
        }
    }
}
