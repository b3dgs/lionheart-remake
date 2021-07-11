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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Layerable;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormulaConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionGroupConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;
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
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.constant.Extension;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.extro.Extro;
import com.b3dgs.lionheart.landscape.BackgroundType;
import com.b3dgs.lionheart.landscape.FactoryLandscape;
import com.b3dgs.lionheart.landscape.ForegroundType;
import com.b3dgs.lionheart.landscape.Landscape;
import com.b3dgs.lionheart.menu.Menu;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.feature.BulletBounceOnGround;
import com.b3dgs.lionheart.object.feature.Canon2Airship;
import com.b3dgs.lionheart.object.feature.Catapult;
import com.b3dgs.lionheart.object.feature.Dragon1;
import com.b3dgs.lionheart.object.feature.Geyzer;
import com.b3dgs.lionheart.object.feature.HotFireBall;
import com.b3dgs.lionheart.object.feature.Jumper;
import com.b3dgs.lionheart.object.feature.Patrol;
import com.b3dgs.lionheart.object.feature.PatrolConfig;
import com.b3dgs.lionheart.object.feature.Pillar;
import com.b3dgs.lionheart.object.feature.Rotating;
import com.b3dgs.lionheart.object.feature.Shooter;
import com.b3dgs.lionheart.object.feature.Spider;
import com.b3dgs.lionheart.object.feature.Spike;
import com.b3dgs.lionheart.object.feature.Stats;
import com.b3dgs.lionheart.object.feature.SwordShade;
import com.b3dgs.lionheart.object.feature.Underwater;
import com.b3dgs.lionheart.object.state.StateCrouch;
import com.b3dgs.lionheart.object.state.StateWin;

/**
 * World game representation.
 */
final class World extends WorldHelper implements MusicPlayer, LoadNextStage
{
    /**
     * Load map tiles data.
     * 
     * @param map The map reference.
     * @param media The map tiles data.
     */
    private static void loadMapTiles(MapTile map, Media media)
    {
        try (FileReading reading = new FileReading(media))
        {
            final MapTilePersister mapPersister = map.getFeature(MapTilePersister.class);
            mapPersister.load(reading);
            map.getFeature(MapTileCollision.class)
               .loadCollisions(Medias.create(Folder.LEVEL, CollisionFormulaConfig.FILENAME),
                               Medias.create(Folder.LEVEL, CollisionGroupConfig.FILENAME));
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Get stage by difficulty.
     * 
     * @param difficulty The difficulty.
     * @param index The stage index.
     * @return The stage media.
     */
    private static Media getStage(Difficulty difficulty, int index)
    {
        if (Difficulty.NORMAL.equals(difficulty) || index > 9 && index < 13)
        {
            return Stage.values()[index];
        }
        return StageHard.values()[index];
    }

    private final MapTileWater mapWater = services.create(MapTileWater.class);
    private final CheckpointHandler checkpoint = services.create(CheckpointHandler.class);
    private final Hud hud = services.create(Hud.class);
    private final ScreenShaker shaker = services.create(ScreenShaker.class);
    private final DeviceController device;

    private final Tick tick = new Tick();

    private final BlockingDeque<Runnable> tasks = new LinkedBlockingDeque<>();
    private final Thread task;
    private volatile Audio audio;

    private Landscape landscape;
    private int trackerInitY;
    private double trackerY;
    private StageConfig stage;
    private StateHandler player;
    private Difficulty difficulty;
    private boolean paused;
    private boolean cheats;
    private boolean fly;

    /**
     * Create the world.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    World(Services services)
    {
        super(services);

        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_CUSTOM)));

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
        services.add(tick);
        map.addFeature(new LayerableModel(4, 2));
        map.addFeature(new MapTilePersisterOptimized(), true);

        camera.setIntervals(Constant.CAMERA_HORIZONTAL_MARGIN, 0);

        task = new Thread(() ->
        {
            while (!Thread.currentThread().isInterrupted())
            {
                try
                {
                    tasks.take().run();
                }
                catch (@SuppressWarnings("unused") final InterruptedException exception)
                {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, World.class.getSimpleName());
        task.start();
    }

    /**
     * Load map from level.
     * 
     * @param config The stage config.
     */
    private void loadMap(StageConfig config)
    {
        final Media media = config.getMapFile();
        if (!media.exists())
        {
            MapTileHelper.importAndSave(Medias.create(media.getPath().replace(Extension.MAP, Extension.IMAGE)),
                                        media,
                                        new MapTilePersisterOptimized());
        }

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
                map.loadAfter(map.getMedia());
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
        map.loadSheets(Medias.create(media.getParentPath(), TileSheetsConfig.FILENAME));
        loadMapTiles(map, media);
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
        final Media bottomRip = Medias.create(media.getPath().replace(Extension.MAP, "_bottom" + Extension.IMAGE));
        final Media bottom = Medias.create(media.getPath().replace(Extension.MAP, "_bottom" + Extension.MAP));
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
            mapBottom.loadSheets(Medias.create(media.getParentPath(), TileSheetsConfig.FILENAME));
            loadMapTiles(mapBottom, bottom);

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
        if (Constant.DEBUG)
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
     * @param init The initial configuration.
     * @param foreground The foreground type.
     * @return The created player.
     */
    private Featurable createPlayerAndLoadCheckpoints(InitConfig init, ForegroundType foreground)
    {
        final Featurable featurable = spawn(Medias.create(Folder.HERO, "valdyn", "Valdyn.xml"), 0, 0);
        featurable.getFeature(Stats.class).apply(init);
        if (Settings.getInstance().getRasterHeroWater())
        {
            featurable.getFeature(Underwater.class)
                      .loadRaster("raster/" + Folder.HERO + "/valdyn/", ForegroundType.LAVA.equals(foreground));
        }

        final Optional<Coord> spawn = init.getSpawn();
        checkpoint.load(stage, featurable, spawn);

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

        player = featurable.getFeature(StateHandler.class);
        hud.setFeaturable(featurable);
        services.add(featurable.getFeature(SwordShade.class));
        trackPlayer(featurable);
        return featurable;
    }

    /**
     * Track player with camera.
     * 
     * @param player The player to track.
     */
    private void trackPlayer(Featurable player)
    {
        tracker.addFeature(new LayerableModel(player.getFeature(Layerable.class).getLayerRefresh().intValue() + 10));
        trackerInitY = player.getFeature(Transformable.class).getHeight() / 2 + 8;
        tracker.setOffset(0, trackerInitY);
        tracker.track(player);
        services.add(tracker);
    }

    /**
     * Create entity from configuration.
     * 
     * @param stage The stage configuration.
     * @param entity The entity configuration.
     * @param entitiesRasters The rasters used.
     */
    private void createEntity(StageConfig stage, EntityConfig entity, HashMap<Media, Set<Integer>> entitiesRasters)
    {
        final Featurable featurable = spawn(entity.getMedia(), entity.getSpawnX(map), entity.getSpawnY(map));
        if (Difficulty.LIONHARD.equals(difficulty))
        {
            featurable.ifIs(Stats.class, Stats::initLionhard);
        }
        entity.getSecret().ifPresent(secret -> featurable.getFeature(EntityModel.class).setSecret(true));
        entity.getNext()
              .ifPresent(next -> featurable.getFeature(EntityModel.class).setNext(next, entity.getNextSpawn()));
        entity.getMirror().ifPresent(mirror ->
        {
            final Mirrorable mirrorable = featurable.getFeature(Mirrorable.class);
            mirrorable.mirror(mirror.booleanValue() ? Mirror.HORIZONTAL : Mirror.NONE);
            mirrorable.update(1.0);
        });
        entity.getSpike().ifPresent(config -> featurable.ifIs(Spike.class, spike -> spike.load(config)));
        entity.getRotating().ifPresent(config -> featurable.ifIs(Rotating.class, rotating -> rotating.load(config)));
        entity.getHotFireBall().ifPresent(config -> featurable.ifIs(HotFireBall.class, hot -> hot.load(config)));
        entity.getGeyzer().ifPresent(config -> featurable.ifIs(Geyzer.class, geyzer -> geyzer.load(config)));
        final List<PatrolConfig> patrols = entity.getPatrols();
        if (!patrols.isEmpty())
        {
            featurable.ifIs(Patrol.class, patrol -> patrol.load(patrols));
            featurable.ifIs(Jumper.class, jumper -> jumper.setJump(entity.getJump()));
        }
        else
        {
            featurable.ifIs(Spider.class, s -> s.load(entity.getSpider()));
        }
        entity.getDragon1().ifPresent(config -> featurable.ifIs(Dragon1.class, dragon1 -> dragon1.load(config)));
        entity.getCanon2().ifPresent(config -> featurable.ifIs(Canon2Airship.class, canon2 -> canon2.load(config)));
        entity.getShooter().ifPresent(config -> featurable.ifIs(Shooter.class, shooter -> shooter.load(config)));
        entity.getPillar().ifPresent(config -> featurable.ifIs(Pillar.class, pillar -> pillar.load(config)));
        entity.getCatapult().ifPresent(config -> featurable.ifIs(Catapult.class, catapult -> catapult.load(config)));
        featurable.ifIs(BulletBounceOnGround.class, bounce -> bounce.load(entity.getVx()));
        stage.getRasterFolder().ifPresent(r ->
        {
            featurable.ifIs(Underwater.class, underwater -> underwater.loadRaster(r));
            featurable.ifIs(BulletBounceOnGround.class, bullet -> bullet.loadRaster(r));
        });
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
    private void updateCheats()
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
     * Update fly mode cheat.
     */
    private void updateCheatsFly()
    {
        if (device.isFiredOnce(DeviceMapping.CTRL_LEFT))
        {
            device.setDisabled(Constant.DEVICE_MOUSE, fly, fly);
            fly = !fly;
            device.setDisabled(Constant.DEVICE_KEYBOARD, fly, fly);

            unlockPlayer(fly);
        }
        if (fly)
        {
            player.getFeature(Transformable.class)
                  .moveLocation(1.0, device.getHorizontalDirection(), device.getVerticalDirection());
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
        for (int i = 0; i < 14; i++)
        {
            if (device.isFiredOnce(Integer.valueOf(i + DeviceMapping.F1.getIndex().intValue())))
            {
                sequencer.end(SceneBlack.class, getStage(difficulty, i), getInitConfig(Optional.empty()));
            }
        }
        if (device.isFiredOnce(DeviceMapping.K5))
        {
            player.getFeature(Stats.class).win();
            tick.addAction(() ->
            {
                stopMusic();
                sequencer.end(Extro.class, player.getFeature(Stats.class).hasAmulet());
            }, 200L);
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
        services.add(config);
        stage = services.add(StageConfig.imports(new Configurer(config)));

        if (stage.getBoss().isPresent() || stage.getBackground() == BackgroundType.NORKA)
        {
            Sfx.cacheStart(Sfx.BOSS1_BOWL,
                           Sfx.BOSS1_HURT,
                           Sfx.BOSS3_HURT,
                           Sfx.BOSS3_JUMP,
                           Sfx.BOSS_DAEMON_FIRE,
                           Sfx.BOSS_DAEMON_LAND,
                           Sfx.BOSS_FLYER,
                           Sfx.BOSS_NORKA_FIRE,
                           Sfx.BOSS_NORKA_HURT,
                           Sfx.BOSS_NORKA_PLATFORM);
        }
        else
        {
            Sfx.cacheStart();
        }

        loadMap(stage);

        final Settings settings = Settings.getInstance();
        final FactoryLandscape factoryLandscape = new FactoryLandscape(services,
                                                                       source,
                                                                       settings.getBackgroundFlicker());
        landscape = services.add(factoryLandscape.createLandscape(stage.getBackground(), stage.getForeground()));

        difficulty = init.getDifficulty();
        cheats = init.isCheats();
        createPlayerAndLoadCheckpoints(init, stage.getForeground().getType());

        final String theme = stage.getBackground().getWorld().getFolder();
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
                                                                          .setNext(stage.getBossNext().get(),
                                                                                   Optional.empty());
                playMusic(Music.BOSS);
            }
        });
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

        final HashMap<Media, Set<Integer>> entitiesRasters = new HashMap<>();
        stage.getEntities().forEach(entity -> createEntity(stage, entity, entitiesRasters));

        factory.createCache(spawner, Medias.create(Folder.EFFECT, theme), 4);
        factory.createCache(spawner, Medias.create(Folder.PROJECTILE, theme), 6);

        hud.load();
        handler.updateRemove();
        handler.updateAdd();

        Sfx.cacheEnd();

        tick.restart();
    }

    @Override
    public void loadNextStage(String next, int tickDelay, Optional<Coord> spawn)
    {
        if (tickDelay > 0)
        {
            player.getFeature(Stats.class).win();
            tick.addAction(() ->
            {
                sequencer.end(SceneBlack.class, Medias.create(next), getInitConfig(spawn));
            }, tickDelay);
        }
        else
        {
            sequencer.end(SceneBlack.class, Medias.create(next), getInitConfig(spawn));
        }
    }

    @Override
    public void playMusic(Media media)
    {
        tasks.offer(() ->
        {
            if (audio != null)
            {
                audio.stop();
            }
            audio = AudioFactory.loadAudio(media);

            final Settings settings = Settings.getInstance();
            if (settings.getVolumeMaster() > 0)
            {
                audio.setVolume(settings.getVolumeMusic());
                audio.play();
            }
        });
    }

    @Override
    public void stopMusic()
    {
        if (audio != null)
        {
            task.interrupt();
            audio.stop();

            try
            {
                task.join(com.b3dgs.lionengine.Constant.HUNDRED);
            }
            catch (final InterruptedException exception)
            {
                Thread.currentThread().interrupt();
                Verbose.exception(exception);
            }
            tasks.clear();
            audio = null;
        }
    }

    @Override
    public void update(double extrp)
    {
        device.update(extrp);
        updatePause();
        updateQuit();
        updateCheats();

        if (!paused)
        {
            tick.update(extrp);
            shaker.update(extrp);
            super.update(extrp);
            checkpoint.update(extrp);
            landscape.update(extrp, camera);
            if (trackerY > 0)
            {
                trackerY = UtilMath.clamp(trackerY += 0.5, 0.0, 21.0);
                tracker.setOffset(0, trackerInitY + (int) Math.floor(trackerY));
            }
        }
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
        hud.setScreenSize(width, height);
    }
}
