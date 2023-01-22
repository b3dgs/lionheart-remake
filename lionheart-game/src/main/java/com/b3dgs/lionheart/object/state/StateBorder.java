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
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.state.attack.StatePrepareAttack;

/**
 * Border state implementation.
 */
public final class StateBorder extends State
{
    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateBorder(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateWalk.class, () -> !hasWin() && !collideX.get() && isGoHorizontal());
        addTransition(StateCrouch.class, () -> !hasWin() && collideY.get() && isGoDown());
        addTransition(StateJump.class, () -> !hasWin() && collideY.get() && (isGoUpOnce() || isFire(DeviceMapping.UP)));
        addTransition(StatePrepareAttack.class, () -> !hasWin() && collideY.get() && isFire());
        addTransition(StateFall.class,
                      () -> !hasWin()
                            && model.hasGravity()
                            && !collideY.get()
                            && !steep.is()
                            && Double.compare(transformable.getY(), transformable.getOldY()) != 0);
        addTransition(StateWin.class, this::hasWin);
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.zero();
    }

    @Override
    protected void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        super.onCollideLeg(result, category);

        if (result.containsY(CollisionName.LIANA))
        {
            collideY.set(true);
            tileCollidable.apply(result);
            body.resetGravity();
        }
    }
}
