/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionheart.object;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionheart.Constant;

/**
 * Takeable feature implementation.
 */
@FeatureInterface
public final class Takeable extends FeatureModel implements CollidableListener, Recyclable
{
    /** Taken effect. */
    public static final Media EFFECT = Medias.create(Constant.FOLDER_EFFECTS, "Taken.xml");

    private final Spawner spawner;
    private final StatsConfig stats;

    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Transformable transformable;

    private boolean spawned;

    /**
     * Create takeable.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Takeable(Services services, SetupSurfaceRastered setup)
    {
        super();

        spawner = services.get(Spawner.class);
        stats = StatsConfig.imports(setup);
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision collision)
    {
        if (!spawned)
        {
            collidable.getFeature(StatsModel.class).apply(stats);
            spawner.spawn(EFFECT, transformable.getX(), transformable.getY());
            identifiable.destroy();
            spawned = true;
        }
    }

    @Override
    public void recycle()
    {
        spawned = false;
    }
}
