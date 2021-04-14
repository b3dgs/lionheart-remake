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

    /** Volume master. */
    private static final String VOLUME = "volume";
    /** Volume music. */
    private static final String VOLUME_MUSIC = VOLUME + ".music";
    /** Volume sfx. */
    private static final String VOLUME_SFX = VOLUME + ".sfx";

    /** Zoom value. */
    private static final String ZOOM = "zoom";

    /** Single instance. */
    private static final Settings INSTANCE = new Settings();

    /**
     * Load default properties.
     */
    private static void loadDefault()
    {
        try (InputStream input = Settings.class.getResourceAsStream(FILENAME))
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
        return getInt(VOLUME_MUSIC, com.b3dgs.lionengine.Constant.HUNDRED);
    }

    /**
     * Get sfx volume.
     * 
     * @return The sfx volume.
     */
    public int getVolumeSfx()
    {
        return getInt(VOLUME_SFX, com.b3dgs.lionengine.Constant.HUNDRED);
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
