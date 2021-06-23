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
public final class Scene extends SequenceGame<World>
{
    private final AppInfo info = new AppInfo(this::getFps, services);
    private final Media stage;
    private final Media music;
    private final InitConfig init;

    /**
     * Create the scene.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @param stage The stage run.
     * @param init The initial config.
     * @throws LionEngineException If invalid argument.
     */
    Scene(Context context, Media stage, InitConfig init)
    {
        super(context, Util.getResolution(Constant.RESOLUTION_GAME, context), World::new);

        this.stage = stage;
        this.init = init;
        music = StageConfig.imports(new Configurer(stage)).getMusic();
        services.add(init.getDifficulty());

        setSystemCursorVisible(false);
    }

    @Override
    public void load()
    {
        world.load(stage, init);
    }

    @Override
    protected void onLoaded(double extrp, Graphic g)
    {
        final double zoom = Settings.getInstance().getZoom();
        if (Double.compare(zoom, 1.0) != 0)
        {
            services.get(Zooming.class).setZoom(UtilMath.clamp(zoom, 0.8, 1.3));
        }

        services.get(DeviceController.class).setVisible(true);
        world.playMusic(music);
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
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        super.onTerminated(hasNextSequence);

        world.stopMusic();
    }
}
