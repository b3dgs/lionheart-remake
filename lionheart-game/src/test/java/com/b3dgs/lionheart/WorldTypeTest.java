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

import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.UtilTests;

/**
 * Test {@link WorldType}.
 */
final class WorldTypeTest
{
    /**
     * Test the enum.
     * 
     * @throws Exception If error.
     */
    @Test
    void testEnum() throws Exception
    {
        UtilTests.testEnum(WorldType.class);
    }

    /**
     * Test get
     */
    @Test
    void testGet()
    {
        for (final WorldType type : WorldType.values())
        {
            assertEquals(type.getFolder(), type.name().toLowerCase(Locale.ENGLISH));
            assertEquals(type.toString(),
                         UtilConversion.toTitleCase(type.name().replace(Constant.UNDERSCORE, Constant.SPACE)));
        }
    }
}