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
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.CameraTracker;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.state.StateIdleAnimal;
import com.b3dgs.lionheart.object.state.attack.StateAttackAnimal;

/**
 * Animal feature implementation.
 */
@FeatureInterface
public final class Animal extends FeatureModel implements Routine, CollidableListener
{
    private static final double SPEED_GROUND = 3.0;
    private static final double SPEED_BOAT = 0.6;

    private final Trackable target = services.get(Trackable.class);
    private final Rasterable playerSprite = target.getFeature(Rasterable.class);
    private final StateHandler playerState = target.getFeature(StateHandler.class);
    private final Camera camera = services.get(Camera.class);
    private final CameraTracker tracker = services.get(CameraTracker.class);

    private int offsetY;
    private double cameraHeight;
    private boolean on;
    private double boatEffectY;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Animatable animatable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Animal(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        target.getFeature(TileCollidable.class).addListener((result, category) -> off());

        final Collidable collidable = target.getFeature(Collidable.class);
        collidable.addListener((c, with, by) ->
        {
            if (with.getName().startsWith(CollisionName.LEG) && by.getName().startsWith(CollisionName.GROUND))
            {
                off();
            }
        });
        start(collidable, 76);
    }

    /**
     * Start locking.
     * 
     * @param collidable The collidable reference.
     * @param offsetY The vertical offset.
     */
    private void start(Collidable collidable, int offsetY)
    {
        collidable.getFeature(StateHandler.class).changeState(StateIdleAnimal.class);
        collidable.getFeature(Transformable.class).teleportY(transformable.getY() + offsetY);
        collidable.getFeature(Body.class).resetGravity();
        camera.setIntervals(0, 0);
        tracker.stop();
        target.getFeature(Mirrorable.class).mirror(Mirror.NONE);
        target.getFeature(EntityModel.class).setJumpOnHurt(false);
        on = true;
    }

    /**
     * Disable player on animal locking.
     */
    private void off()
    {
        if (on)
        {
            on = false;
            camera.setIntervals(Constant.CAMERA_HORIZONTAL_MARGIN, 0);
            tracker.track(target, true);
            target.getFeature(EntityModel.class).setJumpOnHurt(true);
        }
    }

    @Override
    public void update(double extrp)
    {
        if (on)
        {
            double speed;
            if (camera.getX() < 8824 - camera.getWidth())
            {
                speed = SPEED_GROUND;
                cameraHeight = 0;
            }
            else
            {
                if (camera.getX() < 12168 - camera.getWidth())
                {
                    speed = SPEED_BOAT;
                }
                else
                {
                    speed = 0.0;
                }
                cameraHeight += 0.9 * extrp;
                if (cameraHeight > 32)
                {
                    cameraHeight = 32;
                }
            }
            camera.setLimitBottom((int) Math.floor(cameraHeight));
            camera.moveLocation(extrp, speed, 0.0);
            camera.setLocationY(target.getY() - 80);
            target.moveLocationX(extrp, speed);

            if (target.getX() < camera.getX() + transformable.getWidth() / 2)
            {
                target.teleportX(camera.getX() + transformable.getWidth() / 2);
            }
            else if (target.getX() > camera.getX() + camera.getWidth() - transformable.getWidth())
            {
                target.teleportX(camera.getX() + camera.getWidth() - transformable.getWidth());
            }

            if (playerState.isState(StateIdleAnimal.class) || playerState.isState(StateAttackAnimal.class))
            {
                final int frame = animatable.getFrame();
                if (frame == 1 || frame == 4 || frame == 8)
                {
                    offsetY = -1;
                }
                else if (frame == 2 || frame == 9)
                {
                    offsetY = -2;
                }
                else if (frame == 3 || frame == 10)
                {
                    offsetY = -3;
                }
                else if (frame >= 5 && frame <= 7 || frame >= 12 && frame <= 14)
                {
                    offsetY = 1;
                }
                else if (frame == 10)
                {
                    offsetY = -4;
                }
                playerSprite.setFrameOffsets(0, offsetY);
            }
            camera.setShake2(0, 0);
        }
        else
        {
            boatEffectY = UtilMath.wrapAngleDouble(boatEffectY + 5 * extrp);
            camera.setShake2(0, (int) Math.round(UtilMath.sin(boatEffectY) * 2));
        }

        transformable.setLocationX(target.getX());
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (with.getName().startsWith(CollisionName.ANIMAL)
            && by.getName().startsWith(Anim.LEG)
            && collidable.getFeature(Stats.class).getHealth() > 0)
        {
            start(collidable, with.getOffsetY());
        }
    }
}
