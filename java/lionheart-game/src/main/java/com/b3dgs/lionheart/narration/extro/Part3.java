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

import java.io.Closeable;
import java.util.function.Consumer;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.FadeSide;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.narration.NarrationFactory;

/**
 * Extro part 3 implementation.
 */
public final class Part3 implements Closeable
{
    private static final int FADE_SPEED = 5;

    private static final int STORY0_INDEX = 0;
    private static final int STORY1_INDEX = 1;
    private static final int STORY2_INDEX = 2;

    private static final int TIME_STORY0_MS = 41_400;
    private static final int TIME_STORY1_MS = 56_500;
    private static final int TIME_STORY2_MS = 71_700;
    private static final int TIME_FADE_OUT_MS = 85_200;

    private final Resolution resolution;
    private final Stories stories;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Part3(Context context)
    {
        super();

        resolution = Util.getResolution(Constant.RESOLUTION, context);
        stories = new Stories(resolution.width(), resolution.height());
    }

    /**
     * Init resolution.
     * 
     * @param source The source resolution.
     */
    public void initResolution(Consumer<Resolution> source)
    {
        source.accept(resolution);
    }

    /**
     * Load part.
     * 
     * @param n The narration factory.
     * @param source The resolution source.
     */
    public void load(NarrationFactory n, Consumer<Resolution> source)
    {
        stories.load();

        n.add(TIME_STORY0_MS, () -> initResolution(source));
        n.add(TIME_STORY0_MS, FadeSide.IN, 255);
        n.add(TIME_STORY0_MS, TIME_FADE_OUT_MS + 3000, stories::render);
        n.add(TIME_STORY0_MS, () -> stories.setStory(STORY0_INDEX));
        n.add(TIME_STORY1_MS, () -> stories.setStory(STORY1_INDEX));
        n.add(TIME_STORY2_MS, () -> stories.setStory(STORY2_INDEX));
        n.add(TIME_FADE_OUT_MS, FadeSide.OUT, FADE_SPEED);
    }

    @Override
    public void close()
    {
        stories.dispose();
    }
}
