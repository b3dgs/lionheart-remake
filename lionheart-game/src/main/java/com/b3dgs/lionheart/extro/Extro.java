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
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Music;

/**
 * Extro implementation.
 */
public final class Extro extends Sequence
{
    /** Part 1. */
    private final Part1 part1;
    /** Part 2. */
    private final Part2 part2;
    /** Part 3. */
    private final Part3 part3;
    /** Part 4. */
    private final Part4 part4;
    /** Part 5. */
    private final Part5 part5;
    /** Music. */
    private final Audio audio;
    /** Music seek. */
    private long seek;
    /** Alternative. */
    private final boolean alternative;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Extro(Context context)
    {
        super(context, Constant.MENU_RESOLUTION);

        alternative = true;

        part1 = new Part1(context);
        part2 = new Part2(context);
        part3 = new Part3();
        part4 = new Part4(alternative);
        part5 = new Part5();

        audio = AudioFactory.loadAudio(Music.EXTRO.get());
        audio.setVolume(Constant.AUDIO_VOLUME);
    }

    @Override
    public void load()
    {
        part1.load();
        part2.load();
        part3.load();
        part4.load();
        part5.load();

        audio.play();
    }

    @Override
    public void update(double extrp)
    {
        seek = audio.getTicks() + 135000;

        if (seek < 23200)
        {
            part1.update(seek, extrp);
        }
        else if (seek < 35000)
        {
            part2.update(seek, extrp);
        }
        else if (seek < 86000)
        {
            part3.update(seek, extrp);
        }
        else if (seek > 86000 && seek < 135000)
        {
            part4.update(seek, extrp);
        }
        else if (seek > 135000 && alternative)
        {
            part5.update(seek, extrp);
        }
    }

    @Override
    public void render(Graphic g)
    {
        if (seek < 23200)
        {
            part1.render(getWidth(), getHeight(), seek, g);
        }
        else if (seek < 35000)
        {
            part2.render(getWidth(), getHeight(), seek, g);
        }
        else if (seek < 85000)
        {
            part3.render(getWidth(), getHeight(), seek, g);
        }
        else if (seek < 135000)
        {
            part4.render(getWidth(), getHeight(), seek, g);
        }
        else if (seek > 135000 && alternative)
        {
            part5.render(getWidth(), getHeight(), seek, g);
        }
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        audio.stop();
    }
}
