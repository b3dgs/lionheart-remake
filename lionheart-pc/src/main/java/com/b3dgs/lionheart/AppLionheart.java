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

import java.io.File;
import java.util.Arrays;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.UtilStream;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.sc68.Sc68Format;
import com.b3dgs.lionengine.audio.wav.WavFormat;
import com.b3dgs.lionengine.awt.graphic.EngineAwt;
import com.b3dgs.lionengine.awt.graphic.ImageLoadStrategy;
import com.b3dgs.lionengine.awt.graphic.ToolsAwt;
import com.b3dgs.lionengine.graphic.engine.Loader;

/**
 * Program starts here.
 */
public final class AppLionheart
{
    /**
     * Main function.
     * 
     * @param args The arguments (none).
     */
    public static void main(String[] args) // CHECKSTYLE IGNORE LINE: TrailingComment|UncommentedMain
    {
        EngineAwt.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, AppLionheart.class);

        Settings.load();
        final String input = Settings.getInstance().getInput();
        if (!Medias.create(input).exists())
        {
            final File file = UtilStream.getCopy(Medias.create(Constant.INPUT_FILE_DEFAULT));
            file.renameTo(new File(file.getPath().replace(file.getName(), input)));
        }

        run(new Gamepad());
    }

    /**
     * Run game.
     * 
     * @param gamepad The gamepad handler.
     */
    static void run(Gamepad gamepad)
    {
        AudioFactory.addFormat(new WavFormat());
        AudioFactory.addFormat(Sc68Format.getFailsafe());

        Util.init(Tools::generateWorldRaster);

        final Settings settings = Settings.getInstance();
        final ImageLoadStrategy[] strategies = ImageLoadStrategy.values();
        ToolsAwt.setLoadStrategy(strategies[UtilMath.clamp(settings.getFlag(), 0, strategies.length)]);
        AudioFactory.setVolume(settings.getVolumeMaster());

        Loader.start(Config.windowed(settings.getResolution(),
                                     Arrays.asList(gamepad),
                                     Medias.create("icon-16.png"),
                                     Medias.create("icon-32.png"),
                                     Medias.create("icon-48.png"),
                                     Medias.create("icon-64.png"),
                                     Medias.create("icon-128.png"),
                                     Medias.create("icon-256.png")),
                     Loading.class);
    }

    /**
     * Private constructor.
     */
    private AppLionheart()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
