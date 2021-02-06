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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
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

/**
 * Canon2 feature implementation.
 * <ol>
 * <li>Fire on delay bullet bounce.</li>
 * </ol>
 */
@FeatureInterface
public final class Canon2 extends FeatureModel implements Routine, Recyclable
{
    private final Tick tick = new Tick();
    private final MapTile map = services.get(MapTile.class);

    private CanonConfig config;

    @FeatureGet private Launcher launcher;
    @FeatureGet private Rasterable rasterable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Canon2(Services services, Setup setup)
    {
        super(services, setup);
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
        if (config != null)
        {
            tick.update(extrp);
            if (tick.elapsed(config.getFireDelay()))
            {
                launcher.fire();
                tick.restart();
            }
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        launcher.addListener(l ->
        {
            l.ifIs(Rasterable.class,
                   r -> rasterable.getMedia().ifPresent(m -> r.setRaster(true, m, map.getTileHeight())));

            final Force direction = l.getDirection();
            final double vx = direction.getDirectionHorizontal() * config.getVx();
            direction.setDirection(vx, direction.getDirectionVertical() * config.getVy());
            direction.setDestination(vx, 0.0);
        });
    }

    @Override
    public void recycle()
    {
        tick.restart();
    }
}
