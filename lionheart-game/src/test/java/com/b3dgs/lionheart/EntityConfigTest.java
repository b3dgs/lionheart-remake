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
 * Test {@link EntityConfig}.
 */
final class EntityConfigTest
{
    /**
     * Test import.
     */
    @Test
    void testImport()
    {
        Medias.setLoadFromJar(EntityConfigTest.class);

        final Xml root = new Xml(EntityConfig.NODE_ENTITY);
        root.writeString(EntityConfig.ATT_FILE, "entity/Bee.xml");
        root.writeDouble(EntityConfig.ATT_TX, 1.0);
        root.writeDouble(EntityConfig.ATT_TY, 1.0);

        final MapTile map = new MapTileGame();
        map.create(16, 16, 4, 4);

        final EntityConfig config = EntityConfig.imports(root);
        assertEquals(root, config.getRoot());
        assertEquals("entity/Bee.xml", config.getMedia().getPath());
        assertEquals(16.0, config.getSpawnX(map));
        assertEquals(0.0, config.getSpawnY(map));
    }
}
