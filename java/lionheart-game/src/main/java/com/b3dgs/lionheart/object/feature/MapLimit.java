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
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.networkable.Networkable;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionheart.object.EntityModel;

/**
 * Map limit feature implementation.
 * <ol>
 * <li>Clamp location on limit.
 * </ol>
 */
@FeatureInterface
public final class MapLimit extends FeatureModel implements RoutineUpdate
{
    private final MapTile map = services.get(MapTile.class);

    private final Transformable transformable;
    private final EntityModel model;
    private final Networkable networkable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @param model The model feature.
     * @param networkable The networkable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public MapLimit(Services services,
                    SetupSurfaceRastered setup,
                    Transformable transformable,
                    EntityModel model,
                    Networkable networkable)
    {
        super(services, setup);

        this.transformable = transformable;
        this.model = model;
        this.networkable = networkable;
    }

    @Override
    public void update(double extrp)
    {
        final Camera camera = model.getCamera();

        if (transformable.getX() < 2)
        {
            transformable.teleportX(2);
            model.getMovement().zero();
        }
        else if (transformable.getX() > map.getWidth() - map.getTileWidth())
        {
            transformable.teleportX(map.getWidth() - map.getTileWidth());
            model.getMovement().zero();
        }
        if (networkable.isOwner() && transformable.getX() < camera.getX())
        {
            transformable.teleportX(camera.getX());
            model.getMovement().zero();
        }
    }
}
