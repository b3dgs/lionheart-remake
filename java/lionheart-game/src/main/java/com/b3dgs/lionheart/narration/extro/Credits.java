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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.FadeSide;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.narration.NarrationFactory;

/**
 * Credits implementation.
 */
public final class Credits implements Closeable
{
    /** Start time. */
    static final int T_START = 103_000;
    /** Start time. */
    static final int T_START_ALTERNATIVE = 175_500;
    /** Scroll speed. */
    static double SCROLL_SPEED = 0.24;
    /** Fade speed. */
    static int FADE_SPEED = 5;

    private static final String PART4_FOLDER = "part4";
    private static final String PART5_FOLDER = "part5";
    private static final String FILENAME_IMAGE = "credits.png";
    private static final String FILENAME_TEXT = "credits.txt";

    private static final char TEXT_CENTER = 'C';
    private static final int TEXT_SIZE_EMPTY = 12;
    private static final int TEXT_SIZE_SMALL = 11;
    private static final int TEXT_SIZE_SEPARATOR = 14;
    private static final int TEXT_SIZE_MEDIUM = 24;
    private static final int TEXT_BEGIN_INDEX = 4;
    private static final int TEXT_SMALL_OFFSET_WIDTH = 32;
    private static final int TEXT_SCROLL_END_HEIGHT = 48;

    private static final int T_FADE_IN = 163_000;

    private final String lang = Settings.getInstance().getLang();
    private final List<SpriteFont> texts = new ArrayList<>();
    private final Audio audio;
    private final Audio audioAlternative = AudioFactory.loadAudio(Music.CREDITS);
    private final Sprite sprite;
    private final boolean alternative;
    private final int count;
    private final SpriteFont lastText;
    private final int width;
    private final int height;
    private final AtomicBoolean theEnd;

    private int textFirstToRender;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param audio The audio reference.
     * @param alternative The alternative flag.
     * @param theEnd The end flag.
     */
    public Credits(Context context, Audio audio, boolean alternative, AtomicBoolean theEnd)
    {
        this.audio = audio;
        this.alternative = alternative;
        this.theEnd = theEnd;

        final Resolution resolution = Util.getResolution(Constant.RESOLUTION, context);
        width = resolution.getWidth();
        height = resolution.getHeight();

        audioAlternative.setVolume(Settings.getInstance().getVolumeMusic());
        sprite = Util.get(Folder.EXTRO, this.alternative ? PART5_FOLDER : PART4_FOLDER, FILENAME_IMAGE);
        loadTextLines();
        count = texts.size();
        lastText = texts.get(count - 1);
    }

    /**
     * Load part.
     * 
     * @param narration The narration factory.
     */
    public void load(NarrationFactory narration)
    {
        sprite.setOrigin(Origin.MIDDLE);
        sprite.setLocation(width / 2, height / 2);
        init(narration);
    }

    private void init(NarrationFactory n)
    {
        if (alternative)
        {
            n.add(T_FADE_IN, FadeSide.IN, FADE_SPEED);
            n.add(T_FADE_IN, -1, sprite::render);
            n.add(T_START_ALTERNATIVE, this::start);
            n.add(T_START_ALTERNATIVE, -1, this::update, this::render);
        }
        else
        {
            n.add(T_START, FadeSide.IN, 255);
            n.add(T_START, -1, sprite::render);
            n.add(T_START, this::start);
            n.add(T_START, -1, this::update, this::render);
        }
    }

    @Override
    public void close()
    {
        sprite.dispose();
        final int n = texts.size();
        for (int i = 0; i < n; i++)
        {
            texts.get(i).dispose();
        }
    }

    private void loadTextLines()
    {
        int y = height;
        final List<String> lines = Util.readLines(Medias.create(Folder.TEXT, lang, Folder.EXTRO, FILENAME_TEXT));
        for (int i = 0; i < lines.size(); i++)
        {
            final String line = lines.get(i);
            if (!line.isEmpty())
            {
                y = loadTextLine(line, y);
            }
            else
            {
                y += TEXT_SIZE_EMPTY;
            }
        }
    }

    private int loadTextLine(String line, int oldY)
    {
        int y = oldY;
        final int size = Integer.parseInt(line.substring(1, 3));
        final int tw;
        final int th;
        if (size == 26)
        {
            tw = 26;
            th = 30;
        }
        else if (size == 24)
        {
            tw = 24;
            th = 28;
        }
        else if (size == 14)
        {
            tw = 14;
            th = 18;
        }
        else
        {
            tw = 11;
            th = 15;
        }
        final SpriteFont text = Util.loadFont("font" + size + ".png", "font" + size + ".xml", tw, th);
        if (line.charAt(0) == TEXT_CENTER)
        {
            text.setAlign(Align.CENTER);
            text.setLocation(width / 2, y);
            y += size;
        }
        else
        {
            y += size;
            text.setAlign(Align.LEFT);

            if (size == TEXT_SIZE_SEPARATOR || size == TEXT_SIZE_MEDIUM)
            {
                text.setLocation(width / 2 - sprite.getWidth() / 2 + 2, y);
                y += TEXT_SIZE_SMALL;
            }
            else if (size == TEXT_SIZE_SMALL)
            {
                y += 2;
                text.setLocation(width / 2 - sprite.getWidth() / 2 + TEXT_SMALL_OFFSET_WIDTH, y);
            }
            else
            {
                text.setLocation(width / 2 - sprite.getWidth() / 2, y);
            }
        }
        text.setText(line.substring(TEXT_BEGIN_INDEX));
        texts.add(text);

        return y;
    }

    /**
     * Update start credit action.
     */
    private void start()
    {
        audio.stop();
        audioAlternative.play();
    }

    /**
     * Update scroll text until end.
     * 
     * @param extrp The extrapolation value.
     */
    private void update(double extrp)
    {
        if (lastText.getY() > height - TEXT_SCROLL_END_HEIGHT)
        {
            for (int i = 0; i < count; i++)
            {
                final SpriteFont text = texts.get(i);
                text.setLocation(text.getX(), text.getY() - SCROLL_SPEED * extrp);
            }
        }
        else
        {
            theEnd.set(true);
        }
    }

    /**
     * Render routine.
     * 
     * @param g The graphic output.
     */
    private void render(Graphic g)
    {
        for (int i = textFirstToRender; i < count; i++)
        {
            final SpriteFont text = texts.get(i);
            final double y = text.getY();
            if (y < -text.getHeight())
            {
                textFirstToRender = i;
            }
            else if (y < height)
            {
                text.render(g);
            }
            else
            {
                break;
            }
        }
    }
}
