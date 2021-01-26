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
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionheart.constant.Anim;

/**
 * Canon3 feature implementation.
 * <ol>
 * <li>Fire on delay bullet target player.</li>
 * </ol>
 */
@FeatureInterface
public final class Canon3 extends FeatureModel implements Routine, Recyclable
{
    private final Tick tick = new Tick();
    private final MapTile map = services.get(MapTile.class);
    private final Transformable player = services.get(SwordShade.class).getFeature(Transformable.class);
    private final Animation attack;

    private CanonConfig config;

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
    public Canon3(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        attack = config.getAnimation(Anim.ATTACK);
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
        if (config != null && tick.elapsed(config.getFireDelay()) && animatable.getFrameAnim() == attack.getFirst())
        {
            animatable.play(attack);
            launcher.fire(player);
            tick.restart();
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        launcher.addListener(l -> l.ifIs(Rasterable.class,
                                         r -> r.setRaster(true, rasterable.getMedia().get(), map.getTileHeight())));
    }

    @Override
    public void recycle()
    {
        tick.restart();
    }
}
