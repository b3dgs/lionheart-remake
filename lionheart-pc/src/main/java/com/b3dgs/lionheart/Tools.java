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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.awt.graphic.EngineAwt;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.landscape.LandscapeType;

/**
 * Program starts here.
 */
public final class Tools
{
    private static final String PNG = ".png";
    private static final String FOLDER_RASTER = "raster";
    private static final String FILE_SHEETS = "0.png";
    private static final String FILE_RASTER_TILES = "tiles.png";
    private static final String FILE_RASTER_WATER = "water.png";
    private static final int TILE_HEIGHT = 16;

    /**
     * Main function.
     * 
     * @param args The arguments (none).
     */
    public static void main(String[] args) // CHECKSTYLE IGNORE LINE: TrailingComment|UncommentedMain
    {
        EngineAwt.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, Tools.class);
        // generateTileRaster(LandscapeType.SWAMP_DAWN);
        // generateTileWaterRaster(LandscapeType.SWAMP_DAWN);
        generateObjectWaterRaster(LandscapeType.SWAMP_DAY, "Floater", 1, 1);
        // generateMoonRaster(LandscapeType.SWAMP_DAWN);
    }

    /**
     * Generate tiles raster from sheet.
     * 
     * @param type The landscape type.
     */
    private static void generateTileRaster(LandscapeType type)
    {
        int i = 0;
        final String world = type.getWorld().getFolder();
        final Media sheet = Medias.create(Folder.LEVELS, world, FILE_SHEETS);
        final Media raster = Medias.create(Folder.LEVELS, world, FOLDER_RASTER, type.getTheme(), FILE_RASTER_TILES);

        for (final ImageBuffer b : Graphics.getRasterBuffer(Graphics.getImageBuffer(sheet),
                                                            Graphics.getImageBuffer(raster)))
        {
            Graphics.saveImage(b, Medias.create(i + PNG));
            i++;
        }
    }

    /**
     * Generate water raster from first tile raster.
     * 
     * @param type The landscape type.
     * @param file The object file.
     * @param fh The horizontal frames.
     * @param fv The vertical frames.
     */
    private static void generateObjectWaterRaster(LandscapeType type, String file, int fh, int fv)
    {
        int i = 0;
        final String world = type.getWorld().getFolder();
        final Media sheet = Medias.create(Folder.LEVELS,
                                          world,
                                          FOLDER_RASTER,
                                          type.getTheme(),
                                          "tiles_" + file,
                                          FILE_SHEETS);
        final Media raster = Medias.create(Folder.LEVELS, world, FOLDER_RASTER, type.getTheme(), FILE_RASTER_WATER);

        for (final ImageBuffer b : Graphics.getRasterBufferSmooth(Graphics.getImageBuffer(sheet),
                                                                  Graphics.getImageBuffer(raster),
                                                                  fh,
                                                                  fv))
        {
            Graphics.saveImage(b, Medias.create(i + PNG));
            i++;
        }
    }

    /**
     * Generate water raster from first tile raster.
     * 
     * @param type The landscape type.
     */
    private static void generateTileWaterRaster(LandscapeType type)
    {
        int i = 0;
        final String world = type.getWorld().getFolder();
        final String theme = type.getTheme();
        final Media sheet = Medias.create(Folder.LEVELS, world, FOLDER_RASTER + "_" + theme, FILE_SHEETS);
        final Media raster = Medias.create(Folder.LEVELS, world, FOLDER_RASTER, theme, FILE_RASTER_WATER);

        for (final ImageBuffer b : Graphics.getRasterBufferSmooth(Graphics.getImageBuffer(sheet),
                                                                  Graphics.getImageBuffer(raster),
                                                                  TILE_HEIGHT))
        {
            Graphics.saveImage(b, Medias.create(i + PNG));
            i++;
        }
    }

    /**
     * Generate moon raster.
     * 
     * @param type The landscape type.
     */
    private static void generateMoonRaster(LandscapeType type)
    {
        final String world = type.getWorld().getFolder();
        final String theme = type.getTheme();
        final Media moon = Medias.create(Folder.BACKGROUNDS, world, theme, "moon.png");
        final Media palette = Medias.create(Folder.BACKGROUNDS, world, theme, "palette.png");
        final Media raster = Medias.create(Folder.BACKGROUNDS, world, theme, "raster.png");
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
