/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.core.awt.Engine;
import com.b3dgs.lionheart.menu.Menu;

/**
 * Program starts here.
 * 
 * @version 0.2.0
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class AppLionheart
{
    /** Application name. */
    public static final String NAME = "Lionheart Remake";
    /** Application version. */
    public static final Version VERSION = Version.create(0, 2, 0);
    /** Resources directory. */
    public static final String RESOURCES = "resources";
    /** Sprites directory. */
    public static final String SPRITES_DIR = "sprite";
    /** Levels directory. */
    public static final String LEVELS_DIR = "level";
    /** Sheets directory. */
    public static final String TILES_DIR = "tile";
    /** Rasters directory. */
    public static final String RASTERS_DIR = "raster";
    /** Main entity directory name. */
    public static final String ENTITIES_DIR = "entity";
    /** Backgrounds directory name. */
    public static final String BACKGROUNDS_DIR = "background";
    /** Foregrounds directory name. */
    public static final String FOREGROUNDS_DIR = "foreground";
    /** Effects directory. */
    public static final String EFFECTS_DIR = "effect";
    /** Projectiles directory. */
    public static final String PROJECTILES_DIR = "projectile";
    /** Launchers directory. */
    public static final String LAUNCHERS_DIR = "launcher";
    /** Musics directory. */
    public static final String MUSICS_DIR = "music";
    /** Sound fx directory name. */
    public static final String SFX_DIR = "sfx";
    /** Show collision bounds. */
    public static final boolean SHOW_COLLISIONS = false;
    /** Raster enabled. */
    public static final boolean RASTER_ENABLED = true;
    /** Enable sound. */
    private static final boolean ENABLE_SOUND = true;

    /**
     * Main function.
     * 
     * @param args The arguments (none).
     */
    public static void main(String[] args)
    {
        Engine.start(AppLionheart.NAME, AppLionheart.VERSION, Verbose.CRITICAL, AppLionheart.RESOURCES);
        Sfx.setEnabled(AppLionheart.ENABLE_SOUND);
        SonicArranger.setEnabled(AppLionheart.ENABLE_SOUND);

        final Resolution output = new Resolution(640, 480, 60);
        final Config config = new Config(output, 16, true);
        final Loader loader = new Loader(config);
        loader.start(Menu.class);
    }

    /**
     * Private constructor.
     */
    private AppLionheart()
    {
        throw new RuntimeException();
    }
}
