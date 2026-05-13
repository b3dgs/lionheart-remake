/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.narration.intro;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.FadeSide;
import com.b3dgs.lionheart.GameConfig;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.menu.MenuGame;
import com.b3dgs.lionheart.narration.Narration;

/**
 * Introduction implementation.
 */
public class Intro extends Narration
{
    private static final int MIN_HEIGHT = 144;
    private static final int MAX_WIDTH = 320;
    private static final int MARGIN_WIDTH = 0;
    private static final int FADE_OUT = 10;

    private final Part1 part1 = new Part1(time, getWidth(), getHeight(), getWideFactor(context));
    private final Part2 part2 = new Part2(getWidth(), getHeight(), getRate());
    private final Part3 part3 = new Part3(context);
    private final Part4 part4 = new Part4(context);

    private final Fade fadeOut = new Fade(FadeSide.OUT, FADE_OUT);
    private final GameConfig config;

    private boolean quit;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param config The config reference (must not be <code>null</code>).
     */
    public Intro(Context context, GameConfig config)
    {
        super(context,
              Util.getResolution(context, MIN_HEIGHT, MAX_WIDTH, MARGIN_WIDTH),
              AudioFactory.loadAudio(Music.INTRO));

        this.config = config;
    }

    /**
     * Update skip check.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateSkip(double extrp)
    {
        if (quit || time.isAfter(Part4.T_END))
        {
            fadeOut.update(extrp);
            if (getAlpha() == 255)
            {
                audio.stop();
                end(MenuGame.class, config);
            }
        }
        else if (device.isFiredOnce(DeviceMapping.ATTACK) || deviceCursor.isFiredOnce(DeviceMapping.LEFT))
        {
            quit = true;
            fadeOut.init();
        }
    }

    @Override
    public void load()
    {
        part1.load(this);
        part2.load(this);
        part3.load(this, this::setSource);
        part4.load(this, this::setSource);
    }

    @Override
    protected void onResolutionChanged(int width, int height)
    {
        super.onResolutionChanged(width, height);

        part3.onResolutionChanged(width, height);
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        updateSkip(extrp);
        if (device.isFiredOnce(DeviceMapping.FORCE_EXIT))
        {
            end(null);
        }
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        part1.close();
        part2.close();
        part3.close();
        part4.close();

        super.onTerminated(hasNextSequence);
    }
}
