/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.game.FeatureProvider;
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
    private MapTileSurface map;

    /**
     * Create feature.
     */
    public MapTilePersisterOptimized()
    {
        super();
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        map = provider.getFeature(MapTileSurface.class);
    }

    @Override
    protected void saveTile(FileWriting file, Tile tile) throws IOException
    {
        file.writeChar((char) tile.getNumber());
        file.writeByte(UtilConversion.fromUnsignedByte((short) (tile.getInTileX() % BLOC_SIZE)));
        file.writeByte(UtilConversion.fromUnsignedByte((short) (tile.getInTileY() % BLOC_SIZE)));
    }

    @Override
    protected void loadTile(FileReading file, int sx, int sy) throws IOException
    {
        final int number = file.readChar();
        final int tx = UtilConversion.toUnsignedByte(file.readByte()) + sx * BLOC_SIZE;
        final int ty = UtilConversion.toUnsignedByte(file.readByte()) + sy * BLOC_SIZE;

        map.setTile(tx, ty, number);
    }
}
