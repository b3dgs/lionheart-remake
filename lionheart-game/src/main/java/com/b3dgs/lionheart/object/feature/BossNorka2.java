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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Force;
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
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.ScreenShaker;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;

/**
 * Boss Norka 2 feature implementation.
 * <ol>
 * <li>Move down.</li>
 * <li>Move jump left or right.</li>
 * <li>Move attack.</li>
 * </ol>
 */
@FeatureInterface
public final class BossNorka2 extends FeatureModel implements Routine, Recyclable
{
    private static final int MOVE_DOWN_DELAY_MS = 800;
    private static final int START_ATTACK_DELAY_MS = 500;
    private static final int END_ATTACK_DELAY_MS = 1500;
    private static final int END_ATTACKED_DELAY_MS = 700;

    private static final int ATTACK_DISTANCE_MAX = 96;
    private static final double MOVE_X_SPEED = 1.2;
    private static final double MOVE_Y_SPEED = 4.8;
    private static final int MOVE_DOWN_Y = 80;

    private final Tick tick = new Tick();
    private final Force movement = new Force();
    private final Animation idle;
    private final Animation fall;
    private final Animation turn;
    private final Animation attack;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Trackable target = services.get(Trackable.class);
    private final ScreenShaker shaker = services.get(ScreenShaker.class);

    private Updatable current;
    private int side;
    private boolean forceJump;

    @FeatureGet private Animatable animatable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Body body;
    @FeatureGet private Hurtable hurtable;
    @FeatureGet private Stats stats;
    @FeatureGet private Launcher launcher;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BossNorka2(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation(Anim.IDLE);
        fall = config.getAnimation(Anim.FALL);
        turn = config.getAnimation(Anim.TURN);
        attack = config.getAnimation(Anim.ATTACK);

        movement.setVelocity(0.12);
        movement.setSensibility(0.6);
    }

    /**
     * Update move down.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateMoveDown(double extrp)
    {
        animatable.play(fall);

        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), MOVE_DOWN_DELAY_MS))
        {
            if (transformable.getY() < MOVE_DOWN_Y)
            {
                Sfx.BOSS_DAEMON_LAND.play();
                body.resetGravity();
                transformable.teleportY(MOVE_DOWN_Y);
                current = this::shakeScreen;
                forceJump = true;
                shaker.start();
            }
        }
        else
        {
            body.resetGravity();
        }
    }

    /**
     * Shake screen effect.
     * 
     * @param extrp The extrapolation value.
     */
    private void shakeScreen(double extrp)
    {
        transformable.teleportY(MOVE_DOWN_Y);
        body.resetGravity();
        if (shaker.hasShaken())
        {
            current = this::updateTrack;
        }
    }

    /**
     * Update track player on side.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTrack(double extrp)
    {
        transformable.teleportY(MOVE_DOWN_Y);
        if (side > -1 && target.getX() < transformable.getX())
        {
            animatable.play(turn);
            if (side == 0)
            {
                animatable.setFrame(fall.getFirst());
                mirrorable.mirror(Mirror.HORIZONTAL);
            }
            side = -1;
        }
        else if (side < 1 && transformable.getX() < target.getX())
        {
            animatable.play(turn);
            if (side == 0)
            {
                animatable.setFrame(fall.getFirst());
            }
            side = 1;
        }
        current = this::updateTurn;
    }

    /**
     * Update turn to move or attack.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTurn(double extrp)
    {
        transformable.teleportY(MOVE_DOWN_Y);
        body.resetGravity();
        if (animatable.is(AnimState.FINISHED))
        {
            mirrorable.mirror(side > 0 ? Mirror.HORIZONTAL : Mirror.NONE);
            if (!forceJump
                && Math.abs(transformable.getX() - target.getX()) < ATTACK_DISTANCE_MAX
                && (side < 1 && transformable.getX() > target.getX()
                    || side > -1 && transformable.getX() < target.getX()))
            {
                animatable.play(attack);
                forceJump = true;
                current = this::updateAttack;
                tick.restart();
            }
            else
            {
                movement.setDirection(MOVE_X_SPEED * side, MOVE_Y_SPEED);
                movement.setDestination(MOVE_X_SPEED * side, 0.0);
                animatable.play(idle);
                current = this::updateMove;
            }
        }
    }

    /**
     * Update move with jump.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateMove(double extrp)
    {
        movement.update(extrp);
        if (movement.getDirectionVertical() > 0)
        {
            body.resetGravity();
        }
        transformable.moveLocation(extrp, movement);
        if (transformable.getY() < MOVE_DOWN_Y)
        {
            Sfx.BOSS_DAEMON_LAND.play();
            body.resetGravity();
            transformable.teleportY(MOVE_DOWN_Y);
            current = this::shakeScreen;
            forceJump = false;
            shaker.start();
        }
    }

    /**
     * Update attack fire.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateAttack(double extrp)
    {
        transformable.teleportY(MOVE_DOWN_Y);
        body.resetGravity();

        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), START_ATTACK_DELAY_MS) && animatable.is(AnimState.FINISHED))
        {
            launcher.fire(target);
            current = this::updateEndAttackDelay;
            tick.restart();
        }
    }

    /**
     * Update await after attack.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateEndAttackDelay(double extrp)
    {
        transformable.teleportY(MOVE_DOWN_Y);
        body.resetGravity();

        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), END_ATTACK_DELAY_MS) && !hurtable.isHurting())
        {
            animatable.play(attack);
            animatable.setFrame(attack.getLast());
            animatable.setAnimSpeed(-attack.getSpeed());
            current = this::updateEndAttack;
            tick.restart();
        }
    }

    /**
     * Update end attack before track.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateEndAttack(double extrp)
    {
        transformable.teleportY(MOVE_DOWN_Y);
        body.resetGravity();

        tick.update(extrp);
        if (animatable.is(AnimState.FINISHED) || tick.elapsedTime(source.getRate(), END_ATTACKED_DELAY_MS))
        {
            animatable.play(idle);
            current = this::updateTrack;
        }
    }

    @Override
    public void update(double extrp)
    {
        current.update(extrp);
    }

    @Override
    public void recycle()
    {
        current = this::updateMoveDown;
        forceJump = false;
        tick.restart();
    }
}
