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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Layerable;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionRenderer;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionRendererModel;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewer;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.helper.WorldHelper;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.InputDevicePointer;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.landscape.FactoryLandscape;
import com.b3dgs.lionheart.landscape.Landscape;
import com.b3dgs.lionheart.landscape.LandscapeType;
import com.b3dgs.lionheart.object.feature.SwordShade;

/**
 * World game representation.
 */
final class World extends WorldHelper
{
    private final MapTilePersister mapPersister = map.getFeature(MapTilePersister.class);
    private final MapTileViewer mapViewer = map.getFeature(MapTileViewer.class);
    private final FactoryLandscape factoryLandscape = new FactoryLandscape(source, false);
    private final Hud hud = new Hud(services);
    private final InputDevicePointer pointer = services.add(getInputDevice(InputDevicePointer.class));

    private Landscape landscape;
    private Audio audio;

    /**
     * Create the world.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    World(Services services)
    {
        super(services);

        map.addFeature(new LayerableModel(4, 1));

        camera.setIntervals(16, 0);
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

        if (Constant.DEBUG)
        {
            final MapTileCollisionRenderer renderer = map.addFeatureAndGet(new MapTileCollisionRendererModel());
            renderer.createCollisionDraw();
            mapViewer.addRenderer(renderer);
        }
    }

    /**
     * Load all entities from level.
     * 
     * @param file The file level.
     * @throws IOException If error.
     */
    private void loadEntities(FileReading file) throws IOException
    {
        persister.load(file);

        final Featurable player = spawn(Medias.create(Folder.PLAYERS, "default", "Valdyn.xml"), 200, 64);
        hud.setFeaturable(player);
        services.add(player.getFeature(SwordShade.class));
        trackPlayer(player);
    }

    /**
     * Track player with camera.
     * 
     * @param player The player to track.
     */
    private void trackPlayer(Featurable player)
    {
        tracker.addFeature(new LayerableModel(player.getFeature(Layerable.class).getLayerRefresh().intValue() + 1));
        tracker.setOffset(0, player.getFeature(Transformable.class).getHeight() / 2);
        tracker.track(player);
    }

    /**
     * Prepare cached media.
     */
    private void prepareCache()
    {
        factory.createCache(Medias.create(Folder.EFFECTS), 5);
        Sfx.cache();
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
        if (!Constant.AUDIO_MUTE)
        {
            audio.play();
        }
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
