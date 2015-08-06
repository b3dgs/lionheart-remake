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

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionengine.stream.Stream;

/**
 * Game scene implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Scene extends Sequence
{
    /** Original display. */
    public static final Resolution RESOLUTION_ORIGINAL = new Resolution(272, 208, 60);
    /** Scene display. */
    public static final Resolution RESOLUTION = new Resolution(320, 240, 60);

    /**
     * Import the level and save it.
     * 
     * @param level The level to import.
     */
    private static void importLevelAndSave(Level level)
    {
        final MapTile map = new MapTileGame();
        map.create(level.getRip());
        try (FileWriting writer = Stream.createFileWriting(level.getFile()))
        {
            map.save(writer);
        }
        catch (final IOException exception)
        {
            Verbose.exception(Scene.class, "importMapAndSave", exception);
        }
    }

    /** World instance. */
    private final World world = new World(getConfig(), getInputDevice(Keyboard.class));
    /** Current level. */
    private final Level level;

    /**
     * Create the scene.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader, RESOLUTION_ORIGINAL);

        level = Level.SWAMP_1_1;
    }

    @Override
    protected void load()
    {
        if (!level.getFile().exists())
        {
            importLevelAndSave(level);
        }
        world.loadFromFile(level.getFile());
    }

    @Override
    public void update(double extrp)
    {
        world.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        world.render(g);
    }
}
