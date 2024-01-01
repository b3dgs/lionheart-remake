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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.UtilTests;

/**
 * Test {@link Difficulty}.
 */
final class DifficultyTest
{
    /**
     * Test the enum.
     * 
     * @throws Exception If error.
     */
    @Test
    void testEnum() throws Exception
    {
        UtilTests.testEnum(Difficulty.class);
    }

    /**
     * Test from.
     */
    @Test
    void testFrom()
    {
        final Difficulty[] values = Difficulty.values();
        for (int i = 0; i < values.length; i++)
        {
            assertEquals(values[i], Difficulty.from(i));
        }
        assertEquals(values[values.length - 1], Difficulty.from(-1));
        assertEquals(values[values.length - 1], Difficulty.from(values.length));
    }

    /**
     * Test is.
     */
    @Test
    void testIs()
    {
        final Difficulty[] values = Difficulty.values();
        for (int i = 0; i < values.length; i++)
        {
            assertTrue(values[i].is(values[i]));
            assertTrue(Difficulty.is(i, values[i]));
        }

        assertFalse(Difficulty.BEGINNER.is(Difficulty.NORMAL));
        assertFalse(Difficulty.BEGINNER.is(Difficulty.HARD));
        assertFalse(Difficulty.BEGINNER.is(Difficulty.LIONHARD));

        assertFalse(Difficulty.NORMAL.is(Difficulty.BEGINNER));
        assertFalse(Difficulty.NORMAL.is(Difficulty.HARD));
        assertFalse(Difficulty.NORMAL.is(Difficulty.LIONHARD));

        assertFalse(Difficulty.HARD.is(Difficulty.BEGINNER));
        assertFalse(Difficulty.HARD.is(Difficulty.NORMAL));
        assertFalse(Difficulty.HARD.is(Difficulty.LIONHARD));

        assertFalse(Difficulty.LIONHARD.is(Difficulty.BEGINNER));
        assertFalse(Difficulty.LIONHARD.is(Difficulty.NORMAL));
        assertFalse(Difficulty.LIONHARD.is(Difficulty.HARD));
    }
}
