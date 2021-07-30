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
package com.b3dgs.lionheart.extro;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.RenderableVoid;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Time;
import com.b3dgs.lionheart.Util;

/**
 * Extro part 3 implementation.
 */
public class Part3 extends Sequence
{
    private static final int FADE_SPEED = 3;

    private static final int TIME_STORY0_MS = 41400;
    private static final int TIME_STORY1_MS = 56500;
    private static final int TIME_STORY2_MS = 71700;
    private static final int TIME_FADE_OUT_MS = 85200;

    /** Device controller reference. */
    final DeviceController device;
    /** Alpha speed. */
    double alpha = 255;

    private final Stories stories = new Stories(getWidth(), getHeight());
    private final AppInfo info;
    private final Time time;
    private final Audio audio;

    private Updatable updater = this::updateStory0;

    private Renderable renderer = RenderableVoid.getInstance();
    private Renderable rendererFade = RenderableVoid.getInstance();

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param time The time reference.
     * @param audio The audio reference.
     * @param alternative The alternative end.
     */
    public Part3(Context context, Time time, Audio audio, Boolean alternative)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context));

        this.time = time;
        this.audio = audio;

        final Services services = new Services();
        services.add(context);
        device = services.add(DeviceControllerConfig.create(services,
                                                            Medias.create(Settings.getInstance().getInput())));
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        info = new AppInfo(this::getFps, services);

        load(Part4.class, time, audio, alternative);

        setSystemCursorVisible(false);
    }

    /**
     * Update fist story time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateStory0(double extrp)
    {
        if (time.isAfter(TIME_STORY0_MS))
        {
            stories.setStory(0);
            updater = this::updateStory1;
            renderer = stories;
        }
    }

    /**
     * Update second story time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateStory1(double extrp)
    {
        if (time.isAfter(TIME_STORY1_MS))
        {
            stories.setStory(1);
            updater = this::updateStory2;
        }
    }

    /**
     * Update third story time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateStory2(double extrp)
    {
        if (time.isAfter(TIME_STORY2_MS))
        {
            stories.setStory(2);
            updater = this::updateFadeOutInit;
        }
    }

    /**
     * Update fade out time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOutInit(double extrp)
    {
        if (time.isAfter(TIME_FADE_OUT_MS))
        {
            updater = this::updateFadeOut;
            rendererFade = this::renderFade;
        }
    }

    /**
     * Update fade out effect.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOut(double extrp)
    {
        alpha -= FADE_SPEED * extrp;

        if (alpha < 0)
        {
            alpha = 0;
            end();
        }
    }

    /**
     * Render fade effect.
     * 
     * @param g The graphic output.
     */
    private void renderFade(Graphic g)
    {
        g.setColor(Constant.ALPHAS_BLACK[255 - (int) Math.floor(alpha)]);
        g.drawRect(0, 0, getWidth(), getHeight(), true);
    }

    @Override
    public void load()
    {
        stories.load();
    }

    @Override
    public void update(double extrp)
    {
        time.update(extrp);
        updater.update(extrp);
        info.update(extrp);

        if (device.isFiredOnce(DeviceMapping.FORCE_EXIT))
        {
            end(null);
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        renderer.render(g);
        rendererFade.render(g);
        info.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        stories.dispose();

        if (!hasNextSequence)
        {
            audio.stop();
            Engine.terminate();
        }
    }
}
