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
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.object.Configurable;

/**
 * Spike feature implementation.
 * <p>
 * Add support to spike movement, rising from ground with collision and return back to the ground.
 * </p>
 */
@FeatureInterface
public final class Spike extends FeatureModel implements Configurable, Routine, Recyclable
{
    private static final int PHASE1_DELAY_TICK = 28;
    private static final int PHASE2_DELAY_TICK = 28;
    private static final int PHASE3_DELAY_TICK = 5;

    private final Tick tick = new Tick();
    private final Tick delay = new Tick();
    private final Viewer viewer = services.get(Viewer.class);
    private final Updatable phaseUpdater;
    private final Animation phase1;
    private final Animation phase2;
    private final Animation phase3;
    private int phase;
    private Updatable checker;

    @FeatureGet private Animatable animatable;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Transformable transformable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Spike(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        phase1 = config.getAnimation("phase1");
        phase2 = config.getAnimation("attack");
        phase3 = config.getAnimation("phase3");

        phaseUpdater = extrp ->
        {
            tick.update(extrp);
            if (phase == 0 && tick.elapsed(PHASE1_DELAY_TICK))
            {
                tick.restart();
                animatable.play(phase1);
                phase = 1;
            }
            else if (phase == 1 && animatable.is(AnimState.FINISHED))
            {
                tick.restart();
                phase = 2;
            }
            else if (phase == 2 && tick.elapsed(PHASE2_DELAY_TICK))
            {
                animatable.play(phase2);
                if (viewer.isViewable(transformable, 0, 0))
                {
                    Sfx.SCENERY_SPIKE.play();
                }
                phase = 3;
            }
            else if (phase == 3 && animatable.is(AnimState.FINISHED))
            {
                tick.restart();
                phase = 4;
            }
            else if (phase == 4 && tick.elapsed(PHASE3_DELAY_TICK))
            {
                animatable.play(phase3);
                animatable.setFrame(phase3.getLast());
                phase = 5;
            }
            else if (phase == 5 && animatable.is(AnimState.FINISHED))
            {
                tick.restart();
                phase = 0;
            }
        };
        checker = phaseUpdater;
    }

    /**
     * Load spike configuration.
     * 
     * @param config The configuration reference.
     */
    public void load(SpikeConfig config)
    {
        config.getDelay().ifPresent(delayTick ->
        {
            checker = extrp ->
            {
                delay.update(extrp);
                if (delay.elapsed(delayTick))
                {
                    checker = phaseUpdater;
                }
            };
        });
    }

    @Override
    public void load(XmlReader root)
    {
        root.getChildOptional(SpikeConfig.NODE_SPIKE).map(SpikeConfig::imports).ifPresent(this::load);
    }

    @Override
    public void update(double extrp)
    {
        checker.update(extrp);
    }

    @Override
    public void recycle()
    {
        animatable.setFrame(phase1.getFirst());
        tick.restart();
        delay.restart();
        phase = 0;
    }
}
