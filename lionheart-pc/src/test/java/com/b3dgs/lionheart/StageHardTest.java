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

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.AudioVoidFormat;
import com.b3dgs.lionengine.awt.graphic.EngineAwt;
import com.b3dgs.lionengine.graphic.engine.Loader;
import com.b3dgs.lionengine.graphic.engine.TaskFuture;

/**
 * Test correct {@link StageHard} loading.
 */
final class StageHardTest
{
    /**
     * Init engine.
     */
    @BeforeAll
    static void prepareAll()
    {
        EngineAwt.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, AppLionheart.class);
        Settings.load();
    }

    /**
     * Init engine.
     */
    @BeforeEach
    void prepare()
    {
        if (!Engine.isStarted())
        {
            EngineAwt.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, AppLionheart.class);
        }
        AudioFactory.addFormat(new AudioVoidFormat(Arrays.asList("wav", "sc68")));
    }

    /**
     * Test all stages.
     * 
     * @param stage The stage value.
     */
    @ParameterizedTest
    @EnumSource(StageHard.class)
    void testStageHard(StageHard stage)
    {
        final TaskFuture task = Loader.start(Config.windowed(Constant.RESOLUTION_OUTPUT),
                                             Scene.class,
                                             new GameConfig().with(new InitConfig(stage.exists() ? stage
                                                                                                 : Stage.values()[stage.ordinal()],
                                                                                  1,
                                                                                  1,
                                                                                  1,
                                                                                  1,
                                                                                  true,
                                                                                  1,
                                                                                  Difficulty.HARD,
                                                                                  false,
                                                                                  Optional.empty())),
                                             Boolean.TRUE);
        task.await();
    }
}
