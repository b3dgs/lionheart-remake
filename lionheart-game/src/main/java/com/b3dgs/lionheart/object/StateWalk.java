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
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.state.StateAbstract;

/**
 * Walk state implementation.
 */
public class StateWalk extends StateAbstract
{
    private static final double SPEED = 2.0;
    
    private final EntityModel model;
    private final Animator animator;
    private final Animation animation;
    private final Mirrorable mirrorable;
    private final Force movement;
    private boolean played;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateWalk(EntityModel model, Animation animation)
    {
        super();

        this.model = model;
        this.animation = animation;
        animator = model.getSurface();
        mirrorable = model.getFeature(Mirrorable.class);
        movement = model.getMovement();
        addTransition(StateIdle.class, () -> Double.compare(model.getInput().getHorizontalDirection(), 0.0) == 0);
    }

    @Override
    public void enter()
    {
        animator.play(animation);
        movement.setVelocity(10);
        movement.setSensibility(0.1);
        played = false;
    }

    @Override
    public void update(double extrp)
    {
        if (!played && Double.compare(movement.getDirectionHorizontal(), 0.0) != 0)
        {
            animator.play(animation);
            played = true;
        }
        
        final double side = model.getInput().getHorizontalDirection();
        movement.setDestination(side * SPEED, 0);
        animator.setAnimSpeed(Math.abs(movement.getDirectionHorizontal()) / 8.0);

        if (side < 0 && movement.getDirectionHorizontal() > 0.0)
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else if (side > 0 && movement.getDirectionHorizontal() < 0.0)
        {
            mirrorable.mirror(Mirror.NONE);
        }
    }
}
