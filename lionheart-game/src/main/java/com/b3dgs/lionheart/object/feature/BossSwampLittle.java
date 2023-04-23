/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;

/**
 * Boss Swamp 2 little feature implementation.
 * <ol>
 * <li>Walk in player direction and continues straight on.</li>
 * </ol>
 */
@FeatureInterface
public final class BossSwampLittle extends FeatureModel implements Routine, Recyclable, TileCollidableListener
{
    private static final double SPEED_X = 1.2;

    private final Trackable target = services.get(Trackable.class);
    private final Animation walk;

    private double sh;
    private boolean init;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Collidable collidable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BossSwampLittle(Services services, Setup setup)
    {
        super(services, setup);

        walk = AnimationConfig.imports(setup).getAnimation(Anim.WALK);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        collidable.setCollisionVisibility(Constant.DEBUG_COLLISIONS);
    }

    @Override
    public void update(double extrp)
    {
        if (init)
        {
            sh = transformable.getX() > target.getX() ? -SPEED_X : SPEED_X;
            if (sh > 0)
            {
                mirrorable.mirror(Mirror.HORIZONTAL);
            }
            init = false;
        }
        transformable.moveLocationX(extrp, sh);
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        if (result.startWithX(CollisionName.STEEP)
            && !result.endWithY(CollisionName.GROUND)
            && category.getAxis() == Axis.X
            && (result.containsX(CollisionName.LEFT) && sh > 0 || result.containsX(CollisionName.RIGHT) && sh < 0))
        {
            transformable.teleportX(transformable.getX() - sh * 5);
            if (sh < 0)
            {
                mirrorable.mirror(Mirror.HORIZONTAL);
            }
            else
            {
                mirrorable.mirror(Mirror.NONE);
            }
            sh = -sh;
        }
    }

    @Override
    public void recycle()
    {
        init = true;
        sh = 0.0;
        animatable.play(walk);
    }
}
