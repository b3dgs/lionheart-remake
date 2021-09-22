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
package com.b3dgs.lionheart.intro;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
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
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Time;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.menu.Menu;

/**
 * Introduction implementation.
 */
public class Intro extends Sequence
{
    private static final int MIN_HEIGHT = 180;
    private static final int MAX_WIDTH = 400;
    private static final int MARGIN_WIDTH = 80;
    private static final int SPEED_FADE_IN = 4;
    private static final int SPEED_FADE_OUT = 10;

    private static final int TIME_PART2_MS = 47500;
    private static final int TIME_PART3_MS = 93800;

    /** Device controller reference. */
    final DeviceController device;
    /** Alpha speed. */
    int alphaSpeed = SPEED_FADE_IN;

    private final Time time = new Time(getRate());
    private final Part1 part1;
    private final Part2 part2 = new Part2(time, getWidth(), getHeight(), getRate());
    private final Audio audio = AudioFactory.loadAudio(Music.INTRO);
    private final AppInfo info;
    private final DeviceController deviceCursor;

    private Updatable updaterFade = this::updateFadeIn;
    private Updatable updaterPart = this::updatePart1;

    private Renderable rendererPart = this::renderPart1;
    private Renderable rendererFade = this::renderFade;

    private double alpha = 255.0;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Intro(Context context)
    {
        super(context, Util.getResolution(context, MIN_HEIGHT, MAX_WIDTH, MARGIN_WIDTH), Util.getLoop());

        part1 = new Part1(time, getWidth(), getHeight(), getWideFactor(context));

        final Services services = new Services();
        services.add(context);
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        device = services.add(DeviceControllerConfig.create(services,
                                                            Medias.create(Settings.getInstance().getInput())));
        device.setVisible(false);

        final Media mediaCursor = Medias.create(Constant.INPUT_FILE_CUSTOR);
        deviceCursor = DeviceControllerConfig.create(services, mediaCursor);

        info = new AppInfo(this::getFps, services);

        audio.setVolume(Settings.getInstance().getVolumeMusic());

        setSystemCursorVisible(false);
    }

    /**
     * Get wide depending on factor.
     * 
     * @param context The context reference.
     * @return The wide factor.
     */
    private double getWideFactor(Context context)
    {
        final Resolution output = context.getConfig().getOutput();
        final double factor = getHeight() / (double) output.getHeight();
        return Math.floor(output.getWidth() * factor) / Constant.RESOLUTION.getWidth();
    }

    /**
     * Update fade in routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeIn(double extrp)
    {
        alpha -= alphaSpeed * extrp;

        if (getAlpha() < 0)
        {
            alpha = 0.0;
            updaterFade = this::updateSkip;
            rendererFade = RenderableVoid.getInstance();
        }
    }

    /**
     * Update skip check.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateSkip(double extrp)
    {
        if (device.isFiredOnce(DeviceMapping.CTRL_RIGHT) || deviceCursor.isFiredOnce(DeviceMapping.LEFT))
        {
            alphaSpeed = SPEED_FADE_OUT;
            updaterFade = this::updateFadeOut;
            rendererFade = this::renderFade;
        }
    }

    /**
     * Update fade out on exit.
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
            end(Menu.class);
        }
    }

    /**
     * Update part 1 and next part detection.
     * 
     * @param extrp The extrapolation value.
     */
    private void updatePart1(double extrp)
    {
        part1.update(extrp);

        if (time.isAfter(TIME_PART2_MS))
        {
            updaterPart = this::updatePart2;
            rendererPart = this::renderPart2;
            rendererFade = RenderableVoid.getInstance();
        }
    }

    /**
     * Update part 2 and next part detection.
     * 
     * @param extrp The extrapolation value.
     */
    private void updatePart2(double extrp)
    {
        part2.update(extrp);

        if (time.isAfter(TIME_PART3_MS))
        {
            end();
        }
    }

    /**
     * Render part 1.
     * 
     * @param g The graphic output.
     */
    private void renderPart1(Graphic g)
    {
        part1.render(g);
    }

    /**
     * Render part 2.
     * 
     * @param g The graphic output.
     */
    private void renderPart2(Graphic g)
    {
        part2.render(g);
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
        part1.load();
        part2.load();

        load(Part3.class, time, audio);
    }

    @Override
    protected void onLoaded(double extrp, Graphic g)
    {
        super.onLoaded(extrp, g);

        audio.play();
        time.start();
    }

    @Override
    public void update(double extrp)
    {
        time.update(extrp);
        device.update(extrp);
        deviceCursor.update(extrp);
        updaterFade.update(extrp);
        updaterPart.update(extrp);
        info.update(extrp);

        if (device.isFiredOnce(DeviceMapping.FORCE_EXIT))
        {
            end(null);
        }
    }

    @Override
    public void render(Graphic g)
    {
        rendererPart.render(g);
        rendererFade.render(g);
        info.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        if (!hasNextSequence)
        {
            audio.stop();
            Engine.terminate();
        }
    }
}
