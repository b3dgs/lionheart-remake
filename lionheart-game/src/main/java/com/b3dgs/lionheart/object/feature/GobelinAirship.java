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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.constant.Anim;

/**
 * Gobelin Airship feature implementation.
 * <ol>
 * <li>Attack on delay.</li>
 * </ol>
 */
@FeatureInterface
public final class GobelinAirship extends FeatureModel implements Routine, Recyclable
{
    private static final int ATTACK_DELAY_MS = 1300;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);

    private final Animatable animatable;
    private final Launcher launcher;

    private final Tick tick = new Tick();
    private final Animation idle;
    private final Animation attack;

    private int phase;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param animatable The animatable feature.
     * @param launcher The launcher feature.
     * @throws LionEngineException If invalid arguments.
     */
    public GobelinAirship(Services services, Setup setup, Animatable animatable, Launcher launcher)
    {
        super(services, setup);

        this.animatable = animatable;
        this.launcher = launcher;

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation(Anim.IDLE);
        attack = config.getAnimation(Anim.ATTACK);
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);

        if (phase == 0 && tick.elapsedTime(source.getRate(), ATTACK_DELAY_MS))
        {
            animatable.play(attack);
            phase = 1;
        }
        else if (phase == 1 && animatable.is(AnimState.FINISHED))
        {
            launcher.fire();
            animatable.play(idle);
            tick.restart();
            phase = 0;
        }
    }

    @Override
    public void recycle()
    {
        animatable.play(idle);
        phase = 0;
        tick.restart();
    }
}
