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
package com.b3dgs.lionheart.map;

import java.io.IOException;
import java.util.List;

import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.platform.map.MapTilePlatformRastered;
import com.b3dgs.lionheart.AppLionheart;
import com.b3dgs.lionheart.landscape.LandscapeType;

/**
 * Map implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Map
        extends MapTilePlatformRastered<TileCollision, Tile>
{
    /** Tile width. */
    public static final int TILE_WIDTH = 16;
    /** Tile height. */
    public static final int TILE_HEIGHT = 16;

    /**
     * Convert int to byte value (working on if int is less than 256).
     * 
     * @param value The int value.
     * @return The byte value.
     */
    private static byte toByte(int value)
    {
        return (byte) (value + Byte.MIN_VALUE);
    }

    /**
     * Convert byte to int value.
     * 
     * @param value The byte value.
     * @return The int value.
     */
    private static int fromByte(byte value)
    {
        return value - Byte.MIN_VALUE;
    }

    /**
     * Constructor.
     */
    public Map()
    {
        super(Map.TILE_WIDTH, Map.TILE_HEIGHT);
    }

    /**
     * Set the map landscape (must be called before loading).
     * 
     * @param landscape The landscape type.
     */
    public void setLandscape(LandscapeType landscape)
    {
        if (AppLionheart.RASTER_ENABLED)
        {
            setRaster(UtilityMedia.get(AppLionheart.RASTERS_DIR, landscape.getRaster()), false);
        }
    }

    /*
     * MapTilePlatformRastered
     */

    @Override
    public Tile createTile(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        final TileCollisionGroup group = collision != null ? collision.getGroup() : TileCollisionGroup.NONE;
        switch (group)
        {
            case FLAT:
            case PILLAR:
                return new TileGround(width, height, pattern, number, collision);
            case LIANA_HORIZONTAL:
            case LIANA_STEEP:
            case LIANA_LEANING:
                return new TileLiana(width, height, pattern, number, collision);
            case SLOPE:
                return new TileSlope(width, height, pattern, number, collision);
            case SLIDE:
                return new TileSlide(width, height, pattern, number, collision);
            default:
                return new Tile(width, height, pattern, number, collision);
        }
    }

    @Override
    public TileCollision getCollisionFrom(String collision)
    {
        try
        {
            return TileCollision.valueOf(collision);
        }
        catch (final IllegalArgumentException
                     | NullPointerException exception)
        {
            return TileCollision.NONE;
        }
    }

    @Override
    protected void saveTile(FileWriting file, Tile tile) throws IOException
    {
        file.writeByte(Map.toByte(tile.getPattern().intValue()));
        file.writeByte(Map.toByte(tile.getNumber()));
        file.writeByte(Map.toByte(tile.getX() / tileWidth % MapTileGame.BLOC_SIZE));
        file.writeByte(Map.toByte(tile.getY() / tileHeight));
    }

    @Override
    public Tile loadTile(List<XmlNode> nodes, FileReading file, int i) throws IOException
    {
        final int pattern = Map.fromByte(file.readByte());
        final int number = Map.fromByte(file.readByte());
        final int x = Map.fromByte(file.readByte());
        final int y = Map.fromByte(file.readByte());
        final TileCollision collision = getCollisionFrom(getCollision(nodes, pattern, number));
        final Tile tile = createTile(tileWidth, tileHeight, Integer.valueOf(pattern), number, collision);

        tile.setX((x + i * MapTileGame.BLOC_SIZE) * tileWidth);
        tile.setY(y * tileHeight);

        return tile;
    }
}
