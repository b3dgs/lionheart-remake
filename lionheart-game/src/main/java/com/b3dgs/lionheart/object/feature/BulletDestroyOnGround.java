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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;

/**
 * Destroy bullet on hit ground.
 */
@FeatureInterface
public final class BulletDestroyOnGround extends FeatureModel implements Recyclable, TileCollidableListener
{
    private static final String NODE = "bulletDestroyOnGround";
    private static final String ATT_COLLFROMBOTTOM = "collFromBottom";

    private final boolean collFromBottom = setup.getBoolean(true, ATT_COLLFROMBOTTOM, NODE);
    private final Animation idle;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private TileCollidable tileCollidable;
    @FeatureGet private Hurtable hurtable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BulletDestroyOnGround(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation(Anim.IDLE);
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        if ((category.getName().contains(CollisionName.KNEE) || category.getName().contains(CollisionName.LEG))
            && (result.containsY(CollisionName.GROUND)
                || result.containsY(CollisionName.SLOPE)
                || result.containsY(CollisionName.INCLINE)
                || result.containsY(CollisionName.BLOCK)
                || result.containsY(CollisionName.VERTICAL)
                || result.containsY(CollisionName.HORIZONTAL)
                || result.containsX(CollisionName.VERTICAL))
            && (collFromBottom || transformable.getY() < transformable.getOldY()))
        {
            tileCollidable.apply(result);
            hurtable.kill(true);
        }
    }

    @Override
    public void recycle()
    {
        animatable.play(idle);
    }
}
