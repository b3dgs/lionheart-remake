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
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.intro.Intro;

/**
 * Extro part 6 implementation.
 */
public final class Part6 extends Sequence
{
    private final Sprite credits = Drawable.loadSprite(Medias.create(Folder.EXTRO, "part6", "credits.png"));
    private final Tick tick = new Tick();
    private final Audio audio;
    private Audio audioAlternative;
    private double alphaBack;
    private boolean started;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param audio The audio reference.
     * @param alternative The alternative end.
     */
    public Part6(Context context, Audio audio, Boolean alternative)
    {
        super(context, Constant.EXTRO_RESOLUTION);

        this.audio = audio;

        if (alternative.booleanValue())
        {
            audioAlternative = AudioFactory.loadAudio(Music.CREDITS.get());
            audioAlternative.setVolume(Constant.AUDIO_VOLUME);
        }
    }

    @Override
    public void load()
    {
        credits.load();
        credits.prepare();
        credits.setOrigin(Origin.CENTER_TOP);

        tick.start();
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);

        if (tick.elapsed() < 100)
        {
            alphaBack += 6.0;
        }
        if (!started && tick.elapsed() > 800)
        {
            audio.stop();
            audioAlternative.play();
            started = true;
        }
        alphaBack = UtilMath.clamp(alphaBack, 0.0, 255.0);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        credits.setLocation(getWidth() / 2, 0);
        credits.render(g);

        // Render fade in
        if (alphaBack < 255)
        {
            g.setColor(Intro.ALPHAS_BLACK[255 - (int) alphaBack]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
        }
    }
}
