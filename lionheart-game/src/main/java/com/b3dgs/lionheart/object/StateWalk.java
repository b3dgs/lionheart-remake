/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.object.trait.mirrorable.Mirrorable;
import com.b3dgs.lionengine.game.state.StateGame;
import com.b3dgs.lionengine.game.state.StateInputDirectionalUpdater;
import com.b3dgs.lionengine.game.state.StateTransition;
import com.b3dgs.lionengine.game.state.StateTransitionInputDirectionalChecker;

/**
 * Walk state implementation.
 */
public class StateWalk extends StateGame implements StateInputDirectionalUpdater
{
    /** Animator reference. */
    private final Animator animator;
    /** Animation reference. */
    private final Animation animation;
    /** Mirrorable trait. */
    private final Mirrorable mirrorable;
    /** Movement force. */
    private final Force movement;
    /** Animation played flag. */
    private boolean played;
    /** Direction side. */
    private double side;

    /**
     * Create the state.
     * 
     * @param entity The entity reference.
     * @param animation The animation reference.
     */
    public StateWalk(Entity entity, Animation animation)
    {
        super(EntityState.IDLE);
        animator = entity.surface;
        this.animation = animation;
        mirrorable = entity.getTrait(Mirrorable.class);
        movement = entity.movement;
        addTransition(new WalkToIdle());
    }

    @Override
    public void enter()
    {
        animator.play(animation);
    }

    @Override
    public void updateInput(InputDeviceDirectional input)
    {
        side = input.getHorizontalDirection();
    }

    @Override
    public void update(double extrp)
    {
        if (!played && Double.compare(movement.getDirectionHorizontal(), 0.0) != 0)
        {
            animator.play(animation);
            played = true;
        }
        movement.setDestination(side * 3, 0);
        animator.setAnimSpeed(Math.abs(movement.getDirectionHorizontal()) / 12.0);

        if (side < 0 && movement.getDirectionHorizontal() > 0.0)
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else if (side > 0 && movement.getDirectionHorizontal() < 0.0)
        {
            mirrorable.mirror(Mirror.NONE);
        }
    }

    /**
     * Transition from walk to idle state.
     */
    private static final class WalkToIdle extends StateTransition implements StateTransitionInputDirectionalChecker
    {
        /**
         * Create the transition.
         */
        WalkToIdle()
        {
            super(EntityState.IDLE);
        }

        @Override
        public boolean check(InputDeviceDirectional input)
        {
            return Double.compare(input.getHorizontalDirection(), 0.0) == 0;
        }
    }
}
