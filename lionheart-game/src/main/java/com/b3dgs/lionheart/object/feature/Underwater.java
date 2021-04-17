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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.rasterable.RasterableModel;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.MapTileWater;

/**
 * Floater feature implementation.
 * <p>
 * Follow water level and move down on collide.
 * </p>
 */
@FeatureInterface
public final class Underwater extends FeatureModel implements Routine
{
    private static final String NODE = "underwater";

    private final MapTileWater water = services.get(MapTileWater.class);
    private final SetupSurfaceRastered setup;

    private Rasterable rasterableWater;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Rasterable rasterable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Underwater(Services services, SetupSurfaceRastered setup)
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
        rasterableWater.setRaster(false, Medias.create(raster, Constant.RASTER_FILE_WATER), transformable.getHeight());
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        final FramesConfig config = FramesConfig.imports(setup);

        final Media media;
        if (setup.hasNode(NODE))
        {
            media = Medias.create(setup.getText(NODE), setup.getMedia().getName());
        }
        else
        {
            media = setup.getMedia();
        }
        final SetupSurfaceRastered raster = new SetupSurfaceRastered(media);
        rasterableWater = new RasterableModel(services, raster)
        {
            @Override
            public int getRasterIndex(double y)
            {
                return UtilMath.clamp((int) Math.floor(water.getCurrent() - transformable.getY()),
                                      0,
                                      transformable.getHeight())
                       - 2
                       + config.getOffsetY();
            }
        };
        rasterableWater.prepare(provider);
        rasterableWater.setFrameOffsets(config.getOffsetX(), config.getOffsetY());

        if (setup.hasNode(NODE))
        {
            final AnimationConfig anims = AnimationConfig.imports(raster);
            rasterableWater.setAnimTransform((name, frame) ->
            {
                if (anims.hasAnimation(name))
                {
                    rasterableWater.setVisibility(true);
                    return anims.getAnimation(name).getFirst() + frame - 1;
                }
                rasterableWater.setVisibility(false);
                return Animation.MINIMUM_FRAME;
            });
        }
    }

    @Override
    public void update(double extrp)
    {
        if (Double.compare(transformable.getY(), water.getCurrent()) <= 0)
        {
            rasterableWater.setVisibility(rasterable.isVisible());
            rasterableWater.update(extrp);
        }
        else
        {
            rasterableWater.setVisibility(false);
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
}
