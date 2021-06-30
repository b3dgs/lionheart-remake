/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.GameplayBorder;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.state.attack.StatePrepareAttack;

/**
 * Idle state implementation.
 */
public final class StateIdle extends State
{
    private final GameplayBorder border = new GameplayBorder(model.getMap());

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateIdle(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateBorder.class, () -> !hasWin() && collideY.get() && !isGoHorizontal() && border.is());
        addTransition(StateWalk.class, () -> !hasWin() && !collideX.get() && isWalkingFastEnough());
        addTransition(StateCrouch.class, () -> !hasWin() && collideY.get() && isGoDown());
        addTransition(StateJump.class, () -> !hasWin() && collideY.get() && isGoUpOnce());
        addTransition(StatePrepareAttack.class, () -> !hasWin() && collideY.get() && isFire());
        addTransition(StateSlide.class, () -> !hasWin() && steep.is());
        addTransition(StateFall.class,
                      () -> !hasWin()
                            && model.hasGravity()
                            && !collideY.get()
                            && !steep.is()
                            && Double.compare(transformable.getY(), transformable.getOldY()) != 0);
        addTransition(StateWin.class, this::hasWin);
    }

    private boolean isWalkingFastEnough()
    {
        final double speedH = movement.getDirectionHorizontal();
        return isGoHorizontal() && !UtilMath.isBetween(speedH, -Constant.WALK_MIN_SPEED, Constant.WALK_MIN_SPEED);
    }

    @Override
    protected void onCollideKnee(CollisionResult result, CollisionCategory category)
    {
        if (!category.getName().startsWith(CollisionName.KNEE + "_1"))
        {
            super.onCollideKnee(result, category);
        }
    }

    @Override
    protected void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        super.onCollideLeg(result, category);

        border.notifyTileCollided(result, category);
        body.resetGravity();
    }

    @Override
    protected void onCollided(Collidable collidable, Collision with, Collision by)
    {
        super.onCollided(collidable, with, by);

        border.notifyCollided(collidable, with, by);
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.setVelocity(0.16);
        border.reset();
    }

    @Override
    public void exit()
    {
        super.exit();

        if (border.isLeft() || steep.isLeft())
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else if (border.isRight() || steep.isRight())
        {
            mirrorable.mirror(Mirror.NONE);
        }
        if (steep.is())
        {
            movement.zero();
        }
    }

    @Override
    protected void postUpdate()
    {
        super.postUpdate();

        if (!(steep.isLeft() && isGoRight() || steep.isRight() && isGoLeft()))
        {
            movement.setDestination(device.getHorizontalDirection() * Constant.WALK_SPEED, 0.0);
        }
        border.reset();
    }
}
