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
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.rasterable.RasterableModel;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.MapTileWater;
import com.b3dgs.lionheart.object.feature.Glue.GlueListener;

/**
 * Floater feature implementation.
 * <p>
 * Follow water level and move down on collide.
 * </p>
 */
@FeatureInterface
public final class Floater extends FeatureModel implements Routine, Recyclable
{
    private static final double UP_SPEED = 1.0;
    private static final double DOWN_SPEED = 0.4;

    private final MapTileWater water = services.get(MapTileWater.class);
    private final SetupSurfaceRastered setup;

    private Rasterable rasterable;
    private boolean start;
    private double down;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Glue glue;

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
    }

    /**
     * Load raster data.
     * 
     * @param raster The raster folder.
     */
    public void loadRaster(String raster)
    {
        rasterable.setRaster(false, Medias.create(raster, Constant.RASTER_FILE_WATER), transformable.getHeight());
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        glue.setTransformY(() -> -water.getCurrent() + transformable.getHeight() / 2.5 - down);
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

        rasterable = new RasterableModel(services, new SetupSurfaceRastered(setup.getMedia()))
        {
            @Override
            public int getRasterIndex(double y)
            {
                return -UtilMath.clamp((int) Math.floor(down - transformable.getHeight() / 2.5),
                                       -transformable.getHeight(),
                                       0);
            }
        };
        rasterable.prepare(provider);
    }

    @Override
    public void update(double extrp)
    {
        if (start)
        {
            down -= DOWN_SPEED;
        }
        else
        {
            down += UP_SPEED;
            if (down > 0)
            {
                down = 0.0;
            }
        }
        rasterable.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        if (rasterable.getRasterIndex(0) > 0)
        {
            rasterable.render(g);
        }
    }

    @Override
    public void recycle()
    {
        start = false;
        down = 0.0;
    }
}
