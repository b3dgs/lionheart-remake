/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.AnimatorFrameListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.AnimationConfig;
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

/**
 * Spike feature implementation.
 * <p>
 * Add support to spike movement, rising from ground with collision and return back to the ground.
 * </p>
 */
@FeatureInterface
public final class Spike extends FeatureModel implements Routine, Recyclable
{
    private static final int PHASE1_DELAY_TICK = 150;
    private static final int PHASE2_DELAY_TICK = 150;

    private final Tick tick = new Tick();
    private final Animation phase1;
    private final Animation phase2;
    private final Animation phase3;
    private int phase;

    @FeatureGet private Animatable animatable;
    @FeatureGet private Collidable collidable;

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
        phase2 = config.getAnimation("phase2");
        phase3 = config.getAnimation("phase3");
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        animatable.addListener((AnimatorFrameListener) f -> collidable.setEnabled(f > phase1.getLast()));
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        if (phase == 0 && animatable.is(AnimState.FINISHED))
        {
            tick.restart();
            phase = 1;
        }
        else if (phase == 1 && tick.elapsed(PHASE1_DELAY_TICK))
        {
            animatable.play(phase2);
            phase = 2;
        }
        else if (phase == 2 && animatable.is(AnimState.FINISHED))
        {
            tick.restart();
            phase = 3;
        }
        else if (phase == 3 && tick.elapsed(PHASE2_DELAY_TICK))
        {
            animatable.play(phase3);
            animatable.setFrame(phase3.getLast());
            phase = 4;
        }
        else if (phase == 4 && animatable.is(AnimState.FINISHED))
        {
            tick.restart();
            phase = 0;
        }
    }

    @Override
    public void recycle()
    {
        animatable.play(phase1);
        phase = 0;
    }
}
