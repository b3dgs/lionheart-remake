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
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;

/**
 * Bounce bullet on hit ground.
 */
@FeatureInterface
public final class BulletBounceOnGround extends FeatureModel implements Routine, Recyclable, TileCollidableListener
{
    private static final double BOUNCE_MAX = 3.5;

    private final Viewer viewer = services.get(Viewer.class);
    private final Tick tick = new Tick();

    private Force jump;
    private double bounceX;

    @FeatureGet private Hurtable hurtable;
    @FeatureGet private Body body;
    @FeatureGet private Launchable launchable;
    @FeatureGet private TileCollidable tileCollidable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private EntityModel model;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BulletBounceOnGround(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        if (category.getName().contains(CollisionName.LEG))
        {
            if (result.containsY(CollisionName.GROUND)
                || result.containsY(CollisionName.SLOPE)
                || result.containsY(CollisionName.INCLINE)
                || result.containsY(CollisionName.BLOCK))
            {
                final double bounce = UtilMath.clamp(transformable.getOldY() - transformable.getY(), 0.0, BOUNCE_MAX);
                if (bounce > 0.5 && viewer.isViewable(transformable, 0, 0))
                {
                    Sfx.PROJECTILE_BULLET2.play();
                }
                body.resetGravity();
                tick.restart();
                tileCollidable.apply(result);
                transformable.teleportY(transformable.getY() + 1.0);

                if (result.containsY(CollisionName.SLOPE))
                {
                    bounceX += 0.35;
                    jump.setDestination(bounceX, 0.0);
                }
                if (result.containsY(CollisionName.INCLINE))
                {
                    bounceX += 0.5;
                    jump.setDestination(bounceX, 0.0);
                }
                jump.setDirection(bounceX, bounce);
            }
        }
        else if (category.getName().startsWith(CollisionName.KNEE))
        {
            final int side;
            if (transformable.getX() > transformable.getOldX())
            {
                side = -2;
            }
            else
            {
                side = 1;
            }
            tileCollidable.apply(result);
            transformable.teleportX(transformable.getX() + 1.0 * side);

            final Force direction = launchable.getDirection();
            final double vx = direction.getDirectionHorizontal();
            direction.setDirection(-vx, direction.getDirectionVertical());
            direction.setDestination(-vx, 0.0);
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        jump = model.getJump();
        jump.setVelocity(0.1);
        jump.setSensibility(0.01);
        jump.setDestination(0.0, 0.0);
        jump.setDirection(0.0, 0.0);
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
    }

    @Override
    public void recycle()
    {
        tick.restart();
        tick.set(10);
        bounceX = 0.0;
    }
}
