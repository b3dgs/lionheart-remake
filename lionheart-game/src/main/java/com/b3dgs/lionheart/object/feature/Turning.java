/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.state.State;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.Entity;
import com.b3dgs.lionheart.object.Routine;
import com.b3dgs.lionheart.object.state.StateTurn;

/**
 * Turning feature implementation.
 * <ol>
 * <li>Wait for a delay before start shaking.</li>
 * <li>Shake a defined number of times.</li>
 * <li>Wait for a delay before start rotating.</li>
 * <li>Rotate one time with disabled collision.</li>
 * <li>Once rotated, enable collision and go to step 1.</li>
 * </ol>
 */
@FeatureInterface
public final class Turning extends FeatureModel implements Routine, Recyclable
{
    private static final double CURVE_FORCE = 4.0;
    private static final double CURVE_SPEED = 50.0;
    private static final int DELAY_BEFORE_ROTATE = 50;
    private static final int DELAY_BEFORE_SHAKE = 100;
    private static final int SHAKE_MAX_COUNT = 3;

    private final Tick tick = new Tick();
    private Updatable current;
    private double curve;

    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Glue glue;

    /**
     * Check delay before start shaking.
     * 
     * @param extrp The extrapolation value.
     */
    private void checkShake(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsed(DELAY_BEFORE_SHAKE) && animatable.is(AnimState.FINISHED))
        {
            glue.start();
            glue.setTransformY(this::computeCurve);
            tick.stop();
            current = this::updateShake;
        }
    }

    /**
     * Update shake effect once shake started.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateShake(double extrp)
    {
        curve += CURVE_SPEED;
        if (curve > SHAKE_MAX_COUNT * com.b3dgs.lionengine.Constant.MAX_DEGREE)
        {
            current = this::checkRotate;
            curve = 0.0;
            tick.start();
        }
    }

    /**
     * Check delay before rotate once shake ended.
     * 
     * @param extrp The extrapolation value.
     */
    private void checkRotate(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsed(DELAY_BEFORE_ROTATE))
        {
            current = this::checkShake;
            stateHandler.changeState(StateTurn.class);
            glue.stop();
            glue.setGlue(false);
            collidable.setEnabled(false);
            tick.restart();
        }
    }

    /**
     * Compute curve value with current force.
     * 
     * @return The current computed curve value.
     */
    private double computeCurve()
    {
        return UtilMath.sin(curve) * CURVE_FORCE;
    }

    /**
     * Check if enable collide depending of next state. Disabled if turning.
     * 
     * @param from The state from.
     * @param to The next state.
     */
    private void checkCollideEnabled(Class<? extends State> from, Class<? extends State> to)
    {
        collidable.setEnabled(!Constant.ANIM_NAME_TURN.equals(Entity.getAnimationName(to)));
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        stateHandler.addListener(this::checkCollideEnabled);
    }

    @Override
    public void update(double extrp)
    {
        current.update(extrp);
    }

    @Override
    public void recycle()
    {
        current = this::checkShake;
        tick.restart();
        curve = 0.0;
    }
}
