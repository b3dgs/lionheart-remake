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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.GameplaySteep;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.state.attack.StateAttackFall;
import com.b3dgs.lionheart.object.state.attack.StateAttackJump;

/**
 * Jump state implementation.
 */
public final class StateJump extends State
{
    private static final double SPEED = 5.0 / 3.0;

    private final GameplaySteep steep = new GameplaySteep();

    private final Updatable checkJumpStopped;

    private Updatable check;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateJump(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateFall.class,
                      () -> (Double.compare(jump.getDirectionVertical(), 0.0) <= 0
                             || transformable.getY() < transformable.getOldY()));
        addTransition(StateSlide.class,
                      () -> transformable.getY() > transformable.getOldY()
                            && (steep.isLeft() && isGoingRight() || steep.isRight() && isGoingLeft()));
        addTransition(StateAttackJump.class, () -> control.isFireButtonOnce() && !isGoingDown());
        addTransition(StateAttackFall.class, () -> control.isFireButton() && isGoingDown());

        checkJumpStopped = extrp ->
        {
            if (!control.isFireButton() && Double.compare(control.getVerticalDirection(), 0.0) <= 0)
            {
                check = UpdatableVoid.getInstance();
                jump.setDirectionMaximum(new Force(0.0,
                                                   UtilMath.clamp(Constant.JUMP_MAX - jump.getDirectionVertical(),
                                                                  Constant.JUMP_MIN,
                                                                  Constant.JUMP_MAX)));
            }
        };
    }

    @Override
    protected void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        super.onCollideLeg(result, category);

        steep.onCollideLeg(result, category);
        if (result.startWithY(Constant.COLL_PREFIX_STEEP))
        {
            body.resetGravity();
        }
    }

    @Override
    protected void onCollideKnee(CollisionResult result, CollisionCategory category)
    {
        super.onCollideKnee(result, category);

        steep.onCollideKnee(result, category);
        if (result.startWithX(Constant.COLL_PREFIX_STEEP_VERTICAL))
        {
            tileCollidable.apply(result);
        }
    }

    @Override
    public void enter()
    {
        super.enter();

        check = checkJumpStopped;

        jump.setDirection(0.0, Constant.JUMP_MAX);
        jump.setDirectionMaximum(new Force(0.0, Constant.JUMP_MAX));

        steep.reset();
    }

    @Override
    public void exit()
    {
        super.exit();

        jump.setDirectionMaximum(new Force(0.0, Constant.JUMP_MAX));

        if (mirrorable.is(Mirror.NONE) && steep.isLeft())
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
            movement.setDirection(DirectionNone.INSTANCE);
            movement.setDestination(0.0, 0.0);
        }
        else if (mirrorable.is(Mirror.HORIZONTAL) && steep.isRight())
        {
            mirrorable.mirror(Mirror.NONE);
            movement.setDirection(DirectionNone.INSTANCE);
            movement.setDestination(0.0, 0.0);
        }
    }

    @Override
    public void update(double extrp)
    {
        check.update(extrp);
        body.resetGravity();
        movement.setDestination(control.getHorizontalDirection() * SPEED, 0.0);
    }

    @Override
    protected void postUpdate()
    {
        super.postUpdate();

        if (isGoingHorizontal()
            && !(movement.getDirectionHorizontal() < 0 && isGoingRight()
                 || movement.getDirectionHorizontal() > 0 && isGoingLeft())
            && Math.abs(movement.getDirectionHorizontal()) > SPEED
            && movement.isDecreasingHorizontal())
        {
            movement.setVelocity(0.001);
        }
        else
        {
            movement.setVelocity(0.12);
        }

        steep.reset();
    }
}
