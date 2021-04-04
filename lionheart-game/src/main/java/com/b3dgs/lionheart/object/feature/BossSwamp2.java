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

import java.util.Optional;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Featurable;
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
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.LoadNextStage;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.MusicPlayer;
import com.b3dgs.lionheart.ScreenShaker;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.object.EntityModel;

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
    private static final int END_TICK = 500;
    private static final int PALLET_OFFSET = 3;
    private static final int PALLET_HURT = 9;
    private static final double MOVE_BACK_X = 0.8;
    private static final double MOVE_BACK_X_MARGIN = 64;
    private static final double MOVE_LEFT_X = -4.0;
    private static final double GROUND_Y = 118;
    private static final double MIN_Y_MOVE_BACK = 200;
    private static final double MAX_Y = 388;
    private static final int STAND_DELAY = 300;
    private static final int FLICKER_MAX = 20;
    private static final double MOVE_LEAVE_X = -4.0;
    private static final int EXPLODE_DELAY = 5;
    private static final double MOVE_DEAD_X = -0.2;
    private static final double MOVE_DEAD_Y = -0.5;

    private final Tick tick = new Tick();
    private final Transformable player = services.get(SwordShade.class).getFeature(Transformable.class);
    private final Spawner spawner = services.get(Spawner.class);
    private final Camera camera = services.get(Camera.class);
    private final MusicPlayer music = services.get(MusicPlayer.class);
    private final LoadNextStage stage = services.get(LoadNextStage.class);
    private final ScreenShaker shaker = services.get(ScreenShaker.class);
    private final Animation idle;
    private final Animation land;
    private final CollidableListener listener;

    private BossSwampNeck neck;
    private double moveX;
    private double moveY;
    private boolean movedX;
    private boolean movedY;
    private int step;
    private double lastX;
    private double lastY;
    private int flickerCount;

    @FeatureGet private EntityModel model;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Launcher launcher;
    @FeatureGet private BossSwampEffect effect;
    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Rasterable rasterable;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Stats stats;
    @FeatureGet private Glue glue;

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

        listener = (c, with, by) ->
        {
            if (with.getName().startsWith(Anim.BODY) && by.getName().startsWith(Anim.ATTACK) && flickerCount == 0)
            {
                flickerCount = 1;
                c.ifIs(Stats.class, s ->
                {
                    if (stats.applyDamages(s.getDamages()))
                    {
                        collidable.setEnabled(false);
                        neck.getFeature(Collidable.class).setEnabled(false);
                        if (step > 5)
                        {
                            step = 10;
                            music.playMusic(Music.BOSS_WIN);
                            stage.loadNextStage(model.getNext().get(), END_TICK);
                            tick.restart();
                        }
                    }
                    Sfx.BOSS1_HURT.play();
                });
                if (step == 8)
                {
                    tick.set(STAND_DELAY);
                }
            }
        };
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
                neck.setEnabled(null);
                animatable.play(land);
                moveX = 1.0;
                moveY = -4.0;
                glue.start();
                step = 4;
            }
        }
        else if (step == 4 && transformable.getY() < GROUND_Y)
        {
            transformable.teleportY(GROUND_Y);
            moveX = 0.0;
            moveY = 0.0;
            tick.restart();
            Sfx.MONSTER_LAND.play();
            launcher.setLevel(1);
            launcher.fire(player);
            shaker.start();
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
        if (shaker.hasShaken())
        {
            lastY = transformable.getY();
            step = 6;
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
            neck.setEnabled(listener);
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
            glue.stop();
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
            neck.destroy();
            final Featurable boss = spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "Boss.xml"),
                                                  player.getX(),
                                                  MAX_Y);
            boss.getFeature(EntityModel.class).setNext(model.getNext().get(), Optional.empty());
            boss.getFeature(Stats.class).applyDamages(stats.getHealthMax() - stats.getHealth());
        }
    }

    /**
     * Move dead down.
     * 
     * @param extrp The extrapolation value.
     */
    private void moveDead(double extrp)
    {
        effect.update(extrp);
        moveX = MOVE_DEAD_X;
        moveY = MOVE_DEAD_Y;

        tick.update(extrp);
        if (tick.elapsed(EXPLODE_DELAY))
        {
            spawnExplode();
            spawnExplode();
            spawnExplode();

            tick.restart();
        }
    }

    /**
     * Spawn explode.
     */
    private void spawnExplode()
    {
        final int width = transformable.getWidth() / 2;
        final int height = transformable.getHeight() / 2;

        spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "ExplodeBoss.xml"),
                      transformable.getX() + UtilRandom.getRandomInteger(width) - width / 2,
                      transformable.getY() + UtilRandom.getRandomInteger(height));
    }

    /**
     * Update hurt flicker effect.
     */
    private void updateFlickerHurt()
    {
        rasterable.setAnimOffset(UtilMath.clamp(getFrameOffset(), 0, 2) * PALLET_OFFSET);
        if (flickerCount > 0)
        {
            if (flickerCount % 2 == 1)
            {
                rasterable.setAnimOffset(PALLET_HURT);
            }
            flickerCount++;
            if (flickerCount > FLICKER_MAX)
            {
                flickerCount = 0;
            }
        }
    }

    /**
     * Get frame offset based on health.
     * 
     * @return The frame offset.
     */
    private int getFrameOffset()
    {
        return (stats.getHealthMax() - stats.getHealth()) / 2;
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        collidable.setCollisionVisibility(Constant.DEBUG);
        launcher.addListener(l ->
        {
            final int offset = UtilMath.clamp(getFrameOffset(), 0, 2);
            l.ifIs(Bird.class, b -> b.getFeature(Rasterable.class).setAnimOffset2(offset * 20));
            l.ifIs(BossSwampEgg.class, e -> e.setFrameOffset(offset));
        });
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
                neck.setEnabled(listener);
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
        else if (step == 10)
        {
            moveDead(extrp);
        }
        transformable.moveLocation(extrp, moveX, moveY);
        neck.setLocation(transformable);
        neck.setFrameOffset(getFrameOffset());

        updateFlickerHurt();
    }

    @Override
    public void recycle()
    {
        neck = spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "Neck.xml"), 0, 0)
                      .getFeature(BossSwampNeck.class);
        moveX = 0.0;
        moveY = 0.0;
        movedX = false;
        movedY = false;
        launcher.setLevel(0);
        step = 0;
        tick.restart();
        collidable.setEnabled(true);
        animatable.play(idle);
    }
}
