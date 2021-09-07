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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormulaConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionGroupConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.landscape.BackgroundType;
import com.b3dgs.lionheart.object.XmlLoader;

/**
 * Static utility functions.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class Util
{
    private static volatile Consumer<BackgroundType> init;

    /**
     * Init task.
     * 
     * @param init The init task.
     */
    public static void init(Consumer<BackgroundType> init)
    {
        Util.init = init;
    }

    /**
     * Run init task.
     * 
     * @param type The background type.
     */
    public static void run(BackgroundType type)
    {
        if (init != null)
        {
            init.accept(type);
        }
    }

    /**
     * Get resolution adapted to output from source.
     * 
     * @param source The source resolution.
     * @param context The context reference.
     * @return The adjusted source resolution based on output wide.
     */
    public static Resolution getResolution(Resolution source, Context context)
    {
        final Resolution output = context.getConfig().getOutput();
        if (Settings.getInstance().getResolutionResize())
        {
            final double factor = source.getHeight() / (double) output.getHeight();
            return new Resolution((int) Math.round(output.getWidth() * factor),
                                  (int) Math.round(output.getHeight() * factor),
                                  source.getRate());
        }
        return output;
    }

    /**
     * Get resolution adapted to output from source.
     * 
     * @param context The context reference.
     * @param minHeight The minimum height.
     * @param maxWidth The maximum width.
     * @param marginWidth The width margin.
     * @return The adjusted source resolution based on output wide.
     */
    public static Resolution getResolution(Context context, int minHeight, int maxWidth, int marginWidth)
    {
        final Resolution resolution;
        final Resolution output = context.getConfig().getOutput();
        if (Settings.getInstance().getResolutionResize())
        {
            final Resolution adjusted = getResolution(Constant.RESOLUTION, context);
            final double ratio = (double) output.getWidth() / (double) output.getHeight();
            final int width = adjusted.getWidth() - (adjusted.getWidth() - maxWidth + marginWidth);
            final int height = (int) Math.floor(width / ratio);

            if (height < minHeight)
            {
                resolution = new Resolution((int) Math.floor(minHeight * ratio), minHeight, adjusted.getRate());
            }
            else
            {
                resolution = new Resolution(width, height, adjusted.getRate());
            }
        }
        else
        {
            resolution = output;
        }
        return resolution;
    }

    /**
     * Read media lines.
     * 
     * @param media The media reference.
     * @return The ordered lines found.
     */
    public static List<String> readLines(Media media)
    {
        final List<String> lines = new ArrayList<>();
        try (BufferedReader data = new BufferedReader(new InputStreamReader(media.getInputStream(),
                                                                            StandardCharsets.UTF_8)))
        {
            String line;
            while ((line = data.readLine()) != null)
            {
                lines.add(line);
            }
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
        }
        return lines;
    }

    /**
     * Convert multi line text to single line text with separator for font.
     * 
     * @param media The media reference.
     * @return The text single line with separator.
     */
    public static String toFontText(Media media)
    {
        final StringBuilder builder = new StringBuilder();
        final List<String> lines = Util.readLines(media);
        final int n = lines.size();
        for (int i = 0; i < n; i++)
        {
            builder.append(lines.get(i));
            if (i < n - 1)
            {
                builder.append('%');
            }
        }
        return builder.toString();
    }

    /**
     * Load map tiles data.
     * 
     * @param map The map reference.
     * @param media The map tiles data.
     */
    public static void loadMapTiles(MapTile map, Media media)
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

    /**
     * Load entity data.
     * 
     * @param entity The entity to load.
     * @param config The config data.
     */
    public static void loadEntityFeature(Featurable entity, EntityConfig config)
    {
        entity.getFeatures().forEach(feature ->
        {
            if (feature instanceof XmlLoader)
            {
                ((XmlLoader) feature).load(config.getRoot());
            }
        });
    }

    /**
     * Get stage by difficulty.
     * 
     * @param difficulty The difficulty.
     * @param index The stage index.
     * @return The stage media.
     */
    public static Media getStage(Difficulty difficulty, int index)
    {
        if (Difficulty.NORMAL.equals(difficulty) || index > 9 && index < 13)
        {
            return Stage.values()[index];
        }
        return StageHard.values()[index];
    }

    /**
     * Private constructor.
     */
    private Util()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
