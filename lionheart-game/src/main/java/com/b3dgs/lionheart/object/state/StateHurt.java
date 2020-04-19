/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Hurtable;

/**
 * Hurt state implementation.
 */
public final class StateHurt extends State
{
    private final Hurtable hurtable = model.getFeature(Hurtable.class);

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateHurt(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateIdle.class, () -> !hurtable.isHurting() && !model.hasGravity());
        addTransition(StateFall.class, () -> !hurtable.isHurting() && model.hasGravity());
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.setVelocity(Constant.WALK_VELOCITY_MAX);
    }

    @Override
    public void exit()
    {
        super.exit();

        jump.setDirectionMaximum(new Force(0.0, Constant.JUMP_MAX));
    }

    @Override
    public void update(double extrp)
    {
        body.resetGravity();
        movement.setDestination(input.getHorizontalDirection() * Constant.WALK_SPEED, 0.0);
    }
}
