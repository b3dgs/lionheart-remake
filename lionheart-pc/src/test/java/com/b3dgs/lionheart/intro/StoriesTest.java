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
package com.b3dgs.lionheart.intro;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.AudioVoidFormat;
import com.b3dgs.lionengine.awt.graphic.EngineAwt;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.Loader;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.AppLionheart;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Util;

/**
 * Test intro stories {@link Stories}.
 */
@Tag("manual")
final class StoriesTest
{
    /**
     * Init engine.
     */
    @BeforeEach
    void prepare()
    {
        EngineAwt.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, AppLionheart.class);
        AudioFactory.addFormat(new AudioVoidFormat(Arrays.asList("wav", "sc68")));
    }

    /**
     * Test each pictures and text with all languages.
     * 
     * @param lang The language value.
     */
    @Tag("manual")
    @ParameterizedTest
    @ValueSource(strings =
    {
        "en", "fr", "de", "es", "pt", "pt"
    })
    void testStoriesManual(String lang)
    {
        Settings.getInstance().setLang(lang);
        Loader.start(Config.windowed(Constant.RESOLUTION_OUTPUT), Mock.class, Boolean.FALSE).await();
    }

    /**
     * Test each pictures and text with all languages.
     * 
     * @param lang The language value.
     */
    @ParameterizedTest
    @ValueSource(strings =
    {
        "en", "fr", "de", "es", "pt", "pt"
    })
    void testStories(String lang)
    {
        Settings.getInstance().setLang(lang);
        Loader.start(Config.windowed(Constant.RESOLUTION_OUTPUT), Mock.class, Boolean.TRUE).await();
    }

    /**
     * Mock sequence.
     */
    private static class Mock extends Sequence
    {
        private final Stories stories = new Stories(getWidth(), getHeight());
        private final DeviceController device;
        private final Boolean auto;
        private int story;
        private boolean fired;

        /**
         * Create mock.
         * 
         * @param context The context reference.
         * @param auto <code>true</code> for auto skip, <code>false</code> for manual.
         */
        public Mock(Context context, Boolean auto)
        {
            super(context, Util.getResolution(Constant.RESOLUTION, context));

            this.auto = auto;

            final Services services = new Services();
            services.add(context);
            services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
            device = DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_DEFAULT));
        }

        @Override
        public void load()
        {
            stories.load();
        }

        @Override
        public void update(double extrp)
        {
            if (auto.booleanValue() || device.isFired())
            {
                if (auto.booleanValue() || !fired)
                {
                    fired = true;
                    story++;
                    if (story > 3)
                    {
                        end();
                    }
                    else
                    {
                        stories.setStory(story);
                    }
                }
            }
            else
            {
                fired = false;
            }
        }

        @Override
        public void render(Graphic g)
        {
            g.clear(0, 0, getWidth(), getHeight());

            stories.render(g);
        }

        @Override
        public void onTerminated(boolean hasNextSequence)
        {
            Engine.terminate();
        }
    }
}
