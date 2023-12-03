/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
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
    private static final double CURVE_FORCE = 2.2;
    /** Shake curve speed value. */
    private static final double CURVE_SPEED = 60.0;
    /** Delay before starting rotation. */
    private static final int BEFORE_ROTATE_DELAY_MS = 800;
    /** Delay before starting to shake. */
    private static final int BEFORE_SHAKE_DELAY_MS = 1500;
    /** Total number of shakes in shaking state. */
    private static final int SHAKE_MAX_COUNT = 3;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Camera camera = services.get(Camera.class);

    private final StateHandler stateHandler;
    private final Animatable animatable;
    private final Transformable transformable;
    private final Glue glue;

    private final Tick tick = new Tick();

    private Updatable check;
    private double curve;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param stateHandler The state feature.
     * @param animatable The animatable feature.
     * @param transformable The transformable feature.
     * @param glue The glue feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Turning(Services services,
                   Setup setup,
                   StateHandler stateHandler,
                   Animatable animatable,
                   Transformable transformable,
                   Glue glue)
    {
        super(services, setup);

        this.stateHandler = stateHandler;
        this.animatable = animatable;
        this.transformable = transformable;
        this.glue = glue;
    }

    /**
     * Start as idle.
     */
    protected void startIdle()
    {
        check = this::checkShake;
        tick.start();
    }

    /**
     * Start as turn.
     */
    protected void startTurn()
    {
        check = this::checkRotate;
        tick.set(BEFORE_ROTATE_DELAY_MS);
    }

    /**
     * Reset internal delay.
     */
    protected void resetDelay()
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
        if (animatable.is(AnimState.FINISHED) && tick.elapsedTime(source.getRate(), BEFORE_SHAKE_DELAY_MS))
        {
            glue.start();
            glue.setTransformY(this::computeCurve);
            tick.stop();
            if (camera.isViewable(transformable, 0, 0))
            {
                Sfx.SCENERY_TURNING.play();
            }
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
        curve += CURVE_SPEED * extrp;
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
        if (tick.elapsedTime(source.getRate(), BEFORE_ROTATE_DELAY_MS))
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
        tick.stop();
        curve = 0.0;
        startIdle();
    }
}
