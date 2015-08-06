/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileCollision;
import com.b3dgs.lionengine.game.map.MapTileCollisionModel;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.object.ComponentCollision;
import com.b3dgs.lionengine.game.object.ComponentRenderer;
import com.b3dgs.lionengine.game.object.ComponentUpdater;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionheart.landscape.FactoryLandscape;
import com.b3dgs.lionheart.landscape.Landscape;
import com.b3dgs.lionheart.landscape.LandscapeType;

/**
 * World game representation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class World extends WorldGame
{
    /** Services reference. */
    private final Services services = new Services();
    /** Camera reference. */
    private final Camera camera = services.create(Camera.class);
    /** Factory reference. */
    private final Factory factory = services.create(Factory.class);
    /** Handler reference. */
    private final Handler handler = services.create(Handler.class);
    /** Map reference. */
    private final MapTile map = services.create(MapTileGame.class);
    /** Map collision. */
    private final MapTileCollision mapCollision = map.createFeature(MapTileCollisionModel.class);
    /** Landscape factory. */
    private final FactoryLandscape factoryLandscape;
    /** Landscape. */
    private Landscape landscape;

    /**
     * Create the world.
     * 
     * @param config The config reference.
     * @param keyboard The keyboard reference.
     */
    public World(Config config, Keyboard keyboard)
    {
        super(config);

        final double scaleH = width / (double) Scene.RESOLUTION.getWidth();
        final double scaleV = height / (double) Scene.RESOLUTION.getHeight();

        factoryLandscape = new FactoryLandscape(source, scaleH, scaleV, false);

        handler.addUpdatable(new ComponentUpdater());
        handler.addUpdatable(new ComponentCollision());
        handler.addRenderable(new ComponentRenderer());

        services.add(keyboard);
    }

    @Override
    public void update(double extrp)
    {
        handler.update(extrp);
        landscape.update(extrp, camera);
    }

    @Override
    public void render(Graphic g)
    {
        landscape.renderBackground(g);
        map.render(g);
        handler.render(g);
        landscape.renderForeground(g);
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        map.save(file);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        map.load(file);

        camera.setView(0, 0, width, height);
        camera.setLimits(map);
        camera.setIntervals(width, 0);

        landscape = factoryLandscape.createLandscape(LandscapeType.SWAMP_DAY);
    }
}
