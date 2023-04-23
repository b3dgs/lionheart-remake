/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.SplitType;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.game.Action;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.SequenceGame;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.Zooming;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;

/**
 * Game scene implementation.
 */
public class Scene extends SequenceGame<World>
{
    private final AppInfo info;
    private final Media music;
    private final InitConfig init;
    private final Boolean exit;
    private final AtomicReference<Action> closer = new AtomicReference<>(() ->
    {
        // Void
    });

    /**
     * Create the scene.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @param config The game config.
     * @throws LionEngineException If invalid argument.
     */
    public Scene(Context context, GameConfig config)
    {
        this(context, config, Boolean.FALSE);
    }

    /**
     * Create the scene.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @param config The game config.
     * @param exit <code>true</code> if exit after loaded, <code>false</code> else.
     * @throws LionEngineException If invalid argument.
     */
    public Scene(Context context, GameConfig config, Boolean exit)
    {
        super(context,
              Util.getResolution(Constant.RESOLUTION_GAME, context),
              Util.getLoop(context.getConfig().getOutput()),
              s -> new World(s, config));

        init = config.getInit();
        this.exit = exit;

        music = StageConfig.imports(new Configurer(init.getStage())).getMusic();

        services.add(config);
        services.add(init.getDifficulty());

        if (config.getType().is(GameType.STORY))
        {
            Util.saveProgress(config);
        }

        final DeviceController device;
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_DEFAULT)));
        device.setVisible(false);

        info = new AppInfo(this::getFps, services);

        world.prepare();
        config.getNetwork().ifPresent(n ->
        {
            try
            {
                world.prepareNetwork(n, closer, init);
            }
            catch (final IOException exception)
            {
                Verbose.exception(exception);
            }
        });

        checkSplit(config);
        Util.setFilter(this, context, Util.getResolution(Constant.RESOLUTION_GAME, context), 2);
    }

    private void checkSplit(GameConfig config)
    {
        final SplitType split = config.getSplit();
        switch (split)
        {
            case NONE:
                break;
            case TWO_HORIZONTAL:
                setSplit(this::render, g -> world.render(g, 0), true);
                break;
            case TWO_VERTICAL:
                setSplit(this::render, g -> world.render(g, 0), false);
                break;
            case FOUR:
                setSplit(this::render, g -> world.render(g, 0), g -> world.render(g, 1), g -> world.render(g, 2));
                break;
            default:
                throw new LionEngineException(split);
        }
    }

    @Override
    public void load()
    {
        try
        {
            world.load(init);
        }
        catch (final Exception exception) // CHECKSTYLE IGNORE LINE: IllegalCatch|TrailingComment
        {
            Sfx.cacheStop();
            world.stopMusic();
            throw exception;
        }
    }

    @Override
    protected void onLoaded(double extrp, Graphic g)
    {
        final double zoom = Settings.getInstance().getZoom();
        if (Double.compare(zoom, 1.0) != 0)
        {
            services.get(Zooming.class).setZoom(UtilMath.clamp(zoom, Constant.ZOOM_MIN, Constant.ZOOM_MAX));
        }

        services.get(DeviceController.class).setVisible(true);

        Sfx.cacheEnd();
        world.playMusic(music);
        setSystemCursorVisible(false);
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        info.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        super.render(g);

        info.render(g);

        if (exit.booleanValue())
        {
            end();
        }
    }

    @Override
    protected void onResolutionChanged(int width, int height)
    {
        super.onResolutionChanged(width, height);

        info.onResolutionChanged(width, height);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        super.onTerminated(hasNextSequence);

        world.stopMusic();
        closer.get().execute();
    }
}
