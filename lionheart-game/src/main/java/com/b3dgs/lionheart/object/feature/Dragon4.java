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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Dragon4 feature implementation.
 * <ol>
 * <li>Fire on idle stand.</li>
 * </ol>
 */
@FeatureInterface
public final class Dragon4 extends FeatureModel implements RoutineUpdate
{
    private final Shooter shooter;
    private final Patrol patrol;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param shooter The shooter feature.
     * @param patrol The patrol feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Dragon4(Services services, Setup setup, Shooter shooter, Patrol patrol)
    {
        super(services, setup);

        this.shooter = shooter;
        this.patrol = patrol;
    }

    @Override
    public void update(double extrp)
    {
        shooter.setEnabled(Double.compare(patrol.getSh(), 0.25) == 0 && patrol.getSv() < 0.1);
    }
}
