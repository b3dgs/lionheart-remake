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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.object.state.StateJumpSpider;

/**
 * Turtle feature implementation.
 * <ol>
 * <li>Jump on random.</li>
 * </ol>
 */
@FeatureInterface
public final class Turtle extends FeatureModel implements RoutineUpdate, Recyclable
{
    private static final int IDLE_TIME_MS = 3000;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);

    private final StateHandler stateHandler;
    private final Floater floater;

    private final Tick tick = new Tick();

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param stateHandler The state feature.
     * @param floater The floater feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Turtle(Services services, Setup setup, StateHandler stateHandler, Floater floater)
    {
        super(services, setup);

        this.stateHandler = stateHandler;
        this.floater = floater;
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);

        if (tick.elapsedTime(source.getRate(), IDLE_TIME_MS))
        {
            stateHandler.changeState(StateJumpSpider.class);
            floater.stop();
            tick.restart();
        }
    }

    @Override
    public void recycle()
    {
        tick.restart();
    }
}
