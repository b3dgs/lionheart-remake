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

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.AudioVoidFormat;
import com.b3dgs.lionengine.awt.graphic.EngineAwt;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionengine.graphic.engine.Loader;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Test all fronts.
 */
@Tag("manual")
final class FontsTest
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
     * Test all fonts.
     */
    @Tag("manual")
    @Test
    void testFontsManual()
    {
        Loader.start(Config.windowed(Constant.RESOLUTION_OUTPUT), Mock.class).await();
    }

    /**
     * Mock sequence.
     */
    private static class Mock extends Sequence
    {
        private static SpriteFont load(String media, String data, int lw, int lh)
        {
            return Drawable.loadSpriteFont(Medias.create(Folder.SPRITE, media),
                                           Medias.create(Folder.SPRITE, data),
                                           lw,
                                           lh);
        }

        private final SpriteFont[] fonts =
        {
            load("fontintro.png", "fontintro.xml", 24, 28), load("fontmenu.png", "fontmenu.xml", 26, 30),
            load("fontmenu_dark.png", "fontmenu.xml", 26, 30), load("fontmenu_blue.png", "fontmenu.xml", 26, 30),
            load("font.png", "fontdata.xml", 12, 12), load("fonttip.png", "fontdata.xml", 12, 12),
            load("font11.png", "font11.xml", 11, 15), load("font14.png", "font14.xml", 14, 18),
            load("font24.png", "font24.xml", 24, 28), load("font26.png", "font26.xml", 26, 30)
        };
        private final DeviceController device;
        private boolean fired;
        private int font;

        /**
         * Create mock.
         * 
         * @param context The context reference.
         */
        public Mock(Context context)
        {
            super(context, Util.getResolution(Constant.RESOLUTION_OUTPUT, context));

            final Services services = new Services();
            services.add(context);
            services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
            device = DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_DEFAULT));
        }

        @Override
        public void load()
        {
            for (final SpriteFont font : fonts)
            {
                font.load();
                font.prepare();
                font.setText("""
                        ABCDEFGHIJKLMNOPQRSTUVWXYZ%\
                        ÀÁÃÂÄÇÈÉÊËÌÍÎÏÑÒÓÕÔÖÙÚÛÜŸ%\
                        abcdefghijklmnopqrstuvwxyz%\
                        àáãâäçèéêëñìíîïòóõôöùúûüÿ%\
                        0123456789%\
                        /\\,*:?!¿¡.-&()'""");
                font.setLocation(10, 10);
            }
        }

        @Override
        public void update(double extrp)
        {
            if (device.isFired())
            {
                if (!fired)
                {
                    fired = true;
                    font++;
                    if (font >= fonts.length)
                    {
                        end();
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
            if (font < fonts.length)
            {
                fonts[font].render(g);
            }
        }

        @Override
        public void onTerminated(boolean hasNextSequence)
        {
            for (final SpriteFont font : fonts)
            {
                font.dispose();
            }
            Engine.terminate();
        }
    }
}
