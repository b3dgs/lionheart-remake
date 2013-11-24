/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionheart.intro;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.SonicArranger;

/**
 * Introduction implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Intro
        extends Sequence
{
    /** Original introduction display. */
    public static final Resolution INTRO_DISPLAY = new Resolution(320, 240, 60);
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
    /** Music seek. */
    private int seek;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Intro(Loader loader)
    {
        super(loader, Intro.INTRO_DISPLAY);
        part1 = new Part1();
        part2 = new Part2();
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        part1.load();
        part2.load();
        SonicArranger.play(Music.INTRO);
    }

    @Override
    protected void update(double extrp)
    {
        seek = SonicArranger.seek();
        if (seek < 47200)
        {
            part1.update(seek, extrp);
        }
        else
        {
            part2.update(seek, extrp);
        }
    }

    @Override
    protected void render(Graphic g)
    {
        if (seek < 47200)
        {
            part1.render(width, height, seek, g);
        }
        else
        {
            part2.render(width, height, seek, g);
        }
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        SonicArranger.stop();
        if (!hasNextSequence)
        {
            Sfx.terminateAll();
            SonicArranger.terminate();
        }
    }
}
