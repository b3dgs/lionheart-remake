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
package com.b3dgs.lionheart.object.state;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Hurtable;
import com.b3dgs.lionheart.object.feature.Patrol;
import com.b3dgs.lionheart.object.feature.Stats;
import com.b3dgs.lionheart.object.feature.SwordShade;

/**
 * Hurt state implementation.
 */
public final class StateHurt extends State
{
    private static final double HURT_JUMP_FORCE = 3.5;
    private static final int FLICKER_COUNT = 16;

    private final Hurtable hurtable = model.getFeature(Hurtable.class);
    private final Stats stats = model.getFeature(Stats.class);
    private final Updatable updateFlicker;

    private Updatable updater;
    private int frameFlicker;
    private int flicker;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateHurt(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateIdle.class,
                      () -> !hurtable.isHurting()
                            && animatable.getFrame() == animatable.getAnim().getLast()
                            && !model.hasGravity());
        addTransition(StateFall.class, () -> !hurtable.isHurting() && model.hasGravity());

        updateFlicker = extrp ->
        {
            if (flicker < FLICKER_COUNT)
            {
                if (flicker % 3 != 0)
                {
                    if (frameFlicker > 0)
                    {
                        animatable.setFrame(frameFlicker);
                    }
                    else if (frameFlicker == 0)
                    {
                        rasterable.setAnimOffset(model.getFrames() / 2);
                    }
                }
                else
                {
                    if (frameFlicker > 0)
                    {
                        animatable.setFrame(animation.getFirst());
                    }
                    rasterable.setAnimOffset(0);
                }
                if (frameFlicker < 0 || flicker == FLICKER_COUNT / 3 && stats.getHealth() == 0)
                {
                    hurtable.kill();
                }
                flicker++;
            }
        };
    }

    @Override
    public void enter()
    {
        final int old = animatable.getFrame();
        final Animation anim = animatable.getAnim();

        super.enter();

        if (model.hasFeature(SwordShade.class))
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

        if (model.hasFeature(SwordShade.class))
        {
            jump.setDirection(0.0, HURT_JUMP_FORCE);
        }
        movement.setVelocity(Constant.WALK_VELOCITY_MAX);
        hurtable.setEnabled(false);
    }

    @Override
    public void update(double extrp)
    {
        updater.update(extrp);
        if (!model.hasFeature(Patrol.class))
        {
            body.resetGravity();
        }
        movement.setDestination(input.getHorizontalDirection() * Constant.WALK_SPEED, 0.0);
    }

    @Override
    public void exit()
    {
        super.exit();

        rasterable.setAnimOffset(0);
        jump.setDirectionMaximum(Constant.JUMP_MAX);
        hurtable.setEnabled(true);
    }
}
