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
import com.b3dgs.lionengine.UtilMath;
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
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;

/**
 * Boss Norka 1 feature implementation.
 * <ol>
 * <li>Move down.</li>
 * <li>Move left or right.</li>
 * <li>Move forward.</li>
 * <li>Move prepare attack and attack.</li>
 * <li>Move backward.</li>
 * </ol>
 */
@FeatureInterface
public final class BossNorka1 extends FeatureModel implements Routine, Recyclable
{
    private static final int MOVE_DOWN_DELAY_TICK = 0;// 150;
    private static final int PATROL_DELAY_TICK = 50;
    private static final int PATROL_END_DELAY_TICK = 50;
    private static final int APPROACHED_DELAY_TICK = 60;
    private static final int ATTACK_PREPARE_DELAY_TICK = 50;
    private static final int ATTACK_DELAY_TICK = 50;
    private static final int MOVE_BACK_DELAY_TICK = 15;

    private static final double MOVE_SPEED = 2.5;
    private static final int MOVE_DOWN_Y = 192;
    private static final double CURVE_SPEED = 8.0;
    private static final double CURVE_AMPLITUDE = 3.0;
    private static final int[] PATROL_X =
    {
        112, 208, 320
    };

    private final Tick tick = new Tick();
    private final Force movement = new Force();
    private final Animation idle;
    private final Animation walk;
    private final Animation approach;
    private final Animation approached;
    private final Animation attackprepare;
    private final Animation attack;

    private final Transformable player = services.get(SwordShade.class).getFeature(Transformable.class);

    private Updatable current;
    private double angle;
    private int patrol;
    private int subpatrol;
    private int side;

    @FeatureGet private Animatable animatable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Hurtable hurtable;
    @FeatureGet private Stats stats;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BossNorka1(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation(Anim.IDLE);
        walk = config.getAnimation(Anim.WALK);
        approach = config.getAnimation("approach");
        approached = config.getAnimation("approached");
        attackprepare = config.getAnimation("attackprepare");
        attack = config.getAnimation(Anim.ATTACK);

        movement.setVelocity(1.0);
        movement.setSensibility(0.5);
    }

    /**
     * Update move down.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateMoveDown(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsed(MOVE_DOWN_DELAY_TICK))
        {
            transformable.moveLocationY(extrp, -MOVE_SPEED);
            if (transformable.getY() < MOVE_DOWN_Y)
            {
                transformable.teleportY(MOVE_DOWN_Y);
                current = this::updateAwaitPatrol;
                patrol = 0;
                subpatrol = 0;
                tick.restart();
            }
        }
    }

    /**
     * Update await next patrol.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateAwaitPatrol(double extrp)
    {
        tick.update(extrp);
        updateCurve();
        if (tick.elapsed(PATROL_DELAY_TICK))
        {
            patrol = (patrol + 2) % PATROL_X.length;
            side = UtilMath.getSign(PATROL_X[patrol] - transformable.getX());
            mirrorable.mirror(side > 0 ? Mirror.HORIZONTAL : Mirror.NONE);
            current = this::updatePatrol;
            animatable.play(walk);
        }
    }

    /**
     * Update patrol left and right.
     * 
     * @param extrp The extrapolation value.
     */
    private void updatePatrol(double extrp)
    {
        transformable.moveLocationX(extrp, MOVE_SPEED * 2 * side);
        if (side < 0 && transformable.getX() < PATROL_X[patrol] || side > 0 && transformable.getX() > PATROL_X[patrol])
        {
            transformable.teleportX(PATROL_X[patrol]);
            subpatrol++;
            if (subpatrol > 2)
            {
                current = this::updateEndPatrol;
                subpatrol = 0;
            }
            else
            {
                current = this::updateAwaitPatrol;
            }
            mirrorable.mirror(Mirror.NONE);
            animatable.play(idle);
            tick.restart();
        }
    }

    /**
     * Update approach phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateEndPatrol(double extrp)
    {
        tick.update(extrp);
        updateCurve();
        if (tick.elapsed(PATROL_END_DELAY_TICK))
        {
            animatable.play(approach);
            current = this::updateApproach;
        }
    }

    /**
     * Update approach phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateApproach(double extrp)
    {
        if (animatable.is(AnimState.FINISHED))
        {
            animatable.play(approached);
            mirrorable.mirror(transformable.getX() < player.getX() ? Mirror.HORIZONTAL : Mirror.NONE);
            current = this::updateApproached;
            tick.restart();
        }
    }

    /**
     * Update approached phase and await before attack.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateApproached(double extrp)
    {
        tick.update(extrp);
        updateCurve();
        if (tick.elapsed(APPROACHED_DELAY_TICK))
        {
            animatable.play(attackprepare);
            current = this::updateAttackPrepare;
            Sfx.BOSS_FLYER.play();
            tick.restart();
        }
    }

    /**
     * Update prepare attack phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateAttackPrepare(double extrp)
    {
        tick.update(extrp);
        updateCurve();
        if (tick.elapsed(ATTACK_PREPARE_DELAY_TICK))
        {
            final double dh = player.getX() - transformable.getOldX();
            final double dv = player.getY() - transformable.getOldY();

            final double nh = Math.abs(dh);
            final double nv = Math.abs(dv);

            final int max = (int) Math.ceil(Math.max(nh, nv));
            final double sx;
            final double sy;

            if (Double.compare(nh, 1.0) >= 0 || Double.compare(nv, 1.0) >= 0)
            {
                sx = dh / max;
                sy = dv / max;
            }
            else
            {
                sx = dh;
                sy = dv;
            }

            movement.setDestination(sx * MOVE_SPEED, sy * MOVE_SPEED);
            movement.setDirection(sx * MOVE_SPEED, sy * MOVE_SPEED);
            animatable.play(attack);
            mirrorable.mirror(transformable.getX() < player.getX() ? Mirror.HORIZONTAL : Mirror.NONE);
            current = this::updateAttack;
            tick.restart();
        }
    }

    /**
     * Update attack phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateAttack(double extrp)
    {
        transformable.moveLocation(extrp, movement);
        tick.update(extrp);
        if (tick.elapsed(ATTACK_DELAY_TICK))
        {
            animatable.play(approached);
            mirrorable.mirror(Mirror.NONE);
            current = this::updateMoveBackPrepare;
            tick.restart();
        }
    }

    /**
     * Update prepare move back phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateMoveBackPrepare(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsed(MOVE_BACK_DELAY_TICK))
        {
            animatable.play(approach);
            animatable.setFrame(approach.getLast());
            animatable.setAnimSpeed(-animatable.getAnimSpeed());
            current = this::updateMoveBack;
        }
    }

    /**
     * Update move back phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateMoveBack(double extrp)
    {
        if (transformable.getY() < MOVE_DOWN_Y + UtilMath.cos(angle) * CURVE_AMPLITUDE)
        {
            transformable.moveLocationY(extrp, MOVE_SPEED);
        }
        if (transformable.getY() > MOVE_DOWN_Y + UtilMath.cos(angle) * CURVE_AMPLITUDE
            && animatable.is(AnimState.FINISHED))
        {
            transformable.teleportY(MOVE_DOWN_Y + UtilMath.cos(angle) * CURVE_AMPLITUDE);
            animatable.play(idle);
            patrol = (patrol + 1) % PATROL_X.length;
            current = this::updateAwaitPatrol;
            tick.restart();
        }
    }

    /**
     * Update curve effect.
     */
    private void updateCurve()
    {
        angle = UtilMath.wrapAngleDouble(angle + CURVE_SPEED);
        transformable.teleportY(MOVE_DOWN_Y + UtilMath.cos(angle) * CURVE_AMPLITUDE);
    }

    @Override
    public void update(double extrp)
    {
        if (!hurtable.isHurting())
        {
            current.update(extrp);
        }
        else
        {
            current = this::updateMoveBackPrepare;
            tick.restart();
            tick.set(MOVE_BACK_DELAY_TICK);
        }
    }

    @Override
    public void recycle()
    {
        current = this::updateMoveDown;
        animatable.play(idle);
        angle = 0.0;
        tick.restart();
    }
}
