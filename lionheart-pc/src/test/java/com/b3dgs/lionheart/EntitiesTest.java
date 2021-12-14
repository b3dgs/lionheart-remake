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

import java.util.Arrays;
import java.util.Locale;
import java.util.OptionalInt;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.AudioVoidFormat;
import com.b3dgs.lionengine.awt.graphic.EngineAwt;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.graphic.engine.Loader;
import com.b3dgs.lionengine.graphic.engine.TaskFuture;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Test entities.
 */
final class EntitiesTest
{
    /**
     * Test world entities.
     * 
     * @param world The world to test.
     */
    @Tag("manual")
    @ParameterizedTest
    @EnumSource(WorldType.class)
    void testWorld(WorldType world)
    {
        EngineAwt.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, EntitiesTest.class);
        AudioFactory.addFormat(new AudioVoidFormat(Arrays.asList("wav", "sc68")));
        Settings.load();
        Settings.getInstance().setInput(Constant.INPUT_FILE_DEFAULT);

        final Media stage = Medias.create(Folder.STAGE,
                                          world.name().toLowerCase(Locale.ENGLISH) + Factory.FILE_DATA_DOT_EXTENSION);
        if (stage.exists())
        {
            final TaskFuture task = Loader.start(Config.windowed(Constant.RESOLUTION_OUTPUT),
                                                 TestScene.class,
                                                 stage,
                                                 Constant.INIT_DEBUG);
            task.await();
        }
        else
        {
            Engine.terminate();
        }
    }

    /**
     * Test world bosses.
     * 
     * @param world The world to test.
     */
    @Tag("manual")
    @ParameterizedTest
    @ValueSource(strings =
    {
        "boss_swamp.xml", "boss_lava.xml", "boss_dragonfly.xml", "boss_norka1.xml", "boss_norka2.xml", "boss_norka3.xml"
    })
    void testBossManual(String world)
    {
        EngineAwt.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, EntitiesTest.class);
        AudioFactory.addFormat(new AudioVoidFormat(Arrays.asList("wav", "sc68")));
        Settings.load();
        Settings.getInstance().setInput(Constant.INPUT_FILE_DEFAULT);

        final Media stage = Medias.create(Folder.STAGE, world);
        if (stage.exists())
        {
            final TaskFuture task = Loader.start(Config.windowed(Constant.RESOLUTION_OUTPUT),
                                                 TestScene.class,
                                                 stage,
                                                 Constant.INIT_DEBUG);
            task.await();
        }
        else
        {
            Engine.terminate();
        }
    }

    /**
     * Test world bosses.
     * 
     * @param world The world to test.
     */
    @ParameterizedTest
    @ValueSource(strings =
    {
        "boss_swamp.xml", "boss_lava.xml", "boss_dragonfly.xml", "boss_norka1.xml", "boss_norka2.xml", "boss_norka3.xml"
    })
    void testBoss(String world)
    {
        EngineAwt.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, EntitiesTest.class);
        AudioFactory.addFormat(new AudioVoidFormat(Arrays.asList("wav", "sc68")));
        Settings.load();
        Settings.getInstance().setInput(Constant.INPUT_FILE_DEFAULT);

        final Media stage = Medias.create(Folder.STAGE, world);
        if (stage.exists())
        {
            final TaskFuture task = Loader.start(Config.windowed(Constant.RESOLUTION_OUTPUT),
                                                 TestScene.class,
                                                 stage,
                                                 Constant.INIT_DEBUG,
                                                 OptionalInt.of(400));
            task.await();
        }
        else
        {
            Engine.terminate();
        }
    }
}
