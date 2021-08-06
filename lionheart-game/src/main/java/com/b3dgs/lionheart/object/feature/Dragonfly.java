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
import com.b3dgs.lionengine.game.Feature;
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
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.state.StateIdleDragon;
import com.b3dgs.lionheart.object.state.attack.StateAttackDragon;

/**
 * Dragonfly feature implementation.
 */
@FeatureInterface
public final class Dragonfly extends FeatureModel implements Routine, CollidableListener
{
    private static final String NODE = "dragonfly";
    private static final String ATT_FREE = "free";

    private static final int DEFAULT_Y = 110;
    private static final int OFFSET_X = -24;
    private static final int OFFSET_Y = -50;
    private static final double SPEED = 13.0 / 31.0;

    private final Trackable target = services.get(Trackable.class);
    private final Stats playerStats = target.getFeature(Stats.class);
    private final Rasterable playerSprite = target.getFeature(Rasterable.class);
    private final StateHandler playerState = target.getFeature(StateHandler.class);
    private final Camera camera = services.get(Camera.class);
    private final CameraTracker tracker = services.get(CameraTracker.class);
    private int offsetY;
    private boolean on = true;
    private int oldHealth;
    private final boolean free;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Launcher launcher;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Dragonfly(Services services, Setup setup)
    {
        super(services, setup);

        free = setup.getBoolean(false, ATT_FREE, NODE);
    }

    /**
     * Start locking.
     * 
     * @param feature The feature reference.
     * @param offsetY The vertical offset.
     */
    private void start(Feature feature, int offsetY)
    {
        feature.getFeature(StateHandler.class).changeState(StateIdleDragon.class);
        feature.getFeature(Transformable.class).teleportY(transformable.getY() + offsetY);
        feature.getFeature(Body.class).resetGravity();
        camera.setIntervals(0, 0);
        tracker.stop();
        target.getFeature(Mirrorable.class).mirror(Mirror.NONE);
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
        }
    }

    /**
     * Check player respawn.
     */
    private void updateRespawn()
    {
        if (oldHealth == 0 && playerStats.getHealth() > 0)
        {
            transformable.teleportY(target.getY() + OFFSET_Y);
            start(target, DEFAULT_Y);
        }
        oldHealth = playerStats.getHealth();
    }

    /**
     * Update frame offset.
     */
    private void updateFrameOffset()
    {
        final int frame = animatable.getFrame();
        if (frame == 1)
        {
            offsetY = 0;
        }
        else if (frame == 2 || frame == 13)
        {
            offsetY = -1;
        }
        else if (frame == 3 || frame == 12)
        {
            offsetY = -2;
        }
        else if (frame == 4 || frame == 11)
        {
            offsetY = -3;
        }
        else if (frame == 5 || frame == 10)
        {
            offsetY = -4;
        }
        else if (frame == 6 || frame == 9)
        {
            offsetY = -5;
        }
        else if (frame == 7 || frame == 8)
        {
            offsetY = -6;
        }
        playerSprite.setFrameOffsets(-4, offsetY);
    }

    /**
     * Update fire orientation.
     */
    private void updateFireOrientation()
    {
        if (!free)
        {
            final int side = Double.compare(transformable.getY(), transformable.getOldY());
            if (side < 0)
            {
                launcher.setLevel(2);
            }
            else if (side > 0)
            {
                launcher.setLevel(1);
            }
            else
            {
                launcher.setLevel(0);
            }
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        target.getFeature(TileCollidable.class).addListener((result, category) -> off());

        final Collidable collidable = target.getFeature(Collidable.class);
        collidable.addListener((c, with, by) ->
        {
            if (with.getName().startsWith(CollisionName.LEG) && by.getName().startsWith(CollisionName.GROUND)
                || with.getName().startsWith(CollisionName.GRIP) && by.getName().startsWith(CollisionName.GRIP))
            {
                off();
            }
        });

        start(collidable, DEFAULT_Y);
        oldHealth = playerStats.getHealth();
    }

    @Override
    public void update(double extrp)
    {
        updateRespawn();

        if (on)
        {
            if (!free)
            {
                if (camera.getX() < 718 * 16)
                {
                    camera.moveLocation(extrp, SPEED, 0.0);
                    target.moveLocationX(extrp, SPEED);
                }
                if (camera.getX() < 705 * 16)
                {
                    launcher.fire();
                }
                camera.setLocation(camera.getX(), target.getY() - 64);
            }

            if (target.getX() < camera.getX() + transformable.getWidth() / 10)
            {
                target.teleportX(camera.getX() + transformable.getWidth() / 10);
            }
            else if (target.getX() > camera.getX() + camera.getWidth() - transformable.getWidth() / 3)
            {
                target.teleportX(camera.getX() + camera.getWidth() - transformable.getWidth() / 3);
            }

            if (playerState.isState(StateIdleDragon.class) || playerState.isState(StateAttackDragon.class))
            {
                if (target.getY() > 256)
                {
                    target.teleportY(256);
                }
                else if (target.getY() < 0)
                {
                    target.teleportY(0);
                }

                updateFrameOffset();

                transformable.setLocationY(target.getY() + OFFSET_Y);
            }
        }
        transformable.setLocationX(target.getX() + OFFSET_X);

        updateFireOrientation();
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
