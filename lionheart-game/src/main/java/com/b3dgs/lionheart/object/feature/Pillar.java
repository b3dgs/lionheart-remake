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
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionheart.object.XmlLoader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Pillar feature implementation.
 * <ol>
 * <li>Move on delay.</li>
 * </ol>
 */
@FeatureInterface
public final class Pillar extends FeatureModel implements XmlLoader, XmlSaver, Routine, Recyclable
{
    private static final double SPEED_MOVE = 0.5;
    private static final double SPEED_CLOSE = -2.0;
    private static final int MIN_Y = -6;
    private static final int MAX_Y = 80;

    private final Tick tick = new Tick();

    private PillarConfig config;
    private Updatable updater;
    private double y;
    private int side;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Rasterable rasterable;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Identifiable identifiable;

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
    }

    /**
     * Close pillar.
     */
    public void close()
    {
        updater = this::updateClose;
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
            rasterable.setVisibility(true);
            collidable.setEnabled(true);
            updater = this::updateMove;
        }
    }

    /**
     * Update until delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateMove(double extrp)
    {
        y += SPEED_MOVE * side;
        if (y > MAX_Y)
        {
            y = MAX_Y;
            side = -side;
        }
        else if (y < MIN_Y)
        {
            y = MIN_Y;
            side = -side;
        }
    }

    /**
     * Update until delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateClose(double extrp)
    {
        y += SPEED_CLOSE;
        if (y < MIN_Y)
        {
            identifiable.destroy();
            updater = UpdatableVoid.getInstance();
        }
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
        y = transformable.getY();
        tick.restart();
    }

    @Override
    public void load(XmlReader root)
    {
        load(new PillarConfig(root));
    }

    @Override
    public void save(Xml root)
    {
        config.save(root);
    }

    @Override
    public void update(double extrp)
    {
        updater.update(extrp);
        transformable.setLocationY(y);
    }

    @Override
    public void recycle()
    {
        config = null;
        rasterable.setVisibility(false);
        collidable.setEnabled(false);
        updater = UpdatableVoid.getInstance();
        side = 1;
    }
}
