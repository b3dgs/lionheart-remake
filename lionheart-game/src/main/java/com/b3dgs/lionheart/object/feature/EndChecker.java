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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.graphic.engine.Sequencer;
import com.b3dgs.lionheart.Checkpoint;
import com.b3dgs.lionheart.Scene;
import com.b3dgs.lionheart.StageConfig;

/**
 * End checker feature implementation.
 * <ol>
 * <li>Check for level end proximity.</li>
 * <li>Trigger next level on end.</li>
 * </ol>
 */
@FeatureInterface
public final class EndChecker extends FeatureModel implements Routine
{
    private final Sequencer sequencer = services.get(Sequencer.class);
    private final Checkpoint checkpoint = services.get(Checkpoint.class);
    private final Media nextStage;

    @FeatureGet private Transformable transformable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public EndChecker(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);

        nextStage = services.get(StageConfig.class).getNextStage();
    }

    @Override
    public void update(double extrp)
    {
        if (checkpoint.isOnEnd(transformable))
        {
            sequencer.end(Scene.class, nextStage);
        }
    }
}
