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

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.geom.Coord;

/**
 * Test {@link InitConfig}.
 */
final class InitConfigTest
{
    /**
     * Test first constructor.
     */
    @Test
    void testConstructor1()
    {
        final InitConfig init = new InitConfig(Medias.create("stage"), 1, 2, Difficulty.NORMAL);

        assertEquals(Medias.create("stage"), init.getStage());
        assertEquals(1, init.getHealthMax());
        assertEquals(0, init.getTalisment());
        assertEquals(2, init.getLife());
        assertEquals(0, init.getSword());
        assertEquals(Constant.CREDITS, init.getCredits());
        assertFalse(init.isAmulet());
        assertEquals(Difficulty.NORMAL, init.getDifficulty());
        assertFalse(init.isCheats());
        assertFalse(init.getSpawn().isPresent());
    }

    /**
     * Test second constructor.
     */
    @Test
    void testConstructor2()
    {
        final InitConfig init = new InitConfig(Medias.create("stage"),
                                               1,
                                               2,
                                               3,
                                               4,
                                               true,
                                               5,
                                               Difficulty.BEGINNER,
                                               true,
                                               Optional.of(new Coord(1.0, 2.0)));

        assertEquals(Medias.create("stage"), init.getStage());
        assertEquals(1, init.getHealthMax());
        assertEquals(2, init.getTalisment());
        assertEquals(3, init.getLife());
        assertEquals(4, init.getSword());
        assertEquals(5, init.getCredits());
        assertTrue(init.isAmulet());
        assertEquals(Difficulty.BEGINNER, init.getDifficulty());
        assertTrue(init.isCheats());
        assertEquals(new Coord(1.0, 2.0), init.getSpawn().get());
    }
}
