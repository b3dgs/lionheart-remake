/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionheart.MapTileWater;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.feature.Glue.GlueListener;

/**
 * Floater feature implementation.
 * <p>
 * Follow water level and move down on collide.
 * </p>
 */
@FeatureInterface
public final class Floater extends FeatureModel implements Routine, Recyclable, CollidableListener
{
    private static final String NODE = "floater";
    private static final String ATT_SPEEDUP = "speedUp";
    private static final String ATT_SPEEDDOWN = "speedDown";
    private static final String ATT_MAX = "max";
    private static final String ATT_WATER_LEVEL = "waterLevel";
    private static final String ATT_HIT = "hit";

    private final MapTileWater water = services.get(MapTileWater.class);
    private final Viewer viewer = services.get(Viewer.class);

    private final double speedUp;
    private final double speedDown;
    private final int max;
    private final boolean hit;
    private final boolean waterLevel;
    private boolean start;
    private double down;
    private Localizable origin;

    @FeatureGet private EntityModel model;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Glue glue;
    @FeatureGet private Body body;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Floater(Services services, Setup setup)
    {
        super(services, setup);

        speedUp = setup.getDouble(ATT_SPEEDUP, NODE);
        speedDown = setup.getDouble(ATT_SPEEDDOWN, NODE);
        max = setup.getInteger(0, ATT_MAX, NODE);
        waterLevel = setup.getBoolean(true, ATT_WATER_LEVEL, NODE);
        hit = setup.getBoolean(false, ATT_HIT, NODE);
    }

    /**
     * Stop glue.
     */
    public void stop()
    {
        glue.setTransformY(null);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        glue.start();
        glue.addListener(new GlueListener()
        {
            @Override
            public void notifyStart(Transformable transformable)
            {
                start = true;
            }

            @Override
            public void notifyEnd(Transformable transformable)
            {
                start = false;
            }
        });
    }

    @Override
    public void update(double extrp)
    {
        if (origin == null)
        {
            origin = Geom.createLocalizable(transformable.getX(), transformable.getY());
        }
        if (Double.compare(transformable.getY(), water.getCurrent() - transformable.getHeight() / 2.5 + down) <= 0)
        {
            if (waterLevel)
            {
                glue.setTransformY(() -> -water.getCurrent() + transformable.getHeight() / 2.5 - down);
            }
        }
        else
        {
            glue.setTransformY(null);
        }
        if (start)
        {
            down -= speedDown * extrp;
            if (max > 0 && down < -max)
            {
                down = -max;
            }
        }
        else
        {
            down += speedUp * extrp;
            if (down > 0)
            {
                down = 0.0;
            }
        }
        if (!viewer.isViewable(transformable, viewer.getWidth() / 2, viewer.getHeight() / 2)
            && !viewer.isViewable(origin, 0, 0))
        {
            transformable.teleport(origin.getX(), origin.getY());
            body.resetGravity();
            recycle();
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        final Force movement = model.getMovement();
        if (hit && by.getName().startsWith(Anim.ATTACK))
        {
            final Transformable other = collidable.getFeature(Transformable.class);
            if (transformable.getX() > other.getX())
            {
                movement.setDirection(3.5, 0.0);
            }
            else
            {
                movement.setDirection(-3.5, 0.0);
            }
        }
    }

    @Override
    public void recycle()
    {
        glue.setTransformY(null);
        start = false;
        down = 0.0;
    }
}
