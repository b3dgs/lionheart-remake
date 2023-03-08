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
import java.util.Locale;
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
import com.b3dgs.lionheart.constant.Folder;

/**
 * Program starts here.
 */
public final class AppLionheart
{
    private static final String ARG_SETTINGS = "-settings";
    private static final String ARG_INPUT = "-input";
    private static final String ARG_GAME = "-game";
    private static final String ARG_STAGE = "-stage";
    private static final String ARG_DIFFICULTY = "-difficulty";
    private static final String ARG_PLAYERS = "-players";
    private static final String ARG_HEALTH = "-health";
    private static final String ARG_LIFE = "-life";

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
        try
        {
            System.setProperty("sun.java2d.uiScale", "1.0");
        }
        catch (final SecurityException exception)
        {
            Verbose.exception(exception);
        }
        EngineAwt.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, AppLionheart.class);

        final List<String> params = Arrays.asList(args);
        loadSettings(params);
        loadInput(params);

        if (params.isEmpty())
        {
            run(new GameConfig(), new Gamepad(), false);
        }
        else
        {
            final GameConfig config = loadConfig(params);

            run(config, new Gamepad(), true);
        }
    }

    /**
     * Run game.
     * 
     * @param config The config reference.
     * @param gamepad The gamepad handler.
     * @param direct <code>true</code> for direct start, <code>false</code> to show menu.
     */
    static void run(GameConfig config, Gamepad gamepad, boolean direct)
    {
        run(gamepad, Loading.class, config, Boolean.valueOf(direct));
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

    private static <T> T getParam(List<String> params,
                                  String param,
                                  T def,
                                  Function<String, T> converter,
                                  int lowerNoUpper)
    {
        final int index = params.indexOf(param);
        if (index > -1 && index + 1 < params.size())
        {
            try
            {
                final String arg;
                if (lowerNoUpper < 0)
                {
                    arg = params.get(index + 1).toLowerCase(Locale.ENGLISH);
                }
                else if (lowerNoUpper > 0)
                {
                    arg = params.get(index + 1).toUpperCase(Locale.ENGLISH);
                }
                else
                {
                    arg = params.get(index + 1);
                }
                return converter.apply(arg);
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
        final File file = getParam(params, ARG_SETTINGS, Settings.getFile(), File::new, 0);
        Settings.load(file);
    }

    private static void loadInput(List<String> params)
    {
        final Media media = getParam(params, ARG_INPUT, Medias.create(Constant.INPUT_FILE_DEFAULT), Medias::create, 0);
        if (!media.exists())
        {
            Tools.prepareInputCustom();
        }
    }

    private static GameConfig loadConfig(List<String> params)
    {
        final GameType game = getParam(params, ARG_GAME, GameType.STORY, GameType::valueOf, 1);
        final Optional<String> stage = getParam(params, ARG_STAGE, Optional.empty(), Optional::ofNullable, -1);
        final Difficulty difficulty = getParam(params, ARG_DIFFICULTY, Difficulty.NORMAL, Difficulty::valueOf, 1);
        final int players = getParam(params, ARG_PLAYERS, Integer.valueOf(1), Integer::parseInt, 0).intValue();

        final Map<Integer, Integer> controls = new HashMap<>();
        for (int i = 0; i < players; i++)
        {
            controls.put(Integer.valueOf(i), Integer.valueOf(i));
        }

        if (game == GameType.STORY)
        {
            return new GameConfig(game,
                                  players,
                                  Optional.empty(),
                                  stage,
                                  controls,
                                  new InitConfig(null, 0, 0, difficulty));
        }

        final int health = getParam(params, ARG_HEALTH, Integer.valueOf(4), Integer::parseInt, 0).intValue();
        final int life = getParam(params, ARG_LIFE, Integer.valueOf(2), Integer::parseInt, 0).intValue();

        final Media stageMedia;
        if (game == GameType.TRAINING)
        {
            final boolean hard = difficulty.ordinal() > Difficulty.NORMAL.ordinal();
            final String suffix = hard ? "_hard" : com.b3dgs.lionengine.Constant.EMPTY_STRING;
            Media media = Medias.create(Folder.STAGE, Folder.STORY, stage.get() + suffix + ".xml");
            if (!media.exists())
            {
                media = Medias.create(Folder.STAGE, Folder.STORY, stage.get() + ".xml");
            }
            stageMedia = media;
        }
        else
        {
            stageMedia = Medias.create(Folder.STAGE, game.name().toLowerCase(Locale.ENGLISH), stage.get() + ".xml");
        }

        return new GameConfig(game,
                              players,
                              Optional.empty(),
                              Optional.empty(),
                              controls,
                              new InitConfig(stageMedia, health, life, difficulty));
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
