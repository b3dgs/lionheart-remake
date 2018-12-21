/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
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
import com.b3dgs.lionheart.InputDeviceControlVoid;

/**
 * Patrol feature implementation.
 */
public final class Patrol extends FeatureModel implements Routine, CollidableListener, Recyclable
{
    /** Explode effect. */
    public static final Media EFFECT = Medias.create(Constant.FOLDER_EFFECTS, "ExplodeBig.xml");

    private final Tick change = new Tick();
    private final Spawner spawner;

    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private EntityModel model;

    private double direction = 0.3;
    private boolean spawned;

    /**
     * Create patrol.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Patrol(Services services, SetupSurfaceRastered setup)
    {
        super();

        spawner = services.get(Spawner.class);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        model.setInput(new InputDeviceControlVoid()
        {
            @Override
            public double getHorizontalDirection()
            {
                return direction;
            }
        });
        change.start();
    }

    @Override
    public void update(double extrp)
    {
        change.update(extrp);
        if (change.elapsed(100L))
        {
            direction = -direction;
            change.restart();
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision collision)
    {
        if (!spawned)
        {
            spawner.spawn(EFFECT, transformable.getX(), transformable.getY() - transformable.getHeight() / 2);
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
