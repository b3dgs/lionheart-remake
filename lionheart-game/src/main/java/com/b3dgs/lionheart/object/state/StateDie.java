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
import com.b3dgs.lionengine.game.DirectionNone;
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
    private static final int DIE_HORIZONTAL_OFFSET_MAX = 25;
    /** Initial die horizontal speed. */
    private static final double DIE_VX = 0.8;
    /** Initial die vertical speed. */
    private static final double DIE_VY = 0.4;
    /** Horizontal die acceleration. */
    private static final double DIE_AX = 0.1;
    /** Vertical die acceleration. */
    private static final double DIE_AY = 0.2;

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

        addTransition(StateDead.class, () -> transformable.getX() - x > DIE_HORIZONTAL_OFFSET_MAX);
    }

    @Override
    public void enter()
    {
        super.enter();

        stats.applyDamages(stats.getHealth());
        movement.setDirection(DirectionNone.INSTANCE);
        movement.setDestination(0.0, 0.0);
        x = transformable.getX();
        vx = DIE_VX;
        vy = DIE_VY;
        Sfx.VALDYN_DIE.play();
    }

    @Override
    public void update(double extrp)
    {
        body.resetGravity();
        if (Double.compare(transformable.getX() - x, DIE_HORIZONTAL_OFFSET_MAX) <= 0)
        {
            transformable.moveLocation(extrp, vx, vy);
            vx += DIE_AX;
            vy += DIE_AY;
        }
    }
}
