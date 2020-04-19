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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Stats;

/**
 * Dead state implementation.
 */
public final class StateDead extends State
{
    /** Stay in dead state during this delay in tick. */
    private static final long DEAD_DELAY_TICK = 60L;

    private final Tick tick = new Tick();
    private final Stats stats = model.getFeature(Stats.class);

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateDead(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateIdle.class, () -> tick.elapsed(DEAD_DELAY_TICK));
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.zero();
        tick.restart();
    }

    @Override
    public void exit()
    {
        super.exit();

        transformable.teleport(204, 64);
        model.getCamera().resetInterval(transformable);
        mirrorable.mirror(Mirror.NONE);
        stats.fillHealth();
        stats.decreaseLife();
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
    }
}
