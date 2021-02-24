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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionheart.constant.Anim;

/**
 * Pillar feature implementation.
 * <ol>
 * <li>Move on delay.</li>
 * </ol>
 */
@FeatureInterface
public final class Pillar extends FeatureModel implements Routine, Recyclable
{
    private final Tick tick = new Tick();
    private final Animation idle;

    private PillarConfig config;
    private Updatable updater;

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
    public Pillar(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation(Anim.IDLE);
    }

    /**
     * Load configuration.
     * 
     * @param config The configuration to load.
     */
    public void load(PillarConfig config)
    {
        this.config = config;
        updater = this::updateDelay;
        tick.restart();
    }

    /**
     * Update until delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateDelay(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsed(config.getDelay()))
        {
            animatable.play(idle);
            animatable.setFrame(idle.getLast());
            animatable.setAnimSpeed(-animatable.getAnimSpeed());
            rasterable.setVisibility(true);
            collidable.setEnabled(true);
            updater = UpdatableVoid.getInstance();
        }
    }

    @Override
    public void update(double extrp)
    {
        updater.update(extrp);
    }

    @Override
    public void recycle()
    {
        config = null;
        rasterable.setVisibility(false);
        collidable.setEnabled(false);
        animatable.setFrame(idle.getLast());
        updater = UpdatableVoid.getInstance();
    }
}
