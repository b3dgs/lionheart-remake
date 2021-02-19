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
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
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

/**
 * Shooter feature implementation.
 * <ol>
 * <li>Fire on delay.</li>
 * </ol>
 */
@FeatureInterface
public final class Shooter extends FeatureModel implements Routine, Recyclable
{
    private final Tick tick = new Tick();

    private ShooterConfig config;
    private Updatable updater;
    private boolean enabled;

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
    public Shooter(Services services, Setup setup)
    {
        super(services, setup);
    }

    /**
     * Load configuration.
     * 
     * @param config The configuration to load.
     */
    public void load(ShooterConfig config)
    {
        this.config = config;
        updater = this::updatePrepare;
        tick.restart();
    }

    /**
     * Set fire enabled flag.
     * 
     * @param enabled <code>true</code> if enabled, <code>false</code> else.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Update prepare fire, fire on delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updatePrepare(double extrp)
    {
        tick.update(extrp);
        if (enabled && tick.elapsed(config.getFireDelay()))
        {
            updater = this::updateFired;
            launcher.fire();
            tick.restart();
        }
    }

    /**
     * Update after fired, delay before prepare.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFired(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsed(config.getFireDelay()))
        {
            updater = this::updatePrepare;
            tick.restart();
        }
    }

    @Override
    public void update(double extrp)
    {
        updater.update(extrp);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        final MapTile map = services.get(MapTile.class);
        launcher.addListener(l ->
        {
            rasterable.getMedia()
                      .ifPresent(media -> l.ifIs(Rasterable.class, r -> r.setRaster(true, media, map.getTileHeight())));

            if (config != null)
            {
                final Force direction = l.getDirection();
                direction.setDirection(direction.getDirectionHorizontal() * config.getSvx(),
                                       direction.getDirectionVertical() * config.getSvy());

                direction.setDestination(direction.getDirectionHorizontal() * config.getDvx(),
                                         direction.getDirectionVertical() * config.getDvy());
            }
        });
    }

    @Override
    public void recycle()
    {
        config = null;
        updater = UpdatableVoid.getInstance();
        enabled = true;
    }
}
