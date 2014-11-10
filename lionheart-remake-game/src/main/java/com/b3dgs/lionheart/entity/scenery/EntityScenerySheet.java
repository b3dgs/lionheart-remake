/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.entity.scenery;

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.EntityMover;
import com.b3dgs.lionheart.entity.player.Valdyn;
import com.b3dgs.lionheart.purview.ascend.AscendableModel;
import com.b3dgs.lionheart.purview.ascend.AscendableServices;
import com.b3dgs.lionheart.purview.ascend.AscendableUsedServices;

/**
 * Sheet base scenery implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class EntityScenerySheet
        extends EntityScenery
        implements AscendableUsedServices
{
    /** Half circle. */
    protected static final int HALF_CIRCLE = 180;
    /** Effect speed. */
    private static final int EFFECT_SPEED = 9;
    /** Effect amplitude. */
    private static final int AMPLITUDE = 6;

    /** Ascendable model. */
    private final AscendableServices ascendable;
    /** Initial vertical location, default sheet location y. */
    private int initialY = Integer.MIN_VALUE;
    /** Effect start flag, <code>true</code> when effect is occurring, <code>false</code> else. */
    private boolean effectStart;
    /** Effect counter, represent the value used to calculate the effect. */
    private double effectCounter;
    /** Effect side, -1 to decrease, 1 to increase. */
    private int effectSide;
    /** First hit flag, when sheet is hit for the first time. */
    private boolean firstHit;

    /**
     * @see Entity#Entity(SetupSurfaceRasteredGame)
     */
    protected EntityScenerySheet(SetupSurfaceRasteredGame setup)
    {
        super(setup);
        ascendable = new AscendableModel(this);
    }

    /**
     * Update the sheet effect.
     * 
     * @param extrp The extrapolation value.
     */
    protected void updateSheet(double extrp)
    {
        if (effectStart)
        {
            increaseEffectCounter(EntityScenerySheet.EFFECT_SPEED * effectSide * extrp);
            // Detect end
            if (effectCounter >= EntityScenerySheet.HALF_CIRCLE)
            {
                effectCounter = 0;
                effectSide = 0;
            }
            if (effectCounter <= 0)
            {
                effectCounter = 0;
                effectSide = 0;
            }
            setLocationY(initialY - UtilMath.sin(effectCounter) * EntityScenerySheet.AMPLITUDE);
        }
    }

    /**
     * Increase the effect counter value.
     * 
     * @param value The increment value.
     */
    protected final void increaseEffectCounter(double value)
    {
        effectCounter += value;
    }

    /**
     * Reset the effect side value to its default (increase).
     */
    protected final void resetEffectSide()
    {
        effectSide = 1;
    }

    /**
     * Reset the effect counter.
     */
    protected final void resetEffectCounter()
    {
        effectCounter = 0;
    }

    /**
     * Reset the start effect flag.
     */
    protected final void resetEffectStart()
    {
        effectStart = false;
    }

    /**
     * Get the effect counter value.
     * 
     * @return The effect counter value.
     */
    protected final double getEffectCounter()
    {
        return effectCounter;
    }

    /**
     * Get the initial vertical location.
     * 
     * @return The default vertical location.
     */
    protected final int getInitialY()
    {
        return initialY;
    }

    /*
     * EntityScenery
     */

    @Override
    public void update(double extrp)
    {
        // Keep original location y
        if (initialY == Integer.MIN_VALUE)
        {
            initialY = getLocationIntY();
        }
        super.update(extrp);
        updateSheet(extrp);
    }

    @Override
    public void checkCollision(Valdyn entity)
    {
        ascendable.checkAscendBy(entity, entity.getCollisionLeg());
    }

    /*
     * AscendableUser
     */

    @Override
    public final void onAscendingBy(EntityMover entity)
    {
        if (!effectStart)
        {
            effectSide = 1;
            effectStart = true;
        }
        if (!firstHit)
        {
            firstHit = true;
            if (effectCounter > EntityScenerySheet.HALF_CIRCLE / 2)
            {
                effectSide = -1;
            }
            else
            {
                effectSide = 1;
            }
            effectStart = true;
        }
    }

    @Override
    public final void onDescended(EntityMover entity)
    {
        firstHit = false;
        if (effectStart)
        {
            // Go back to 0 as effect is lower than its half way
            if (effectCounter < EntityScenerySheet.HALF_CIRCLE / 2)
            {
                effectSide = -1;
            }
            if (effectCounter == 0)
            {
                effectStart = false;
            }
        }
    }
}
