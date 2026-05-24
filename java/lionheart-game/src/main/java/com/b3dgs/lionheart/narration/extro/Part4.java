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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.FadeSide;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.narration.NarrationFactory;

/**
 * Extro part 4 implementation.
 */
public final class Part4 implements Closeable
{
    private static final int FADE_SPEED = 5;

    private static final int STORY0_INDEX = 3;
    private static final int STORY1_INDEX = 4;
    private static final int STORY2_INDEX = 5;
    private static final int STORY3_INDEX = 7;
    private static final int STORY4_INDEX = 8;

    private static final int AMULET_X_OFFSET = -48;
    private static final int AMULET_Y = 152;
    private static final int AMULET_GLOW_COUNT = 5;

    private static final String PART4_FOLDER = "part4";
    private static final String FILE_AMULET = "amulet.png";

    private static final int T_FADE_IN = 87_000;
    private static final int T_STORY0 = 89_100;
    private static final int T_END_OR_ALTERNATIVE = 103_000;
    private static final int T_STORY1 = 108_200;
    private static final int T_STORY2 = 123_400;
    private static final int T_FADE_OUT = 138_600;

    private final Animation glow = new Animation(Animation.DEFAULT_NAME, 1, 4, 0.15, true, false);
    private final SpriteAnimated amulet = Util.get(2, 2, Folder.EXTRO, PART4_FOLDER, FILE_AMULET);

    private final Audio audio;
    private final Audio audioAlternative;
    private final boolean alternative;
    private final Resolution resolution;
    private final Stories stories;

    private int glowed;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param audio The audio reference.
     * @param audioAlternative The audio alternative reference.
     * @param alternative The alternative flag.
     */
    public Part4(Context context, Audio audio, Audio audioAlternative, boolean alternative)
    {
        super();

        this.audio = audio;
        this.audioAlternative = audioAlternative;
        this.alternative = alternative;
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
        stories.setStory(STORY0_INDEX);
        stories.setStart(STORY0_INDEX);

        amulet.setLocation(resolution.width() / 2 + AMULET_X_OFFSET, AMULET_Y);

        init(n, source);
    }

    private void init(NarrationFactory n, Consumer<Resolution> source)
    {
        n.add(T_FADE_IN, () -> initResolution(source));
        n.add(T_FADE_IN, FadeSide.IN, FADE_SPEED);
        if (alternative)
        {
            n.add(T_FADE_IN, T_FADE_OUT + 3000, stories::render);
            n.add(T_FADE_IN, T_STORY1, this::updateAmulet, amulet::render);
        }
        else
        {
            n.add(T_FADE_IN, T_STORY1, stories::render);
        }
        n.add(T_STORY0, () -> stories.setStory(STORY1_INDEX));
        if (alternative)
        {
            n.add(T_END_OR_ALTERNATIVE, this::startEndAlternative);
            n.add(T_STORY1, () -> stories.setStory(STORY3_INDEX));
            n.add(T_STORY2, () -> stories.setStory(STORY4_INDEX));
            n.add(T_FADE_OUT, FadeSide.OUT, FADE_SPEED);
        }
    }

    @Override
    public void close()
    {
        stories.dispose();
        amulet.dispose();
    }

    /**
     * End alternative setup.
     */
    private void startEndAlternative()
    {
        audio.stop();
        audioAlternative.play();
        stories.setStory(STORY2_INDEX);
        amulet.play(glow);
    }

    /**
     * Update amulet routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateAmulet(double extrp)
    {
        amulet.update(extrp);

        if (amulet.getAnimState() == AnimState.FINISHED)
        {
            glowed++;

            if (glowed < AMULET_GLOW_COUNT)
            {
                amulet.play(glow);
            }
        }
    }
}
