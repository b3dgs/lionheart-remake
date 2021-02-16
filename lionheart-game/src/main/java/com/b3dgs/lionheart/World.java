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
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionRenderer;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionRendererModel;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.raster.MapTileRastered;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewer;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.helper.MapTileHelper;
import com.b3dgs.lionengine.helper.WorldHelper;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.InputDevicePointer;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.landscape.FactoryLandscape;
import com.b3dgs.lionheart.landscape.Landscape;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.feature.BulletBounceOnGround;
import com.b3dgs.lionheart.object.feature.Canon1;
import com.b3dgs.lionheart.object.feature.Canon2;
import com.b3dgs.lionheart.object.feature.Canon2Airship;
import com.b3dgs.lionheart.object.feature.Canon3;
import com.b3dgs.lionheart.object.feature.Dragon1;
import com.b3dgs.lionheart.object.feature.Dragon2;
import com.b3dgs.lionheart.object.feature.Floater;
import com.b3dgs.lionheart.object.feature.Geyzer;
import com.b3dgs.lionheart.object.feature.HotFireBall;
import com.b3dgs.lionheart.object.feature.Jumper;
import com.b3dgs.lionheart.object.feature.Patrol;
import com.b3dgs.lionheart.object.feature.PatrolConfig;
import com.b3dgs.lionheart.object.feature.Rotating;
import com.b3dgs.lionheart.object.feature.Spider;
import com.b3dgs.lionheart.object.feature.Spike;
import com.b3dgs.lionheart.object.feature.SwordShade;
import com.b3dgs.lionheart.object.state.StateWin;

/**
 * World game representation.
 */
final class World extends WorldHelper implements MusicPlayer, LoadNextStage
{
    private final MapTileWater mapWater = services.create(MapTileWater.class);
    private final Checkpoint checkpoint = services.create(Checkpoint.class);
    private final Hud hud = new Hud(services);
    private final InputDevicePointer pointer = services.add(getInputDevice(InputDevicePointer.class));
    private final Tick tick = new Tick();

    private Landscape landscape;
    private Audio audio;
    private int trackerInitY;
    private double trackerY;
    private StageConfig stage;
    private StateHandler player;

    /**
     * Create the world.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    World(Services services)
    {
        super(services);

        services.add((MusicPlayer) this::playMusic);
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
            MapTileHelper.importAndSave(Medias.create(media.getPath().replace(".lvl", ".png")), media);
        }

        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        map.addListener(tile ->
        {
            if (CollisionName.LIANA_TOP.equals(mapGroup.getGroup(tile)))
            {
                spawn(Medias.create(Folder.EFFECTS, "Liana.xml"), tile);
            }
            else if (CollisionName.BLOCK.equals(mapGroup.getGroup(tile)))
            {
                spawn(Medias.create(Folder.EFFECTS, "ancienttown", "Block.xml"), tile);
            }
        });

        final Optional<String> raster = config.getRasterFolder();
        raster.ifPresent(r -> map.getFeature(MapTileRastered.class)
                                 .setRaster(Medias.create(r, Constant.RASTER_FILE_TILE),
                                            config.getLinesPerRaster(),
                                            config.getRasterLineOffset()));
        try (FileReading reading = new FileReading(media))
        {
            final MapTilePersister mapPersister = map.getFeature(MapTilePersister.class);
            mapPersister.load(reading);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }

        if (Constant.DEBUG)
        {
            final MapTileCollisionRenderer renderer = map.addFeatureAndGet(new MapTileCollisionRendererModel());
            renderer.createCollisionDraw();

            final MapTileViewer mapViewer = map.getFeature(MapTileViewer.class);
            mapViewer.addRenderer(renderer);
        }

        raster.ifPresent(r ->
        {
            mapWater.create(r);
            mapWater.addFeature(new LayerableModel(map.getFeature(Layerable.class).getLayerDisplay().intValue() + 1));
            handler.add(mapWater);
        });
    }

    /**
     * Create player and track with camera.
     * 
     * @param start The spawn tile.
     * @return The created player.
     */
    private Featurable createPlayer(Coord start)
    {
        final Featurable featurable = spawn(Medias.create(Folder.PLAYERS, "default", "Valdyn.xml"), start);
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
        entity.getEnd().ifPresent(secret -> featurable.getFeature(EntityModel.class).setEnd(true));
        entity.getMirror().ifPresent(mirror -> featurable.getFeature(Mirrorable.class).mirror(Mirror.HORIZONTAL));
        entity.getSpike().ifPresent(config -> featurable.ifIs(Spike.class, spike -> spike.load(config)));
        entity.getCanon().ifPresent(config ->
        {
            featurable.ifIs(Canon1.class, canon -> canon.load(config));
            featurable.ifIs(Canon2.class, canon -> canon.load(config));
            featurable.ifIs(Canon3.class, canon -> canon.load(config));
        });
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
        entity.getDragon2().ifPresent(config -> featurable.ifIs(Dragon2.class, dragon2 -> dragon2.load(config)));
        entity.getCanon2().ifPresent(config -> featurable.ifIs(Canon2Airship.class, canon2 -> canon2.load(config)));
        stage.getRasterFolder().ifPresent(r ->
        {
            featurable.ifIs(Floater.class, floater -> floater.loadRaster(r));
            featurable.ifIs(BulletBounceOnGround.class, buller -> buller.loadRaster(r));
        });
    }

    /**
     * Load the stage from configuration.
     * 
     * @param config The stage configuration.
     */
    public void load(Media config)
    {
        Sfx.cacheStart();

        stage = services.add(StageConfig.imports(new Configurer(config)));

        loadMap(stage);

        final FactoryLandscape factoryLandscape = new FactoryLandscape(services, source, true);
        landscape = factoryLandscape.createLandscape(stage.getBackground(), stage.getForeground());

        final Coord start = stage.getStart();
        final Featurable player = createPlayer(new Coord(start.getX() * map.getTileWidth(),
                                                         start.getY() * map.getTileHeight()));
        checkpoint.load(stage, player);

        final String theme = stage.getBackground().getWorld().getFolder();
        checkpoint.addListener(new CheckpointListener()
        {
            @Override
            public void notifyReachedEnd()
            {
                loadNextStage();
            }

            @Override
            public void notifyReachedBoss()
            {
                // TODO Swamp
                // camera.setLimitLeft((int) camera.getX());
                // spawn(Medias.create(Folder.BOSS, "swamp", "Boss1.xml"),
                // player.getFeature(Transformable.class).getX(),
                // -100.0);
                // trackerY = 1.0;

                // TODO Lava
                spawn(Medias.create(Folder.BOSS, theme, "Boss.xml"), 860 * map.getTileWidth(), 3 * map.getTileHeight());

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

        factory.createCache(spawner, Medias.create(Folder.EFFECTS, theme), 4);
        factory.createCache(spawner, Medias.create(Folder.PROJECTILES, theme), 6);

        hud.load();
        handler.updateRemove();
        handler.updateAdd();

        Sfx.cacheEnd();
        playMusic(stage.getMusic());

        System.gc();

        tick.restart();
    }

    @Override
    public void loadNextStage(int tickDelay)
    {
        if (tickDelay > 0)
        {
            player.changeState(StateWin.class);
            tick.addAction(() ->
            {
                audio.stop();
                sequencer.end(SceneBlack.class, stage.getNextStage());
            }, tickDelay);
        }
        else
        {
            audio.stop();
            sequencer.end(SceneBlack.class, stage.getNextStage());
        }
    }

    @Override
    public void playMusic(Media media)
    {
        if (audio != null)
        {
            audio.stop();
        }
        audio = AudioFactory.loadAudio(media);
        if (!Constant.AUDIO_MUTE)
        {
            AudioFactory.setVolume(Constant.AUDIO_VOLUME);
            audio.setVolume(Constant.AUDIO_VOLUME / 2);
            audio.play();
        }
        else
        {
            AudioFactory.setVolume(0);
        }
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        pointer.update(extrp);
        super.update(extrp);
        checkpoint.update(extrp);
        landscape.update(extrp, camera);
        if (trackerY > 0)
        {
            trackerY = UtilMath.clamp(trackerY += 0.5, 0.0, 21.0);
            tracker.setOffset(0, trackerInitY + (int) Math.floor(trackerY));
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
    }
}
