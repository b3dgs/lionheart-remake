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

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Extro part 3 implementation.
 */
public final class Part3 extends Sequence
{
    /** Stories. */
    private static final String STORY1 = Util.toFontText(Medias.create(Folder.TEXTS, Folder.EXTRO, "story1.txt"));
    private static final String STORY2 = Util.toFontText(Medias.create(Folder.TEXTS, Folder.EXTRO, "story2.txt"));
    private static final String STORY3 = Util.toFontText(Medias.create(Folder.TEXTS, Folder.EXTRO, "story3.txt"));

    private final Sprite[] pics = new Sprite[3];
    private final SpriteFont font = Drawable.loadSpriteFont(Medias.create(Folder.SPRITES, "font.png"),
                                                            Medias.create(Folder.SPRITES, "fontdata.xml"),
                                                            12,
                                                            12);
    private final Tick tick = new Tick();
    private final AppInfo info;

    private double alphaBack = 255;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param audio The audio reference.
     * @param alternative The alternative end.
     */
    public Part3(Context context, Audio audio, Boolean alternative)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context));

        for (int i = 0; i < pics.length; i++)
        {
            pics[i] = Drawable.loadSprite(Medias.create(Folder.EXTRO, "part3", "pic" + i + ".png"));
            pics[i].load();
            pics[i].prepare();
        }

        final Services services = new Services();
        services.add(context);
        services.add(DeviceControllerConfig.create(services, Medias.create("input.xml")));
        info = new AppInfo(this::getFps, services);

        load(Part4.class, audio, alternative);

        tick.start();
    }

    @Override
    public void load()
    {
        font.load();
        font.prepare();
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);

        if (tick.elapsed() > 3000)
        {
            alphaBack -= 6.0;
        }
        alphaBack = UtilMath.clamp(alphaBack, 0.0, 255.0);

        if (tick.elapsed() > 3050)
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
        if (tick.elapsed() > 390)
        {
            pics[0].setLocation(0, 0);
            pics[0].render(g);
        }
        if (tick.elapsed() >= 1290)
        {
            pics[1].setLocation(160, 14);
            pics[1].render(g);
        }
        if (tick.elapsed() >= 2190)
        {
            pics[2].setLocation(80, 29);
            pics[2].render(g);
        }

        // Render texts
        if (tick.elapsed() > 390 && tick.elapsed() < 1290)
        {
            font.draw(g, 1, 128, Align.LEFT, STORY1);
        }
        if (tick.elapsed() >= 1290 && tick.elapsed() < 2190)
        {
            font.draw(g, 1, 128, Align.LEFT, STORY2);
        }
        if (tick.elapsed() >= 2190)
        {
            font.draw(g, 1, 128, Align.LEFT, STORY3);
        }

        // Render fade in
        if (alphaBack < 255)
        {
            g.setColor(Constant.ALPHAS_BLACK[255 - (int) alphaBack]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
        }

        info.render(g);
    }
}
