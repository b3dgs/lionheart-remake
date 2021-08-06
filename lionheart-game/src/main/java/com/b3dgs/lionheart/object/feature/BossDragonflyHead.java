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
import com.b3dgs.lionengine.AnimatorFrameListener;
import com.b3dgs.lionengine.AnimatorStateListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.AnimationConfig;
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
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionheart.constant.Anim;

/**
 * Boss Dragonfly Head feature implementation.
 * <ol>
 * <li>Track player vertically.</li>
 * <li>Shoot on delay.</li>
 * </ol>
 */
@FeatureInterface
public final class BossDragonflyHead extends FeatureModel implements Routine, Recyclable
{
    private static final int MIN_X = -48;
    private static final int MAX_X = 80;
    private static final int MIN_Y = -96;
    private static final int MAX_Y = 64;
    private static final int FIRE_DELAY_TICK = 100;
    private static final int FIRED_DELAY_TICK = 70;
    private static final double TRACK_SPEED = 1.25;

    private final Transformable[] limbs = new Transformable[6];
    private final Tick tick = new Tick();
    private final Force force = new Force();
    private final Animation idle;
    private final Animation attack;
    private final Animation turn;

    private final Trackable target = services.get(Trackable.class);
    private final Spawner spawner = services.get(Spawner.class);

    private Updatable updater;
    private boolean mirror;
    private boolean start;
    private double startX;
    private double startY;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Launcher launcher;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Rasterable rasterable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BossDragonflyHead(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation(Anim.IDLE);
        attack = config.getAnimation(Anim.ATTACK);
        turn = config.getAnimation(Anim.TURN);

        force.setVelocity(0.2);
        force.setSensibility(0.2);
    }

    /**
     * Update tracking.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTrack(double extrp)
    {
        if (start)
        {
            startX = transformable.getX();
            startY = transformable.getY();
            start = false;
        }

        tick.update(extrp);
        if (animatable.getFrame() == 1
            && tick.elapsed(FIRE_DELAY_TICK)
            && Math.abs(target.getY() - transformable.getY() + transformable.getHeight() / 4) < 8)
        {
            launcher.fire();
            animatable.play(attack);
            updater = this::updateFired;
            tick.restart();
        }
        else
        {
            computeForce();
            force.update(extrp);
            transformable.moveLocation(extrp, force);
            fixLimit();

            if (mirror && transformable.getX() > target.getX())
            {
                animatable.play(turn);
                animatable.setAnimSpeed(-animatable.getAnimSpeed());
                animatable.setFrame(turn.getLast());
                mirror = false;
            }
            else if (!mirror && transformable.getX() < target.getX())
            {
                animatable.play(turn);
                mirror = true;
            }
        }
    }

    /**
     * Compute movement force.
     */
    private void computeForce()
    {
        final double dh = target.getX() - transformable.getOldX();
        final double dv = target.getY() - transformable.getOldY() + transformable.getHeight() / 3;

        final double nh = Math.abs(dh);
        final double nv = Math.abs(dv);

        final double max = Math.ceil(Math.max(nh, nv));
        final double sx;
        double sy;

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
        if (sy > -0.5 && sy < -0.01)
        {
            sy = -0.5;
        }
        else if (sy > 0.01 && sy < 0.5)
        {
            sy = 0.5;
        }

        force.setDestination(sx * TRACK_SPEED, sy * TRACK_SPEED);
    }

    /**
     * Fix movement amplitude.
     */
    private void fixLimit()
    {
        if (transformable.getX() - startX < MIN_X)
        {
            transformable.teleportX(startX + MIN_X);
        }
        else if (transformable.getX() - startX > MAX_X)
        {
            transformable.teleportX(startX + MAX_X);
        }

        if (transformable.getY() - startY < MIN_Y)
        {
            transformable.teleportY(startY + MIN_Y);
        }
        else if (transformable.getY() - startY > MAX_Y)
        {
            transformable.teleportY(startY + MAX_Y);
        }
    }

    /**
     * Update on fired.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFired(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsed(FIRED_DELAY_TICK))
        {
            animatable.play(idle);
            updater = this::updateTrack;
            tick.restart();
        }
    }

    /**
     * Update limbs locations.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateLimbs(double extrp)
    {
        for (int i = 0; i < limbs.length; i++)
        {
            limbs[i].teleport(16 + startX + (transformable.getX() - startX) * (0.05 * (i * i / 1.5)),
                              8 + startY + (transformable.getY() - startY) * (0.05 * (i * i / 1.5)));
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        animatable.addListener((AnimatorFrameListener) f ->
        {
            if (f > 7 && f < 14)
            {
                rasterable.setFrameOffsets(-13, 0);
            }
            else if (f == 14)
            {
                rasterable.setFrameOffsets(0, 7);
            }
            else
            {
                rasterable.setFrameOffsets(0, 0);
            }
        });
        launcher.addListener(l ->
        {
            l.getFeature(Animatable.class).addListener((AnimatorStateListener) s ->
            {
                if (s == AnimState.FINISHED)
                {
                    l.getFeature(Identifiable.class).destroy();
                }
            });
        });

        for (int i = 0; i < limbs.length; i++)
        {
            limbs[i] = spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "BossBowl.xml"), transformable)
                              .getFeature(Transformable.class);
        }
    }

    @Override
    public void update(double extrp)
    {
        updater.update(extrp);
        updateLimbs(extrp);
    }

    @Override
    public void recycle()
    {
        updater = this::updateTrack;
        mirror = false;
        start = true;
        tick.restart();
    }
}
