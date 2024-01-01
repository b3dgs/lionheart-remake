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
package com.b3dgs.lionheart.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.TickAction;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.AudioVoidFormat;
import com.b3dgs.lionengine.awt.graphic.EngineAwt;
import com.b3dgs.lionengine.graphic.engine.Loader;
import com.b3dgs.lionengine.graphic.engine.TaskFuture;
import com.b3dgs.lionheart.AppLionheart;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.DevicePushMock;
import com.b3dgs.lionheart.Settings;

/**
 * Test correct {@link Menu} loading.
 */
final class MenuTest
{
    /**
     * Init engine.
     */
    @BeforeEach
    void prepare()
    {
        if (!Engine.isStarted())
        {
            EngineAwt.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, AppLionheart.class);
            AudioFactory.addFormat(new AudioVoidFormat(Arrays.asList("wav", "sc68")));
        }
    }

    /**
     * Test menu.
     * 
     * @param lang The language value.
     */
    @ParameterizedTest
    @ValueSource(strings =
    {
        "en", "fr", "de", "es", "pt", "pt"
    })
    void testMenu(String lang)
    {
        Medias.create(Constant.FILE_PROGRESS).getFile().delete();
        Settings.getInstance().setLang(lang);

        final DevicePushMock push = new DevicePushMock();
        final List<TickAction> actions = new ArrayList<>();

        DevicePushMock.addSimu(actions, push, DeviceMapping.ATTACK);
        DevicePushMock.addSimu(actions, push, DeviceMapping.ATTACK);
        DevicePushMock.addSimu(actions, push, DeviceMapping.DOWN);
        DevicePushMock.addSimu(actions, push, DeviceMapping.DOWN);
        DevicePushMock.addSimu(actions, push, DeviceMapping.DOWN);
        DevicePushMock.addSimu(actions, push, DeviceMapping.ATTACK);
        DevicePushMock.addSimu(actions, push, DeviceMapping.DOWN);
        DevicePushMock.addSimu(actions, push, DeviceMapping.DOWN);
        DevicePushMock.addSimu(actions, push, DeviceMapping.DOWN);
        DevicePushMock.addSimu(actions, push, DeviceMapping.ATTACK);
        DevicePushMock.addSimu(actions, push, DeviceMapping.DOWN);
        DevicePushMock.addSimu(actions, push, DeviceMapping.DOWN);
        DevicePushMock.addSimu(actions, push, DeviceMapping.DOWN);
        DevicePushMock.addSimu(actions, push, DeviceMapping.DOWN);
        DevicePushMock.addSimu(actions, push, DeviceMapping.DOWN);
        DevicePushMock.addSimu(actions, push, DeviceMapping.ATTACK);

        final TaskFuture task = Loader.start(Config.windowed(Constant.RESOLUTION_OUTPUT),
                                             TestMenu.class,
                                             push,
                                             actions);
        task.await();
    }
}
