/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.rasterable.RasterableModel;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionheart.Constant;
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
    private final SetupSurfaceRastered setup;

    private final double speedUp;
    private final double speedDown;
    private final int max;
    private final boolean hit;
    private final boolean waterLevel;
    private Rasterable rasterableWater;
    private boolean start;
    private double down;

    @FeatureGet private EntityModel model;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Glue glue;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Rasterable rasterable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Floater(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);

        this.setup = setup;
        speedUp = setup.getDouble(ATT_SPEEDUP, NODE);
        speedDown = setup.getDouble(ATT_SPEEDDOWN, NODE);
        max = setup.getInteger(ATT_MAX, NODE);
        waterLevel = setup.getBooleanDefault(true, ATT_WATER_LEVEL, NODE);
        hit = setup.getBooleanDefault(false, ATT_HIT, NODE);
    }

    /**
     * Load raster data.
     * 
     * @param raster The raster folder.
     */
    public void loadRaster(String raster)
    {
        rasterableWater.setRaster(false, Medias.create(raster, Constant.RASTER_FILE_WATER), transformable.getHeight());
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

        rasterableWater = new RasterableModel(services, new SetupSurfaceRastered(setup.getMedia()))
        {
            @Override
            public int getRasterIndex(double y)
            {
                return UtilMath.clamp((int) Math.floor(water.getCurrent() - transformable.getY()),
                                      0,
                                      transformable.getHeight())
                       - 2;
            }
        };
        rasterableWater.prepare(provider);
    }

    @Override
    public void update(double extrp)
    {
        if (Double.compare(transformable.getY(), water.getCurrent()) <= 0)
        {
            if (waterLevel)
            {
                glue.setTransformY(() -> -water.getCurrent() + transformable.getHeight() / 2.5 - down);
            }
            rasterableWater.setVisibility(rasterable.isVisible());
            rasterableWater.update(extrp);
        }
        else
        {
            glue.setTransformY(null);
            rasterableWater.setVisibility(false);
        }
        if (start)
        {
            down -= speedDown;
            if (down < -max)
            {
                down = -max;
            }
        }
        else
        {
            down += speedUp;
            if (down > 0)
            {
                down = 0.0;
            }
        }
    }

    @Override
    public void render(Graphic g)
    {
        if (rasterableWater.getRasterIndex(0) > 0)
        {
            rasterableWater.render(g);
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        final Force movement = model.getMovement();
        if (hit && by.getName().startsWith(Anim.ATTACK) && movement.isZero())
        {
            final Transformable other = collidable.getFeature(Transformable.class);
            if (transformable.getX() > other.getX())
            {
                movement.setDirection(3.0, 0.0);
            }
            else
            {
                movement.setDirection(-3.0, 0.0);
            }
        }
    }

    @Override
    public void recycle()
    {
        start = false;
        down = 0.0;
    }
}
