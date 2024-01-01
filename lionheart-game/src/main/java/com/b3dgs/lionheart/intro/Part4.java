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
package com.b3dgs.lionheart.intro;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
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
import com.b3dgs.lionheart.GameConfig;
import com.b3dgs.lionheart.Time;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.menu.Menu;

/**
 * Intro part 4 implementation.
 */
public class Part4 extends Sequence
{
    private static final int FADE_SPEED = 6;

    private static final int STORY0_INDEX = 0;
    private static final int STORY1_INDEX = 1;
    private static final int STORY2_INDEX = 2;
    private static final int STORY3_INDEX = 3;

    private static final int TIME_START_MS = 114_200;
    private static final int TIME_STORY1_MS = 130_000;
    private static final int TIME_STORY2_MS = 155_300;
    private static final int TIME_STORY3_MS = 180_600;
    private static final int TIME_END_MS = 200_200;

    /** Device controller reference. */
    final DeviceController device;
    /** Alpha speed. */
    int alphaSpeed = FADE_SPEED;

    private final Stories stories = new Stories(getWidth(), getHeight());
    private final GameConfig config;
    private final AppInfo info;
    private final Time time;
    private final Audio audio;
    private final DeviceController deviceCursor;

    private Updatable updater = this::updateInit;
    private Renderable rendererFade = this::renderFade;

    private double alpha = 255.0;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param config The config reference (must not be <code>null</code>).
     * @param time The time reference.
     * @param audio The audio reference.
     */
    public Part4(Context context, GameConfig config, Time time, Audio audio)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context), Util.getLoop(context.getConfig().getOutput()));

        this.config = config;
        this.time = time;
        this.audio = audio;

        final Services services = new Services();
        services.add(context);
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_DEFAULT)));

        final Media mediaCursor = Medias.create(Constant.INPUT_FILE_CURSOR);
        deviceCursor = DeviceControllerConfig.create(services, mediaCursor);

        info = new AppInfo(this::getFps, services);

        setSystemCursorVisible(false);
        Util.setFilter(this, context, Util.getResolution(Constant.RESOLUTION, context), 2);
    }

    /**
     * Update init until time start.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateInit(double extrp)
    {
        if (time.isAfter(TIME_START_MS))
        {
            stories.setStory(STORY0_INDEX);
            updater = this::updateFadeIn;
            rendererFade = this::renderFade;
        }
    }

    /**
     * Update fade in process.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeIn(double extrp)
    {
        alpha -= alphaSpeed * extrp;

        if (getAlpha() < 0)
        {
            alpha = 0.0;
            updater = this::updateStory1;
            rendererFade = RenderableVoid.getInstance();
        }
    }

    /**
     * Update first story delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateStory1(double extrp)
    {
        if (time.isAfter(TIME_STORY1_MS))
        {
            stories.setStory(STORY1_INDEX);
            updater = this::updateStory2;
        }
    }

    /**
     * Update second story delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateStory2(double extrp)
    {
        if (time.isAfter(TIME_STORY2_MS))
        {
            stories.setStory(STORY2_INDEX);
            updater = this::updateStory3;
        }
    }

    /**
     * Update third story delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateStory3(double extrp)
    {
        if (time.isAfter(TIME_STORY3_MS))
        {
            stories.setStory(STORY3_INDEX);
            updater = this::updateFadeOutStart;
        }
    }

    /**
     * Update start fading out delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOutStart(double extrp)
    {
        if (time.isAfter(TIME_END_MS))
        {
            updater = this::updateFadeOut;
            rendererFade = this::renderFade;
        }
    }

    /**
     * Update fade out process until end.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOut(double extrp)
    {
        alpha += alphaSpeed * extrp;

        if (getAlpha() > 255)
        {
            alpha = 255.0;
            audio.stop();
            end(Menu.class, config);
            updater = UpdatableVoid.getInstance();
        }
    }

    /**
     * Check skip routine.
     */
    private void checkSkip()
    {
        if (device.isFiredOnce(DeviceMapping.ATTACK) || deviceCursor.isFiredOnce(DeviceMapping.LEFT))
        {
            updater = this::updateFadeOut;
            rendererFade = this::renderFade;
        }
    }

    /**
     * Get alpha value.
     * 
     * @return The alpha value.
     */
    private int getAlpha()
    {
        return (int) Math.floor(alpha);
    }

    /**
     * Render fade.
     * 
     * @param g The graphic output.
     */
    private void renderFade(Graphic g)
    {
        final int a = getAlpha();
        if (a > 0)
        {
            g.setColor(Constant.ALPHAS_BLACK[a]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
            g.setColor(ColorRgba.BLACK);
        }
    }

    @Override
    public void load()
    {
        stories.load();
    }

    @Override
    public void update(double extrp)
    {
        device.update(extrp);
        deviceCursor.update(extrp);
        time.update(extrp);
        updater.update(extrp);
        info.update(extrp);

        checkSkip();

        if (device.isFiredOnce(DeviceMapping.FORCE_EXIT))
        {
            end(null);
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        stories.render(g);
        rendererFade.render(g);
        info.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        super.onTerminated(hasNextSequence);

        stories.dispose();

        if (!hasNextSequence)
        {
            audio.stop();
            Engine.terminate();
        }
    }
}
