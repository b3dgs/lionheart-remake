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

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Featurable;
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

/**
 * Rotating feature implementation.
 * <ol>
 * <li>Turn around fixed reference.</li>
 * </ol>
 */
@FeatureInterface
public final class Rotating extends FeatureModel implements Routine, Recyclable
{
    private final Spawner spawner = services.get(Spawner.class);
    private final List<Transformable> rings = new ArrayList<>();

    private RotatingConfig config;
    private int count;
    private double angle;

    @FeatureGet private Transformable transformable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Rotating(Services services, Setup setup)
    {
        super(services, setup);
    }

    /**
     * Load configuration.
     * 
     * @param config The configuration to load.
     */
    public void load(RotatingConfig config)
    {
        rings.stream().map(r -> r.getFeature(Identifiable.class)).forEach(Identifiable::destroy);
        rings.clear();

        this.config = config;

        for (int i = 0; i < config.getLength(); i++)
        {
            rings.add(spawner.spawn(Medias.create(config.getRing()), transformable).getFeature(Transformable.class));
        }
        final Featurable platform = spawner.spawn(Medias.create(config.getExtremity()), transformable);
        rings.add(platform.getFeature(Transformable.class));
        count = rings.size();
    }

    @Override
    public void update(double extrp)
    {
        angle = UtilMath.wrapAngleDouble(angle + config.getSpeed());

        for (int i = 0; i < count; i++)
        {
            rings.get(i)
                 .setLocation(transformable.getX() + (i + 0.5) * UtilMath.cos(angle) * 16,
                              transformable.getY() + (i + 0.5) * UtilMath.sin(angle) * 16);
        }
    }

    @Override
    public void recycle()
    {
        angle = 0.0;
    }
}
