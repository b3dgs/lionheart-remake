/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Stats;

/**
 * Die state implementation.
 */
public final class StateDie extends State
{
    /** Maximum horizontal die offset. */
    private static final int DIE_HORIZONTAL_OFFSET_MAX = 48;
    /** Initial die horizontal speed. */
    private static final double DIE_VX = 1.5;
    /** Initial die vertical speed. */
    private static final double DIE_VY = 3.0;
    /** Vertical die acceleration. */
    private static final double DIE_AY = -0.15;
    /** Stay in die state during this delay in milli. */
    private static final long DIE_DELAY_MS = 500;

    private final Tick tick = new Tick();
    private final Stats stats = model.getFeature(Stats.class);

    /** Initial horizontal position. */
    private double x;
    /** Current die horizontal speed. */
    private double vx;
    /** Current die vertical speed. */
    private double vy;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateDie(EntityModel model, Animation animation)
    {
        super(model, animation);

        final SourceResolutionProvider source = model.getServices().get(SourceResolutionProvider.class);
        addTransition(StateDead.class,
                      () -> tick.elapsedTime(source.getRate(), DIE_DELAY_MS)
                            || x - transformable.getX() > DIE_HORIZONTAL_OFFSET_MAX);
    }

    @Override
    public void enter()
    {
        super.enter();

        stats.applyDamages(stats.getHealth());
        movement.zero();
        jump.zero();
        x = transformable.getX();
        vx = DIE_VX;
        vy = DIE_VY;
        Sfx.VALDYN_DIE.play();
        tick.restart();
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        body.resetGravity();
        if (Double.compare(x - transformable.getX(), DIE_HORIZONTAL_OFFSET_MAX) <= 0)
        {
            movement.setDirection(-vx, 0.0);
            movement.setDestination(-vx, 0.0);
            jump.setDirection(0.0, vy);
            jump.setDestination(0.0, vy);
            vy += DIE_AY;
        }
    }

    @Override
    public void exit()
    {
        super.exit();

        movement.zero();
        jump.zero();
    }
}
