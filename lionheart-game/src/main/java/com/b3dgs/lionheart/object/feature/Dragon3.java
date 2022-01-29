/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.object.EntityModel;

/**
 * Dragon3 feature implementation.
 * <ol>
 * <li>Fire on delay.</li>
 * <li>Follow player a moment.</li>
 * </ol>
 */
@FeatureInterface
public final class Dragon3 extends FeatureModel implements Routine, Recyclable
{
    private static final int FIRED_DELAY_MS = 1300;

    private final Tick tick = new Tick();
    private final Animation idle;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);

    private Updatable current;

    @FeatureGet private Animatable animatable;
    @FeatureGet private Launcher launcher;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Identifiable identifiable;
    @FeatureGet private EntityModel model;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Dragon3(Services services, Setup setup)
    {
        super(services, setup);

        idle = AnimationConfig.imports(setup).getAnimation(Anim.IDLE);
    }

    /**
     * Update fire phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFire(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), FIRED_DELAY_MS))
        {
            launcher.fire();
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
        current = this::updateFire;
        animatable.play(idle);
        tick.restart();
    }
}
