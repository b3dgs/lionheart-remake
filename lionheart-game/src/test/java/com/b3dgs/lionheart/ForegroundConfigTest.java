/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionheart.landscape.ForegroundConfig;
import com.b3dgs.lionheart.landscape.ForegroundType;

/**
 * Test {@link ForegroundConfig}.
 */
final class ForegroundConfigTest
{
    /**
     * Test import with all data.
     */
    @Test
    void testImportFull()
    {
        Medias.setLoadFromJar(ForegroundConfigTest.class);

        final Xml root = new Xml("root");
        final Xml node = root.createChild(ForegroundConfig.NODE_FOREGROUND);
        node.writeEnum(ForegroundConfig.ATT_FOREGROUND_TYPE, ForegroundType.WATER);
        node.writeInteger(ForegroundConfig.ATT_WATER_DEPTH, 1);
        node.writeDouble(ForegroundConfig.ATT_WATER_DEPTH_SPEED, 2.0);
        node.writeInteger(ForegroundConfig.ATT_WATER_OFFSET, 3);
        node.writeDouble(ForegroundConfig.ATT_WATER_SPEED, 4.0);
        node.writeBoolean(ForegroundConfig.ATT_WATER_EFFECT, true);
        node.writeInteger(ForegroundConfig.ATT_WATER_RAISE, 5);
        node.writeInteger(ForegroundConfig.ATT_WIDTH_MAX, 6);

        final ForegroundConfig config = ForegroundConfig.imports(root);
        assertEquals(ForegroundType.WATER, config.getType());
        assertEquals(1, config.getWaterDepth().getAsInt());
        assertEquals(2.0, config.getWaterDepthSpeed().getAsDouble());
        assertEquals(3, config.getWaterOffset().getAsInt());
        assertEquals(4.0, config.getWaterSpeed().getAsDouble());
        assertTrue(config.getWaterEffect());
        assertEquals(5, config.getWaterRaise());
        assertEquals(6, config.getWidthMax().getAsInt());
    }

    /**
     * Test import with minimum data.
     */
    @Test
    void testImportMinimum()
    {
        Medias.setLoadFromJar(ForegroundConfigTest.class);

        final Xml root = new Xml("root");
        final Xml node = root.createChild(ForegroundConfig.NODE_FOREGROUND);
        node.writeEnum(ForegroundConfig.ATT_FOREGROUND_TYPE, ForegroundType.WATER);

        final ForegroundConfig config = ForegroundConfig.imports(root);
        assertEquals(ForegroundType.WATER, config.getType());
        assertFalse(config.getWaterDepth().isPresent());
        assertFalse(config.getWaterDepthSpeed().isPresent());
        assertFalse(config.getWaterOffset().isPresent());
        assertFalse(config.getWaterSpeed().isPresent());
        assertTrue(config.getWaterEffect());
        assertEquals(0, config.getWaterRaise());
        assertFalse(config.getWidthMax().isPresent());
    }
}
