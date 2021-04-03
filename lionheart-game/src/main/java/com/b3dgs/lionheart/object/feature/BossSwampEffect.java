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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * Boss Swamp 1 effect.
 */
@FeatureInterface
public final class BossSwampEffect extends FeatureModel implements Recyclable
{
    /** Fly effect speed. */
    static final double EFFECT_SPEED = 0.2;
    /** Fly effect margin. */
    static final int EFFECT_MARGIN = 5;

    private final Tick effectTickX = new Tick();
    private final Tick effectTickY = new Tick();

    private double effectX;
    private double effectY;

    @FeatureGet private Transformable transformable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BossSwampEffect(Services services, Setup setup)
    {
        super(services, setup);
    }

    /**
     * Set the horizontal effect.
     * 
     * @param effectX The horizontal effect.
     */
    public void setEffectX(double effectX)
    {
        this.effectX = effectX;
    }

    /**
     * Set the vertical effect.
     * 
     * @param effectY The vertical effect.
     */
    public void setEffectY(double effectY)
    {
        this.effectY = effectY;
    }

    /**
     * Update effect.
     * 
     * @param extrp The extrapolation value.
     */
    public void update(double extrp)
    {
        effectTickX.update(extrp);
        effectTickY.update(extrp);

        if (effectTickX.elapsed(10L) && UtilRandom.getRandomInteger(100) == 0)
        {
            effectX = -effectX;
            effectTickX.restart();
        }
        if (effectTickY.elapsed(10L) && UtilRandom.getRandomInteger(100) == 0)
        {
            effectY = -effectY;
            effectTickY.restart();
        }

        transformable.moveLocation(extrp, effectX, effectY);
    }

    @Override
    public void recycle()
    {
        effectX = EFFECT_SPEED;
        effectY = EFFECT_SPEED;
        effectTickX.restart();
        effectTickY.restart();
    }
}
