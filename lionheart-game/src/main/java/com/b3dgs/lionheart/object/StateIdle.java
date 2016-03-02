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

import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.game.state.StateGame;
import com.b3dgs.lionengine.game.state.StateTransition;
import com.b3dgs.lionengine.game.state.StateTransitionInputDirectionalChecker;

/**
 * Idle state implementation.
 */
public class StateIdle extends StateGame
{
    /** Animator reference. */
    private final Animator animator;
    /** Animation reference. */
    private final Animation animation;

    /**
     * Create the state.
     * 
     * @param entity The entity reference.
     * @param animation The animation reference.
     */
    public StateIdle(Entity entity, Animation animation)
    {
        super(EntityState.IDLE);
        animator = entity.surface;
        this.animation = animation;
        addTransition(new IdleToWalk());
    }

    @Override
    public void enter()
    {
        animator.play(animation);
    }

    @Override
    public void update(double extrp)
    {
        // Nothing to do
    }

    /**
     * Transition from idle to walk state.
     */
    private static final class IdleToWalk extends StateTransition implements StateTransitionInputDirectionalChecker
    {
        /**
         * Create the transition.
         */
        IdleToWalk()
        {
            super(EntityState.WALK);
        }

        @Override
        public boolean check(InputDeviceDirectional input)
        {
            return Double.compare(input.getHorizontalDirection(), 0.0) != 0;
        }
    }
}
