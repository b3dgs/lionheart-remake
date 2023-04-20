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

import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
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
import com.b3dgs.lionheart.constant.Extension;
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
     * Main.
     * <p>
     * Arguments:
     * </p>
     * <ul>
     * <li>-settings <code>[string]</code></li>
     * <li>-input <code>[string]</code></li>
     * <li>-game [<code>story, training, speedrun, battle, versus</code>]</li>
     * <li>-stage {story=[<code>original, beginner, veteran</code>],
     * training=[<code>original-X, beginner-X, veteran-X</code>],
     * speedrun=[1], battle=[1], versus=[1]}</li>
     * <li>-difficulty [<code>beginner, normal, hard, lionhard</code>]</li>
     * <li>-player {story=[1], training=[1], speedrun=[1, 2, 3, 4], battle=[1, 2, 3, 4], versus=[2, 3, 4]}</li>
     * </ul>
     * <p>
     * Examples:
     * </p>
     * <ul>
     * <li>story: -game story -stage beginner -difficulty beginner</li>
     * <li>training: -game training -stage veteran-6 -difficulty lionhard</li>
     * <li>speedrun: -game speedrun -stage 1 -players 2</li>
     * <li>battle: -game battle -stage 1 -players 3</li>
     * <li>versus: -game versus -stage 1 -players 4</li>
     * </ul>
     * 
     * @param args The arguments.
     */
    public static void main(String[] args) // CHECKSTYLE IGNORE LINE: TrailingComment|UncommentedMain
    {
        Tools.disableAutoScale();

        if (!Engine.isStarted())
        {
            EngineAwt.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, AppLionheart.class);
        }

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

            run(config, new Gamepad(), config.getInit() != null);
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
     * @param sequence The the next sequence to start (must not be <code>null</code>).
     * @param args The sequence arguments list if needed by its constructor.
     */
    static void run(Gamepad gamepad, Class<? extends Sequencable> sequence, Object... args)
    {
        Check.notNull(sequence);

        AudioFactory.addFormat(new WavFormat());
        AudioFactory.addFormat(Sc68Format.getFailsafe());

        Util.init(Tools::generateWorldRaster);

        final Settings settings = Settings.getInstance();
        final ImageLoadStrategy[] strategies = ImageLoadStrategy.values();
        ToolsAwt.setLoadStrategy(strategies[UtilMath.clamp(settings.getFlagStrategy(), 0, strategies.length)]);
        AudioFactory.setVolume(settings.getVolumeMaster());

        final List<InputDevice> devices = Arrays.asList(gamepad);
        final Media[] icons = Tools.getIcons(16, 32, 48, 64, 128, 256);
        Loader.start(configure(settings, devices, icons), sequence, args);
    }

    /**
     * Get converted parameter value.
     * 
     * @param <T> The parameter type.
     * @param params The parameters list.
     * @param param The parameter name.
     * @param def The defaut value.
     * @param conv The converter function.
     * @param lowerNoUpper -1 to lower case, 0 for default, 1 for upper case.
     * @return The converted value.
     */
    private static <T> T getParam(List<String> params, String param, T def, Function<String, T> conv, int lowerNoUpper)
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
                return conv.apply(arg);
            }
            catch (final Exception exception)
            {
                Verbose.exception(exception);
            }
        }
        return def;
    }

    /**
     * Load settings from parameters.
     * 
     * @param params The parameters.
     */
    private static void loadSettings(List<String> params)
    {
        final File file = getParam(params, ARG_SETTINGS, Settings.getFile(), File::new, 0);
        Settings.load(file);
    }

    /**
     * Load input from parameters.
     * 
     * @param params The parameters.
     */
    private static void loadInput(List<String> params)
    {
        final Media media = getParam(params, ARG_INPUT, Medias.create(Constant.INPUT_FILE_DEFAULT), Medias::create, 0);
        if (!media.exists())
        {
            Tools.prepareInputCustom();
        }
    }

    /**
     * Load game configuration from parameters.
     * 
     * 
     * @param params The parameters.
     * @return The game configuration.
     */
    private static GameConfig loadConfig(List<String> params)
    {
        final GameType game = getParam(params, ARG_GAME, GameType.STORY, GameType::valueOf, 1);
        final Optional<String> stage = getParam(params, ARG_STAGE, Optional.empty(), Optional::ofNullable, -1);
        final Difficulty difficulty = getParam(params, ARG_DIFFICULTY, Difficulty.NORMAL, Difficulty::valueOf, 1);
        final int players = UtilMath.clamp(getParam(params,
                                                    ARG_PLAYERS,
                                                    Integer.valueOf(1),
                                                    Integer::parseInt,
                                                    0).intValue(),
                                           game.is(GameType.VERSUS) ? 2 : 1,
                                           4);

        final Map<Integer, Integer> controls = new HashMap<>();
        for (int i = 0; i < players; i++)
        {
            controls.put(Integer.valueOf(i), Integer.valueOf(i));
        }

        if (game == GameType.STORY && stage.isPresent())
        {
            return new GameConfig(game, 1, Optional.empty(), stage, controls, new InitConfig(null, 0, 0, difficulty));
        }

        final int health = getParam(params, ARG_HEALTH, Integer.valueOf(4), Integer::parseInt, 0).intValue();
        final int life = getParam(params, ARG_LIFE, Integer.valueOf(2), Integer::parseInt, 0).intValue();

        final Media stageMedia;
        if (game == GameType.TRAINING && stage.isPresent())
        {
            final String[] s = stage.get().split("-");
            final String set = s[0].toLowerCase(Locale.ENGLISH);
            stageMedia = Util.getStage(set, difficulty, Integer.parseInt(s[1]));
        }
        else if (stage.isPresent())
        {
            stageMedia = Medias.create(Folder.STAGE,
                                       game.name().toLowerCase(Locale.ENGLISH),
                                       Constant.STAGE_PREFIX + stage.get() + Extension.STAGE);
        }
        else
        {
            return new GameConfig(game, players, Optional.empty(), Optional.empty(), Collections.emptyMap(), null);
        }

        if (stageMedia.exists())
        {
            return new GameConfig(game,
                                  players,
                                  Optional.empty(),
                                  Optional.empty(),
                                  controls,
                                  new InitConfig(stageMedia, health, life, difficulty));
        }
        return new GameConfig(game, players, Optional.empty(), Optional.empty(), Collections.emptyMap(), null);
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
        if (settings.isResolutionWindowed())
        {
            return Config.windowed(settings.getResolution(getDesktopResolution()), devices, icons);
        }
        return Config.fullscreen(settings.getResolution(getDesktopResolution()), devices, icons);
    }

    /**
     * Get desktop resolution.
     * 
     * @return The desktop resolution (<code>null</code> if error).
     */
    private static Resolution getDesktopResolution()
    {
        try
        {
            final DisplayMode desktop = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                           .getDefaultScreenDevice()
                                                           .getDisplayMode();
            return new Resolution(desktop.getWidth(), desktop.getHeight(), desktop.getRefreshRate());
        }
        catch (final Throwable exception)
        {
            Verbose.exception(exception);
            return null;
        }
    }

    /**
     * Private constructor.
     */
    private AppLionheart()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
