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
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.SplitType;

/**
 * Test {@link GameConfig}.
 */
final class GameConfigTest
{
    /**
     * Test constructor.
     */
    @Test
    void testConstructor()
    {
        final GameConfig config = new GameConfig();

        assertEquals(GameType.STORY, config.getType());
        assertEquals(1, config.getPlayers());
        assertFalse(config.getNetwork().isPresent());
        assertFalse(config.getStages().isPresent());
        assertTrue(config.isOneButton());
        assertNull(config.getInit());
        assertEquals(SplitType.NONE, config.getSplit());
    }

    /**
     * Test with other.
     */
    @Test
    void testWithOther2()
    {
        final GameConfig config = new GameConfig().with(GameType.SPEEDRUN, 2, false, Collections.emptyMap());

        assertEquals(GameType.SPEEDRUN, config.getType());
        assertEquals(2, config.getPlayers());
        assertFalse(config.getNetwork().isPresent());
        assertFalse(config.getStages().isPresent());
        assertFalse(config.isOneButton());
        assertNull(config.getInit());
        assertEquals(SplitType.TWO_HORIZONTAL, config.getSplit());
    }

    /**
     * Test with other.
     */
    @Test
    void testWithOther4()
    {
        final GameConfig config = new GameConfig().with(GameType.TRAINING, 4, false, Collections.emptyMap());

        assertEquals(GameType.TRAINING, config.getType());
        assertEquals(4, config.getPlayers());
        assertFalse(config.getNetwork().isPresent());
        assertFalse(config.getStages().isPresent());
        assertFalse(config.isOneButton());
        assertNull(config.getInit());
        assertEquals(SplitType.FOUR, config.getSplit());
    }

    /**
     * Test with stage.
     */
    @Test
    void testWithStage()
    {
        final GameConfig config = new GameConfig().with("test");

        assertEquals(GameType.STORY, config.getType());
        assertEquals(1, config.getPlayers());
        assertFalse(config.getNetwork().isPresent());
        assertEquals("test", config.getStages().get());
        assertTrue(config.isOneButton());
        assertNull(config.getInit());
        assertEquals(SplitType.NONE, config.getSplit());
    }

    /**
     * Test with init.
     */
    @Test
    void testWithInit()
    {
        final InitConfig init = new InitConfig(Medias.create("test"), 0, 0, Difficulty.NORMAL);
        final GameConfig config = new GameConfig().with(init);

        assertFalse(config.getStages().isPresent());
        assertEquals(GameType.STORY, config.getType());
        assertEquals(1, config.getPlayers());
        assertFalse(config.getNetwork().isPresent());
        assertFalse(config.getStages().isPresent());
        assertTrue(config.isOneButton());
        assertEquals(init, config.getInit());
        assertEquals(SplitType.NONE, config.getSplit());
    }

    /**
     * Test with two buttons.
     */
    @Test
    void testWithTwoButtons()
    {
        final GameConfig config = new GameConfig().with(false);

        assertFalse(config.getStages().isPresent());
        assertEquals(GameType.STORY, config.getType());
        assertEquals(1, config.getPlayers());
        assertFalse(config.getNetwork().isPresent());
        assertFalse(config.getStages().isPresent());
        assertFalse(config.isOneButton());
        assertNull(config.getInit());
        assertEquals(SplitType.NONE, config.getSplit());
    }
}
