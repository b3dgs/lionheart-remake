/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionheart.object.state;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Patrol;
import com.b3dgs.lionheart.object.feature.Spider;
import com.b3dgs.lionheart.object.feature.Turtle;
import com.b3dgs.lionheart.object.state.attack.StateAttackFall;
import com.b3dgs.lionheart.object.state.attack.StateAttackJump;

/**
 * Fall state implementation.
 */
public final class StateFall extends State
{
    private final Viewer viewer = model.getServices().get(Viewer.class);

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateFall(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateLand.class, () -> !steep.is() && collideY.get() && !model.hasFeature(Patrol.class));
        addTransition(StatePatrol.class,
                      () -> !steep.is()
                            && (collideY.get() || !model.getConfig().getFall().orElse(Boolean.TRUE).booleanValue())
                            && model.hasFeature(Patrol.class));
        addTransition(StateSlide.class, () -> steep.is() && !isGoHorizontal());
        addTransition(StateSlideSlow.class,
                      () -> steep.is() && (is(Mirror.NONE) && isGoRight() || is(Mirror.HORIZONTAL) && isGoLeft()));
        addTransition(StateSlideFast.class,
                      () -> steep.is() && (is(Mirror.HORIZONTAL) && isGoRight() || is(Mirror.NONE) && isGoLeft()));
        addTransition(StateGripIdle.class, () -> grip.get() && !isGoDown());
        addTransition(StateLianaIdle.class,
                      () -> !grip.get() && liana.is() && !liana.isLeft() && !liana.isRight() && !isGoDown());
        addTransition(StateLianaSlide.class, () -> (liana.isLeft() || liana.isRight()) && !isGoDown());
        addTransition(StateAttackJump.class, () -> !collideY.get() && !isGoDown() && isFireOnce());
        addTransition(StateAttackFall.class, () -> !collideY.get() && isGoDown() && isFireOnce());
    }

    @Override
    protected void onCollideHand(CollisionResult result, CollisionCategory category)
    {
        super.onCollideHand(result, category);

        if (!isGoDown() && result.startWithY(CollisionName.LIANA))
        {
            tileCollidable.apply(result);
        }
    }

    @Override
    public void update(double extrp)
    {
        movement.setDestination(device.getHorizontalDirection() * Constant.WALK_SPEED, 0.0);
    }

    @Override
    public void exit()
    {
        super.exit();

        if (is(Mirror.NONE) && (steep.isLeft() || liana.isLeft()))
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else if (is(Mirror.HORIZONTAL) && (steep.isRight() || liana.isRight()))
        {
            mirrorable.mirror(Mirror.NONE);
        }

        if (model.hasFeature(Patrol.class)
            && !model.hasFeature(Spider.class)
            && !model.hasFeature(Turtle.class)
            && viewer.isViewable(transformable, 0, 0))
        {
            Sfx.MONSTER_LAND.play();
        }
    }

    @Override
    protected void postUpdate()
    {
        super.postUpdate();

        if (isGoHorizontal()
            && !(movement.getDirectionHorizontal() < 0 && isGoRight()
                 || movement.getDirectionHorizontal() > 0 && isGoLeft())
            && Math.abs(movement.getDirectionHorizontal()) > Constant.WALK_SPEED
            && movement.isDecreasingHorizontal())
        {
            movement.setVelocity(Constant.WALK_VELOCITY_SLOPE_DECREASE);
        }
        else
        {
            movement.setVelocity(Constant.WALK_VELOCITY_MAX);
        }
    }
}
