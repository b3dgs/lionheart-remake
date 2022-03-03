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

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.SequenceGame;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.Zooming;
import com.b3dgs.lionengine.io.DeviceController;

/**
 * Game scene implementation.
 */
public class Scene extends SequenceGame<World>
{
    private final AppInfo info = new AppInfo(this::getFps, services);
    private final Media music;
    private final InitConfig init;
    private final Boolean exit;

    /**
     * Create the scene.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @param init The initial config.
     * @throws LionEngineException If invalid argument.
     */
    public Scene(Context context, InitConfig init)
    {
        this(context, init, Boolean.FALSE);
    }

    /**
     * Create the scene.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @param init The initial config.
     * @param exit <code>true</code> if exit after loaded, <code>false</code> else.
     * @throws LionEngineException If invalid argument.
     */
    Scene(Context context, InitConfig init, Boolean exit)
    {
        super(context, Util.getResolution(Constant.RESOLUTION_GAME, context), Util.getLoop(), World::new);

        this.init = init;
        this.exit = exit;
        music = StageConfig.imports(new Configurer(init.getStage())).getMusic();
        services.add(init.getDifficulty());

        Util.setFilter(this);
        Util.saveProgress(init);
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
    }
}
