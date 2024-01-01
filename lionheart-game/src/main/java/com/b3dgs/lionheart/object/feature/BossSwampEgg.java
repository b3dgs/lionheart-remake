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
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;

/**
 * Boss Swamp 2 egg feature implementation.
 * <ol>
 * <li>Fall with turning animation.</li>
 * <li>Hatch after land.</li>
 * <li>Spawn Little on hatched.</li>
 * </ol>
 */
@FeatureInterface
public final class BossSwampEgg extends FeatureModel implements Routine, Recyclable, TileCollidableListener
{
    private static final String ANIM_HATCH = "hatch";
    private static final double FALL_VELOCITY = 0.12;

    private final Spawner spawner = services.get(Spawner.class);

    private final Animatable animatable;
    private final TileCollidable tileCollidable;
    private final Transformable transformable;
    private final Identifiable identifiable;
    private final Rasterable rasterable;
    private final Body body;

    private final Force force = new Force();
    private final Animation fall;
    private final Animation hatch;

    private boolean falling;
    private int offset;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param animatable The animatable feature.
     * @param tileCollidable The tile collidable feature.
     * @param collidable The collidable feature.
     * @param transformable The transformable feature.
     * @param identifiable The identifiable feature.
     * @param rasterable The rasterable feature.
     * @param body The body feature.
     * @throws LionEngineException If invalid arguments.
     */
    public BossSwampEgg(Services services,
                        Setup setup,
                        Animatable animatable,
                        TileCollidable tileCollidable,
                        Collidable collidable,
                        Transformable transformable,
                        Identifiable identifiable,
                        Rasterable rasterable,
                        Body body)
    {
        super(services, setup);

        this.animatable = animatable;
        this.tileCollidable = tileCollidable;
        this.transformable = transformable;
        this.identifiable = identifiable;
        this.rasterable = rasterable;
        this.body = body;

        final AnimationConfig config = AnimationConfig.imports(setup);

        fall = config.getAnimation(Anim.FALL);
        hatch = config.getAnimation(ANIM_HATCH);

        force.setDirection(DirectionNone.INSTANCE);
        force.setVelocity(FALL_VELOCITY);
        force.setSensibility(FALL_VELOCITY);

        body.setGravity(Constant.GRAVITY / 2 * 0.06);
        body.setGravityMax(Constant.GRAVITY / 2);

        collidable.setCollisionVisibility(Constant.DEBUG_COLLISIONS);
    }

    /**
     * Set the frame offset.
     * 
     * @param offset The frame offset.
     */
    public void setFrameOffset(int offset)
    {
        this.offset = offset;
        rasterable.setAnimOffset(UtilMath.clamp(offset, 1, 3) * 15);
    }

    @Override
    public void update(double extrp)
    {
        if (falling)
        {
            if (force.getDirectionVertical() > 0)
            {
                body.resetGravity();
            }
            body.update(extrp);
            force.update(extrp);
            transformable.moveLocation(extrp, body, force);
        }
        else if (animatable.is(AnimState.FINISHED) && animatable.getFrame() == hatch.getLast())
        {
            spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "ExplodeLittle.xml"),
                          transformable.getX(),
                          transformable.getY() + transformable.getHeight() / 2)
                   .getFeature(Rasterable.class)
                   .setAnimOffset2(offset);

            spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "Little.xml"), transformable)
                   .getFeature(Rasterable.class)
                   .setAnimOffset2(offset);

            identifiable.destroy();
        }
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        if (falling && result.containsY(CollisionName.GROUND))
        {
            tileCollidable.apply(result);
            animatable.play(hatch);
            Sfx.MONSTER_LAND.play();
            falling = false;
        }
    }

    @Override
    public void recycle()
    {
        falling = true;
        force.setDirection(0.0, 2.0);
        body.resetGravity();
        animatable.play(fall);
    }
}
