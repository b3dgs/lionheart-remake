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
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Time;
import com.b3dgs.lionheart.Util;

/**
 * Extro part 3 implementation.
 */
public final class Part3 extends Sequence
{
    private final Stories stories = new Stories(getWidth(), getHeight());
    private final AppInfo info;
    private final Time time;
    private final Audio audio;

    private double alphaBack = 255;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param time The time reference.
     * @param audio The audio reference.
     * @param alternative The alternative end.
     */
    public Part3(Context context, Time time, Audio audio, Boolean alternative)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context));

        this.time = time;
        this.audio = audio;

        final Services services = new Services();
        services.add(context);
        services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_CUSTOM)));
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        info = new AppInfo(this::getFps, services);

        load(Part4.class, time, audio, alternative);

        setSystemCursorVisible(false);
    }

    @Override
    public void load()
    {
        stories.load();
    }

    @Override
    public void update(double extrp)
    {
        time.update(extrp);

        if (time.isAfter(41000))
        {
            stories.setStory(0);
        }
        if (time.isAfter(56000))
        {
            stories.setStory(1);
        }
        if (time.isAfter(71000))
        {
            stories.setStory(2);
        }

        if (time.isAfter(84500))
        {
            alphaBack -= 6.0;
        }
        alphaBack = UtilMath.clamp(alphaBack, 0.0, 255.0);

        if (time.isAfter(85335))
        {
            end();
        }

        info.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        if (time.isAfter(41000))
        {
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
        if (!hasNextSequence)
        {
            audio.stop();
            Engine.terminate();
        }
    }
}
