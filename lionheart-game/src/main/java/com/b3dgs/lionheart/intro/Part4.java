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
package com.b3dgs.lionheart.intro;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.menu.Menu;

/**
 * Intro part 4 implementation.
 */
public final class Part4 extends Sequence
{
    /** Stories. */
    private final Stories stories = new Stories();
    /** Input device reference. */
    private final DeviceController device;
    /** Application info. */
    private final AppInfo info;
    /** Audio. */
    private final Audio audio;
    /** Back alpha. */
    private double alphaBack;
    /** Alpha speed. */
    private double alphaSpeed = 3.0;
    /** Current seek. */
    private long seek;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param audio The audio reference.
     */
    public Part4(Context context, Audio audio)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context));

        this.audio = audio;

        final Services services = new Services();
        services.add(context);
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_CUSTOM)));
        info = new AppInfo(this::getFps, services);

        load(Menu.class);

        setSystemCursorVisible(false);
    }

    @Override
    public void load()
    {
        stories.load();
        audio.play();
    }

    @Override
    public void update(double extrp)
    {
        seek = audio.getTicks();

        if (seek > 113500)
        {
            stories.setStory(0);
        }
        if (seek > 130000)
        {
            stories.setStory(1);
        }
        if (seek > 154000)
        {
            stories.setStory(2);
        }
        if (seek > 180000)
        {
            stories.setStory(3);
        }

        // First Fade in
        if (seek > 113500 && seek < 201000)
        {
            alphaBack += alphaSpeed;
        }

        // First Fade out
        if (seek > 200000 && seek < 205000)
        {
            alphaBack += alphaSpeed;
        }
        alphaBack = UtilMath.clamp(alphaBack, 0.0, 255.0);

        if (alphaSpeed > 0 && (seek > 200000 || device.isFiredOnce(DeviceMapping.CTRL_RIGHT)))
        {
            alphaSpeed = -alphaSpeed * 2;
        }
        if (alphaBack == 0 && alphaSpeed < 0)
        {
            end();
        }

        info.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        stories.render(g);

        // Render fade in
        if (alphaBack < 255)
        {
            g.setColor(Constant.ALPHAS_BLACK[255 - (int) Math.floor(alphaBack)]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
        }

        info.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        super.onTerminated(hasNextSequence);

        audio.stop();
    }
}
