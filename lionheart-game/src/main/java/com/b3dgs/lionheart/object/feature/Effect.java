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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.AnimatorStateListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.object.state.StateIdle;

/**
 * Effect feature implementation.
 * <ol>
 * <li>Listen to animation state changes.</li>
 * <li>On {@link AnimState#FINISHED}, destroy and reset.</li>
 * </ol>
 */
@FeatureInterface
public final class Effect extends FeatureModel implements Routine, Recyclable
{
    private static final String NODE_SFX_EXPLODE = "sfx_explode";
    private static final String ATT_COUNT = "count";
    private static final int DELAY_MS = 150;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Viewer viewer = services.get(Viewer.class);

    private final Transformable transformable;
    private final StateHandler stateHandler;

    private final Tick tick = new Tick();
    private final int count;

    private int current;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param identifiable The identifiable feature.
     * @param animatable The animatable feature.
     * @param transformable The transformable feature.
     * @param stateHandler The state feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Effect(Services services,
                  Setup setup,
                  Identifiable identifiable,
                  Animatable animatable,
                  Transformable transformable,
                  StateHandler stateHandler)
    {
        super(services, setup);

        this.transformable = transformable;
        this.stateHandler = stateHandler;

        count = setup.getInteger(0, ATT_COUNT, NODE_SFX_EXPLODE);

        animatable.addListener((AnimatorStateListener) state ->
        {
            if (AnimState.FINISHED == state)
            {
                identifiable.destroy();
            }
        });
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        if (current < count && tick.elapsedTime(source.getRate(), DELAY_MS))
        {
            if (viewer.isViewable(transformable, 0, 0))
            {
                Sfx.playRandomExplode();
            }
            current++;
            tick.restart();
        }
    }

    @Override
    public void recycle()
    {
        stateHandler.changeState(StateIdle.class);
        current = 0;
        tick.restart();
        tick.set(DELAY_MS);
    }
}
