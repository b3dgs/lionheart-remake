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

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Time;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.menu.Menu;

/**
 * Credits implementation.
 */
public final class Credits extends Sequence
{
    private static final ColorRgba COLOR = new ColorRgba(238, 238, 238);
    private static final double SCROLL_SPEED = 0.2;

    private final List<Text> texts = new ArrayList<>();
    private final Sprite credits;
    private final Time time;
    private final Audio audio;
    private final Audio audioAlternative = AudioFactory.loadAudio(Music.CREDITS);
    private final DeviceController device;
    private final boolean alternative;
    private final int count;

    private double alphaBack;
    private boolean started;
    private final Text lastText;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param time The time reference.
     * @param audio The audio reference.
     * @param alternative The alternative end.
     */
    public Credits(Context context, Time time, Audio audio, Boolean alternative)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context));

        this.time = time;
        this.alternative = Boolean.TRUE.equals(alternative);

        if (this.alternative)
        {
            credits = Drawable.loadSprite(Medias.create(Folder.EXTRO, "part5", "credits.png"));
        }
        else
        {
            alphaBack = 255;
            credits = Drawable.loadSprite(Medias.create(Folder.EXTRO, "part4", "credits.png"));
        }

        final Services services = new Services();
        services.add(context);
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_CUSTOM)));
        device.setVisible(false);

        int y = 256;
        final List<String> lines = Util.readLines(Medias.create(Folder.TEXT,
                                                                Settings.getInstance().getLang(),
                                                                Folder.EXTRO,
                                                                "credits.txt"));
        for (int i = 0; i < lines.size(); i++)
        {
            final String line = lines.get(i);
            if (!line.isEmpty())
            {
                final int size = Integer.parseInt(line.substring(1, 3));
                final Text text;

                if (line.charAt(0) == 'C')
                {
                    text = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SERIF, size, TextStyle.NORMAL);
                    text.setAlign(Align.CENTER);
                    text.setLocation(getWidth() / 2, y);
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
                        text = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SERIF, size, TextStyle.NORMAL);
                    }

                    y += size;
                    text.setAlign(Align.LEFT);

                    if (size == 14 || size == 24)
                    {
                        text.setLocation(getWidth() / 2 - credits.getWidth() / 2 + 2, y);
                        y += 11;
                    }
                    else if (size == 11)
                    {
                        y += 2;
                        text.setLocation(getWidth() / 2 - credits.getWidth() / 2 + 32, y);
                    }
                    else
                    {
                        text.setLocation(getWidth() / 2 - credits.getWidth() / 2, y);
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
        count = texts.size();
        lastText = texts.get(count - 1);

        this.audio = audio;

        audioAlternative.setVolume(Settings.getInstance().getVolumeMusic());
    }

    @Override
    public void load()
    {
        credits.load();
        credits.prepare();
        credits.setOrigin(Origin.MIDDLE);
        credits.setLocation(getWidth() / 2, getHeight() / 2);
    }

    @Override
    public void update(double extrp)
    {
        time.update(extrp);

        if (!alternative && time.isBefore(103400) || alternative && time.isBefore(161200))
        {
            alphaBack += 6.0;
        }
        if (!started && (!alternative || time.isAfter(174500)))
        {
            audio.stop();
            audioAlternative.play();
            started = true;
        }
        alphaBack = UtilMath.clamp(alphaBack, 0.0, 255.0);

        if (started)
        {
            if (lastText.getLocationY() > getHeight() - 48)
            {
                for (int i = 0; i < count; i++)
                {
                    final Text text = texts.get(i);
                    text.setLocation(text.getLocationX(), text.getLocationY() - SCROLL_SPEED);
                }
            }
            else if (device.isFired(DeviceMapping.CTRL_RIGHT))
            {
                end(Menu.class);
            }
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        credits.render(g);

        for (int i = 0; i < count; i++)
        {
            final Text text = texts.get(i);
            if (text.getLocationY() < 256)
            {
                text.render(g);
            }
        }

        // Render fade in
        if (alphaBack < 255)
        {
            g.setColor(Constant.ALPHAS_BLACK[255 - (int) alphaBack]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
        }
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        super.onTerminated(hasNextSequence);

        audio.stop();
        audioAlternative.stop();
    }
}
