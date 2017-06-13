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
package com.b3dgs.lionheart;

import java.io.IOException;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.sequence.ResolutionChanger;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.WorldGame;
import com.b3dgs.lionengine.game.feature.collidable.ComponentCollision;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.game.feature.tile.map.raster.MapTileRastered;
import com.b3dgs.lionengine.game.feature.tile.map.raster.MapTileRasteredModel;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewer;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;
import com.b3dgs.lionengine.io.InputDevicePointer;
import com.b3dgs.lionengine.util.UtilMath;
import com.b3dgs.lionheart.landscape.FactoryLandscape;
import com.b3dgs.lionheart.landscape.Landscape;
import com.b3dgs.lionheart.landscape.LandscapeType;

/**
 * World game representation.
 */
public class World extends WorldGame
{
    /** Resolution changer. */
    private final ResolutionChanger changer = services.get(ResolutionChanger.class);
    /** Map reference. */
    private final MapTile map = services.create(MapTileGame.class);
    /** Map viewer. */
    private final MapTileViewer mapViewer = map.addFeatureAndGet(new MapTileViewerModel(services));
    /** Map persister. */
    private final MapTilePersister mapPersister = map.addFeatureAndGet(new MapTilePersisterModel(services));
    /** Map raster. */
    private final MapTileRastered mapRaster = map.addFeatureAndGet(new MapTileRasteredModel(services));
    /** Pointer device. */
    private final InputDevicePointer pointer = getInputDevice(InputDevicePointer.class);
    /** Landscape factory. */
    private final FactoryLandscape factoryLandscape;
    /** Landscape. */
    private Landscape landscape;

    private double scale = 1.0;

    /**
     * Create the world.
     * 
     * @param context The context reference.
     * @param services The services reference.
     */
    public World(Context context, Services services)
    {
        super(context, services);

        final double scaleH = config.getSource().getWidth() / (double) Constant.NATIVE.getWidth();
        final double scaleV = config.getSource().getHeight() / (double) Constant.NATIVE.getHeight();

        factoryLandscape = new FactoryLandscape(source, scaleH, scaleV, false);

        handler.addComponent(new ComponentCollision());
        handler.add(map);
    }

    @Override
    public void update(double extrp)
    {
        pointer.update(extrp);
        if (pointer.getClick() == 1)
        {
            camera.moveLocation(extrp, -pointer.getMoveX(), pointer.getMoveY());
        }
        else if (pointer.getClick() == 2)
        {
            scale = UtilMath.clamp(scale + pointer.getMoveY() / 100.0, 0.5, 1.42);
            changer.setResolution(source.getScaled(scale, scale));
        }
        super.update(extrp);
        landscape.update(extrp, camera);
        camera.moveLocation(extrp, 0.0, 0.0);
    }

    @Override
    public void render(Graphic g)
    {
        landscape.renderBackground(g);
        super.render(g);
        landscape.renderForeground(g);
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        mapPersister.save(file);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        mapPersister.load(file);
        mapRaster.loadSheets(Medias.create(map.getMedia().getParentPath(), "raster3.xml"), false);
        mapViewer.clear();
        mapViewer.addRenderer(mapRaster);

        camera.setLimits(map);
        camera.setIntervals(0, 0);

        landscape = factoryLandscape.createLandscape(LandscapeType.SWAMP_DAY);
    }

    @Override
    public void onResolutionChanged(int width, int height, int rate)
    {
        camera.setView(0, 0, width, height, height);
        landscape.setScreenSize(width, height);
    }
}
