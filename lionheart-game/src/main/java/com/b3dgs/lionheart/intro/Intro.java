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
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
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

    /** Device controller reference. */
    final DeviceController device;
    /** Alpha speed. */
    int alphaSpeed = 4;

    private final Time time = new Time(getRate());
    private final Part1 part1;
    private final Part2 part2 = new Part2(time);
    private final Audio audio = AudioFactory.loadAudio(Music.INTRO);
    private final AppInfo info;

    private int alphaBack;
    private boolean skip;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Intro(Context context)
    {
        super(context, Util.getResolution(context, MIN_HEIGHT, MAX_WIDTH, MARGIN_WIDTH));

        final Resolution output = context.getConfig().getOutput();
        final double factor = getHeight() / (double) output.getHeight();
        final double wide = Math.floor(output.getWidth() * factor) / Constant.RESOLUTION.getWidth();

        part1 = new Part1(time, wide);

        final Services services = new Services();
        services.add(context);
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_CUSTOM)));
        device.setVisible(false);
        info = new AppInfo(this::getFps, services);

        audio.setVolume(Settings.getInstance().getVolumeMusic());

        setSystemCursorVisible(false);
    }

    @Override
    public void load()
    {
        part1.load();
        part2.load();

        load(Part3.class, time, audio);

        audio.play();
    }

    @Override
    protected void onLoaded(double extrp, Graphic g)
    {
        part1.render(g, getWidth(), getHeight());
    }

    @Override
    public void update(double extrp)
    {
        time.update(extrp);
        device.update(extrp);

        if (time.isBefore(47200))
        {
            part1.update(extrp);
        }
        else if (time.isBetween(47200, 88000))
        {
            part2.update(extrp);
        }
        else if (time.isAfter(94000))
        {
            end();
        }

        alphaBack += alphaSpeed;
        alphaBack = UtilMath.clamp(alphaBack, 0, 255);

        if (!skip)
        {
            skip = device.isFiredOnce(DeviceMapping.CTRL_RIGHT);
        }
        if (alphaSpeed > 0 && (time.isAfter(201000) || skip))
        {
            alphaSpeed = -alphaSpeed * 2;
        }
        if (alphaBack == 0 && alphaSpeed < 0)
        {
            if (skip)
            {
                audio.stop();
                end(Menu.class);
            }
            else
            {
                end();
            }
        }
        if (device.isFiredOnce(DeviceMapping.FORCE_EXIT))
        {
            end(null);
        }

        info.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        if (time.isBefore(47200))
        {
            part1.render(g, getWidth(), getHeight());
        }
        else if (time.isBetween(47200, 88000))
        {
            part2.render(g, getWidth(), getHeight());
        }

        if (alphaBack < 255)
        {
            g.setColor(Constant.ALPHAS_BLACK[255 - alphaBack]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
            g.setColor(ColorRgba.BLACK);
        }

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
