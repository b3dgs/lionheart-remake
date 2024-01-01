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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;

/**
 * Boss Spider cave bowl feature implementation.
 * <ol>
 * <li>Fall with turning animation.</li>
 * <li>Hatch after land and destroy.</li>
 * </ol>
 */
@FeatureInterface
public final class BossSpidercaveBowl extends FeatureModel implements Routine, Recyclable, TileCollidableListener
{
    private static final String ANIM_HATCH = "hatch";

    private final Animatable animatable;
    private final TileCollidable tileCollidable;
    private final Identifiable identifiable;
    private final Launchable launchable;

    private final Animation fall;
    private final Animation hatch;

    private boolean falling;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param animatable The animatable feature.
     * @param tileCollidable The tile collidable feature.
     * @param collidable The collidable feature.
     * @param identifiable The identifiable feature.
     * @param launchable The launchable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public BossSpidercaveBowl(Services services,
                              Setup setup,
                              Animatable animatable,
                              TileCollidable tileCollidable,
                              Collidable collidable,
                              Identifiable identifiable,
                              Launchable launchable)
    {
        super(services, setup);

        this.animatable = animatable;
        this.tileCollidable = tileCollidable;
        this.identifiable = identifiable;
        this.launchable = launchable;

        final AnimationConfig config = AnimationConfig.imports(setup);

        fall = config.getAnimation(Anim.FALL);
        hatch = config.getAnimation(ANIM_HATCH);

        collidable.setCollisionVisibility(Constant.DEBUG_COLLISIONS);
    }

    @Override
    public void update(double extrp)
    {
        if (falling)
        {
            launchable.getDirection().setVelocity(0.04);
            launchable.getDirection().setDestination(-1.5, -2.0);
        }
        else if (animatable.is(AnimState.FINISHED) && animatable.getFrame() == hatch.getLast())
        {
            identifiable.destroy();
        }
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        if (falling && result.containsY(CollisionName.GROUND))
        {
            tileCollidable.apply(result);
            launchable.getDirection().zero();
            animatable.play(hatch);
            falling = false;
        }
    }

    @Override
    public void recycle()
    {
        falling = true;
        animatable.play(fall);
    }
}
