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
import com.b3dgs.lionengine.AnimatorStateListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionheart.ScreenShaker;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;

/**
 * Boss Underworld bubble feature implementation.
 * <ol>
 * <li>Move horizontally.</li>
 * </ol>
 */
@FeatureInterface
public final class BossUnderworldBubble extends FeatureModel implements Routine, Recyclable
{
    private static final double ROTATE_ANGLE_SPEED = 2.0;
    private static final double ROTATE_MARGIN_SPEED = 0.5;
    private static final double ROTATE_MARGIN_MAX = 50;
    private static final double BULLET_SPEED = 2.5;

    private final Trackable target = services.get(Trackable.class);
    private final ScreenShaker shaker = services.get(ScreenShaker.class);

    private final Transformable transformable;

    private final Tick tick = new Tick();
    private final Animation attack;

    private Updatable updater;
    private double x;
    private double y;
    private double rotateAngle;
    private double rotateMargin;
    private Force force;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @param animatable The animatable feature.
     * @param identifiable The identifiable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public BossUnderworldBubble(Services services,
                                Setup setup,
                                Transformable transformable,
                                Animatable animatable,
                                Identifiable identifiable)
    {
        super(services, setup);

        this.transformable = transformable;

        attack = AnimationConfig.imports(setup).getAnimation(Anim.ATTACK);

        animatable.addListener((AnimatorStateListener) state ->
        {
            if (AnimState.FINISHED == state)
            {
                animatable.play(attack);
            }
        });
        identifiable.addListener(id -> shaker.start());
    }

    /**
     * Update spawn init.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateSpawn(double extrp)
    {
        x = transformable.getX();
        y = transformable.getY();
        updater = this::updateRotate;
    }

    /**
     * Update rotate phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateRotate(double extrp)
    {
        rotateAngle += ROTATE_ANGLE_SPEED * extrp;
        rotateMargin += ROTATE_MARGIN_SPEED * extrp;

        if (rotateMargin > ROTATE_MARGIN_MAX)
        {
            rotateMargin = ROTATE_MARGIN_MAX;
            force = computeVector();
            updater = this::updateAttack;
            Sfx.MONSTER_LAND.play();
            shaker.start();
        }

        transformable.setLocation(x + UtilMath.cos(rotateAngle) * rotateMargin,
                                  y + UtilMath.sin(rotateAngle) * rotateMargin);
    }

    /**
     * Update attack move.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateAttack(double extrp)
    {
        transformable.moveLocation(extrp, force);
    }

    /**
     * Compute the force vector depending of the target.
     * 
     * @return The computed force to reach target.
     */
    private Force computeVector()
    {
        final double sx = transformable.getX();
        final double sy = transformable.getY();

        final double dx = target.getX();
        final double dy = target.getY();

        final double dist = Math.max(Math.abs(sx - dx), Math.abs(sy - dy));

        final Force vector = new Force(BULLET_SPEED, BULLET_SPEED);

        final double vecX = (dx - sx) / dist * vector.getDirectionHorizontal();
        final double vecY = (dy - sy) / dist * vector.getDirectionVertical();

        final Force force = new Force(vector);
        force.setDirection(vecX, vecY);

        return force;
    }

    @Override
    public void update(double extrp)
    {
        updater.update(extrp);
    }

    @Override
    public void recycle()
    {
        tick.stop();
        updater = this::updateSpawn;
        rotateAngle = 0.0;
        rotateMargin = 0.0;
    }
}
