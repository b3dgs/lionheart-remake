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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Verbose;

/**
 * General settings from properties.
 */
public class Settings
{
    /** Settings file. */
    private static final String FILENAME = "lionheart.properties";

    /** Resolution key. */
    private static final String RESOLUTION = "resolution";
    /** Resolution width. */
    private static final String RESOLUTION_WIDTH = RESOLUTION + ".width";
    /** Resolution height. */
    private static final String RESOLUTION_HEIGHT = RESOLUTION + ".height";
    /** Resolution resize key. */
    private static final String RESOLUTION_RESIZE = RESOLUTION + ".resize";

    /** Volume master. */
    private static final String VOLUME = "volume";
    /** Volume music. */
    private static final String VOLUME_MUSIC = VOLUME + ".music";
    /** Volume sfx. */
    private static final String VOLUME_SFX = VOLUME + ".sfx";

    /** Raster key. */
    private static final String RASTER = "raster";
    /** Raster map key. */
    private static final String RASTER_MAP = RASTER + ".map";
    /** Raster map water key. */
    private static final String RASTER_MAP_WATER = RASTER + ".map.water";
    /** Raster object key. */
    private static final String RASTER_OBJECT = RASTER + ".object";
    /** Raster object water key. */
    private static final String RASTER_OBJECT_WATER = RASTER + ".object.water";
    /** Raster hero water key. */
    private static final String RASTER_HERO_WATER = RASTER + ".hero.water";

    /** Hud key. */
    private static final String HUD = "hud";
    /** Hud visible key. */
    private static final String HUD_VISIBLE = HUD + ".visible";

    /** Background key. */
    private static final String BACKGROUND = "background";
    /** Background flicker key. */
    private static final String BACKGROUND_FLICKER = BACKGROUND + ".flicker";

    /** Zoom value. */
    private static final String ZOOM = "zoom";

    /** Single instance. */
    private static final Settings INSTANCE = new Settings();

    /**
     * Load default properties.
     */
    private static void loadDefault()
    {
        try (InputStream input = Medias.create(FILENAME).getInputStream())
        {
            INSTANCE.load(input);
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
        }
    }

    /**
     * Init.
     */
    static
    {
        final File file = new File(System.getProperty("user.home"), FILENAME);
        if (file.exists())
        {
            try (InputStream input = new FileInputStream(file))
            {
                INSTANCE.load(input);
            }
            catch (@SuppressWarnings("unused") final IOException exception)
            {
                loadDefault();
            }
        }
        else
        {
            loadDefault();
        }
    }

    /**
     * Get settings.
     * 
     * @return The settings.
     */
    public static Settings getInstance()
    {
        return INSTANCE;
    }

    /** Properties data. */
    private final Properties properties = new Properties();

    /**
     * Load properties.
     * 
     * @param input The properties input.
     * @throws IOException If error.
     */
    public void load(InputStream input) throws IOException
    {
        properties.load(input);
    }

    /**
     * Get output resolution.
     * 
     * @return The output resolution.
     */
    public Resolution getResolution()
    {
        return new Resolution(getInt(RESOLUTION_WIDTH, Constant.RESOLUTION.getWidth()),
                              getInt(RESOLUTION_HEIGHT, Constant.RESOLUTION.getHeight()),
                              Constant.RESOLUTION_OUTPUT.getRate());
    }

    /**
     * Get resize resolution.
     * 
     * @return The resize resolution.
     */
    public boolean getResolutionResize()
    {
        return getBoolean(RESOLUTION_RESIZE, true);
    }

    /**
     * Get master volume.
     * 
     * @return The master volume.
     */
    public int getVolumeMaster()
    {
        return getInt(VOLUME, com.b3dgs.lionengine.Constant.HUNDRED);
    }

    /**
     * Get music volume.
     * 
     * @return The music volume.
     */
    public int getVolumeMusic()
    {
        return (int) (getVolumeMaster() / 100.0 * getInt(VOLUME_MUSIC, com.b3dgs.lionengine.Constant.HUNDRED));
    }

    /**
     * Get sfx volume.
     * 
     * @return The sfx volume.
     */
    public int getVolumeSfx()
    {
        return (int) (getVolumeMaster() / 100.0 * getInt(VOLUME_SFX, com.b3dgs.lionengine.Constant.HUNDRED));
    }

    /**
     * Get raster flag.
     * 
     * @return The raster flag.
     */
    public boolean getRaster()
    {
        return getBoolean(RASTER, true);
    }

    /**
     * Get raster map flag.
     * 
     * @return The raster map flag.
     */
    public boolean getRasterMap()
    {
        return getRaster() && getBoolean(RASTER_MAP, true);
    }

    /**
     * Get raster map water flag.
     * 
     * @return The raster map water flag.
     */
    public boolean getRasterMapWater()
    {
        return getRaster() && getBoolean(RASTER_MAP_WATER, true);
    }

    /**
     * Get raster object flag.
     * 
     * @return The raster object flag.
     */
    public boolean getRasterObject()
    {
        return getRaster() && getBoolean(RASTER_OBJECT, true);
    }

    /**
     * Get raster object water flag.
     * 
     * @return The raster object water flag.
     */
    public boolean getRasterObjectWater()
    {
        return getRaster() && getBoolean(RASTER_OBJECT_WATER, true);
    }

    /**
     * Get raster hero water.
     * 
     * @return The raster hero water flag.
     */
    public boolean getRasterHeroWater()
    {
        return getRaster() && getBoolean(RASTER_HERO_WATER, true);
    }

    /**
     * Get hud visible value.
     * 
     * @return The hud visible value.
     */
    public boolean getHudVisible()
    {
        return getBoolean(HUD_VISIBLE, true);
    }

    /**
     * Get background flicker value.
     * 
     * @return The background flicker value.
     */
    public boolean getBackgroundFlicker()
    {
        return getBoolean(BACKGROUND_FLICKER, true);
    }

    /**
     * Get zoom value.
     * 
     * @return The zoom value.
     */
    public double getZoom()
    {
        return getDouble(ZOOM, 1.0);
    }

    /**
     * Get boolean from key.
     * 
     * @param key The key name.
     * @param def The default value.
     * @return The value read.
     */
    private boolean getBoolean(String key, boolean def)
    {
        try
        {
            return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(def)));
        }
        catch (final NumberFormatException exception)
        {
            Verbose.exception(exception);
            return def;
        }
    }

    /**
     * Get integer from key.
     * 
     * @param key The key name.
     * @param def The default value.
     * @return The value read.
     */
    private int getInt(String key, int def)
    {
        try
        {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(def)));
        }
        catch (final NumberFormatException exception)
        {
            Verbose.exception(exception);
            return def;
        }
    }

    /**
     * Get double from key.
     * 
     * @param key The key name.
     * @param def The default value.
     * @return The value read.
     */
    private double getDouble(String key, double def)
    {
        try
        {
            return Double.parseDouble(properties.getProperty(key, String.valueOf(def)));
        }
        catch (final NumberFormatException exception)
        {
            Verbose.exception(exception);
            return def;
        }
    }
}
