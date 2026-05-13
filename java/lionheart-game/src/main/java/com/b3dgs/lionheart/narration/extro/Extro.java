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
package com.b3dgs.lionheart.narration.extro;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.FadeSide;
import com.b3dgs.lionheart.GameConfig;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.menu.MenuGame;
import com.b3dgs.lionheart.narration.Narration;

/**
 * Extro implementation.
 */
public class Extro extends Narration
{
    private static final int MIN_HEIGHT = 208;
    private static final int MAX_WIDTH = 400;
    private static final int MARGIN_WIDTH = 80;
    private static final int FADE_OUT = 5;

    /** The end flag. */
    protected final AtomicBoolean theEnd = new AtomicBoolean();

    private final Audio audioAlternative = AudioFactory.loadAudio(Music.EXTRO_ALTERNATIVE);

    private final Fade fadeOut = new Fade(FadeSide.OUT, FADE_OUT);
    private final Part1 part1 = new Part1(context, getWidth(), getHeight(), getRate());
    private final Part2 part2 = new Part2(context);
    private final Part3 part3 = new Part3(context);
    private final Part4 part4;
    private final Part5 part5 = new Part5(context);
    private final Credits credits;

    private final GameConfig config;
    private final boolean alternative;

    private boolean quit;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param config The game config reference.
     * @param alternative The alternative end.
     */
    public Extro(Context context, GameConfig config, Boolean alternative)
    {
        super(context,
              Util.getResolution(context, MIN_HEIGHT, MAX_WIDTH, MARGIN_WIDTH),
              AudioFactory.loadAudio(Music.EXTRO));

        this.config = config;
        this.alternative = Boolean.TRUE.equals(alternative);
        part4 = new Part4(context, audio, audioAlternative, this.alternative);
        credits = new Credits(context, audioAlternative, this.alternative, theEnd);
    }

    @Override
    public void load()
    {
        part1.load(this);
        part2.load(this, this::setSource);
        part3.load(this, this::setSource);
        part4.load(this, this::setSource);
        if (alternative)
        {
            part5.load(this, this::setSource);
        }
        credits.load(this);
    }

    @Override
    protected void onResolutionChanged(int width, int height)
    {
        super.onResolutionChanged(width, height);

        part2.onResolutionChanged(width, height);
    }

    /**
     * Update skip check.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateSkip(double extrp)
    {
        if (quit)
        {
            fadeOut.update(extrp);
            if (getAlpha() == 255)
            {
                audio.stop();
                end(MenuGame.class, config);
            }
        }
        else if (theEnd.get())
        {
            if (device.isFiredOnce(DeviceMapping.ATTACK) || deviceCursor.isFiredOnce(DeviceMapping.LEFT))
            {
                quit = true;
                fadeOut.init();
            }
            else if (device.isFiredOnce(DeviceMapping.FORCE_EXIT))
            {
                end(null);
            }
        }
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        updateSkip(extrp);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        part1.close();
        part3.close();
        part4.close();
        part5.close();
        credits.close();

        super.onTerminated(hasNextSequence);
    }
}
