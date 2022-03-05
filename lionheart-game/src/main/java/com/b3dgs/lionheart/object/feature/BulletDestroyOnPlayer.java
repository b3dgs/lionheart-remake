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

import java.io.IOException;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;
import com.b3dgs.lionheart.object.Snapshotable;

/**
 * Destroy bullet on collide with player.
 */
@FeatureInterface
public final class BulletDestroyOnPlayer extends FeatureModel implements Snapshotable
{
    @FeatureGet private Launchable launchable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BulletDestroyOnPlayer(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    public void save(FileWriting file) throws IOException
    {
        final Force force = launchable.getDirection();
        file.writeDouble(force.getDirectionHorizontal());
        file.writeDouble(force.getDirectionVertical());
        file.writeDouble(force.getVelocity());
        file.writeDouble(force.getSensibility());
    }

    @Override
    public void load(FileReading file) throws IOException
    {
        final Force force = new Force(file.readDouble(), file.readDouble(), file.readDouble(), file.readDouble());
        force.setDestination(force.getDirectionHorizontal(), force.getDirectionVertical());
        launchable.setVector(force);
    }
}
