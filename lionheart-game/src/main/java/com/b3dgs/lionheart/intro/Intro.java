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
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.Util;

/**
 * Introduction implementation.
 */
public final class Intro extends Sequence
{
    private static final int MIN_HEIGHT = 180;
    private static final int MAX_WIDTH = 400;
    private static final int MARGIN_WIDTH = 80;

    /** Part 1. */
    private final Part1 part1;
    /** Part 2. */
    private final Part2 part2 = new Part2();
    /** Music. */
    private final Audio audio = AudioFactory.loadAudio(Music.INTRO.get());
    /** Input device reference. */
    private final DeviceController device;

    /** Music seek. */
    private long seek;
    /** Back alpha. */
    private int alphaBack;
    /** Alpha speed. */
    private int alphaSpeed = 4;

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

        part1 = new Part1(wide);

        final Services services = new Services();
        services.add(context);
        device = DeviceControllerConfig.create(services, Medias.create("input.xml"));

        audio.setVolume(Constant.AUDIO_VOLUME);
    }

    @Override
    public void load()
    {
        part1.load();
        part2.load();

        load(Part3.class, audio);

        audio.play();
    }

    @Override
    public void update(double extrp)
    {
        seek = audio.getTicks();
        device.update(extrp);

        if (seek < 47200)
        {
            part1.update(seek, extrp);
        }
        else if (seek >= 47200 && seek < 88000)
        {
            part2.update(seek, extrp);
        }
        else if (seek >= 94000)
        {
            end();
        }

        alphaBack += alphaSpeed;
        alphaBack = UtilMath.clamp(alphaBack, 0, 255);
        if (alphaSpeed > 0 && (seek > 201000 || device.isFiredOnce(DeviceMapping.CTRL_RIGHT)))
        {
            alphaSpeed = -alphaSpeed * 2;
        }
        if (alphaBack == 0 && alphaSpeed < 0)
        {
            end();
        }
    }

    @Override
    public void render(Graphic g)
    {
        if (seek < 47200)
        {
            part1.render(getWidth(), getHeight(), seek, g);
        }
        else if (seek >= 47200 && seek < 88000)
        {
            part2.render(getWidth(), getHeight(), seek, g);
        }

        if (alphaBack < 255)
        {
            g.setColor(Constant.ALPHAS_BLACK[255 - alphaBack]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
        }
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        if (!hasNextSequence)
        {
            audio.stop();
        }
    }
}
