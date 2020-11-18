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
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Folder;

/**
 * NestLittle feature implementation.
 * <ol>
 * <li>Throw fly follow on delay.</li>
 * </ol>
 */
@FeatureInterface
public final class NestLittle extends FeatureModel implements Routine, Recyclable
{
    private static final int FIRE_DELAY = 200;

    private final Tick tick = new Tick();

    private final Spawner spawner;

    @FeatureGet private Transformable transformable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public NestLittle(Services services, Setup setup)
    {
        super(services, setup);

        spawner = services.get(Spawner.class);
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsed(FIRE_DELAY))
        {
            Sfx.ENEMY_INSEKT.play();
            spawner.spawn(Medias.create(Folder.MONSTERS, "swamp", "Fly.xml"), transformable);
            tick.restart();
        }
    }

    @Override
    public void recycle()
    {
        tick.restart();
    }
}
