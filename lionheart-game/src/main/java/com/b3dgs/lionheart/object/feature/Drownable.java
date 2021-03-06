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
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionheart.MapTileWater;
import com.b3dgs.lionheart.object.state.StateDrowned;

/**
 * Drownable feature implementation.
 * <ol>
 * <li>Check if current position is under drown start.</li>
 * <li>Trigger {@link StateDrowned}.</li>
 * </ol>
 */
@FeatureInterface
public final class Drownable extends FeatureModel implements Routine, Recyclable
{
    private final MapTileWater water = services.get(MapTileWater.class);
    /** Current drown check. */
    private Updatable check;

    @FeatureGet private Transformable transformable;
    @FeatureGet private StateHandler stateHandler;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Drownable(Services services, Setup setup)
    {
        super(services, setup);
    }

    /**
     * Check start drown.
     * 
     * @param extrp The extrapolation value.
     */
    private void checkStart(double extrp)
    {
        if (transformable.getY() < water.getCurrent() - transformable.getHeight() + 8)
        {
            stateHandler.changeState(StateDrowned.class);
            check = UpdatableVoid.getInstance();
        }
    }

    @Override
    public void update(double extrp)
    {
        check.update(extrp);
    }

    @Override
    public void recycle()
    {
        check = this::checkStart;
    }
}
