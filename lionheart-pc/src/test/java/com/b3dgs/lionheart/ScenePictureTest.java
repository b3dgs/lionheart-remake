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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.AudioVoidFormat;
import com.b3dgs.lionengine.awt.graphic.EngineAwt;
import com.b3dgs.lionengine.graphic.engine.Loader;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Test {@link ScenePicture}.
 */
final class ScenePictureTest
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
     * @param pic The picture file.
     * @param text The text value.
     */
    @Tag("manual")
    @ParameterizedTest
    @CsvSource(value =
    {
        "en:pic1:swamp", "en:pic2:ancienttown", "en:pic3:lava", "en:pic4:airship", "en:pic5:dragonfly", "en:pic6:tower",
        "fr:pic1:swamp", "fr:pic2:ancienttown", "fr:pic3:lava", "fr:pic4:airship", "fr:pic5:dragonfly", "fr:pic6:tower",
        "es:pic1:swamp", "es:pic2:ancienttown", "es:pic3:lava", "es:pic4:airship", "es:pic5:dragonfly", "es:pic6:tower",
        "de:pic1:swamp", "de:pic2:ancienttown", "de:pic3:lava", "de:pic4:airship", "de:pic5:dragonfly", "de:pic6:tower",
    }, delimiter = ':')
    void testPicturesManual(String lang, String pic, String text)
    {
        Settings.getInstance().setLang(lang);
        Loader.start(Config.windowed(Constant.RESOLUTION_OUTPUT),
                     ScenePicture.class,
                     Constant.INIT_DEBUG,
                     Medias.create(Folder.SPRITE, pic + ".png"),
                     text + ".txt")
              .await();
    }

    /**
     * Test each pictures and text with all languages.
     * 
     * @param lang The language value.
     * @param pic The picture file.
     * @param text The text value.
     */
    @ParameterizedTest
    @CsvSource(value =
    {
        "en:pic1:swamp", "en:pic2:ancienttown", "en:pic3:lava", "en:pic4:airship", "en:pic5:dragonfly", "en:pic6:tower",
        "fr:pic1:swamp", "fr:pic2:ancienttown", "fr:pic3:lava", "fr:pic4:airship", "fr:pic5:dragonfly", "fr:pic6:tower",
        "es:pic1:swamp", "es:pic2:ancienttown", "es:pic3:lava", "es:pic4:airship", "es:pic5:dragonfly", "es:pic6:tower",
        "de:pic1:swamp", "de:pic2:ancienttown", "de:pic3:lava", "de:pic4:airship", "de:pic5:dragonfly", "de:pic6:tower",
    }, delimiter = ':')
    void testPictures(String lang, String pic, String text)
    {
        Settings.getInstance().setLang(lang);
        Loader.start(Config.windowed(Constant.RESOLUTION_OUTPUT),
                     ScenePicture.class,
                     Constant.INIT_DEBUG,
                     Medias.create(Folder.SPRITE, pic + ".png"),
                     text + ".txt",
                     Boolean.TRUE)
              .await();
    }
}
