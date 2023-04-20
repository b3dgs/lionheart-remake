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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;

/**
 * Test {@link SpawnConfig}.
 */
final class SpawnConfigTest
{
    /**
     * Test import.
     */
    @Test
    void testImport()
    {
        Medias.setLoadFromJar(SpawnConfigTest.class);

        final Xml root = new Xml(SpawnConfig.NODE_SPAWN);
        root.writeInteger(SpawnConfig.ATT_DELAY, 1);

        final Xml entity = root.createChild(EntityConfig.NODE_ENTITY);
        entity.writeString(EntityConfig.ATT_FILE, "entity/Bee.xml");
        entity.writeDouble(EntityConfig.ATT_TX, 1.0);
        entity.writeDouble(EntityConfig.ATT_TY, 2.0);

        final MapTile map = new MapTileGame();
        map.create(16, 16, 4, 4);

        final SpawnConfig config = SpawnConfig.imports(root);
        assertEquals(1, config.getDelay());

        final EntityConfig expected = EntityConfig.imports(entity);
        final EntityConfig read = config.getEntities().iterator().next();

        assertEquals(expected.getMedia(), read.getMedia());
        assertEquals(expected.getSpawnX(map), read.getSpawnX(map));
        assertEquals(expected.getSpawnY(map), read.getSpawnY(map));
    }
}
