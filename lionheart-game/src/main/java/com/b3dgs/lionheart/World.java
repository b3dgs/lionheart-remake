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
package com.b3dgs.lionheart;

import java.io.IOException;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.feature.CameraTracker;
import com.b3dgs.lionengine.game.feature.HandlerPersister;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.WorldGame;
import com.b3dgs.lionengine.game.feature.collidable.ComponentCollision;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormulaConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionGroupConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionModel;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionRenderer;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionRendererModel;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.raster.MapTileRastered;
import com.b3dgs.lionengine.game.feature.tile.map.raster.MapTileRasteredModel;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewer;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.Zooming;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;
import com.b3dgs.lionengine.io.InputDeviceDirectional;
import com.b3dgs.lionengine.io.InputDevicePointer;
import com.b3dgs.lionheart.landscape.FactoryLandscape;
import com.b3dgs.lionheart.landscape.Landscape;
import com.b3dgs.lionheart.landscape.LandscapeType;
import com.b3dgs.lionheart.landscape.WorldType;
import com.b3dgs.lionheart.object.Entity;

/**
 * World game representation.
 */
final class World extends WorldGame
{
    private final MapTile map = services.create(MapTileGame.class);
    private final MapTileViewer mapViewer = map.addFeatureAndGet(new MapTileViewerModel(services));
    private final MapTilePersister mapPersister = map.addFeatureAndGet(new MapTilePersisterOptimized(services));
    private final MapTileRastered mapRaster = map.addFeatureAndGet(new MapTileRasteredModel(services));
    private final Zooming zooming = services.get(Zooming.class);
    private final InputDevicePointer pointer = getInputDevice(InputDevicePointer.class);
    private final FactoryLandscape factoryLandscape;
    private final Hud hud = new Hud();
    private Landscape landscape;
    private Audio audio;

    private double scale = 1;

    /**
     * Create the world.
     * 
     * @param services The services reference.
     */
    public World(Services services)
    {
        super(services);

        try
        {
            services.add(getInputDevice(InputDeviceDirectional.class));
        }
        catch (final LionEngineException exception)
        {
            services.add(InputDeviceControlVoid.getInstance());
        }

        factoryLandscape = new FactoryLandscape(source, false);

        handler.addComponent(new ComponentCollision());
        handler.add(map);
    }

    @Override
    public void update(double extrp)
    {
        pointer.update(extrp);
        if (pointer.getClick() == 2)
        {
            scale = UtilMath.clamp(scale + pointer.getMoveY() / 100.0, 0.5, 1.42);
            zooming.setZoom(scale);
        }
        super.update(extrp);
        landscape.update(extrp, camera);
        camera.moveLocation(extrp, 0.0, 0.0);
        hud.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        landscape.renderBackground(g);
        super.render(g);
        landscape.renderForeground(g);
        hud.render(g);
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
        mapRaster.loadSheets(Medias.create(map.getMedia().getParentPath(), "raster1.xml"), false);
        mapViewer.clear();
        mapViewer.addRenderer(mapRaster);

        final String world = WorldType.SWAMP.getFolder();

        map.addFeature(new LayerableModel(3, 1));
        map.addFeatureAndGet(new MapTileGroupModel())
           .loadGroups(Medias.create(Constant.FOLDER_LEVELS, world, TileGroupsConfig.FILENAME));
        map.addFeatureAndGet(new MapTileCollisionModel(services))
           .loadCollisions(Medias.create(Constant.FOLDER_LEVELS, world, CollisionFormulaConfig.FILENAME),
                           Medias.create(Constant.FOLDER_LEVELS, world, CollisionGroupConfig.FILENAME));

        if (Constant.DEBUG)
        {
            final MapTileCollisionRenderer mapCollisionRenderer;
            mapCollisionRenderer = map.addFeatureAndGet(new MapTileCollisionRendererModel(services));
            mapCollisionRenderer.createCollisionDraw();
            mapViewer.addRenderer(mapCollisionRenderer);
        }

        camera.setLimits(map);
        camera.setIntervals(16, 0);

        landscape = factoryLandscape.createLandscape(LandscapeType.SWAMP_DUSK);

        final HandlerPersister handlerPersister = new HandlerPersister(services);
        handlerPersister.load(file);

        final Entity valdyn = factory.create(Medias.create(Constant.FOLDER_PLAYERS, "default", "Valdyn.xml"));

        final Transformable valdynTransformable = valdyn.getFeature(Transformable.class);
        valdynTransformable.teleport(200, 64);
        handler.add(valdyn);
        hud.setFeaturable(valdyn);

        final CameraTracker tracker = new CameraTracker(services);
        tracker.addFeature(new LayerableModel(2));
        tracker.setOffset(0, valdynTransformable.getHeight() / 2);
        tracker.track(valdynTransformable);
        handler.add(tracker);

        audio = AudioFactory.loadAudio(Music.SWAMP.get());
        audio.setVolume(50);
        audio.play();

        zooming.setZoom(scale);
    }

    @Override
    public void onResolutionChanged(int width, int height)
    {
        camera.setView(0, 0, width, height, height);
        landscape.setScreenSize(width, height);
    }
}
