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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.io.DeviceControllerVoid;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;

/**
 * Guard feature implementation.
 * <p>
 * Attack on close distance, salto on far distance.
 * </p>
 */
@FeatureInterface
public final class Guard extends FeatureModel implements TileCollidableListener
{
    /** Max attack distance. */
    public static final double ATTACK_DISTANCE_MAX = 56.0;

    private final Transformable player = services.get(SwordShade.class).getFeature(Transformable.class);

    private double sh;
    private double sv;

    @FeatureGet private EntityModel model;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Rasterable rasterable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Guard(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);
    }

    /**
     * Apply mirror.
     */
    public void applyMirror()
    {
        if (mirrorable.is(Mirror.NONE) && player.getX() > transformable.getX())
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
            rasterable.setFrameOffsets(-22, 0);
        }
        else if (mirrorable.is(Mirror.HORIZONTAL) && player.getX() < transformable.getX())
        {
            mirrorable.mirror(Mirror.NONE);
            rasterable.setFrameOffsets(0, 0);
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        model.setInput(new DeviceControllerVoid()
        {
            @Override
            public double getHorizontalDirection()
            {
                return sh;
            }

            @Override
            public double getVerticalDirection()
            {
                return sv;
            }
        });
        model.getMovement().setVelocity(1.0);
        mirrorable.update(1.0);
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        if (category.getAxis() == Axis.X && result.startWithX(CollisionName.STEEP))
        {
            sh = -sh;
            transformable.teleportX(transformable.getOldX() + sh);
        }
    }
}
