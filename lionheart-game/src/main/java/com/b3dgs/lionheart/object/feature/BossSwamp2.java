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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Camera;
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
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Boss Swamp 2 feature implementation.
 * <ol>
 * <li>Fly vertical down on spawn and track player horizontally.</li>
 * <li>Start spawn eggs on proximity.</li>
 * <li>Fly horizontal left while spawning eggs until border.</li>
 * <li>Eggs spawns little.</li>
 * <li>Fly horizontal back and track player.</li>
 * <li>Land on proximity.</li>
 * <li>Spawn 2 birds.</li>
 * <li>Take off slightly and show neck.</li>
 * <li>Fly away on neck hurt.</li>
 * <li>Spawn BossSwamp1 on exited screen.</li>
 * </ol>
 */
@FeatureInterface
public final class BossSwamp2 extends FeatureModel implements Routine, Recyclable
{
    private static final double MOVE_BACK_X = 0.8;
    private static final double MOVE_BACK_X_MARGIN = 64;
    private static final double MOVE_LEFT_X = -4.0;
    private static final double GROUND_Y = 121;
    private static final double MIN_Y_MOVE_BACK = 200;
    private static final double MAX_Y = 300;
    private static final int SHAKE_DELAY = 2;
    private static final int STAND_DELAY = 300;
    private static final double MOVE_LEAVE_X = -4.0;

    private final Tick tick = new Tick();
    private final Transformable player = services.get(SwordShade.class).getFeature(Transformable.class);
    private final Spawner spawner = services.get(Spawner.class);
    private final Camera camera = services.get(Camera.class);
    private final Animation idle;
    private final Animation land;

    private double moveX;
    private double moveY;
    private boolean movedX;
    private boolean movedY;
    private int step;
    private double lastX;
    private double lastY;
    private int shakeX;
    private int shakeY;
    private int shakeCount;

    @FeatureGet private Animatable animatable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Launcher launcher;
    @FeatureGet private BossSwampEffect effect;
    @FeatureGet private Identifiable identifiable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BossSwamp2(Services services, Setup setup)
    {
        super(services, setup);

        idle = AnimationConfig.imports(setup).getAnimation(Anim.IDLE);
        land = AnimationConfig.imports(setup).getAnimation(Anim.LAND);
    }

    /**
     * Follow player on horizontal axis.
     */
    private void followHorizontal()
    {
        if (transformable.getX() < player.getX() + transformable.getWidth() / 2 - MOVE_BACK_X_MARGIN)
        {
            if (!movedX)
            {
                moveX = MOVE_BACK_X;
            }
            effect.setEffectX(BossSwampEffect.EFFECT_SPEED);
        }
        else if (transformable.getX() > player.getX() + transformable.getWidth() / 2 + MOVE_BACK_X_MARGIN)
        {
            if (!movedX)
            {
                moveX = -MOVE_BACK_X;
            }
            effect.setEffectX(-BossSwampEffect.EFFECT_SPEED);
        }
        else
        {
            moveX = 0.0;
            movedX = true;
        }
    }

    /**
     * Move down until minimum height.
     */
    private void moveDown()
    {
        if (transformable.getY() < MIN_Y_MOVE_BACK - BossSwampEffect.EFFECT_MARGIN)
        {
            if (!movedY)
            {
                moveY = 0.0;
                movedY = true;
            }
            else
            {
                effect.setEffectY(BossSwampEffect.EFFECT_SPEED);
            }
        }
        else if (transformable.getY() > MIN_Y_MOVE_BACK + BossSwampEffect.EFFECT_MARGIN)
        {
            if (!movedY)
            {
                moveY = -1.0;
            }
            else
            {
                effect.setEffectY(-BossSwampEffect.EFFECT_SPEED);
            }
        }
    }

    /**
     * Move left straight.
     * 
     * @param extrp The extrapolation value.
     */
    private void moveLeft(double extrp)
    {
        effect.update(extrp);
        if (step == 1 && transformable.getX() > lastX - camera.getWidth() * 2)
        {
            moveX = MOVE_LEFT_X;
            launcher.fire();
            step = 2;
        }
        else if (step == 2 && transformable.getX() < lastX - transformable.getWidth() - camera.getWidth() * 2)
        {
            moveX = 0.0;
            step = 3;
            movedX = false;
            movedY = false;
            transformable.teleport(player.getX(), MAX_Y);
        }
    }

    /**
     * Move back right slowly.
     * 
     * @param extrp The extrapolation value.
     */
    private void moveBottomRightLand(double extrp)
    {
        if (step == 3)
        {
            effect.update(extrp);
            followHorizontal();
            moveDown();

            if (movedX && movedY)
            {
                animatable.play(land);
                moveX = 1.0;
                moveY = -4.0;
                step = 4;
            }
        }
        else if (step == 4 && transformable.getY() < GROUND_Y)
        {
            transformable.teleportY(GROUND_Y);
            moveX = 0.0;
            moveY = 0.0;
            tick.restart();
            tick.set(SHAKE_DELAY);
            launcher.setLevel(1);
            launcher.fire(player);
            shakeCount = 0;
            step = 5;
        }
    }

    /**
     * Shake screen effect.
     * 
     * @param extrp The extrapolation value.
     */
    private void shakeScreen(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsed(SHAKE_DELAY))
        {
            tick.restart();
            if (shakeX == 0)
            {
                shakeX = 4;
                shakeY = -4;
            }
            else
            {
                shakeX = 0;
                shakeY = 0;
            }
            shakeCount++;
            camera.setShake(shakeX, shakeY);
            if (shakeCount > 5)
            {
                lastY = transformable.getY();
                step = 6;
            }
        }
    }

    /**
     * Update take off phase.
     * 
     * @param extrp The extrapolation.
     */
    private void takeOff(double extrp)
    {
        tick.update(extrp);
        if (step == 6 && tick.elapsed(STAND_DELAY))
        {
            animatable.play(idle);
            step = 7;
        }
        else if (step == 7)
        {
            if (transformable.getY() < lastY + 32)
            {
                moveX = 0.5;
                moveY = 1.2;
            }
            else
            {
                moveX = 0.0;
                moveY = 0.0;
                lastX = transformable.getX();
                lastY = transformable.getY();
                tick.restart();
                step = 8;
            }
        }
    }

    /**
     * Await for neck to be hit.
     * 
     * @param extrp The extrapolation value.
     */
    private void awaitNeck(double extrp)
    {
        effect.update(extrp);
        if (transformable.getX() < lastX - 4)
        {
            effect.setEffectX(BossSwampEffect.EFFECT_SPEED);
        }
        else if (transformable.getX() > lastX + 4)
        {
            effect.setEffectX(-BossSwampEffect.EFFECT_SPEED);
        }

        if (transformable.getY() < lastY - 2)
        {
            effect.setEffectY(BossSwampEffect.EFFECT_SPEED);
        }
        else if (transformable.getY() > lastY + 2)
        {
            effect.setEffectY(-BossSwampEffect.EFFECT_SPEED);
        }

        tick.update(extrp);
        if (tick.elapsed(STAND_DELAY))
        {
            lastX = transformable.getX();
            moveX = MOVE_LEAVE_X;
            step = 9;
        }
    }

    /**
     * Move leave area.
     * 
     * @param extrp The extrapolation value.
     */
    private void moveLeave(double extrp)
    {
        effect.update(extrp);
        if (transformable.getX() < lastX - transformable.getWidth() - camera.getWidth() * 2)
        {
            identifiable.destroy();
            spawner.spawn(Medias.create(Folder.ENTITIES, "boss", "swamp", "Boss1.xml"), player.getX(), MAX_Y);
        }
    }

    @Override
    public void update(double extrp)
    {
        if (step == 0)
        {
            effect.update(extrp);
            followHorizontal();
            moveDown();

            if (movedX && movedY)
            {
                lastX = transformable.getX();
                step = 1;
            }
        }
        else if (step > 0 && step < 3)
        {
            moveLeft(extrp);
        }
        else if (step > 2 && step < 5)
        {
            moveBottomRightLand(extrp);
        }
        else if (step == 5)
        {
            shakeScreen(extrp);
        }
        else if (step > 5 && step < 8)
        {
            takeOff(extrp);
        }
        else if (step == 8)
        {
            awaitNeck(extrp);
        }
        else if (step == 9)
        {
            moveLeave(extrp);
        }
        transformable.moveLocation(extrp, moveX, moveY);
    }

    @Override
    public void recycle()
    {
        moveX = 0.0;
        moveY = 0.0;
        movedX = false;
        movedY = false;
        launcher.setLevel(0);
        step = 0;
        animatable.play(idle);
    }
}
