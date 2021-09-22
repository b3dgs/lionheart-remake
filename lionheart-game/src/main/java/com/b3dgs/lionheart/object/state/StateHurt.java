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
package com.b3dgs.lionheart.object.state;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.game.feature.state.StateLast;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Executioner;
import com.b3dgs.lionheart.object.feature.Hurtable;
import com.b3dgs.lionheart.object.feature.Patrol;
import com.b3dgs.lionheart.object.feature.Stats;
import com.b3dgs.lionheart.object.feature.Trackable;

/**
 * Hurt state implementation.
 */
public final class StateHurt extends State
{
    private static final double HURT_JUMP_FORCE = 2.5;
    private static final int FLICKER_COUNT = 16;
    private static final int FLICKER_DELAY_MS = 16;

    private final Hurtable hurtable = model.getFeature(Hurtable.class);
    private final Stats stats = model.getFeature(Stats.class);
    private final Tick tick = new Tick();
    private final Updatable updateFlicker;

    private Updatable updater;
    private int frameFlicker;
    private int flicker;
    private double velocity = -1;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateHurt(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateLast.class, () -> !hurtable.isHurting() && !model.hasFeature(Patrol.class));
        addTransition(StatePatrol.class, () -> !hurtable.isHurting() && model.hasFeature(Patrol.class));

        final SourceResolutionProvider source = model.getServices().get(SourceResolutionProvider.class);

        updateFlicker = extrp ->
        {
            if (flicker < FLICKER_COUNT && tick.elapsedTime(source.getRate(), FLICKER_DELAY_MS))
            {
                if (flicker % 3 != 0)
                {
                    if (frameFlicker > 0)
                    {
                        hurtable.setShading(frameFlicker);
                    }
                    else if (frameFlicker == 0)
                    {
                        hurtable.setShading(animatable.getFrame() - animation.getFirst() + 1);
                    }
                }
                else
                {
                    if (frameFlicker > 0)
                    {
                        animatable.setFrame(animation.getFirst());
                    }
                    hurtable.setShading(0);
                }
                if (frameFlicker < 0 || flicker == FLICKER_COUNT / 3 && stats.getHealth() == 0)
                {
                    hurtable.kill();
                }
                flicker++;
                tick.restart();
            }
        };
    }

    @Override
    public void enter()
    {
        final int old = animatable.getFrame();
        final Animation anim = animatable.getAnim();

        super.enter();

        if (model.hasFeature(Trackable.class))
        {
            updater = UpdatableVoid.getInstance();
        }
        else
        {
            if (anim != null)
            {
                animatable.play(anim);
            }
            animatable.setFrame(old);
            flicker = 0;
            frameFlicker = hurtable.getFrame().orElse(0);
            if (frameFlicker > 0)
            {
                animatable.stop();
            }
            updater = updateFlicker;
        }

        if (model.getJumpOnHurt() && (model.hasFeature(Trackable.class) || model.hasFeature(Executioner.class)))
        {
            velocity = jump.getVelocity();
            jump.setVelocity(0.1);
            jump.setDirection(0.0, HURT_JUMP_FORCE);
        }
        movement.setVelocity(Constant.WALK_VELOCITY_MAX);
        hurtable.setEnabled(false);
        tick.restart();
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        updater.update(extrp);
        if (!model.hasFeature(Patrol.class))
        {
            body.resetGravity();
        }
        movement.setDestination(device.getHorizontalDirection() * Constant.WALK_SPEED, 0.0);
    }

    @Override
    public void exit()
    {
        super.exit();

        if (model.getJumpOnHurt())
        {
            jump.setVelocity(velocity);
            jump.setDirectionMaximum(Constant.JUMP_MAX);
        }
        hurtable.setShading(0);
        hurtable.setEnabled(true);
    }
}
