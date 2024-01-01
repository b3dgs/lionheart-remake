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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

/**
 * Effect feature implementation.
 * <ol>
 * <li>Listen to animation state changes.</li>
 * <li>On {@link AnimState#FINISHED}, destroy and reset.</li>
 * </ol>
 */
@FeatureInterface
public final class NorkaPlatform extends FeatureModel implements Routine, Recyclable
{
    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);

    private final Transformable transformable;
    private final Hurtable hurtable;
    private final Launchable launchable;

    private final Tick tick = new Tick();

    private boolean first;
    private double startX;
    private int delay;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @param hurtable The hurtable feature.
     * @param launchable The launchable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public NorkaPlatform(Services services,
                         Setup setup,
                         Transformable transformable,
                         Hurtable hurtable,
                         Launchable launchable)
    {
        super(services, setup);

        this.transformable = transformable;
        this.hurtable = hurtable;
        this.launchable = launchable;
    }

    private int getMaxX()
    {
        final int max;
        if (Double.compare(launchable.getDirection().getDirectionVertical(), -3.0) == 0)
        {
            max = 48;
            delay = 4600;
        }
        else if (Double.compare(launchable.getDirection().getDirectionVertical(), -0.5) == 0)
        {
            max = 112;
            delay = 4300;
        }
        else
        {
            max = 76;
            delay = 4000;
        }
        return max;
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);

        if (first)
        {
            startX = transformable.getX();
            if (startX > 224)
            {
                final Force direction = launchable.getDirection();
                final double dx = direction.getDirectionHorizontal();
                final double dy = direction.getDirectionVertical();
                direction.setDirection(-dx, dy);
                direction.setDestination(-dx, dy);
            }
            first = false;
        }

        if (!tick.isStarted() && Math.abs(startX - transformable.getX()) > getMaxX())
        {
            launchable.getDirection().zero();
            tick.start();
        }
        if (tick.elapsedTime(source.getRate(), delay))
        {
            hurtable.kill(true);
        }
    }

    @Override
    public void recycle()
    {
        first = true;
        tick.stop();
    }
}
