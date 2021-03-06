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
package com.b3dgs.lionheart.object.state.executioner;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Executioner;
import com.b3dgs.lionheart.object.feature.SwordShade;
import com.b3dgs.lionheart.object.state.StatePatrol;

/**
 * Attack1 executioner state implementation.
 */
public final class StateExecutionerAttack1 extends State
{
    private static final double ATTACK_SPEED = 1.8;
    private static final double ATTACK1_DISTANCE_MIN = 16.0;

    private final Transformable player = model.getServices().get(SwordShade.class).getFeature(Transformable.class);

    private final Tick tick = new Tick();

    private Mirror mirror;
    private double velocity;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateExecutionerAttack1(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StatePatrol.class,
                      () -> Math.abs(player.getX() - transformable.getX()) < ATTACK1_DISTANCE_MIN
                            || Math.abs(player.getX() - transformable.getX()) > Executioner.ATTACK1_DISTANCE_MAX
                            || movement.isZero());
    }

    @Override
    public void enter()
    {
        super.enter();

        mirror = mirrorable.getMirror();
        velocity = movement.getVelocity();
        movement.zero();
        movement.setDestination(0.0, 0.0);
        movement.setVelocity(0.001);
        int side;
        if (mirrorable.getMirror() == Mirror.HORIZONTAL)
        {
            side = -1;
        }
        else
        {
            side = 1;
        }
        movement.setDirection(ATTACK_SPEED * side, 0.0);
        jump.setDirection(0.0, 2.0);
        jump.setDestination(0.0, 0.0);
        jump.setVelocity(0.05);
        body.resetGravity();
        tick.restart();
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        tick.update(extrp);
        mirrorable.mirror(mirror);
    }

    @Override
    public void exit()
    {
        super.exit();

        movement.setVelocity(velocity);
    }
}
