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

import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.AudioVoidFormat;

/**
 * Test {@link Sfx}.
 */
final class SfxTest
{
    /**
     * Init engine.
     */
    @BeforeAll
    static void prepare()
    {
        AudioFactory.addFormat(new AudioVoidFormat(Arrays.asList("wav")));
    }

    /**
     * Test the enum.
     * 
     * @throws Exception If error.
     */
    @Test
    void testEnum() throws Exception
    {
        UtilTests.testEnum(Sfx.class);
    }

    /**
     * Test cache.
     */
    @Test
    void testCache()
    {
        Sfx.cache(Sfx.MENU_SELECT);
    }

    /**
     * Test cache.
     */
    @Test
    void testCacheAll()
    {
        Sfx.cacheStart();
        Sfx.cacheEnd();
    }

    /**
     * Test play random.
     */
    @Test
    void testPlayRandom()
    {
        Sfx.playRandomExplode();
        UtilTests.pause(100);
        Sfx.playRandomExplode();
        UtilTests.pause(100);
        Sfx.playRandomExplode();
        Sfx.EFFECT_EXPLODE1.stop();
    }
}
