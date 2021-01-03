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

import java.io.IOException;

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileSurface;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * Handle the map persistence by providing saving and loading functions.
 */
@FeatureInterface
public class MapTilePersisterOptimized extends MapTilePersisterModel
{
    @FeatureGet private MapTileSurface map;

    /**
     * Create feature.
     */
    public MapTilePersisterOptimized()
    {
        super();
    }

    @Override
    protected void saveTile(FileWriting file, Tile tile) throws IOException
    {
        file.writeChar((char) tile.getNumber());
        file.writeByte(UtilConversion.fromUnsignedByte((short) (tile.getInTileX() % BLOC_SIZE)));
        file.writeChar((char) tile.getInTileY());
    }

    @Override
    protected void loadTile(FileReading file, int i) throws IOException
    {
        final int number = file.readChar();
        final byte b = file.readByte();
        final int tx = UtilConversion.toUnsignedByte(b) + i * BLOC_SIZE;
        final int ty = file.readChar();
        map.setTile(tx, ty, number);
    }
}
