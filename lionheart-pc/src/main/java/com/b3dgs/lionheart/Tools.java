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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.FeaturableConfig;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.ImageInfo;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.landscape.BackgroundType;
import com.b3dgs.lionheart.object.feature.Underwater;

/**
 * Program starts here.
 */
public final class Tools
{
    private static final String BLANK = com.b3dgs.lionengine.Constant.EMPTY_STRING;
    private static final String PNG = ".png";
    private static final String XML = Factory.FILE_DATA_DOT_EXTENSION;
    private static final String FILE_SHEETS = "0.png";
    private static final String FILE_RASTER_INSIDE = "tiles_inside.png";
    private static final int TILE_HEIGHT = 16;

    private static final int COLOR2 = new ColorRgba(0, 128, 128).getRgba();

    /**
     * Generate raster for whole world.
     * 
     * @param type The world type.
     */
    public static void generateWorldRaster(WorldType type)
    {
        for (final BackgroundType background : BackgroundType.values())
        {
            if (type.equals(background.getWorld()))
            {
                generateWorldRaster(background);
            }
        }
    }

    /**
     * Generate raster for whole world.
     * 
     * @param type The landscape type.
     */
    public static void generateWorldRaster(BackgroundType type)
    {
        final ExecutorService executor = Executors.newFixedThreadPool(Math.max(1,
                                                                               Runtime.getRuntime()
                                                                                      .availableProcessors()
                                                                                  / 2));
        executor.execute(() ->
        {
            if (BackgroundType.LAVA.equals(type))
            {
                generateTileRasterInside(type);
            }
            else
            {
                generateTileRaster(type);
            }

            generateTileWaterRaster(type);

            generateHeroWaterRaster(type);
        });

        final List<Media> medias = new ArrayList<>();
        medias.addAll(Medias.create(Folder.ENTITY, type.getWorld().getFolder()).getMedias());
        medias.addAll(Medias.create(Folder.PROJECTILE, type.getWorld().getFolder()).getMedias());
        medias.addAll(Medias.create(Folder.EFFECT, type.getWorld().getFolder()).getMedias());

        for (final Media media : medias)
        {
            executor.execute(() ->
            {
                if (media.getName().endsWith(XML))
                {
                    if (BackgroundType.LAVA.equals(type))
                    {
                        generateObjectRasterInside(type, media);
                    }
                    else
                    {
                        generateObjectRaster(type, media);
                    }

                    if (new Xml(media).getChild(FeaturableConfig.NODE_FEATURES)
                                      .getChildren(FeaturableConfig.NODE_FEATURE)
                                      .stream()
                                      .map(Xml::getText)
                                      .collect(Collectors.toList())
                                      .contains(Underwater.class.getName()))
                    {
                        generateObjectWaterRaster(type, media);
                    }
                }
            });
        }

        executor.shutdown();
        try
        {
            executor.awaitTermination(60L, TimeUnit.SECONDS);
        }
        catch (final InterruptedException exception)
        {
            Thread.currentThread().interrupt();
            Verbose.exception(exception);
        }
    }

    /**
     * Check for void tiles.
     * 
     * @param level The media reference.
     */
    static void check(Media level)
    {
        final ImageBuffer buffer = Graphics.getImageBuffer(level);
        for (int y = 0; y < buffer.getHeight(); y += 16)
        {
            for (int x = 0; x < buffer.getWidth(); x += 16)
            {
                if (isColor(buffer, x, y))
                {
                    setColor(buffer, x, y);
                }
            }
        }
        Graphics.saveImage(buffer, Medias.create("stage10_hard.png"));
    }

    private static boolean isColor(ImageBuffer buffer, int x, int y)
    {
        for (int y1 = 0; y1 < 16; y1++)
        {
            for (int x1 = 0; x1 < 16; x1++)
            {
                if (buffer.getRgb(x + x1, y + y1) != ColorRgba.BLACK.getRgba())
                {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean setColor(ImageBuffer buffer, int x, int y)
    {
        for (int y1 = 0; y1 < 16; y1++)
        {
            for (int x1 = 0; x1 < 16; x1++)
            {
                buffer.setRgb(x + x1, y + y1, COLOR2);
            }
        }
        return true;
    }

    /**
     * Generate tiles raster from sheet.
     * 
     * @param type The landscape type.
     */
    static void generateTileRaster(BackgroundType type)
    {
        final String world = type.getWorld().getFolder();
        final Media raster = Medias.create(Folder.RASTER, world, type.getTheme(), Constant.RASTER_FILE_TILE);
        final String folderTile = Constant.RASTER_FILE_TILE.replace(PNG, BLANK) + "_0";

        if (raster.exists() && !Medias.create(raster.getParentPath(), folderTile).exists())
        {
            final Media sheet = Medias.create(Folder.LEVEL, world, FILE_SHEETS);
            int i = 0;

            for (final ImageBuffer b : Graphics.getRasterBuffer(Graphics.getImageBuffer(sheet),
                                                                Graphics.getImageBuffer(raster)))
            {
                Graphics.saveImage(b, Medias.create(raster.getParentPath(), folderTile, i + PNG));
                i++;
            }
        }
    }

    /**
     * Generate water raster from first tile raster.
     * 
     * @param type The landscape type.
     */
    static void generateTileWaterRaster(BackgroundType type)
    {
        final String world = type.getWorld().getFolder();
        final String folderWater = Constant.RASTER_FILE_WATER.replace(PNG, BLANK);

        if (!Medias.create(Folder.RASTER, world, folderWater).exists())
        {
            int i = 0;
            final String theme = type.getTheme();
            final String folderTile = Constant.RASTER_FILE_TILE.replace(PNG, BLANK) + "_0";
            Media sheet = Medias.create(Folder.RASTER, world, theme, folderTile, FILE_SHEETS);
            if (Medias.create(Folder.RASTER, world, theme, Constant.RASTER_FILE_TILE).exists())
            {
                if (!sheet.exists())
                {
                    generateTileRaster(type);
                }
            }
            else
            {
                sheet = Medias.create(Folder.LEVEL, world, FILE_SHEETS);
            }

            final Media raster = Medias.create(Folder.RASTER, world, theme, Constant.RASTER_FILE_WATER);
            if (!Medias.create(raster.getParentPath(), folderWater).exists() && raster.exists())
            {
                final ImageBuffer base;
                if (type == BackgroundType.LAVA)
                {
                    final Media sheetT = Medias.create(Folder.LEVEL, world, FILE_SHEETS);
                    final Media rasterT = Medias.create(Folder.RASTER,
                                                        world,
                                                        type.getTheme(),
                                                        Constant.RASTER_FILE_TILE);
                    base = Graphics.getRasterBuffer(Graphics.getImageBuffer(sheetT),
                                                    Graphics.getImageBuffer(rasterT))[0];
                }
                else
                {
                    base = Graphics.getImageBuffer(sheet);
                }

                for (final ImageBuffer b : Graphics.getRasterBufferSmooth(base,
                                                                          Graphics.getImageBuffer(raster),
                                                                          TILE_HEIGHT))
                {
                    Graphics.saveImage(b, Medias.create(raster.getParentPath(), folderWater, i + PNG));
                    i++;
                }
            }
        }
    }

    /**
     * Generate tiles raster from sheet.
     * 
     * @param type The landscape type.
     */
    static void generateTileRasterInside(BackgroundType type)
    {
        final String world = type.getWorld().getFolder();
        final Media raster = Medias.create(Folder.RASTER, world, type.getTheme(), FILE_RASTER_INSIDE);
        final String folderTile = Constant.RASTER_FILE_TILE.replace(PNG, BLANK) + "_0";

        if (!Medias.create(raster.getParentPath(), folderTile).exists())
        {
            final Media sheet = Medias.create(Folder.LEVEL, world, FILE_SHEETS);
            int i = 0;

            for (final ImageBuffer b : Graphics.getRasterBufferInside(Graphics.getImageBuffer(sheet),
                                                                      Graphics.getImageBuffer(raster),
                                                                      TILE_HEIGHT))
            {
                Graphics.saveImage(b, Medias.create(raster.getParentPath(), folderTile, i + PNG));
                i++;
            }
            Graphics.saveImage(Graphics.getImageBuffer(sheet),
                               Medias.create(raster.getParentPath(), folderTile, i + PNG));
        }
    }

    /**
     * Generate tiles raster from sheet.
     * 
     * @param type The landscape type.
     * @param object The object name.
     */
    static void generateObjectRaster(BackgroundType type, Media object)
    {
        if (new SetupSurfaceRastered(object).isExtern())
        {
            final String world = type.getWorld().getFolder();
            final Media raster = Medias.create(Folder.RASTER, world, type.getTheme(), Constant.RASTER_FILE_TILE);
            final String folder = Constant.RASTER_FILE_TILE.replace(PNG, BLANK)
                                  + com.b3dgs.lionengine.Constant.UNDERSCORE
                                  + object.getName().replace(XML, BLANK);

            if (raster.exists() && !Medias.create(raster.getParentPath(), folder).exists())
            {
                final Media objectImage = new SetupSurfaceRastered(object).getSurfaceFile();
                int i = 0;

                for (final ImageBuffer b : Graphics.getRasterBuffer(Graphics.getImageBuffer(objectImage),
                                                                    Graphics.getImageBuffer(raster)))
                {
                    Graphics.saveImage(b, Medias.create(raster.getParentPath(), folder, i + PNG));
                    i++;
                }
                Graphics.saveImage(Graphics.getImageBuffer(objectImage),
                                   Medias.create(raster.getParentPath(), folder, i + PNG));
            }
        }
    }

    /**
     * Generate tiles raster from sheet.
     * 
     * @param type The landscape type.
     * @param object The object name.
     */
    static void generateObjectRasterInside(BackgroundType type, Media object)
    {
        if (new SetupSurfaceRastered(object).isExtern())
        {
            final String world = type.getWorld().getFolder();
            final Media raster = Medias.create(Folder.RASTER, world, type.getTheme(), FILE_RASTER_INSIDE);
            final String folder = Constant.RASTER_FILE_TILE.replace(PNG, BLANK)
                                  + com.b3dgs.lionengine.Constant.UNDERSCORE
                                  + object.getName().replace(XML, BLANK);

            if (!Medias.create(raster.getParentPath(), folder).exists())
            {
                final Media objectImage = new SetupSurfaceRastered(object).getSurfaceFile();
                final int tileHeight = ImageInfo.get(objectImage).getHeight()
                                       / FramesConfig.imports(new Configurer(object)).getVertical();
                int i = 0;
                for (final ImageBuffer b : Graphics.getRasterBufferInside(Graphics.getImageBuffer(objectImage),
                                                                          Graphics.getImageBuffer(raster),
                                                                          tileHeight))
                {
                    Graphics.saveImage(b, Medias.create(raster.getParentPath(), folder, i + PNG));
                    i++;
                }
                Graphics.saveImage(Graphics.getImageBuffer(objectImage),
                                   Medias.create(raster.getParentPath(), folder, i + PNG));
            }
        }
    }

    /**
     * Generate water raster from first tile raster.
     * 
     * @param type The landscape type.
     * @param object The object media.
     */
    static void generateObjectWaterRaster(BackgroundType type, Media object)
    {
        final String world = type.getWorld().getFolder();
        final Media raster = Medias.create(Folder.RASTER, world, type.getTheme(), Constant.RASTER_FILE_WATER);
        final String folder = Constant.RASTER_FILE_WATER.replace(PNG, BLANK)
                              + com.b3dgs.lionengine.Constant.UNDERSCORE
                              + object.getName().replace(XML, BLANK);

        if (!Medias.create(raster.getParentPath(), folder).exists())
        {
            final String folderTile = Constant.RASTER_FILE_TILE.replace(PNG, BLANK)
                                      + com.b3dgs.lionengine.Constant.UNDERSCORE
                                      + object.getName().replace(XML, BLANK);
            Media objectImage = Medias.create(raster.getParentPath(), folderTile, FILE_SHEETS);
            if (!objectImage.exists())
            {
                objectImage = new Setup(object).getSurfaceFile();
            }

            final int tileHeight = ImageInfo.get(objectImage).getHeight()
                                   / FramesConfig.imports(new Configurer(object)).getVertical();
            int i = 0;

            for (final ImageBuffer b : Graphics.getRasterBufferSmooth(Graphics.getImageBuffer(objectImage),
                                                                      Graphics.getImageBuffer(raster),
                                                                      tileHeight))
            {
                Graphics.saveImage(b, Medias.create(raster.getParentPath(), folder, i + PNG));
                i++;
            }
        }
    }

    /**
     * Generate water raster from first tile raster.
     * 
     * @param type The landscape type.
     */
    static void generateHeroWaterRaster(BackgroundType type)
    {
        final Media raster = Medias.create(Folder.RASTER,
                                           Folder.HERO,
                                           "valdyn",
                                           BackgroundType.LAVA.equals(type) ? Constant.RASTER_FILE_LAVA
                                                                            : Constant.RASTER_FILE_WATER);

        final String folder = raster.getName().replace(PNG, BLANK)
                              + com.b3dgs.lionengine.Constant.UNDERSCORE
                              + "Valdyn";

        if (!Medias.create(raster.getParentPath(), folder).exists())
        {
            final Media object = Medias.create(raster.getParentPath(), "Valdyn.xml");
            final Media objectImage = Medias.create(raster.getParentPath(), "Valdyn.png");

            final int tileHeight = ImageInfo.get(objectImage).getHeight()
                                   / FramesConfig.imports(new Configurer(object)).getVertical();
            int i = 0;

            for (final ImageBuffer b : Graphics.getRasterBufferSmooth(Graphics.getImageBuffer(objectImage),
                                                                      Graphics.getImageBuffer(raster),
                                                                      tileHeight))
            {
                Graphics.saveImage(b, Medias.create(raster.getParentPath(), folder, i + PNG));
                i++;
            }
        }
    }

    /**
     * Generate moon raster.
     * 
     * @param type The landscape type.
     */
    static void generateMoonRaster(BackgroundType type)
    {
        final String world = type.getWorld().getFolder();
        final String theme = type.getTheme();
        final Media moon = Medias.create(Folder.BACKGROUND, world, theme, "moon.png");
        final Media palette = Medias.create(Folder.BACKGROUND, world, theme, "palette.png");
        final Media raster = Medias.create(Folder.BACKGROUND, world, theme, "raster.png");
        final ImageBuffer[] rasters = Graphics.getRasterBufferOffset(moon, palette, raster, 1);

        Graphics.generateTileset(rasters, Medias.create("moon_raster.png"));
    }

    /**
     * Private constructor.
     */
    private Tools()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
