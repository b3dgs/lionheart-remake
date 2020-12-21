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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionheart.constant.Anim;

/**
 * Boss Swamp 1 bowl feature implementation.
 * <ol>
 * <li>Move horizontally.</li>
 * </ol>
 */
@FeatureInterface
public final class BossSwampBowl extends FeatureModel implements Routine, Recyclable, CollidableListener
{
    private static final int PALLET_OFFSET = 2;
    private static final int HIT_TICK_DELAY = 1;

    private final Tick hitTick = new Tick();

    private boolean hit;
    private double effect;
    private int frame;

    @FeatureGet private Animatable animatable;
    @FeatureGet private Rasterable rasterable;
    @FeatureGet private Collidable collidable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BossSwampBowl(Services services, Setup setup)
    {
        super(services, setup);
    }

    /**
     * Get the effect value.
     * 
     * @return The effect value.
     */
    public double getEffect()
    {
        return effect;
    }

    /**
     * Check if hit.
     * 
     * @return <code>true</code> if hit, <code>false</code> else.
     */
    public boolean isHit()
    {
        return hit;
    }

    /**
     * Set hit.
     */
    public void hit()
    {
        hit = true;
        hitTick.start();
    }

    /**
     * Set the frame offset.
     * 
     * @param offset The frame offset.
     */
    public void setFrameOffset(int offset)
    {
        rasterable.setAnimOffset(UtilMath.clamp(offset, 0, 2) * PALLET_OFFSET);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        collidable.setCollisionVisibility(com.b3dgs.lionheart.Constant.DEBUG);
    }

    @Override
    public void update(double extrp)
    {
        effect = UtilMath.wrapDouble(effect + 0.08, 0, Constant.ANGLE_MAX);

        hitTick.update(extrp);
        if (hitTick.elapsed(HIT_TICK_DELAY))
        {
            frame = UtilMath.wrap(frame + 1, 1, 3);
            animatable.setFrame(frame);
            hitTick.restart();
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (with.getName().startsWith(Anim.BODY) && by.getName().startsWith(Anim.ATTACK))
        {
            hit();
        }
    }

    @Override
    public void recycle()
    {
        hit = false;
        effect = 0.0;
        frame = 1;
        hitTick.stop();
        animatable.setFrame(frame);
    }
}
