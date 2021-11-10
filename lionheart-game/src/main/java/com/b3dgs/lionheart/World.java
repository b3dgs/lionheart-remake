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
package com.b3dgs.lionheart;

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
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Layerable;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
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
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterListener;
import com.b3dgs.lionengine.game.feature.tile.map.raster.MapTileRastered;
import com.b3dgs.lionengine.game.feature.tile.map.raster.MapTileRasteredModel;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewer;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.helper.EntityChecker;
import com.b3dgs.lionengine.helper.MapTileHelper;
import com.b3dgs.lionengine.helper.WorldHelper;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionengine.io.DevicePointer;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.constant.Extension;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.extro.Extro;
import com.b3dgs.lionheart.landscape.FactoryLandscape;
import com.b3dgs.lionheart.landscape.ForegroundType;
import com.b3dgs.lionheart.landscape.Landscape;
import com.b3dgs.lionheart.menu.Menu;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.feature.BulletBounceOnGround;
import com.b3dgs.lionheart.object.feature.Stats;
import com.b3dgs.lionheart.object.feature.TakeableConfig;
import com.b3dgs.lionheart.object.feature.Trackable;
import com.b3dgs.lionheart.object.feature.Underwater;
import com.b3dgs.lionheart.object.state.StateCrouch;
import com.b3dgs.lionheart.object.state.StateWin;

/**
 * World game representation.
 */
// CHECKSTYLE IGNORE LINE: FanOutComplexity|DataAbstractionCoupling
final class World extends WorldHelper implements MusicPlayer, LoadNextStage
{
    private static final int MOUSE_HIDE_DELAY_MS = 1000;
    private static final int EXTRO_DELAY_MS = 3000;
    private static final int PARALLEL_LOAD_TIMEOUT_SEC = 30;
    private static final int CURSOR_OX = -2;
    private static final int CURSOR_OY = -2;
    private static final int CHEATS_WIDTH = 90;
    private static final int CHEATS_STAGE_WIDTH = 40;
    private static final String MAP_BOTTOM = "_bottom";

    private final MapTileWater mapWater = services.create(MapTileWater.class);
    private final CheckpointHandler checkpoint;
    private final Hud hud = services.create(Hud.class);
    private final ScreenShaker shaker = services.create(ScreenShaker.class);
    private final DeviceController device;
    private final DeviceController deviceCursor;
    private final Cursor cursor;

    private final Tick tick = new Tick();
    private final Tick tickMouse = new Tick();
    private final List<CheatMenu> menus = new ArrayList<>();

    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final BlockingDeque<Runnable> musicToPlay = new LinkedBlockingDeque<>();
    private final Thread musicTask;
    private Audio music;

    private Landscape landscape;
    private int trackerInitY;
    private double trackerY;
    private StateHandler player;
    private Difficulty difficulty;
    private boolean paused;
    private boolean cheats;
    private boolean cheatsMenu;
    private boolean fly;
    private boolean pressed;

    /**
     * Create the world.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    World(Services services)
    {
        super(services);

        device = services.add(DeviceControllerConfig.create(services,
                                                            Medias.create(Settings.getInstance().getInput())));
        device.setVisible(false);

        final Media mediaCursor = Medias.create(Constant.INPUT_FILE_CUSTOR);
        deviceCursor = DeviceControllerConfig.create(services, mediaCursor);

        cursor = services.create(Cursor.class);
        cursor.setArea(0, 0, camera.getWidth(), camera.getHeight());
        cursor.setViewer(camera);
        cursor.setVisible(false);
        cursor.setSync((DevicePointer) getInputDevice(DeviceControllerConfig.imports(services, mediaCursor)
                                                                            .iterator()
                                                                            .next()
                                                                            .getDevice()));
        cursor.addImage(0, Medias.create(Folder.SPRITE, "cursor.png"));
        cursor.setRenderingOffset(CURSOR_OX, CURSOR_OY);
        cursor.load();

        createCheatsMenu();

        services.add(new CheatsProvider()
        {
            @Override
            public boolean getCheats()
            {
                return cheats;
            }

            @Override
            public boolean isFly()
            {
                return fly;
            }
        });
        checkpoint = services.create(CheckpointHandler.class);
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
     * Create cheats menu.
     */
    private void createCheatsMenu()
    {
        final int w = CHEATS_WIDTH;
        final CheatMenu maxHeart = new CheatMenu(services, this::isPressed, w, "Max Heart", this::onCheatsMaxHeart);
        final CheatMenu maxLife = new CheatMenu(services, this::isPressed, w, "Max Life", this::onCheatsMaxLife);
        final CheatMenu freeFly = new CheatMenu(services, this::isPressed, w, "Fly", this::onCheatsFly);

        final int stagesCount = (int) Medias.create(Folder.STAGE, Settings.getInstance().getStages())
                                            .getMedias()
                                            .stream()
                                            .filter(m -> !m.getName().endsWith(Constant.STAGE_HARD_SUFFIX))
                                            .count();
        final CheatMenu[] stages = new CheatMenu[stagesCount];
        final int l = CHEATS_STAGE_WIDTH;
        for (int i = 0; i < stages.length; i++)
        {
            final int index = i;
            stages[i] = new CheatMenu(services, this::isPressed, l, String.valueOf(i + 1), () -> onCheatsStage(index));
        }

        final CheatMenu stage = new CheatMenu(services, this::isPressed, 90, "Stage", null, stages);
        menus.add(maxHeart);
        menus.add(maxLife);
        menus.add(freeFly);
        menus.add(stage);
    }

    private void onCheatsMaxHeart()
    {
        player.getFeature(Stats.class).maxHeart();
    }

    private void onCheatsMaxLife()
    {
        player.getFeature(Stats.class).apply(new TakeableConfig(null, null, 0, 0, Constant.STATS_MAX_LIFE, 0, false));
    }

    private void onCheatsFly()
    {
        cheats = true;
        fly = !fly;
        unlockPlayer(fly);
        cursor.setInputDevice(deviceCursor);
        sequencer.setSystemCursorVisible(false);
    }

    private void onCheatsStage(int index)
    {
        sequencer.end(SceneBlack.class, Util.getStage(difficulty, index + 1), getInitConfig(Optional.empty()));
    }

    /**
     * Check if cursor is pressed.
     * 
     * @return <code>true</code> if pressed, <code>false</code> else.
     */
    private boolean isPressed()
    {
        return pressed;
    }

    /**
     * Close cheats menu.
     */
    private void closeCheatsMenu()
    {
        cheatsMenu = false;
        for (int i = 0; i < menus.size(); i++)
        {
            menus.get(i).hide();
        }
        pressed = false;
    }

    /**
     * Load stage from configuration.
     * 
     * @param settings The settings reference.
     * @param init The initial configuration.
     * @param stage The stage configuration.
     */
    private void loadStage(Settings settings, InitConfig init, StageConfig stage)
    {
        if (settings.getRasterCheck())
        {
            Util.run(stage.getBackground());
        }

        loadMap(settings, stage);

        final FactoryLandscape factoryLandscape = new FactoryLandscape(services,
                                                                       source,
                                                                       settings.getBackgroundFlicker());
        landscape = services.add(factoryLandscape.createLandscape(stage.getBackground(), stage.getForeground()));

        createPlayerAndLoadCheckpoints(settings, init, stage);

        if (settings.getRasterObject())
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

        final Featurable[] entities = createEntities(settings, stage);

        if (settings.getFlagParallel())
        {
            executor.execute(() -> createEffectCache(stage));
        }
        else
        {
            createEffectCache(stage);
        }

        if (settings.getFlagParallel())
        {
            loadRasterEntities(settings, stage, entities);
        }
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
                spawn(Medias.create(Folder.EFFECT, "swamp", "Liana.xml"), tile);
            }
            else if (CollisionName.BLOCK.equals(mapGroup.getGroup(tile)))
            {
                spawn(Medias.create(Folder.EFFECT, "ancienttown", "Block.xml"), tile);
            }
        });

        final Optional<String> raster = config.getRasterFolder();
        if (Settings.getInstance().getRasterMap())
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
        loadMapBottom(config, media, raster);

        createMapCollisionDebug();
    }

    /**
     * Load map bottom part.
     * 
     * @param config The stage config.
     * @param media The media reference.
     * @param raster The raster reference.
     */
    private void loadMapBottom(StageConfig config, Media media, Optional<String> raster)
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
                if (mapRaster.loadSheets() && Settings.getInstance().getRasterMap())
                {
                    mapViewer.clear();
                    mapViewer.addRenderer(mapRaster);
                    mapRaster.setRaster(Medias.create(r, Constant.RASTER_FILE_TILE),
                                        config.getLinesPerRaster(),
                                        config.getRasterLineOffset());
                }
            });

            handler.add(mapBottom);
            loadWaterRaster(config, raster, mapBottom, true);
        }
        else
        {
            loadWaterRaster(config, raster, map, false);
        }
    }

    /**
     * Load raster data.
     * 
     * @param config The stage configuration.
     * @param raster The raster folder.
     * @param map The map tile reference.
     * @param bottom The bottom flag.
     */
    private void loadWaterRaster(StageConfig config, Optional<String> raster, MapTile map, boolean bottom)
    {
        if (Settings.getInstance().getRasterMapWater())
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
        featurable.getFeature(Stats.class).apply(init);
        player = featurable.getFeature(StateHandler.class);
        hud.setFeaturable(featurable);
        services.add(featurable.getFeature(Trackable.class));
        handler.add(featurable);

        final String theme = stage.getBackground().getWorld().getFolder();
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
                if (WorldType.SWAMP.getFolder().equals(theme))
                {
                    camera.setLimitLeft((int) camera.getX());
                    trackerY = 1.0;
                }
                spawn(Medias.create(Folder.BOSS, theme, "Boss.xml"), x, y).getFeature(EntityModel.class)
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

        if (settings.getFlagParallel())
        {
            executor.execute(() -> loadRasterHero(stage, featurable));
        }
        else
        {
            loadRasterHero(stage, featurable);
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
        if (Settings.getInstance().getRasterHeroWater())
        {
            featurable.getFeature(Underwater.class)
                      .loadRaster("raster/" + Folder.HERO + "/valdyn/",
                                  ForegroundType.LAVA.equals(stage.getForeground().getType()));
        }
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
        if (settings.getFlagParallel())
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
                loadRasterEntity(stage, featurable, settings);
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
        stage.getRasterFolder().ifPresent(r ->
        {
            featurable.ifIs(Underwater.class, underwater -> underwater.loadRaster(r));
            featurable.ifIs(BulletBounceOnGround.class, bullet -> bullet.loadRaster(r));
        });
        if (settings.getRasterObject())
        {
            stage.getRasterFolder().ifPresent(raster ->
            {
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
        }
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
     * @param stage The stage configuration.
     */
    private void createEffectCache(StageConfig stage)
    {
        final Spawner cacheSpawner = (media, x, y) ->
        {
            final Featurable f = factory.create(media);
            f.getFeature(Transformable.class).teleport(x, y);

            stage.getRasterFolder().ifPresent(raster ->
            {
                final Media rasterMedia = Medias.create(raster, Constant.RASTER_FILE_TILE);
                if (rasterMedia.exists())
                {
                    f.ifIs(Rasterable.class, r -> r.setRaster(true, rasterMedia, map.getTileHeight()));
                }
            });
            return f;
        };

        final String theme = stage.getBackground().getWorld().getFolder();
        factory.createCache(cacheSpawner, Medias.create(Folder.EFFECT, theme), 4);
        factory.createCache(cacheSpawner, Medias.create(Folder.PROJECTILE, theme), 6);
    }

    /**
     * Update pause checking.
     */
    private void updatePause()
    {
        if (device.isFiredOnce(DeviceMapping.PAUSE))
        {
            paused = !paused;
            hud.setPaused(paused);
        }
    }

    /**
     * Update quit checking.
     */
    private void updateQuit()
    {
        if (device.isFiredOnce(DeviceMapping.QUIT))
        {
            if (paused)
            {
                sequencer.end(Menu.class);
            }
            paused = !paused;
            hud.setExit(paused);
        }
    }

    /**
     * Update cheats activation.
     */
    private void updateCheatsOriginal()
    {
        if (paused && device.isFired(DeviceMapping.CTRL_LEFT))
        {
            if (!player.isState(StateCrouch.class))
            {
                paused = false;
                hud.setPaused(false);
            }
            else if (device.isFiredOnce(DeviceMapping.PAGE_DOWN))
            {
                device.isFiredOnce(DeviceMapping.CTRL_LEFT);
                cheats = !cheats;
                shaker.start();
                paused = false;
                hud.setPaused(false);
            }
        }
        else if (cheats && !player.isState(StateWin.class))
        {
            updateCheatsFly();
            updateCheatsStages();
        }
    }

    /**
     * Update cheats activation with menu.
     */
    private void updateCheatsMenu()
    {
        if (cheatsMenu)
        {
            pressed = deviceCursor.isFiredOnce(DeviceMapping.LEFT);
        }
        else if (!fly)
        {
            if (tickMouse.elapsedTime(source.getRate(), MOUSE_HIDE_DELAY_MS))
            {
                tickMouse.stop();
                sequencer.setSystemCursorVisible(false);
            }
            else
            {
                if (Double.compare(cursor.getMoveX(), 0.0) != 0 || Double.compare(cursor.getMoveY(), 0.0) != 0)
                {
                    tickMouse.restart();
                    sequencer.setSystemCursorVisible(true);
                }
            }
            if (deviceCursor.isFiredOnce(DeviceMapping.RIGHT))
            {
                cheatsMenu = true;
                cursor.setInputDevice(null);
                sequencer.setSystemCursorVisible(true);

                Util.showMenu(camera, cursor, menus, 0, 0);
            }
        }
        else if (deviceCursor.isFiredOnce(DeviceMapping.RIGHT))
        {
            fly = false;
            unlockPlayer(fly);
            cursor.setInputDevice(null);
            sequencer.setSystemCursorVisible(true);
        }
    }

    /**
     * Update fly mode cheat.
     */
    private void updateCheatsFly()
    {
        if (device.isFiredOnce(DeviceMapping.CTRL_LEFT))
        {
            fly = !fly;
            unlockPlayer(fly);
            if (fly)
            {
                cursor.setInputDevice(deviceCursor);
                sequencer.setSystemCursorVisible(false);
            }
            else
            {
                cursor.setInputDevice(null);
                sequencer.setSystemCursorVisible(true);
            }
        }
        if (fly && !cheatsMenu)
        {
            player.getFeature(Transformable.class)
                  .moveLocation(1.0, deviceCursor.getHorizontalDirection(), deviceCursor.getVerticalDirection());
        }
    }

    /**
     * Unlock player for cheats.
     * 
     * @param unlock <code>true</code> to unlock, <code>false</code> else.
     */
    private void unlockPlayer(boolean unlock)
    {
        player.getFeature(TileCollidable.class).setEnabled(!unlock);
        player.getFeature(Collidable.class).setEnabled(!unlock);
        player.getFeature(EntityChecker.class).setCheckerUpdate(() -> !unlock);
    }

    /**
     * Update stage jumping cheat.
     */
    private void updateCheatsStages()
    {
        for (int i = 0; i < Stage.values().length; i++)
        {
            if (device.isFiredOnce(Integer.valueOf(i + DeviceMapping.F1.getIndex().intValue())))
            {
                final Media stage = Util.getStage(difficulty, i + 1);
                if (stage.exists())
                {
                    sequencer.end(SceneBlack.class, stage, getInitConfig(Optional.empty()));
                }
            }
        }
        if (device.isFiredOnce(DeviceMapping.K5))
        {
            player.getFeature(Stats.class).win();
            tick.addAction(() ->
            {
                stopMusic();
                sequencer.end(Extro.class, player.getFeature(Stats.class).hasAmulet());
            }, source.getRate(), EXTRO_DELAY_MS);
        }
    }

    /**
     * Get init configuration.
     * 
     * @param spawn The next spawn.
     * @return The init configuration.
     */
    private InitConfig getInitConfig(Optional<Coord> spawn)
    {
        final Stats stats = player.getFeature(Stats.class);
        return new InitConfig(stats.getHealthMax(),
                              stats.getTalisment(),
                              stats.getLife(),
                              stats.getSword(),
                              stats.hasAmulet(),
                              stats.getCredits(),
                              difficulty,
                              cheats,
                              spawn);
    }

    /**
     * Load the stage from configuration.
     * 
     * @param config The stage configuration.
     * @param init The initial configuration.
     */
    public void load(Media config, InitConfig init)
    {
        try
        {
            hud.load();
            difficulty = init.getDifficulty();
            cheats = init.isCheats();

            final Settings settings = Settings.getInstance();
            services.add(config);

            final StageConfig stage = services.add(StageConfig.imports(new Configurer(config)));
            loadStage(settings, init, stage);
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

        tickMouse.stop();
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
                sequencer.end(SceneBlack.class, Medias.create(next), getInitConfig(spawn));
            }, source.getRate(), delayMs);
        }
        else
        {
            sequencer.end(SceneBlack.class, Medias.create(next), getInitConfig(spawn));
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
        tickMouse.update(extrp);
        device.update(extrp);
        deviceCursor.update(extrp);
        cursor.update(extrp);
        updatePause();
        updateQuit();
        updateCheatsOriginal();
        updateCheatsMenu();

        if (!paused)
        {
            tick.update(extrp);
            shaker.update(extrp);
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

        for (int i = 0; i < menus.size(); i++)
        {
            menus.get(i).updateSub(extrp);
            if (menus.get(i).isHoverSub())
            {
                for (int j = 0; j < menus.size(); j++)
                {
                    menus.get(j).setInactive();
                }
                break;
            }
        }

        for (int i = 0; i < menus.size(); i++)
        {
            menus.get(i).update(extrp);
        }
        if (pressed)
        {
            closeCheatsMenu();
        }
    }

    @Override
    public void render(Graphic g)
    {
        landscape.renderBackground(g);
        super.render(g);
        landscape.renderForeground(g);
        hud.render(g);

        for (int i = 0; i < menus.size(); i++)
        {
            menus.get(i).render(g);
        }
        cursor.render(g);
    }

    @Override
    public void onResolutionChanged(int width, int height)
    {
        camera.setView(0, 0, width, height, height);
        camera.setLimits(map);
        cursor.setArea(0, 0, camera.getWidth(), camera.getHeight());
        landscape.setScreenSize(width, height);
        hud.setScreenSize(width, height);
    }
}
