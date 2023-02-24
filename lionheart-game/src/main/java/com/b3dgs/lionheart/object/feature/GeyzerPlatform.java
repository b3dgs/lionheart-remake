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
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;

/**
 * Geyzer Platform feature implementation.
 * <p>
 * Follow water level and move down on collide.
 * </p>
 */
@FeatureInterface
public final class GeyzerPlatform extends FeatureModel implements Routine, CollidableListener
{
    private final Viewer viewer = services.get(Viewer.class);

    private boolean collide;
    private boolean played;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Body body;
    @FeatureGet private EntityModel model;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public GeyzerPlatform(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        model.getMovement().setVelocity(0.025);
    }

    @Override
    public void update(double extrp)
    {
        if (!played && !collide && transformable.getY() > transformable.getOldY())
        {
            if (viewer.isViewable(transformable, 0, 0))
            {
                Sfx.SCENERY_GEYZERPLATFORM.play();
            }
            played = true;
        }
        collide = false;
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (collidable.hasFeature(Geyzer.class) && with.getName().startsWith(CollisionName.BLOCK))
        {
            final Transformable other = collidable.getFeature(Transformable.class);
            if (Double.compare(other.getY(), other.getOldY()) == 0)
            {
                transformable.teleportY(other.getY() + other.getHeight());
                transformable.check(true);
                model.getMovement().setDirection(0.0, 0.0);
            }
            else
            {
                transformable.setLocationY(other.getY() + other.getHeight());
                model.getMovement().setDirection(0.0, 3.0);
            }
            body.resetGravity();
            collide = true;
            played = false;
        }
    }
}
