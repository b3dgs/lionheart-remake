/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object.state.executioner;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.feature.state.StateLast;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;

/**
 * Defense executioner state implementation.
 */
public final class StateExecutionerDefense extends State
{
    private static final int DEFENSE_DELAY_MS = 500;

    private final Tick tick = new Tick();

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateExecutionerDefense(EntityModel model, Animation animation)
    {
        super(model, animation);

        final SourceResolutionProvider source = model.getServices().get(SourceResolutionProvider.class);
        addTransition(StateLast.class, () -> tick.elapsedTime(source.getRate(), DEFENSE_DELAY_MS));
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.zero();
        tick.restart();
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        tick.update(extrp);
    }
}
