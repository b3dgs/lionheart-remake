/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * Flower feature implementation.
 * <ol>
 * <li>Point player.</li>
 * <li>Throw projectile on delay.</li>
 * </ol>
 */
@FeatureInterface
public final class Flower extends FeatureModel implements Routine
{
    private final Transformable track;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Stats stats;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Flower(Services services, Setup setup)
    {
        super(services, setup);

        track = services.get(SwordShade.class).getFeature(Transformable.class);
    }

    @Override
    public void update(double extrp)
    {
        if (stats.getHealth() > 0)
        {
            final double margin = track.getX() - track.getWidth() / 2 - transformable.getX();
            final int frame = UtilMath.clamp((int) Math.round(margin) / 16, -4, 3) + 5;
            animatable.setFrame(frame);
        }
        else
        {
            animatable.setFrame(9);
        }
    }
}
