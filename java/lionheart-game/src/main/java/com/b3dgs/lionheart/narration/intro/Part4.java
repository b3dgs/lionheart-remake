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

import java.io.Closeable;
import java.util.function.Consumer;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.FadeSide;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.narration.NarrationFactory;

/**
 * Intro part 4 implementation.
 */
public final class Part4 implements Closeable
{
    /** End intro. */
    static final int T_END = 200_200;

    private static final int FADE_SPEED = 6;

    private static final int STORY0_INDEX = 0;
    private static final int STORY1_INDEX = 1;
    private static final int STORY2_INDEX = 2;
    private static final int STORY3_INDEX = 3;

    private static final int T_START = 114_200;
    private static final int T_STORY1 = 130_000;
    private static final int T_STORY2 = 155_300;
    private static final int T_STORY3 = 180_600;

    private final Resolution resolution;
    private final Stories stories;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Part4(Context context)
    {
        super();

        resolution = Util.getResolution(Constant.RESOLUTION, context);
        stories = new Stories(resolution.getWidth(), resolution.getHeight());
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

        n.add(T_START, () -> initResolution(source));
        n.add(T_START, T_END + 3000, stories::render);
        n.add(T_START, FadeSide.IN, FADE_SPEED);
        n.add(T_START, () -> stories.setStory(STORY0_INDEX));
        n.add(T_STORY1, () -> stories.setStory(STORY1_INDEX));
        n.add(T_STORY2, () -> stories.setStory(STORY2_INDEX));
        n.add(T_STORY3, () -> stories.setStory(STORY3_INDEX));
        n.add(T_END, FadeSide.OUT, FADE_SPEED);
    }

    @Override
    public void close()
    {
        stories.dispose();
    }
}
