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

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.menu.Menu;

/**
 * Intro part 4 implementation.
 */
public final class Part4 extends Sequence
{
    /** Stories. */
    private static final String STORY1 = Util.toFontText(Medias.create(Folder.TEXTS, Folder.INTRO, "story1.txt"));
    private static final String STORY2 = Util.toFontText(Medias.create(Folder.TEXTS, Folder.INTRO, "story2.txt"));
    private static final String STORY3 = Util.toFontText(Medias.create(Folder.TEXTS, Folder.INTRO, "story3.txt"));
    private static final String STORY4 = Util.toFontText(Medias.create(Folder.TEXTS, Folder.INTRO, "story4.txt"));

    /** Font. */
    private final SpriteFont font = Drawable.loadSpriteFont(Medias.create(Folder.SPRITES, "font.png"),
                                                            Medias.create(Folder.SPRITES, "fontdata.xml"),
                                                            12,
                                                            12);
    /** Pictures. */
    private final Sprite[] history = new Sprite[4];
    /** Input device reference. */
    private final DeviceController device;
    /** App info. */
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
        services.add(new SourceResolutionProvider()
        {
            @Override
            public int getWidth()
            {
                return Part4.this.getWidth();
            }

            @Override
            public int getHeight()
            {
                return Part4.this.getHeight();
            }

            @Override
            public int getRate()
            {
                return Part4.this.getRate();
            }
        });
        device = services.add(DeviceControllerConfig.create(services, Medias.create("input.xml")));
        info = new AppInfo(this::getFps, services);

        for (int i = 0; i < history.length; i++)
        {
            history[i] = Drawable.loadSprite(Medias.create(Folder.INTRO, "part4", "history" + i + ".png"));
            history[i].load();
        }

        load(Menu.class);
    }

    @Override
    public void load()
    {
        font.load();
    }

    @Override
    public void update(double extrp)
    {
        seek = audio.getTicks();

        // First Fade in
        if (seek > 113500 && seek < 197000)
        {
            alphaBack += alphaSpeed;
        }

        // First Fade out
        if (seek > 197000 && seek < 201000)
        {
            alphaBack += alphaSpeed;
        }
        alphaBack = UtilMath.clamp(alphaBack, 0.0, 255.0);

        if (alphaSpeed > 0 && (seek > 197000 || device.isFiredOnce(DeviceMapping.CTRL_RIGHT)))
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

        // Render histories
        if (seek >= 113500)
        {
            history[0].setLocation(0, 0);
            history[0].render(g);
        }
        if (seek >= 130000)
        {
            history[1].setLocation(45, 20);
            history[1].render(g);
        }
        if (seek >= 154000)
        {
            history[2].setLocation(90, 40);
            history[2].render(g);
        }
        if (seek >= 180000)
        {
            history[3].setLocation(135, 60);
            history[3].render(g);
        }

        // Render texts
        if (seek >= 113500 && seek < 130000)
        {
            font.draw(g, 1, history[0].getHeight() + 2, Align.LEFT, STORY1);
        }
        if (seek >= 130000 && seek < 154000)
        {
            font.draw(g, 1, history[1].getHeight() + 22, Align.LEFT, STORY2);
        }
        if (seek >= 154000 && seek < 180000)
        {
            font.draw(g, 1, history[2].getHeight() + 42, Align.LEFT, STORY3);
        }
        if (seek >= 180000 && seek < 201000)
        {
            font.draw(g, 1, history[3].getHeight() + 62, Align.LEFT, STORY4);
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
        super.onTerminated(hasNextSequence);

        audio.stop();
    }
}
