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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routine;
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
public final class MapLimit extends FeatureModel implements Routine
{
    private final MapTile map = services.get(MapTile.class);
    private final Viewer viewer = services.get(Viewer.class);

    @FeatureGet private Transformable transformable;
    @FeatureGet private EntityModel model;
    @FeatureGet private Networkable networkable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public MapLimit(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);
    }

    @Override
    public void update(double extrp)
    {
        if (networkable.isOwner())
        {
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
            if (transformable.getX() < viewer.getX())
            {
                transformable.teleportX(viewer.getX());
                model.getMovement().zero();
            }
        }
    }
}
