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

import java.util.Map.Entry;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.helper.InputControllerConfig;
import com.b3dgs.lionengine.io.InputDeviceControl;
import com.b3dgs.lionengine.io.InputDeviceDirectional;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.menu.Menu;

/**
 * Introduction implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Intro extends Sequence
{
    /** Alpha black values. */
    static final ColorRgba[] ALPHAS_BLACK;
    /** Alpha white values. */
    static final ColorRgba[] ALPHAS_WHITE;

    /** Init. */
    static
    {
        ALPHAS_BLACK = new ColorRgba[256];
        for (int i = 0; i < 256; i++)
        {
            Intro.ALPHAS_BLACK[i] = new ColorRgba(0, 0, 0, i);
        }
        ALPHAS_WHITE = new ColorRgba[256];
        for (int i = 0; i < 256; i++)
        {
            Intro.ALPHAS_WHITE[i] = new ColorRgba(255, 255, 255, i);
        }
    }

    /** Part 1. */
    private final Part1 part1;
    /** Part 2. */
    private final Part2 part2;
    /** Part 3. */
    private final Part3 part3;
    /** Part 3. */
    private final Part4 part4;
    /** Music. */
    private final Audio audio;
    /** Input device reference. */
    private final InputDeviceControl input;
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
        super(context, Constant.NATIVE_RESOLUTION);

        part1 = new Part1();
        part2 = new Part2();
        part3 = new Part3();
        part4 = new Part4();

        audio = AudioFactory.loadAudio(Music.INTRO.get());
        audio.setVolume(Constant.AUDIO_VOLUME);

        final InputControllerConfig config = InputControllerConfig.imports(new Services(),
                                                                           new Configurer(Medias.create(Folder.PLAYERS,
                                                                                                        "default",
                                                                                                        "Valdyn.xml")));
        try
        {
            input = UtilReflection.createReduce(config.getControl(), getInputDevice(InputDeviceDirectional.class));

            for (final Entry<Integer, Integer> entry : config.getCodes().entrySet())
            {
                input.setFireButton(entry.getKey(), entry.getValue());
            }
        }
        catch (final NoSuchMethodException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /*
     * Sequence
     */

    @Override
    public void load()
    {
        part1.load();
        part2.load();
        part3.load();
        part4.load();

        load(Menu.class);

        audio.play();
    }

    @Override
    public void update(double extrp)
    {
        seek = audio.getTicks();

        if (seek < 47200)
        {
            part1.update(seek, extrp);
        }
        else if (seek >= 47200 && seek < 88000)
        {
            part2.update(seek, extrp);
        }
        else if (seek >= 94000 && seek < 110000)
        {
            part3.update(seek, extrp);
        }
        else if (seek >= 110000 && seek < 201000)
        {
            part4.update(seek, extrp);
        }

        alphaBack += alphaSpeed;
        alphaBack = UtilMath.clamp(alphaBack, 0, 255);
        if (alphaSpeed > 0 && (seek > 201000 || input.isFireButtonOnce(Constant.FIRE1)))
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
        else if (seek >= 94000 && seek < 110000)
        {
            part3.render(getWidth(), getHeight(), seek, g);
        }
        else if (seek >= 110000 && seek < 201000)
        {
            part4.render(getWidth(), getHeight(), seek, g);
        }

        if (alphaBack < 255)
        {
            g.setColor(Intro.ALPHAS_BLACK[255 - alphaBack]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
        }
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        audio.stop();
    }
}
