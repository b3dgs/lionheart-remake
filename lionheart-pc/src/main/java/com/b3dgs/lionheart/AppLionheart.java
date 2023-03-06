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

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.sc68.Sc68Format;
import com.b3dgs.lionengine.audio.wav.WavFormat;
import com.b3dgs.lionengine.awt.graphic.EngineAwt;
import com.b3dgs.lionengine.awt.graphic.ImageLoadStrategy;
import com.b3dgs.lionengine.awt.graphic.ToolsAwt;
import com.b3dgs.lionengine.graphic.engine.Loader;
import com.b3dgs.lionengine.graphic.engine.Sequencable;

/**
 * Program starts here.
 */
public final class AppLionheart
{
    private static final String ARG_SETTINGS = "-settings";
    private static final String ARG_INPUT = "-input";
    private static final String ARG_GAMETYPE = "-gametype";
    private static final String ARG_PLAYERS = "-players";

    /**
     * Main function.
     * <p>
     * Arguments:
     * </p>
     * <ul>
     * <li>-settings: [string]</li>
     * <li>-input [string]</li>
     * <li>-players [int]</li>
     * <li>-gametype {@link GameType} [string])</li>
     * </ul>
     * 
     * @param args The arguments.
     */
    public static void main(String[] args) // CHECKSTYLE IGNORE LINE: TrailingComment|UncommentedMain
    {
        EngineAwt.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, AppLionheart.class);

        final List<String> params = Arrays.asList(args);
        loadSettings(params);
        loadInput(params);
        final GameConfig config = loadConfig(params);

        run(config, new Gamepad());
    }

    /**
     * Run game.
     * 
     * @param config The config reference.
     * @param gamepad The gamepad handler.
     */
    static void run(GameConfig config, Gamepad gamepad)
    {
        run(gamepad, Loading.class, config);
    }

    /**
     * Run game.
     * 
     * @param gamepad The gamepad handler.
     * @param sequencable The the next sequence to start (must not be <code>null</code>).
     * @param arguments The sequence arguments list if needed by its constructor.
     */
    static void run(Gamepad gamepad, Class<? extends Sequencable> sequencable, Object... arguments)
    {
        AudioFactory.addFormat(new WavFormat());
        AudioFactory.addFormat(Sc68Format.getFailsafe());

        Util.init(Tools::generateWorldRaster);

        final Settings settings = Settings.getInstance();
        final ImageLoadStrategy[] strategies = ImageLoadStrategy.values();
        ToolsAwt.setLoadStrategy(strategies[UtilMath.clamp(settings.getFlagStrategy(), 0, strategies.length)]);
        AudioFactory.setVolume(settings.getVolumeMaster());

        Loader.start(configure(settings,
                               Arrays.asList(gamepad),
                               Medias.create("icon-16.png"),
                               Medias.create("icon-32.png"),
                               Medias.create("icon-48.png"),
                               Medias.create("icon-64.png"),
                               Medias.create("icon-128.png"),
                               Medias.create("icon-256.png")),
                     sequencable,
                     arguments);
    }

    private static <T> T getParam(List<String> params, String param, T def, Function<String, T> converter)
    {
        final int index = params.indexOf(param);
        if (index > 0 && index + 1 < params.size())
        {
            try
            {
                return converter.apply(params.get(index + 1));
            }
            catch (final Exception exception)
            {
                Verbose.exception(exception);
            }
        }
        return def;
    }

    private static void loadSettings(List<String> params)
    {
        final File file = getParam(params, ARG_SETTINGS, Settings.getFile(), File::new);
        Settings.load(file);
    }

    private static void loadInput(List<String> params)
    {
        final Media media = getParam(params, ARG_INPUT, Medias.create(Constant.INPUT_FILE_DEFAULT), Medias::create);
        if (!media.exists())
        {
            Tools.prepareInputCustom();
        }
    }

    private static GameConfig loadConfig(List<String> params)
    {
        final GameType gameType = getParam(params, ARG_GAMETYPE, GameType.STORY, GameType::valueOf);
        final int players = getParam(params, ARG_PLAYERS, Integer.valueOf(1), Integer::parseInt).intValue();
        final Map<Integer, Integer> controls = new HashMap<>();
        for (int i = 0; i < players; i++)
        {
            controls.put(Integer.valueOf(i), Integer.valueOf(i));
        }
        return new GameConfig(gameType, players, Optional.empty(), Optional.empty(), controls, null);
    }

    /**
     * Create a 32 bits color depth and fullscreen configuration using output resolution.
     * 
     * @param settings The settings reference.
     * @param devices The devices reference.
     * @param icons The icons (must not be <code>null</code>).
     * @return The created fullscreen configuration.
     * @throws LionEngineException If invalid argument.
     */
    private static Config configure(Settings settings, List<InputDevice> devices, Media... icons)
    {
        if (settings.getResolutionWindowed())
        {
            return Config.windowed(settings.getResolution(), devices, icons);
        }
        return Config.fullscreen(settings.getResolution(), devices, icons);
    }

    /**
     * Private constructor.
     */
    private AppLionheart()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
