/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;

/**
 * Jump state implementation.
 */
final class StateJump extends State
{
    private static final double SPEED = 5.0 / 3.0;
    private static final double JUMP_MAX = 5.4;

    private final Transformable transformable;
    private final Force jump;
    private final Body body;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateJump(EntityModel model, Animation animation)
    {
        super(model, animation);

        transformable = model.getFeature(Transformable.class);
        jump = model.getJump();
        body = model.getFeature(Body.class);

        addTransition(StateFall.class,
                      () -> Double.compare(jump.getDirectionVertical(), 0.0) <= 0
                            || transformable.getY() < transformable.getOldY());
        addTransition(StateAttackJump.class, () -> control.isFireButton() && !isGoingDown());
        addTransition(StateAttackFall.class, () -> control.isFireButton() && isGoingDown());
    }

    @Override
    public void enter()
    {
        super.enter();

        jump.setSensibility(0.1);
        jump.setVelocity(0.18);
        jump.setDirection(0.0, JUMP_MAX);
    }

    @Override
    public void update(double extrp)
    {
        movement.setDestination(control.getHorizontalDirection() * SPEED, 0.0);
        body.resetGravity();
    }
}
