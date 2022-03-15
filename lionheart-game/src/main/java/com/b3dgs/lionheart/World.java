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
package com.b3dgs.lionheart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.Action;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Layerable;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.networkable.ComponentNetwork;
import com.b3dgs.lionengine.game.feature.networkable.IdentifiableCreate;
import com.b3dgs.lionengine.game.feature.networkable.Networkable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionModel;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionRenderer;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionRendererModel;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterListener;
import com.b3dgs.lionengine.game.feature.tile.map.raster.MapTileRastered;
import com.b3dgs.lionengine.game.feature.tile.map.raster.MapTileRasteredModel;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewer;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.helper.EntityInputController;
import com.b3dgs.lionengine.helper.MapTileHelper;
import com.b3dgs.lionengine.helper.WorldHelper;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;
import com.b3dgs.lionengine.network.Channel;
import com.b3dgs.lionengine.network.ChannelBuffer;
import com.b3dgs.lionengine.network.Data;
import com.b3dgs.lionengine.network.Network;
import com.b3dgs.lionengine.network.NetworkType;
import com.b3dgs.lionengine.network.UtilNetwork;
import com.b3dgs.lionengine.network.client.Client;
import com.b3dgs.lionengine.network.client.ClientUdp;
import com.b3dgs.lionengine.network.server.Server;
import com.b3dgs.lionengine.network.server.ServerListener;
import com.b3dgs.lionengine.network.server.ServerUdp;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.constant.Extension;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.landscape.FactoryLandscape;
import com.b3dgs.lionheart.landscape.ForegroundType;
import com.b3dgs.lionheart.landscape.Landscape;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.Snapshotable;
import com.b3dgs.lionheart.object.feature.BulletBounceOnGround;
import com.b3dgs.lionheart.object.feature.Stats;
import com.b3dgs.lionheart.object.feature.Trackable;
import com.b3dgs.lionheart.object.feature.Underwater;

/**
 * World game representation.
 */
// CHECKSTYLE IGNORE LINE: FanOutComplexity|DataAbstractionCoupling
final class World extends WorldHelper implements MusicPlayer, LoadNextStage
{
    private static final int PARALLEL_LOAD_TIMEOUT_SEC = 30;
    private static final String MAP_BOTTOM = "_bottom";

    private final MapTileWater mapWater = services.create(MapTileWater.class);
    private final Hud hud = services.create(Hud.class);
    private final Tick tick = new Tick();

    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final BlockingDeque<Runnable> musicToPlay = new LinkedBlockingDeque<>();
    private final Thread musicTask;
    private final boolean debug;
    private final Network network;

    private CheckpointHandler checkpoint;
    private Cheats cheats;
    private Audio music;
    private DeviceController device;

    private Landscape landscape;
    private int trackerInitY;
    private double trackerY;
    private StateHandler player;
    private Difficulty difficulty;
    private Action rasterRenderer = () ->
    {
        // Nothing to do
    };

    /**
     * Create the world.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param network The network type (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    World(Services services, Network network)
    {
        super(services);

        this.network = network;
        debug = Settings.getInstance().getFlagDebug();

        services.add(new MusicPlayer()
        {
            @Override
            public void playMusic(Media media)
            {
                World.this.playMusic(media);
            }

            @Override
            public void stopMusic()
            {
                World.this.stopMusic();
            }
        });

        map.addFeature(new LayerableModel(4, 2));
        map.addFeature(new MapTilePersisterOptimized(), true);

        camera.setIntervals(Constant.CAMERA_HORIZONTAL_MARGIN, 0);

        musicTask = new Thread(() ->
        {
            while (!Thread.currentThread().isInterrupted())
            {
                try
                {
                    musicToPlay.take().run();
                }
                catch (@SuppressWarnings("unused") final InterruptedException exception)
                {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "Musics");
        musicTask.start();
    }

    /**
     * Prepare network.
     * 
     * @throws IOException If network error.
     */
    void prepareNetwork() throws IOException
    {
        cheats = new Cheats(services, tick);
        checkpoint = services.create(CheckpointHandler.class);
        device = services.get(DeviceController.class);

        if (network.is(NetworkType.SERVER))
        {
            final Channel channel = services.create(ChannelBuffer.class);
            final Server server = services.add(new ServerUdp(channel));
            server.start(network.getIp().get(), network.getPort().getAsInt());
            handler.addComponent(new ComponentNetwork(services));

            server.addListener(new ServerListener()
            {
                @Override
                public void notifyServerStarted(String ip, int port)
                {
                    // Nothing
                }

                @Override
                public void notifyClientConnected(String ip, int port, int id)
                {
                    for (final Featurable featurable : handler.values())
                    {
                        if (featurable.hasFeature(EntityModel.class)
                            && featurable.getFeature(Networkable.class).isOwner())
                        {
                            try
                            {
                                server.send(new IdentifiableCreate(UtilNetwork.SERVER_ID,
                                                                   UtilNetwork.SERVER_ID,
                                                                   featurable.getFeature(Networkable.class).getDataId(),
                                                                   featurable.getMedia()),
                                            Integer.valueOf(id));
                                server.send(new Data(UtilNetwork.SERVER_ID,
                                                     featurable.getFeature(Networkable.class).getDataId(),
                                                     featurable.getFeature(EntityModel.class).networkInit(),
                                                     false),
                                            Integer.valueOf(id));
                            }
                            catch (final IOException exception)
                            {
                                Verbose.exception(exception);
                            }
                        }
                    }
                }

                @Override
                public void notifyClientDisconnected(String ip, int port, int id)
                {
                    for (final Featurable featurable : handler.values())
                    {
                        if (featurable.hasFeature(EntityModel.class)
                            && featurable.getFeature(Networkable.class).getClientId().intValue() == id)
                        {
                            handler.remove(featurable);
                        }
                    }
                }
            });
        }
        else if (network.is(NetworkType.CLIENT))
        {
            final Channel channel = services.create(ChannelBuffer.class);
            final Client client = services.add(new ClientUdp(channel));
            client.connect(network.getIp().get(), network.getPort().getAsInt());
            handler.addComponent(new ComponentNetwork(services));
        }
    }

    /**
     * Load stage from configuration.
     * 
     * @param settings The settings reference.
     * @param init The initial configuration.
     */
    private void loadStage(Settings settings, InitConfig init)
    {
        final StageConfig stage = services.add(StageConfig.imports(new Configurer(init.getStage())));

        if (RasterType.DIRECT == settings.getRaster())
        {
            loadRasterDirect(stage);
        }

        if (settings.getRasterCheck())
        {
            Util.run(stage.getBackground());
        }

        loadMap(settings, stage);

        final FactoryLandscape factoryLandscape = new FactoryLandscape(services,
                                                                       source,
                                                                       settings.getBackgroundFlicker());
        landscape = services.add(factoryLandscape.createLandscape(stage.getBackground(), stage.getForeground()));

        if (!network.is(NetworkType.SERVER))
        {
            createPlayerAndLoadCheckpoints(settings, init, stage);
        }

        if (RasterType.CACHE == settings.getRaster())
        {
            stage.getRasterFolder().ifPresent(r ->
            {
                final Media rasterMedia = Medias.create(r, Constant.RASTER_FILE_TILE);
                if (rasterMedia.exists())
                {
                    spawner.setRaster(rasterMedia);
                }
            });
        }
        if (!network.is(NetworkType.CLIENT))
        {
            final Featurable[] entities = createEntities(settings, stage);

            if (settings.getFlagParallel())
            {
                executor.execute(() -> createEffectCache(settings, stage));
            }
            else
            {
                createEffectCache(settings, stage);
            }

            if (RasterType.CACHE == Settings.getInstance().getRaster() && settings.getFlagParallel())
            {
                loadRasterEntities(settings, stage, entities);
            }
        }
    }

    /**
     * Load raster direct colors.
     * 
     * @param stage The stage reference.
     */
    private void loadRasterDirect(StageConfig stage)
    {
        rasterbar.clearRasterbarColor();

        stage.getRasterFolder().ifPresent(r ->
        {
            final Media rasterTiles = Medias.create(r, "tiles2.png");
            if (rasterTiles.exists())
            {
                rasterbar.addRasterbarColor(Graphics.getImageBuffer(rasterTiles));
            }

            final Media rasterWater = Medias.create(r, "water2.png");
            if (rasterWater.exists())
            {
                rasterbar.addRasterbarColor(Graphics.getImageBuffer(rasterWater));
            }

            final String raster;
            if (ForegroundType.LAVA.equals(stage.getForeground().getType()))
            {
                rasterbar.setRasterbarOffset(-37, 1);
                raster = "lava2.png";
            }
            else
            {
                rasterbar.setRasterbarOffset(-24, 16);
                raster = "water2.png";
            }
            final Media rasterHero = Medias.create(Folder.RASTER, Folder.HERO, "valdyn", raster);
            if (rasterHero.exists())
            {
                rasterbar.addRasterbarColor(Graphics.getImageBuffer(rasterHero));
            }
        });
        rasterRenderer = rasterbar::renderRasterbar;
    }

    /**
     * Load map from level.
     * 
     * @param settings The settings reference.
     * @param config The stage config.
     */
    private void loadMap(Settings settings, StageConfig config)
    {
        final Media media = config.getMapFile();
        if (!media.exists())
        {
            MapTileHelper.importAndSave(Medias.create(media.getPath().replace(Extension.MAP, Extension.IMAGE)),
                                        Medias.create(Folder.LEVEL,
                                                      config.getBackground().getWorld().getFolder(),
                                                      TileSheetsConfig.FILENAME),
                                        media,
                                        new MapTilePersisterOptimized());
        }

        // CHECKSTYLE IGNORE LINE: AnonInnerLength
        map.getFeature(MapTilePersister.class).addListener(new MapTilePersisterListener()
        {
            @Override
            public void notifyMapLoadStart()
            {
                map.loadBefore(map.getMedia());
            }

            @Override
            public void notifyMapLoaded()
            {
                if (settings.getFlagParallel())
                {
                    executor.execute(() -> map.loadAfter(map.getMedia()));
                }
                else
                {
                    map.loadAfter(map.getMedia());
                }
                camera.setLimits(map);
            }
        });
        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        map.addListener(tile ->
        {
            if (CollisionName.LIANA_TOP.equals(mapGroup.getGroup(tile)))
            {
                spawn(Medias.create(Folder.EFFECT, WorldType.SWAMP.getFolder(), "Liana.xml"), tile);
            }
            else if (CollisionName.BLOCK.equals(mapGroup.getGroup(tile)))
            {
                spawn(Medias.create(Folder.EFFECT, WorldType.ANCIENTTOWN.getFolder(), "Block.xml"), tile);
            }
        });

        final Optional<String> raster = config.getRasterFolder();
        if (RasterType.CACHE == settings.getRaster())
        {
            raster.ifPresent(r -> map.getFeature(MapTileRastered.class)
                                     .setRaster(Medias.create(r, Constant.RASTER_FILE_TILE),
                                                config.getLinesPerRaster(),
                                                config.getRasterLineOffset()));
        }
        map.loadSheets(Medias.create(Folder.LEVEL,
                                     config.getBackground().getWorld().getFolder(),
                                     TileSheetsConfig.FILENAME));
        Util.loadMapTiles(map, media);
        loadMapBottom(settings, config, media, raster);

        createMapCollisionDebug();
    }

    /**
     * Load map bottom part.
     * 
     * @param settings The settings reference.
     * @param config The stage config.
     * @param media The media reference.
     * @param raster The raster reference.
     */
    private void loadMapBottom(Settings settings, StageConfig config, Media media, Optional<String> raster)
    {
        final Media bottomRip = Medias.create(media.getPath().replace(Extension.MAP, MAP_BOTTOM + Extension.IMAGE));
        final Media bottom = Medias.create(media.getPath().replace(Extension.MAP, MAP_BOTTOM + Extension.MAP));
        if (bottom.exists() || bottomRip.exists())
        {
            if (!bottom.exists())
            {
                MapTileHelper.importAndSave(bottomRip, bottom, new MapTilePersisterOptimized());
            }
            final MapTileGame mapBottom = new MapTileGame();
            mapBottom.addFeature(new MapTilePersisterOptimized());
            mapBottom.addFeature(new MapTileGroupModel());
            mapBottom.addFeature(new MapTileCollisionModel());
            mapBottom.addFeature(new LayerableModel(4, 5));
            final MapTileViewer mapViewer = mapBottom.addFeatureAndGet(new MapTileViewerModel(services));
            mapBottom.loadSheets(Medias.create(Folder.LEVEL,
                                               config.getBackground().getWorld().getFolder(),
                                               TileSheetsConfig.FILENAME));
            Util.loadMapTiles(mapBottom, bottom);

            raster.ifPresent(r ->
            {
                final MapTileRastered mapRaster = mapBottom.addFeatureAndGet(new MapTileRasteredModel());
                if (mapRaster.loadSheets() && RasterType.CACHE == settings.getRaster())
                {
                    mapViewer.clear();
                    mapViewer.addRenderer(mapRaster);
                    mapRaster.setRaster(Medias.create(r, Constant.RASTER_FILE_TILE),
                                        config.getLinesPerRaster(),
                                        config.getRasterLineOffset());
                }
            });

            handler.add(mapBottom);
            loadWaterRaster(settings, config, raster, mapBottom, true);
        }
        else
        {
            loadWaterRaster(settings, config, raster, map, false);
        }
    }

    /**
     * Load raster data.
     * 
     * @param settings The settings reference.
     * @param config The stage configuration.
     * @param raster The raster folder.
     * @param map The map tile reference.
     * @param bottom The bottom flag.
     */
    private void loadWaterRaster(Settings settings,
                                 StageConfig config,
                                 Optional<String> raster,
                                 MapTile map,
                                 boolean bottom)
    {
        if (RasterType.CACHE == settings.getRaster())
        {
            final ForegroundType foreground = config.getForeground().getType();
            if (foreground == ForegroundType.WATER || foreground == ForegroundType.LAVA)
            {
                raster.ifPresent(r ->
                {
                    mapWater.create(r);
                    mapWater.addFeature(new LayerableModel(4, 3));
                    handler.add(mapWater);

                    if (bottom)
                    {
                        final MapTileWater mapWaterBottom = new MapTileWater(services, true);
                        mapWaterBottom.create(r);
                        mapWaterBottom.addFeature(new LayerableModel(4, 6));
                        handler.add(mapWaterBottom);
                    }
                });
            }
        }
    }

    /**
     * Create map tile collision debug rendering if enabled.
     */
    private void createMapCollisionDebug()
    {
        if (Constant.DEBUG_COLLISIONS)
        {
            final MapTileCollisionRenderer renderer = map.addFeatureAndGet(new MapTileCollisionRendererModel());
            renderer.createCollisionDraw();

            final MapTileViewer mapViewer = map.getFeature(MapTileViewer.class);
            mapViewer.addRenderer(renderer);
        }
    }

    /**
     * Create player and track with camera.
     * 
     * @param settings The settings reference.
     * @param init The initial configuration.
     * @param stage The stage reference.
     */
    private void createPlayerAndLoadCheckpoints(Settings settings, InitConfig init, StageConfig stage)
    {
        final Featurable featurable = factory.create(Medias.create(Folder.HERO, "valdyn", "Valdyn.xml"));

        featurable.getFeature(EntityModel.class).setInput(device);
        featurable.getFeature(Stats.class).apply(init);
        player = featurable.getFeature(StateHandler.class);
        hud.setFeaturable(featurable);
        services.add(featurable.getFeature(Trackable.class));
        handler.add(featurable);

        final WorldType world = stage.getBackground().getWorld();
        final Optional<Coord> spawn = init.getSpawn();
        checkpoint.load(stage, featurable, spawn);
        // CHECKSTYLE IGNORE LINE: AnonInnerLength
        checkpoint.addListener(new CheckpointListener()
        {
            @Override
            public void notifyNextStage(String next, Optional<Coord> spawn)
            {
                loadNextStage(next, 0, spawn);
            }

            @Override
            public void notifyReachedBoss(double x, double y)
            {
                if (WorldType.SWAMP == world)
                {
                    camera.setLimitLeft((int) camera.getX());
                    trackerY = 1.0;
                }
                else if (WorldType.LAVA == world)
                {
                    rasterbar.clearRasterbarColor();
                }
                spawn(Medias.create(Folder.BOSS, world.getFolder(), "Boss.xml"), x, y).getFeature(EntityModel.class)
                                                                                      .setNext(stage.getBossNext(),
                                                                                               Optional.empty());
                playMusic(Music.BOSS);
            }
        });

        final Transformable transformable = featurable.getFeature(Transformable.class);
        if (spawn.isPresent())
        {
            transformable.teleport(spawn.get().getX() * map.getTileWidth(), spawn.get().getY() * map.getTileHeight());
        }
        else
        {
            final Coord coord = checkpoint.getCurrent(transformable);
            transformable.teleport(coord.getX(), coord.getY());
        }
        trackPlayer(featurable);

        if (RasterType.CACHE == settings.getRaster())
        {
            if (settings.getFlagParallel())
            {
                executor.execute(() -> loadRasterHero(stage, featurable));
            }
            else
            {
                loadRasterHero(stage, featurable);
            }
        }
    }

    /**
     * Track player with camera.
     * 
     * @param player The player to track.
     */
    private void trackPlayer(Featurable player)
    {
        tracker.addFeature(new LayerableModel(player.getFeature(Layerable.class).getLayerRefresh().intValue() + 1));
        trackerInitY = player.getFeature(Transformable.class).getHeight() / 2 + 8;
        tracker.setOffset(0, trackerInitY);
        tracker.track(player);
        services.add(tracker);
    }

    /**
     * Load player raster.
     * 
     * @param stage The stage configuration.
     * @param featurable The hero reference.
     */
    private void loadRasterHero(StageConfig stage, Featurable featurable)
    {
        featurable.getFeature(Underwater.class)
                  .loadRaster("raster/" + Folder.HERO + "/valdyn/",
                              ForegroundType.LAVA.equals(stage.getForeground().getType()));
    }

    /**
     * Create entities from stage.
     * 
     * @param settings The settings reference.
     * @param stage The stage config.
     * @return The created entities.
     */
    private Featurable[] createEntities(Settings settings, StageConfig stage)
    {
        final Featurable[] entities;
        if (settings.getFlagParallel() && RasterType.CACHE == settings.getRaster())
        {
            final List<EntityConfig> config = stage.getEntities();
            final int n = config.size();
            entities = new Featurable[n];
            for (int i = 0; i < n; i++)
            {
                entities[i] = createEntity(config.get(i), settings);
            }
        }
        else
        {
            entities = null;
            final List<EntityConfig> entityConfig = stage.getEntities();
            final int n = entityConfig.size();
            for (int i = 0; i < n; i++)
            {
                final Featurable featurable = createEntity(entityConfig.get(i), settings);
                if (RasterType.CACHE == settings.getRaster())
                {
                    loadRasterEntity(stage, featurable, settings);
                }
                handler.add(featurable);
            }
        }
        return entities;
    }

    /**
     * Create entity from configuration.
     * 
     * @param entity The entity configuration.
     * @param settings The settings reference.
     * @return The created entity.
     */
    private Featurable createEntity(EntityConfig entity, Settings settings)
    {
        final Featurable featurable = factory.create(entity.getMedia());
        featurable.getFeature(Transformable.class).teleport(entity.getSpawnX(map), entity.getSpawnY(map));
        if (Difficulty.LIONHARD.equals(difficulty))
        {
            featurable.ifIs(Stats.class, Stats::initLionhard);
        }
        Util.loadEntityFeature(featurable, entity);

        return featurable;
    }

    /**
     * Create entity from configuration.
     * 
     * @param stage The stage configuration.
     * @param featurable The featurable reference.
     * @param settings The settings reference.
     * @return The created entity.
     */
    private Featurable loadRasterEntity(StageConfig stage, Featurable featurable, Settings settings)
    {
        stage.getRasterFolder().ifPresent(raster ->
        {
            featurable.ifIs(Underwater.class, underwater -> underwater.loadRaster(raster));
            featurable.ifIs(BulletBounceOnGround.class, bullet -> bullet.loadRaster(raster));

            final Media media = Medias.create(raster, Constant.RASTER_FILE_TILE);
            if (media.exists())
            {
                featurable.ifIs(Rasterable.class,
                                r -> r.setRaster(true,
                                                 media,
                                                 map.getTileHeight(),
                                                 stage.getLinesPerRaster(),
                                                 stage.getRasterLineOffset()));
            }
        });
        return featurable;
    }

    private void loadRasterEntities(Settings settings, StageConfig stage, Featurable[] entities)
    {
        final int threads = Runtime.getRuntime().availableProcessors();
        final int entitiesPerThread = (int) Math.floor(entities.length / (double) threads);
        int start = 0;

        final List<Future<Featurable[]>> tasks = new ArrayList<>(threads);
        for (int i = 0; i < threads; i++)
        {
            final int end;
            if (i < threads - 1)
            {
                end = start + entitiesPerThread;
            }
            else
            {
                end = entities.length;
            }
            tasks.add(loadRasterEntities(settings, stage, entities, start, end));
            start += entitiesPerThread;
        }

        mergeEntitiesToHandler(tasks);
    }

    private Future<Featurable[]> loadRasterEntities(Settings settings,
                                                    StageConfig stage,
                                                    Featurable[] featurables,
                                                    int start,
                                                    int end)
    {
        return executor.submit(() ->
        {
            final int n = end - start;
            final Featurable[] toAdd = new Featurable[n];
            int i = 0;
            for (i = 0; i < n; i++)
            {
                final int index = start + i;
                toAdd[i] = loadRasterEntity(stage, featurables[index], settings);
            }
            return toAdd;
        });
    }

    private void mergeEntitiesToHandler(List<Future<Featurable[]>> tasks)
    {
        final int n = tasks.size();
        for (int i = 0; i < n; i++)
        {
            try
            {
                final Featurable[] toAdd = tasks.get(i).get();
                for (int j = 0; j < toAdd.length; j++)
                {
                    handler.add(toAdd[j]);
                    toAdd[j] = null;
                }
            }
            catch (final InterruptedException exception)
            {
                Thread.currentThread().interrupt();
                throw new LionEngineException(exception);
            }
            catch (final ExecutionException exception)
            {
                throw new LionEngineException(exception);
            }
        }
    }

    /**
     * Create effect and cache.
     * 
     * @param settings The settings reference.
     * @param stage The stage configuration.
     */
    private void createEffectCache(Settings settings, StageConfig stage)
    {
        final Spawner cacheSpawner = (media, x, y) ->
        {
            final Featurable f = factory.create(media);
            f.getFeature(Transformable.class).teleport(x, y);

            if (RasterType.CACHE == settings.getRaster())
            {
                stage.getRasterFolder().ifPresent(raster ->
                {
                    final Media rasterMedia = Medias.create(raster, Constant.RASTER_FILE_TILE);
                    if (rasterMedia.exists())
                    {
                        f.ifIs(Rasterable.class, r -> r.setRaster(true, rasterMedia, map.getTileHeight()));
                    }
                });
            }
            return f;
        };

        final String theme = stage.getBackground().getWorld().getFolder();
        factory.createCache(cacheSpawner, Medias.create(Folder.EFFECT, theme), 4);
        factory.createCache(cacheSpawner, Medias.create(Folder.PROJECTILE, theme), 6);
    }

    private void quickSave()
    {
        try (FileWriting file = new FileWriting(Medias.create(Constant.FILE_SNAPSHOT)))
        {
            int n = 0;
            for (final Featurable featurable : handler.values())
            {
                if (featurable.hasFeature(EntityInputController.class))
                {
                    services.remove(player.getFeature(Trackable.class));
                }
                if (featurable.hasFeature(EntityModel.class))
                {
                    n++;
                }
            }
            file.writeInteger(n);

            saveSnapshotables(file);
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
        }
    }

    private void saveSnapshotables(FileWriting file) throws IOException
    {
        for (final Featurable featurable : handler.values())
        {
            if (featurable.hasFeature(EntityModel.class))
            {
                file.writeString(featurable.getMedia().getPath());
                for (final Feature feature : featurable.getFeatures())
                {
                    if (feature instanceof Snapshotable)
                    {
                        ((Snapshotable) feature).save(file);
                    }
                }
            }
        }
    }

    private void quickLoad()
    {
        final Media media = Medias.create(Constant.FILE_SNAPSHOT);
        if (media.exists())
        {
            try (FileReading file = new FileReading(media))
            {
                removeSnapshotables();

                final int n = file.readInteger();
                for (int i = 0; i < n; i++)
                {
                    loadSnapshotable(file);
                }
                handler.updateAdd();
            }
            catch (final IOException exception)
            {
                Verbose.exception(exception);
            }
        }
    }

    private void removeSnapshotables()
    {
        handler.updateAdd();
        for (final Featurable featurable : handler.values())
        {
            for (final Feature feature : featurable.getFeatures())
            {
                if (feature instanceof Snapshotable)
                {
                    featurable.getFeature(Identifiable.class).destroy();
                    break;
                }
            }
        }
        handler.updateRemove();
    }

    private void loadSnapshotable(FileReading file) throws IOException
    {
        final Featurable featurable = factory.create(Medias.create(file.readString()));
        if (featurable.hasFeature(EntityInputController.class))
        {
            hud.setFeaturable(featurable);
            player = featurable.getFeature(StateHandler.class);
            trackerInitY = player.getFeature(Transformable.class).getHeight() / 2 + 8;
            tracker.setOffset(0, trackerInitY);
            tracker.track(player.getFeature(Transformable.class));
        }
        for (final Feature feature : featurable.getFeatures())
        {
            if (feature instanceof Snapshotable)
            {
                ((Snapshotable) feature).load(file);
            }
        }
        handler.add(featurable);
    }

    /**
     * Load the stage from configuration.
     * 
     * @param init The initial configuration.
     */
    public void load(InitConfig init)
    {
        try
        {
            if (debug)
            {
                Medias.create(Constant.FILE_SNAPSHOT).getFile().delete();
            }

            hud.load();
            difficulty = init.getDifficulty();

            services.add(init.getStage());

            loadStage(Settings.getInstance(), init);

            cheats.init(player, difficulty, init.isCheats());
        }
        finally
        {
            executor.shutdown();
        }
        try
        {
            executor.awaitTermination(PARALLEL_LOAD_TIMEOUT_SEC, TimeUnit.SECONDS);
        }
        catch (final InterruptedException exception)
        {
            Thread.currentThread().interrupt();
            Verbose.exception(exception);
        }

        handler.updateRemove();
        handler.updateAdd();

        Sfx.cacheEnd();

        tick.restart();
    }

    @Override
    public void loadNextStage(String next, int delayMs, Optional<Coord> spawn)
    {
        if (delayMs > 0)
        {
            player.getFeature(Stats.class).win();
            tick.addAction(() ->
            {
                sequencer.end(SceneBlack.class,
                              Util.getInitConfig(Medias.create(next), player, difficulty, cheats.isEnabled(), spawn));
            }, source.getRate(), delayMs);
        }
        else
        {
            sequencer.end(SceneBlack.class,
                          Util.getInitConfig(Medias.create(next), player, difficulty, cheats.isEnabled(), spawn));
        }
    }

    @Override
    public void playMusic(Media media)
    {
        musicToPlay.offer(() ->
        {
            synchronized (musicTask)
            {
                if (music != null)
                {
                    music.stop();
                }
                music = AudioFactory.loadAudio(media);

                final Settings settings = Settings.getInstance();
                if (settings.getVolumeMaster() > 0)
                {
                    music.setVolume(settings.getVolumeMusic());
                    music.play();
                }
            }
        });
    }

    @Override
    public void stopMusic()
    {
        synchronized (musicTask)
        {
            musicTask.interrupt();
            try
            {
                musicTask.join(com.b3dgs.lionengine.Constant.THOUSAND);
            }
            catch (final InterruptedException exception)
            {
                Thread.currentThread().interrupt();
                Verbose.exception(exception);
            }
            musicToPlay.clear();
            if (music != null)
            {
                music.stop();
                music = null;
            }
        }
    }

    @Override
    public void update(double extrp)
    {
        if (network.is(NetworkType.SERVER))
        {
            camera.setIntervals(0, 0);
            camera.moveLocation(extrp, device.getHorizontalDirection() * 4, device.getVerticalDirection() * 4);
        }

        device.update(extrp);
        cheats.update(extrp);

        if (!cheats.isPaused())
        {
            tick.update(extrp);
            super.update(extrp);
            checkpoint.update(extrp);
            landscape.update(extrp, camera);
            if (trackerY > 0)
            {
                trackerY = UtilMath.clamp(trackerY += 0.5 * extrp, 0.0, 21.0);
                tracker.setOffset(0, trackerInitY + (int) Math.floor(trackerY));
            }
        }
        hud.update(extrp);
        rasterbar.setRasterbarY((int) camera.getY(), mapWater.getCurrent() - 2);

        if (debug)
        {
            if (device.isFiredOnce(DeviceMapping.QUICK_SAVE))
            {
                quickSave();
            }
            if (device.isFiredOnce(DeviceMapping.QUICK_LOAD))
            {
                quickLoad();
            }
        }
    }

    @Override
    public void render(Graphic g)
    {
        landscape.renderBackground(g);
        super.render(g);
        rasterRenderer.execute();
        landscape.renderForeground(g);
        hud.render(g);
        cheats.render(g);
    }

    @Override
    public void onResolutionChanged(int width, int height)
    {
        camera.setView(0, 0, width, height, height);
        camera.setLimits(map);
        cheats.onResolutionChanged(width, height);
        landscape.setScreenSize(width, height);
        hud.setScreenSize(width, height);
    }
}
