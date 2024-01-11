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

import com.b3dgs.lionengine.AnimatorAnimListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionheart.Sfx;

/**
 * TurningCube feature implementation.
 * <ol>
 * <li>Play sound on turning.</li>
 * </ol>
 */
@FeatureInterface
public final class TurningCube extends FeatureModel
{
    private final Viewer viewer = services.get(Viewer.class);

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @param animatable The animatable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public TurningCube(Services services, Setup setup, Transformable transformable, Animatable animatable)
    {
        super(services, setup);

        animatable.addListener((AnimatorAnimListener) anim ->
        {
            if (anim.getFirst() > 1 && viewer.isViewable(transformable, 0, 0))
            {
                Sfx.SCENERY_TURNINGCUBE.play();
            }
        });
    }
}
