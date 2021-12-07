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
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.WorldType;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.object.Editable;
import com.b3dgs.lionheart.object.XmlLoader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Laser Airship feature implementation.
 * <ol>
 * <li>Fire on delay.</li>
 * </ol>
 */
@FeatureInterface
public final class LaserAirship extends FeatureModel
                                implements XmlSaver, XmlLoader, Editable<LaserAirshipConfig>, Routine, Recyclable
{
    private static final int PREPARE_DELAY_MS = 650;
    private static final double DOT_SPEED = 6.0;
    private static final int DOT_HEIGHT = -88;
    private static final int DOT_HIDE = -100;
    private static final String LASER_DOT_FILE = "LaserDot.xml";

    private final Tick tick = new Tick();

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Spawner spawner = services.get(Spawner.class);

    private LaserAirshipConfig config;
    private Updatable current;
    private Transformable dotStart;
    private Transformable dotEnd;
    private Identifiable laser;
    private double dotEndY;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Stats stats;
    @FeatureGet private Launcher launcher;
    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private Identifiable identifiable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public LaserAirship(Services services, Setup setup)
    {
        super(services, setup);

        load(setup.getRoot());
    }

    /**
     * Update fire delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updatePrepare(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), config.getFireDelay()))
        {
            dotStart.teleport(transformable.getX(), transformable.getY());
            dotEnd.teleport(transformable.getX(), transformable.getY());
            current = this::updateFire;
            tick.restart();
        }
        else
        {
            dotStart.teleport(transformable.getX(), DOT_HIDE);
            dotEnd.teleport(transformable.getX(), DOT_HIDE);
        }
    }

    /**
     * Update fire delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFire(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), PREPARE_DELAY_MS))
        {
            current = this::updateFired;
            tick.restart();
        }
    }

    /**
     * Update fire delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFired(double extrp)
    {
        dotEndY -= DOT_SPEED;
        if (dotEndY < DOT_HEIGHT)
        {
            launcher.fire();
            if (config.getStayDelay() < 0)
            {
                tick.stop();
            }
            else
            {
                tick.restart();
            }
            dotEndY = 0.0;
            current = this::updatePrepare;
        }
        dotStart.teleport(transformable.getX(), transformable.getY());
        dotEnd.teleport(transformable.getX(), transformable.getY() + dotEndY);
    }

    @Override
    public LaserAirshipConfig getConfig()
    {
        return config;
    }

    @Override
    public void setConfig(LaserAirshipConfig config)
    {
        this.config = config;
    }

    @Override
    public void load(XmlReader root)
    {
        if (root.hasNode(LaserAirshipConfig.NODE_LASER))
        {
            config = new LaserAirshipConfig(root);
        }
    }

    @Override
    public void save(Xml root)
    {
        config.save(root);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        if (!Settings.isEditor())
        {
            identifiable.addListener(id ->
            {
                dotStart.getFeature(Identifiable.class).destroy();
                dotEnd.getFeature(Identifiable.class).destroy();
                if (laser != null)
                {
                    laser.destroy();
                }
            });
            launcher.addListener(l ->
            {
                if (laser != null)
                {
                    laser.destroy();
                    laser = l.getFeature(Identifiable.class);
                }
                l.getFeature(Laser.class).load(config.getStayDelay(), identifiable);
            });
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
        if (laser != null)
        {
            laser.destroy();
            laser = null;
        }
        if (!Settings.isEditor())
        {
            dotStart = spawner.spawn(Medias.create(Folder.EFFECT, WorldType.AIRSHIP.getFolder(), LASER_DOT_FILE),
                                     transformable.getX(),
                                     transformable.getY())
                              .getFeature(Transformable.class);

            dotEnd = spawner.spawn(Medias.create(Folder.EFFECT, WorldType.AIRSHIP.getFolder(), LASER_DOT_FILE),
                                   transformable.getX(),
                                   transformable.getY())
                            .getFeature(Transformable.class);

            dotStart.teleport(transformable.getX(), DOT_HIDE);
            dotEnd.teleport(transformable.getX(), DOT_HIDE);
        }
        current = this::updatePrepare;
        dotEndY = 0.0;
        tick.restart();
    }
}
