/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.object.state.StateDecay;

/**
 * Canon Airship feature implementation.
 * <ol>
 * <li>Point player.</li>
 * <li>Throw projectile on delay.</li>
 * </ol>
 */
@FeatureInterface
public final class CanonAirship extends FeatureModel implements RoutineUpdate, Recyclable
{
    private static final int FIRE_DELAY_MS = 5000;
    private static final double FIRE_SPEED = 1.5;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Trackable target = services.get(Trackable.class);

    private final Transformable transformable;
    private final Animatable animatable;
    private final Stats stats;
    private final Launcher launcher;
    private final StateHandler stateHandler;
    private final Hurtable hurtable;

    private final Tick tick = new Tick();
    private final Force direction = new Force();
    private final int halfFrames;

    private Updatable current;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @param animatable The animatable feature.
     * @param stats The stats feature.
     * @param launcher The launcher feature.
     * @param stateHandler The state feature.
     * @param hurtable The hurtable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public CanonAirship(Services services,
                        Setup setup,
                        Transformable transformable,
                        Animatable animatable,
                        Stats stats,
                        Launcher launcher,
                        StateHandler stateHandler,
                        Hurtable hurtable)
    {
        super(services, setup);

        this.transformable = transformable;
        this.animatable = animatable;
        this.stats = stats;
        this.launcher = launcher;
        this.stateHandler = stateHandler;
        this.hurtable = hurtable;

        final AnimationConfig config = AnimationConfig.imports(setup);
        halfFrames = config.getAnimation(Anim.IDLE).getFrames() / 2;
    }

    /**
     * Update on alive.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateAlive(double extrp)
    {
        if (stats.getHealth() > 0)
        {
            updateFrame();
            updateFire(extrp);
        }
        else
        {
            stateHandler.changeState(StateDecay.class);
            hurtable.kill(true);
            current = UpdatableVoid.getInstance();
        }
    }

    /**
     * Update frame depending of target location.
     */
    private void updateFrame()
    {
        final int margin = (int) Math.round(target.getX() - target.getWidth() / 4 - transformable.getX()) / 16;
        final int frame = UtilMath.clamp(margin, -halfFrames, halfFrames - 1) + halfFrames + 1;
        animatable.setFrame(frame);
        launcher.setOffset(frame * halfFrames - 17, launcher.getOffsetY());
        direction.setDirection((frame - halfFrames - 0.5) / 2.5 * FIRE_SPEED, -1.0 * FIRE_SPEED);
        direction.setDestination((frame - halfFrames - 0.5) / 2.5 * FIRE_SPEED, -1.0 * FIRE_SPEED);
    }

    /**
     * Update fire delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFire(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), FIRE_DELAY_MS))
        {
            launcher.fire(direction);
            tick.restart();
        }
    }

    @Override
    public void update(double extrp)
    {
        current.update(extrp);
    }

    @Override
    public void recycle()
    {
        current = this::updateAlive;
        tick.restart();
        tick.set(FIRE_DELAY_MS / 2);
    }
}
