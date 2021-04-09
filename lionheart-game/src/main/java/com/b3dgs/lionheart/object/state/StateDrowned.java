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
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Drownable;
import com.b3dgs.lionheart.object.feature.Stats;

/**
 * Drowned state implementation.
 */
public final class StateDrowned extends State
{
    /** Drown limit tick. */
    private static final int DROWN_END_TICK = 60;
    /** Drown fall speed. */
    private static final double DEATH_FALL_SPEED = -0.7;

    private final Tick tick = new Tick();
    private final Stats stats = model.getFeature(Stats.class);
    private final Drownable drownable = model.getFeature(Drownable.class);

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateDrowned(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateRespawn.class, () -> tick.elapsed(DROWN_END_TICK));
    }

    @Override
    public void enter()
    {
        super.enter();

        stats.applyDamages(stats.getHealth());
        movement.zero();
        Sfx.VALDYN_DIE.play();
        tick.restart();
    }

    @Override
    public void exit()
    {
        super.exit();

        drownable.recycle();
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        body.resetGravity();
        model.getMovement().setDestination(0.0, DEATH_FALL_SPEED);
        model.getMovement().setDirection(0.0, DEATH_FALL_SPEED);
    }
}
