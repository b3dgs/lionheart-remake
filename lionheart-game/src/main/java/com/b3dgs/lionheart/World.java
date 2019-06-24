/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart;

import java.io.IOException;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.feature.CameraTracker;
import com.b3dgs.lionengine.game.feature.HandlerPersister;
import com.b3dgs.lionengine.game.feature.Layerable;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.WorldGame;
import com.b3dgs.lionengine.game.feature.collidable.ComponentCollision;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormulaConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionGroupConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;
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
import com.b3dgs.lionengine.io.InputDeviceControlVoid;
import com.b3dgs.lionengine.io.InputDeviceDirectional;
import com.b3dgs.lionengine.io.InputDevicePointer;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.landscape.FactoryLandscape;
import com.b3dgs.lionheart.landscape.Landscape;
import com.b3dgs.lionheart.landscape.LandscapeType;
import com.b3dgs.lionheart.object.Entity;

/**
 * World game representation.
 */
final class World extends WorldGame
{
    private static final String ERROR_INPUT_DEVICE = "Void input device used !";

    private final MapTile map = services.create(MapTileGame.class);
    private final MapTileGroup mapGroup = map.addFeatureAndGet(new MapTileGroupModel());
    private final MapTileCollision mapCollision = map.addFeatureAndGet(new MapTileCollisionModel(services));
    private final MapTileViewer mapViewer = map.addFeatureAndGet(new MapTileViewerModel(services));
    private final MapTileRastered mapRaster = map.addFeatureAndGet(new MapTileRasteredModel(services));
    private final MapTilePersister mapPersister = map.addFeatureAndGet(new MapTilePersisterOptimized(services));
    private final HandlerPersister handlerPersister = new HandlerPersister(services);
    private final FactoryLandscape factoryLandscape = new FactoryLandscape(source, false);
    private final Hud hud = new Hud(services);
    private final Zooming zooming = services.get(Zooming.class);
    private final InputDevicePointer pointer = services.add(getInputDevice(InputDevicePointer.class));
    private final MapTileCollisionRenderer mapCollisionRenderer;

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

        addInputDevice();

        map.addFeature(new LayerableModel(3, 1));
        mapCollisionRenderer = map.addFeatureAndGet(new MapTileCollisionRendererModel(services));

        handler.addComponent(new ComponentCollision());
        handler.add(map);

        camera.setIntervals(16, 0);
    }

    /**
     * Add input device or void if none.
     */
    private void addInputDevice()
    {
        try
        {
            services.add(getInputDevice(InputDeviceDirectional.class));
        }
        catch (final LionEngineException exception)
        {
            Verbose.exception(exception, ERROR_INPUT_DEVICE);
            services.add(InputDeviceControlVoid.getInstance());
        }
    }

    /**
     * Load map from level.
     * 
     * @param file The level file.
     * @param worldType The world type.
     * @param landscapeType The landscape type.
     * @throws IOException If error on reading level.
     */
    private void loadMap(FileReading file, WorldType worldType, LandscapeType landscapeType) throws IOException
    {
        mapPersister.load(file);
        mapRaster.loadSheets(Medias.create(map.getMedia().getParentPath(), landscapeType.getRaster()), false);

        final String world = worldType.getFolder();
        mapGroup.loadGroups(Medias.create(Folder.LEVELS, world, TileGroupsConfig.FILENAME));
        mapCollision.loadCollisions(Medias.create(Folder.LEVELS, world, CollisionFormulaConfig.FILENAME),
                                    Medias.create(Folder.LEVELS, world, CollisionGroupConfig.FILENAME));

        mapViewer.clear();
        mapViewer.addRenderer(mapRaster);

        if (Constant.DEBUG)
        {
            mapCollisionRenderer.createCollisionDraw();
            mapViewer.addRenderer(mapCollisionRenderer);
        }

        camera.setLimits(map);
    }

    /**
     * Load all entities from level.
     * 
     * @param file The file level.
     * @throws IOException If error.
     */
    private void loadEntities(FileReading file) throws IOException
    {
        handlerPersister.load(file);

        final Entity player = factory.create(Medias.create(Folder.PLAYERS, "default", "Valdyn.xml"));

        final Transformable playerTransformable = player.getFeature(Transformable.class);
        playerTransformable.teleport(204, 64);
        handler.add(player);
        hud.setFeaturable(player);

        trackPlayer(playerTransformable);
    }

    /**
     * Track player with camera.
     * 
     * @param player The player to track.
     */
    private void trackPlayer(Transformable player)
    {
        final CameraTracker tracker = new CameraTracker(services);
        tracker.addFeature(new LayerableModel(player.getFeature(Layerable.class).getLayerRefresh().intValue() + 1));
        tracker.setOffset(0, player.getHeight() / 2);
        tracker.track(player);
        handler.add(tracker);
    }

    /**
     * Prepare cached media.
     */
    private void prepareCache()
    {
        factory.createCache(Medias.create(Folder.EFFECTS), 5);
        Sfx.cache();
    }

    /**
     * Update scene zoom on click.
     */
    private void updateZoom()
    {
        if (pointer.getClick() == 2)
        {
            scale = UtilMath.clamp(scale + pointer.getMoveY() / 100.0, 0.5, 1.42);
            zooming.setZoom(scale);
        }
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        mapPersister.save(file);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        final WorldType worldType = WorldType.SWAMP;
        final LandscapeType landscapeType = LandscapeType.SWAMP_DUSK;

        loadMap(file, worldType, landscapeType);
        loadEntities(file);
        prepareCache();

        hud.load();
        landscape = factoryLandscape.createLandscape(landscapeType);

        audio = AudioFactory.loadAudio(worldType.getMusic());
        audio.setVolume(25);
        audio.play();

        zooming.setZoom(scale);
    }

    @Override
    public void update(double extrp)
    {
        pointer.update(extrp);
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
    public void onResolutionChanged(int width, int height)
    {
        camera.setView(0, 0, width, height, height);
        landscape.setScreenSize(width, height);
    }
}
