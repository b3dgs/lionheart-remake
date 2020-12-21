/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
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
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.constant.Folder;

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
    private static final int PALLET_OFFSET = 15;
    private static final String ANIM_HATCH = "hatch";
    private static final double FALL_VELOCITY = 0.1;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Spawner spawner = services.get(Spawner.class);
    private final Force force = new Force();
    private final Animation fall;
    private final Animation hatch;

    private boolean falling;
    private int offset;

    @FeatureGet private Animatable animatable;
    @FeatureGet private TileCollidable tileCollidable;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Rasterable rasterable;
    @FeatureGet private Body body;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BossSwampEgg(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);

        fall = config.getAnimation(Anim.FALL);
        hatch = config.getAnimation(ANIM_HATCH);

        force.setDirection(DirectionNone.INSTANCE);
        force.setVelocity(FALL_VELOCITY);
        force.setSensibility(FALL_VELOCITY);
    }

    /**
     * Set the frame offset.
     * 
     * @param offset The frame offset.
     */
    public void setFrameOffset(int offset)
    {
        this.offset = offset;
        rasterable.setAnimOffset(UtilMath.clamp(offset, 0, 2) * PALLET_OFFSET);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        body.setGravity(Constant.GRAVITY / 2);
        body.setGravityMax(Constant.GRAVITY / 2);
        body.setDesiredFps(source.getRate());

        collidable.setCollisionVisibility(Constant.DEBUG);
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
            spawner.spawn(Medias.create(Folder.EFFECTS, "ExplodeLittle.xml"), transformable);
            spawner.spawn(Medias.create(Folder.ENTITIES, "boss", "swamp", "Little.xml"), transformable)
                   .getFeature(Rasterable.class)
                   .setAnimOffset(offset * 8);
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
