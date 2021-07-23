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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.AnimatorFrameListener;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Time;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Extro part 4 implementation.
 */
public class Part4 extends Sequence
{
    /** Device controller reference. */
    final DeviceController device;
    /** Alpha speed. */
    int alphaSpeed = 6;

    private final Stories stories = new Stories(getWidth(), getHeight());
    private final Animation glow = new Animation(Animation.DEFAULT_NAME, 1, 4, 0.15, true, true);
    private final SpriteAnimated amulet = Drawable.loadSpriteAnimated(Medias.create(Folder.EXTRO,
                                                                                    "part4",
                                                                                    "amulet.png"),
                                                                      2,
                                                                      2);
    private final Time time;
    private final Audio audio;
    private final boolean alternative;
    private final AppInfo info;

    private Audio audioAlternative;
    private double alphaBack;
    private boolean alternativeMusic;
    private int glowed;
    private boolean played;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param time The time reference.
     * @param audio The audio reference.
     * @param alternative The alternative end.
     */
    public Part4(Context context, Time time, Audio audio, Boolean alternative)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context));

        this.time = time;
        this.audio = audio;
        this.alternative = Boolean.TRUE.equals(alternative);

        final Services services = new Services();
        services.add(context);
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_CUSTOM)));
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        info = new AppInfo(this::getFps, services);

        if (this.alternative)
        {
            audioAlternative = AudioFactory.loadAudio(Music.EXTRO_ALTERNATIVE);
            audioAlternative.setVolume(Settings.getInstance().getVolumeMusic());
            load(Part5.class, time, audioAlternative, alternative);
        }
        else
        {
            load(Credits.class, time, audio, alternative);
        }

        setSystemCursorVisible(false);
    }

    @Override
    public void load()
    {
        stories.load();
        stories.setStory(3);

        amulet.load();
        amulet.prepare();
        amulet.setLocation(getWidth() / 2 - 48, 152);
        amulet.addListener((AnimatorFrameListener) f ->
        {
            if (f == 1 && amulet.getAnimState() != AnimState.STOPPED)
            {
                glowed++;
                if (glowed > 5)
                {
                    amulet.stop();
                    amulet.setFrame(1);
                    glowed = 0;
                }
            }
        });
    }

    @Override
    public void update(double extrp)
    {
        time.update(extrp);

        if (time.isBefore(89250))
        {
            alphaBack += alphaSpeed;
        }
        if (alternativeMusic && time.isAfter(136170))
        {
            alphaBack -= alphaSpeed;
        }
        alphaBack = UtilMath.clamp(alphaBack, 0.0, 255.0);

        if (alternative)
        {
            amulet.update(extrp);
        }

        if (alternative && time.isAfter(101670) && !alternativeMusic)
        {
            alternativeMusic = true;
            audio.stop();
            audioAlternative.play();
        }

        if (!alternative && time.isAfter(101670) || alternativeMusic && time.isAfter(138670))
        {
            end();
        }
        if (device.isFiredOnce(DeviceMapping.FORCE_EXIT))
        {
            end(null);
        }

        info.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        if (time.isBefore(107500))
        {
            stories.render(g);
        }

        if (alternative)
        {
            amulet.render(g);
        }

        if (time.isBetween(88335, 107500))
        {
            stories.setStory(4);

            if (alternativeMusic)
            {
                stories.setStory(5);
                if (!played)
                {
                    played = true;
                    amulet.play(glow);
                }
            }
        }
        else if (alternativeMusic && time.isBetween(107500, 122335))
        {
            stories.setStory(7);
            stories.render(g);
        }
        else if (alternativeMusic && time.isBetween(12335, 137000))
        {
            stories.setStory(8);
            stories.render(g);
        }

        // Render fade in
        if (alphaBack < 255)
        {
            g.setColor(Constant.ALPHAS_BLACK[255 - (int) alphaBack]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
        }

        info.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        stories.dispose();
        amulet.dispose();

        if (!hasNextSequence)
        {
            audio.stop();
            Engine.terminate();
        }
    }
}
