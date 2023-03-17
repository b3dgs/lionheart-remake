/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionheart.constant.Folder;

/**
 * General settings from properties.
 */
public final class Settings
{
    /** Settings file. */
    public static final String FILENAME = "lionheart.properties";
    /** Lang file. */
    public static final String FILE_LANG = "lang.txt";

    /** Version key. */
    public static final String VERSION = "version";

    /** Language key. */
    public static final String LANG = "lang";

    /** Resolution key. */
    public static final String RESOLUTION = "resolution";
    /** Resolution windowed. */
    public static final String RESOLUTION_WINDOWED = RESOLUTION + ".windowed";
    /** Resolution width. */
    public static final String RESOLUTION_WIDTH = RESOLUTION + ".width";
    /** Resolution height. */
    public static final String RESOLUTION_HEIGHT = RESOLUTION + ".height";
    /** Resolution rate. */
    public static final String RESOLUTION_RATE = RESOLUTION + ".rate";

    /** Volume master. */
    public static final String VOLUME = "volume";
    /** Volume music. */
    public static final String VOLUME_MUSIC = VOLUME + ".music";
    /** Volume sfx. */
    public static final String VOLUME_SFX = VOLUME + ".sfx";

    /** Filter key. */
    public static final String FILTER = "filter";

    /** Gameplay value. */
    public static final String GAMEPLAY = "gameplay";

    /** Raster key. */
    public static final String RASTER = "raster";
    /** Raster enabled key. */
    public static final String RASTER_TYPE = RASTER + ".type";
    /** Raster check key. */
    public static final String RASTER_CHECK = RASTER + ".check";

    /** Hud key. */
    public static final String HUD = "hud";
    /** Hud visible key. */
    public static final String HUD_VISIBLE = HUD + ".visible";
    /** Hud sword key. */
    public static final String HUD_SWORD = HUD + ".sword";

    /** Flicker key. */
    public static final String FLICKER = "flicker";
    /** Flicker background key. */
    public static final String FLICKER_BACKGROUND = FLICKER + ".background";
    /** Flicker foreground key. */
    public static final String FLICKER_FOREGROUND = FLICKER + ".foreground";

    /** Zoom value. */
    public static final String ZOOM = "zoom";

    /** Flag key. */
    public static final String FLAG = "flag";
    /** Flag load value. */
    public static final String FLAG_STRATEGY = FLAG + ".strategy";
    /** Flag parallel value. */
    public static final String FLAG_PARALLEL = FLAG + ".parallel";
    /** Flag sync value. */
    public static final String FLAG_VSYNC = FLAG + ".vsync";
    /** Flag debug value. */
    public static final String FLAG_DEBUG = FLAG + ".debug";

    /** Single instance. */
    private static final Settings INSTANCE = new Settings();
    /** Default language. */
    private static final String DEFAULT_LANG = "en";
    /** Local language. */
    private static final String LOCALE_LANG = Locale.getDefault().getLanguage();
    /** Editor flag. */
    private static boolean editor;

    /**
     * Load settings.
     */
    public static void load()
    {
        load(getFile());
    }

    /**
     * Load settings.
     * 
     * @param file The custom settings.
     */
    public static void load(File file)
    {
        if (!checkVersion())
        {
            file.delete();
        }

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
     * Load default properties.
     */
    public static void loadDefault()
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
     * Get settings.
     * 
     * @return The settings.
     */
    public static Settings getInstance()
    {
        return INSTANCE;
    }

    /**
     * Check if is in editor mode.
     * 
     * @return <code>true</code> if editor, <code>false</code> else.
     */
    public static boolean isEditor()
    {
        return editor;
    }

    /**
     * Set the editor flag.
     * 
     * @param editor The editor flag.
     */
    public static void setEditor(boolean editor)
    {
        Settings.editor = editor;
    }

    /**
     * Get the file.
     * 
     * @return The file.
     */
    public static File getFile()
    {
        return Medias.create(FILENAME).getFile();
    }

    /**
     * Check version validity.
     * 
     * @return <code>true</code> if correct, <code>false</code> else.
     */
    private static boolean checkVersion()
    {
        final Settings settings = new Settings();
        try (InputStream input = new FileInputStream(getFile()))
        {
            settings.load(input);
            return settings.getVersion().equals(Constant.PROGRAM_VERSION.toString());
        }
        catch (@SuppressWarnings("unused") final IOException exception)
        {
            // Ignore
        }
        return false;
    }

    /**
     * Get default platform language.
     *
     * @return The default language.
     */
    private static String getDefaultLang()
    {
        if (Medias.create(Folder.TEXT, LOCALE_LANG, FILE_LANG).exists())
        {
            return LOCALE_LANG;
        }
        return DEFAULT_LANG;
    }

    /**
     * Get desktop resolution.
     * 
     * @return The desktop resolution (<code>null</code> if error).
     */
    private static Resolution getDesktopResolution()
    {
        try
        {
            final DisplayMode desktop = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                           .getDefaultScreenDevice()
                                                           .getDisplayMode();
            return new Resolution(desktop.getWidth(), desktop.getHeight(), desktop.getRefreshRate());
        }
        catch (final Throwable exception)
        {
            Verbose.exception(exception);
            return null;
        }
    }

    /** Properties data. */
    private final Properties properties = new Properties();

    /**
     * Private.
     */
    private Settings()
    {
        super();
    }

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
     * Get version.
     * 
     * @return The version.
     */
    public String getVersion()
    {
        return properties.getProperty(VERSION, "0.0.0");
    }

    /**
     * Get language.
     * 
     * @return The language.
     */
    public String getLang()
    {
        return properties.getProperty(LANG, getDefaultLang());
    }

    /**
     * Get windowed flag.
     * 
     * @return The windowed flag.
     */
    public boolean getResolutionWindowed()
    {
        return getBoolean(RESOLUTION_WINDOWED, true);
    }

    /**
     * Get output resolution.
     * 
     * @return The output resolution.
     */
    public Resolution getResolution()
    {
        final Resolution desktop = getDesktopResolution();
        final int defWidth;
        final int defHeight;
        final int defRate;
        if (desktop != null)
        {
            defWidth = desktop.getWidth();
            defHeight = desktop.getHeight();
            defRate = desktop.getRate();
        }
        else
        {
            defWidth = Constant.RESOLUTION_OUTPUT.getWidth();
            defHeight = Constant.RESOLUTION_OUTPUT.getHeight();
            defRate = Constant.RESOLUTION.getRate();
        }
        return new Resolution(getInt(RESOLUTION_WIDTH, defWidth),
                              getInt(RESOLUTION_HEIGHT, defHeight),
                              getInt(RESOLUTION_RATE, defRate));
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
     * Get filter type.
     * 
     * @return The filter type.
     */
    public FilterType getFilter()
    {
        return getEnum(FILTER, FilterType.NONE, FilterType.class);
    }

    /**
     * Get gameplay type.
     * 
     * @return The gameplay type.
     */
    public GameplayType getGameplay()
    {
        return getEnum(GAMEPLAY, GameplayType.ORIGINAL, GameplayType.class);
    }

    /**
     * Get raster flag.
     * 
     * @return The raster flag.
     */
    public RasterType getRaster()
    {
        return getEnum(RASTER_TYPE, RasterType.DIRECT, RasterType.class);
    }

    /**
     * Get raster check flag.
     * 
     * @return The raster check flag.
     */
    public boolean getRasterCheck()
    {
        return RasterType.CACHE == getRaster() && getBoolean(RASTER_CHECK, false);
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
     * Get hud sword value.
     * 
     * @return The hud sword value.
     */
    public boolean getHudSword()
    {
        return getBoolean(HUD_SWORD, true);
    }

    /**
     * Get flicker background value.
     * 
     * @return The flicker background value.
     */
    public boolean getFlickerBackground()
    {
        return getBoolean(FLICKER_BACKGROUND, false);
    }

    /**
     * Get flicker foreground value.
     * 
     * @return The flicker foreground value.
     */
    public boolean getFlickerForeground()
    {
        return getBoolean(FLICKER_FOREGROUND, false);
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
     * Get flag strategy value.
     * 
     * @return The flag value.
     */
    public int getFlagStrategy()
    {
        return getInt(FLAG_STRATEGY, 0);
    }

    /**
     * Get flag sync value.
     * 
     * @return The flag sync value.
     */
    public boolean getFlagVsync()
    {
        return getBoolean(FLAG_VSYNC, false);
    }

    /**
     * Get load parallel value.
     * 
     * @return The load parallel value.
     */
    public boolean getFlagParallel()
    {
        return getBoolean(FLAG_PARALLEL, true);
    }

    /**
     * Get flag debug value.
     * 
     * @return The flag debug value.
     */
    public boolean getFlagDebug()
    {
        return getBoolean(FLAG_DEBUG, false);
    }

    /**
     * Set text language.
     * 
     * @param lang The language value.
     */
    public void setLang(String lang)
    {
        properties.setProperty(LANG, lang);
    }

    /**
     * Set raster flag.
     * 
     * @param enabled <code>true</code> to enable raster, <code>false</code> else.
     */
    public void setRaster(boolean enabled)
    {
        properties.setProperty(RASTER, String.valueOf(enabled));
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

    /**
     * Get double from key.
     * 
     * @param key The key name.
     * @param def The default value.
     * @param clazz The enum type.
     * @return The value read.
     */
    private <E extends Enum<E>> E getEnum(String key, E def, Class<E> clazz)
    {
        try
        {
            return Enum.valueOf(clazz, properties.getProperty(key, String.valueOf(def.name())));
        }
        catch (final IllegalArgumentException exception)
        {
            Verbose.exception(exception);
            return def;
        }
    }
}
