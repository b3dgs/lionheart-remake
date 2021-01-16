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
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionheart.Sfx;

/**
 * Canon1 feature implementation.
 * <ol>
 * <li>Fire on delay two sided bullets.</li>
 * </ol>
 */
@FeatureInterface
public final class Canon1 extends FeatureModel implements Routine, Recyclable
{
    private static final int FIRED_DELAY_TICK = 15;

    private final Tick tick = new Tick();
    private final MapTile map = services.get(MapTile.class);
    private final Animation idle;
    private final Animation fire;

    private CanonConfig config;
    private int phase;

    @FeatureGet private Animatable animatable;
    @FeatureGet private Launcher launcher;
    @FeatureGet private Rasterable rasterable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Canon1(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation("idle");
        fire = config.getAnimation("fire");
    }

    /**
     * Load configuration.
     * 
     * @param config The configuration to load.
     */
    public void load(CanonConfig config)
    {
        this.config = config;
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        if (phase == 0 && tick.elapsed(config.getFireDelay()))
        {
            phase = 1;
            animatable.play(fire);
            launcher.fire();
            Sfx.MONSTER_CANON1.play();
            tick.restart();
        }
        else if (phase == 1 && tick.elapsed(Math.min(FIRED_DELAY_TICK, config.getFireDelay())))
        {
            phase = 0;
            animatable.play(idle);
            tick.restart();
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        launcher.addListener(l ->
        {
            l.ifIs(Rasterable.class, r -> r.setRaster(true, rasterable.getMedia().get(), map.getTileHeight()));

            final Force direction = l.getDirection();
            direction.setDirection(direction.getDirectionHorizontal() * config.getVx(),
                                   direction.getDirectionVertical() * config.getVy());
        });
    }

    @Override
    public void recycle()
    {
        animatable.play(idle);
        phase = 0;
        tick.restart();
    }
}
