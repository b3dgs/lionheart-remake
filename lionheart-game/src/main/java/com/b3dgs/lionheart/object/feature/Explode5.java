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
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

/**
 * Explode5 feature implementation.
 * <ol>
 * <li>Trigger 5 explodes.</li>
 * </ol>
 */
@FeatureInterface
public final class Explode5 extends FeatureModel implements Routine, Recyclable
{
    // @formatter:off
    private static final int[][] OFFSET =
    {
        {0, 0, 0},
        {-16, 16, 150},
        {16, 16, 300},
        {16, -16, 450},
        {-16, -16, 600}
    };
    //  @formatter:on
    private static final String EXPLODE_FILE = "Explode.xml";

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Spawner spawner = services.get(Spawner.class);

    private final Identifiable identifiable;
    private final Transformable transformable;

    private final Tick tick = new Tick();

    private int phase;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param identifiable The identifiable feature.
     * @param transformable The transformable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Explode5(Services services, Setup setup, Identifiable identifiable, Transformable transformable)
    {
        super(services, setup);

        this.identifiable = identifiable;
        this.transformable = transformable;
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        if (phase == 0)
        {
            for (int i = 0; i < OFFSET.length; i++)
            {
                final int index = i;
                tick.addAction(() -> spawner.spawn(Medias.create(setup.getMedia().getParentPath(), EXPLODE_FILE),
                                                   transformable.getX() + OFFSET[index][0],
                                                   transformable.getY() + OFFSET[index][1]),
                               source.getRate(),
                               OFFSET[index][2]);
            }
            phase++;
            tick.restart();
        }
        else if (phase == 1 && tick.elapsedTime(source.getRate(), OFFSET[OFFSET.length - 1][2] + OFFSET[1][2]))
        {
            identifiable.destroy();
        }
    }

    @Override
    public void recycle()
    {
        phase = 0;
        tick.restart();
    }
}
