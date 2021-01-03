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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionheart.Sfx;
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
public class Turning extends FeatureModel implements Routine, Recyclable
{
    /** Max shake amplitude in height. */
    private static final double CURVE_FORCE = 2.0;
    /** Shake curve speed value. */
    private static final double CURVE_SPEED = 50.0;
    /** Tick delay before starting rotation. */
    private static final int DELAY_BEFORE_ROTATE = 50;
    /** Tick delay before starting to shake. */
    private static final int DELAY_BEFORE_SHAKE = 100;
    /** Total number of shakes in shaking state. */
    private static final int SHAKE_MAX_COUNT = 3;

    /** Shake and rotate tick. */
    private final Tick tick = new Tick();
    /** Current turning check. */
    private Updatable check;
    /** Current shake curve value. */
    private double curve;

    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Glue glue;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Turning(Services services, Setup setup)
    {
        super(services, setup);
    }

    /**
     * Start as idle.
     */
    protected void startIdle()
    {
        check = this::checkShake;
    }

    /**
     * Start as turn.
     */
    protected void startTurn()
    {
        check = this::checkRotate;
        tick.set(DELAY_BEFORE_ROTATE);
    }

    /**
     * Reset internal tick.
     */
    protected void resetTick()
    {
        tick.restart();
    }

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
            Sfx.SCENERY_TURNING.play();
            check = this::updateShake;
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
            check = this::checkRotate;
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
            check = this::checkShake;
            stateHandler.changeState(StateTurn.class);
            glue.stop();
            glue.setGlue(false);
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

    @Override
    public void update(double extrp)
    {
        check.update(extrp);
    }

    @Override
    public void recycle()
    {
        startIdle();
        curve = 0.0;
        tick.restart();
    }
}
