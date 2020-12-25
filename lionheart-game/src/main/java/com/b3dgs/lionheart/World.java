/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.Set;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Mirror;
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
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
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
import com.b3dgs.lionheart.object.feature.Floater;
import com.b3dgs.lionheart.object.feature.Jumper;
import com.b3dgs.lionheart.object.feature.Patrol;
import com.b3dgs.lionheart.object.feature.PatrolConfig;
import com.b3dgs.lionheart.object.feature.Spike;
import com.b3dgs.lionheart.object.feature.SwordShade;

/**
 * World game representation.
 */
final class World extends WorldHelper
{
    private final MapTileWater mapWater = services.create(MapTileWater.class);
    private final Checkpoint checkpoint = services.create(Checkpoint.class);
    private final Hud hud = new Hud(services);
    private final InputDevicePointer pointer = services.add(getInputDevice(InputDevicePointer.class));

    private Landscape landscape;
    private Audio audio;
    private int trackerInitY;
    private double trackerY;

    /**
     * Create the world.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    World(Services services)
    {
        super(services);

        map.addFeature(new LayerableModel(4, 2));

        camera.setIntervals(16, 0);
    }

    /**
     * Init music volume and play.
     * 
     * @param media The music file.
     */
    private void initMusic(Media media)
    {
        if (audio != null)
        {
            audio.stop();
        }
        audio = AudioFactory.loadAudio(media);
        audio.setVolume(25);
        if (!Constant.AUDIO_MUTE)
        {
            audio.play();
        }
        else
        {
            AudioFactory.setVolume(0);
        }
    }

    /**
     * Load map from level.
     * 
     * @param media The map media.
     * @param raster The raster folder.
     */
    private void loadMap(Media media, String raster)
    {
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
        });

        map.getFeature(MapTileRastered.class).setRaster(Medias.create(raster, Constant.RASTER_FILE_TILE));
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

        mapWater.create(raster);
        mapWater.addFeature(new LayerableModel(map.getFeature(Layerable.class).getLayerDisplay().intValue() + 1));
        handler.add(mapWater);
    }

    /**
     * Create player and track with camera.
     * 
     * @param start The spawn tile.
     * @return The created player.
     */
    private Featurable createPlayer(Coord start)
    {
        final Featurable player = spawn(Medias.create(Folder.PLAYERS, "default", "Valdyn.xml"), start);
        hud.setFeaturable(player);
        services.add(player.getFeature(SwordShade.class));
        trackPlayer(player);
        return player;
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
        entity.getMirror().ifPresent(mirror -> featurable.getFeature(Mirrorable.class).mirror(Mirror.HORIZONTAL));
        entity.getSpike().ifPresent(config -> featurable.ifIs(Spike.class, spike -> spike.load(config)));
        final List<PatrolConfig> patrols = entity.getPatrols();
        if (!patrols.isEmpty())
        {
            featurable.ifIs(Patrol.class, patrol -> patrol.load(patrols));
            featurable.ifIs(Jumper.class, jumper -> jumper.setJump(entity.getJump()));
        }
        featurable.ifIs(Floater.class, floater -> floater.loadRaster(stage.getRasterFolder()));
        featurable.ifIs(Rasterable.class, rasterable ->
        {
            // if (!featurable.hasFeature(Patrol.class) && !featurable.hasFeature(Grasshopper.class))
            // {
            // entitiesRasters.computeIfAbsent(featurable.getMedia(), r -> new TreeSet<>()).add(entity.getRaster());
            // }
        });
    }

    /**
     * Prepare cached media.
     */
    private void prepareCache()
    {
        factory.createCache(spawner, Medias.create(Folder.EFFECTS), 4);
        Sfx.cache();
    }

    /**
     * Load the stage from configuration.
     * 
     * @param config The stage configuration.
     */
    public void load(Media config)
    {
        final StageConfig stage = services.add(StageConfig.imports(new Configurer(config)));

        initMusic(stage.getMusic());
        loadMap(stage.getMapFile(), stage.getRasterFolder());

        final FactoryLandscape factoryLandscape = new FactoryLandscape(services, source, false);
        landscape = factoryLandscape.createLandscape(stage.getBackground(), stage.getForeground());

        final Coord start = stage.getStart();
        final Featurable player = createPlayer(new Coord(start.getX() * map.getTileWidth(),
                                                         start.getY() * map.getTileHeight()));
        checkpoint.load(stage, player);
        checkpoint.addListener(new CheckpointListener()
        {
            @Override
            public void notifyReachedEnd()
            {
                sequencer.end(Scene.class, stage.getNextStage());
            }

            @Override
            public void notifyReachedBoss()
            {
                camera.setLimitLeft((int) camera.getX());
                spawn(Medias.create(Folder.ENTITIES, "boss", "swamp", "Boss1.xml"),
                      player.getFeature(Transformable.class).getX(),
                      -100.0);
                trackerY = 1.0;
                initMusic(Music.BOSS.get());
            }
        });
        spawner.setRaster(Medias.create(stage.getRasterFolder(), Constant.RASTER_FILE_TILE));

        final HashMap<Media, Set<Integer>> entitiesRasters = new HashMap<>();
        stage.getEntities().forEach(entity -> createEntity(stage, entity, entitiesRasters));

        hud.load();
        handler.updateRemove();
        handler.updateAdd();
        prepareCache();
    }

    @Override
    public void update(double extrp)
    {
        pointer.update(extrp);
        super.update(extrp);
        checkpoint.update(extrp);
        landscape.update(extrp, camera);
        if (trackerY > 0)
        {
            trackerY = UtilMath.clamp(trackerY += 0.5, 0.0, 21.0);
            tracker.setOffset(0, trackerInitY + (int) Math.floor(trackerY));
        }
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
