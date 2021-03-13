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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.intro.Intro;

/**
 * Credits implementation.
 */
public final class Credits extends Sequence
{
    private static final ColorRgba COLOR = new ColorRgba(238, 238, 238);

    private final List<Text> texts = new ArrayList<>();
    private final Tick tick = new Tick();
    private final Sprite credits;
    private final Audio audio;

    private final boolean alternative;
    private final Audio audioAlternative;
    private double alphaBack;
    private boolean started;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param audio The audio reference.
     * @param alternative The alternative end.
     */
    public Credits(Context context, Audio audio, Boolean alternative)
    {
        super(context, Constant.EXTRO_RESOLUTION);

        this.alternative = alternative.booleanValue();

        try (BufferedReader data = new BufferedReader(new FileReader(Medias.create("text", "credits.txt").getFile())))
        {
            String line;
            int y = 256;
            while ((line = data.readLine()) != null)
            {
                if (!line.isEmpty())
                {
                    final int size = Integer.parseInt(line.substring(1, 3));
                    final Text text;

                    if (line.charAt(0) == 'C')
                    {
                        text = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SERIF, size, TextStyle.NORMAL);
                        text.setAlign(Align.CENTER);
                        text.setLocation(Constant.EXTRO_RESOLUTION.getWidth() / 2, y);
                        y += size;
                    }
                    else
                    {
                        if (size == 11)
                        {
                            text = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SERIF, size, TextStyle.BOLD);
                        }
                        else
                        {
                            text = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SERIF,
                                                       size,
                                                       TextStyle.NORMAL);
                        }

                        y += size;
                        text.setAlign(Align.LEFT);
                        text.setLocation(0, y);

                        if (size == 14 || size == 26)
                        {
                            text.setLocation(68, y);
                            y += 11;
                        }
                        else if (size == 11)
                        {
                            text.setLocation(88, y);
                        }
                        else
                        {
                            text.setLocation(0, y);
                        }
                    }
                    text.setText(line.substring(4));
                    texts.add(text);

                    text.setColor(COLOR);
                }
                else
                {
                    y += 12;
                }
            }
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
        }

        this.audio = audio;

        audioAlternative = AudioFactory.loadAudio(Music.CREDITS.get());
        audioAlternative.setVolume(Constant.AUDIO_VOLUME);

        if (alternative.booleanValue())
        {
            credits = Drawable.loadSprite(Medias.create(Folder.EXTRO, "part6", "credits.png"));
        }
        else
        {
            alphaBack = 255;
            credits = Drawable.loadSprite(Medias.create(Folder.EXTRO, "part4", "credits.png"));
        }

        tick.start();
    }

    @Override
    public void load()
    {
        credits.load();
        credits.prepare();
        credits.setOrigin(Origin.CENTER_TOP);
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);

        if (tick.elapsed() < 100)
        {
            alphaBack += 6.0;
        }
        if (!started && (!alternative || tick.elapsed() > 900))
        {
            audio.stop();
            audioAlternative.play();
            started = true;
        }
        alphaBack = UtilMath.clamp(alphaBack, 0.0, 255.0);

        if (started)
        {
            for (final Text text : texts)
            {
                text.setLocation(text.getLocationX(), text.getLocationY() - 0.2);
            }
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        credits.setLocation(getWidth() / 2, 0);
        credits.render(g);

        for (final Text text : texts)
        {
            if (text.getLocationY() < 256)
            {
                text.render(g);
            }
        }

        // Render fade in
        if (alphaBack < 255)
        {
            g.setColor(Intro.ALPHAS_BLACK[255 - (int) alphaBack]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
        }
    }
}
