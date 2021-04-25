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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
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
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormulaConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionGroupConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionModel;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionRenderer;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionRendererModel;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterModel;
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
import com.b3dgs.lionheart.landscape.FactoryLandscape;
import com.b3dgs.lionheart.landscape.ForegroundType;
import com.b3dgs.lionheart.landscape.Landscape;
import com.b3dgs.lionheart.menu.Menu;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.feature.BulletBounceOnGround;
import com.b3dgs.lionheart.object.feature.Canon2Airship;
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

    private final MapTileWater mapWater = services.create(MapTileWater.class);
    private final CheckpointHandler checkpoint = services.create(CheckpointHandler.class);
    private final Hud hud = services.create(Hud.class);
    private final ScreenShaker shaker = services.create(ScreenShaker.class);
    private final DeviceController device = services.add(DeviceControllerConfig.create(services,
                                                                                       Medias.create("input.xml")));

    private final Tick tick = new Tick();

    private Landscape landscape;
    private Audio audio;
    private int trackerInitY;
    private double trackerY;
    private StageConfig stage;
    private StateHandler player;
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

        camera.setIntervals(Constant.CAMERA_HORIZONTAL_MARGIN, 0);
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
            MapTileHelper.importAndSave(Medias.create(media.getPath().replace(Extension.MAP, Extension.IMAGE)), media);
        }

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
        raster.ifPresent(r -> map.getFeature(MapTileRastered.class)
                                 .setRaster(Medias.create(r, Constant.RASTER_FILE_TILE),
                                            config.getLinesPerRaster(),
                                            config.getRasterLineOffset()));
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
        if (bottomRip.exists())
        {
            if (!bottom.exists())
            {
                MapTileHelper.importAndSave(bottomRip, bottom);
            }
            final MapTileGame mapBottom = new MapTileGame();
            mapBottom.addFeature(new MapTilePersisterModel());
            mapBottom.addFeature(new MapTileGroupModel());
            mapBottom.addFeature(new MapTileCollisionModel());
            mapBottom.addFeature(new LayerableModel(4, 5));
            final MapTileViewer mapViewer = mapBottom.addFeatureAndGet(new MapTileViewerModel(services));
            loadMapTiles(mapBottom, bottom);

            raster.ifPresent(r ->
            {
                final MapTileRastered mapRaster = mapBottom.addFeatureAndGet(new MapTileRasteredModel());
                if (mapRaster.loadSheets())
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
     * @return The created player.
     */
    private Featurable createPlayerAndLoadCheckpoints(InitConfig init)
    {
        final Featurable featurable = spawn(Medias.create(Folder.HERO, "valdyn", "Valdyn.xml"), 0, 0);
        featurable.getFeature(Stats.class).apply(init);
        featurable.getFeature(Underwater.class).loadRaster("raster/" + Folder.HERO + "/valdyn/");

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
        tracker.addFeature(new LayerableModel(player.getFeature(Layerable.class).getLayerRefresh().intValue() + 1));
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
        entity.getSecret().ifPresent(secret -> featurable.getFeature(EntityModel.class).setSecret(true));
        entity.getNext()
              .ifPresent(next -> featurable.getFeature(EntityModel.class).setNext(next, entity.getNextSpawn()));
        entity.getMirror()
              .ifPresent(mirror -> featurable.getFeature(Mirrorable.class)
                                             .mirror(mirror.booleanValue() ? Mirror.HORIZONTAL : Mirror.NONE));
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
            featurable.ifIs(Spider.class, Spider::track);
        }
        entity.getDragon1().ifPresent(config -> featurable.ifIs(Dragon1.class, dragon1 -> dragon1.load(config)));
        entity.getCanon2().ifPresent(config -> featurable.ifIs(Canon2Airship.class, canon2 -> canon2.load(config)));
        entity.getShooter().ifPresent(config -> featurable.ifIs(Shooter.class, shooter -> shooter.load(config)));
        entity.getPillar().ifPresent(config -> featurable.ifIs(Pillar.class, pillar -> pillar.load(config)));
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
                stopMusic();
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
                stopMusic();
                sequencer.end(SceneBlack.class, Stage.values()[i], getInitConfig(Optional.empty()));
            }
        }
        if (device.isFiredOnce(DeviceMapping.K5))
        {
            player.changeState(StateWin.class);
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
        Sfx.cacheStart();

        services.add(config);
        stage = services.add(StageConfig.imports(new Configurer(config)));

        loadMap(stage);

        final FactoryLandscape factoryLandscape = new FactoryLandscape(services, source, true);
        landscape = factoryLandscape.createLandscape(stage.getBackground(), stage.getForeground());

        cheats = init.isCheats();
        createPlayerAndLoadCheckpoints(init);

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
        stage.getRasterFolder().ifPresent(r ->
        {
            final Media rasterMedia = Medias.create(r, Constant.RASTER_FILE_TILE);
            if (rasterMedia.exists())
            {
                spawner.setRaster(rasterMedia);
            }
        });

        final HashMap<Media, Set<Integer>> entitiesRasters = new HashMap<>();
        stage.getEntities().forEach(entity -> createEntity(stage, entity, entitiesRasters));

        factory.createCache(spawner, Medias.create(Folder.EFFECT, theme), 4);
        factory.createCache(spawner, Medias.create(Folder.PROJECTILE, theme), 6);

        hud.load();
        handler.updateRemove();
        handler.updateAdd();

        Sfx.cacheEnd();

        System.gc();

        tick.restart();
    }

    @Override
    public void loadNextStage(String next, int tickDelay, Optional<Coord> spawn)
    {
        if (tickDelay > 0)
        {
            player.changeState(StateWin.class);
            tick.addAction(() ->
            {
                audio.stop();
                sequencer.end(SceneBlack.class, Medias.create(next), getInitConfig(spawn));
            }, tickDelay);
        }
        else
        {
            audio.stop();
            sequencer.end(SceneBlack.class, Medias.create(next), getInitConfig(spawn));
        }
    }

    @Override
    public void playMusic(Media media)
    {
        stopMusic();
        audio = AudioFactory.loadAudio(media);

        final Settings settings = Settings.getInstance();
        if (settings.getVolumeMaster() > 0)
        {
            audio.setVolume(settings.getVolumeMusic());
            audio.play();
        }
    }

    @Override
    public void stopMusic()
    {
        if (audio != null)
        {
            audio.stop();
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
