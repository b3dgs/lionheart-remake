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
import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.AnimatorFrameListener;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Extro part 4 implementation.
 */
public final class Part4 extends Sequence
{
    /** Stories. */
    private static final String LANG = Settings.getInstance().getLang();
    private static final String STORY4 = Util.toFontText(Medias.create(Folder.TEXT, LANG, Folder.EXTRO, "story4.txt"));
    private static final String STORY5 = Util.toFontText(Medias.create(Folder.TEXT, LANG, Folder.EXTRO, "story5.txt"));
    private static final String STORY6 = Util.toFontText(Medias.create(Folder.TEXT, LANG, Folder.EXTRO, "story6.txt"));
    private static final String STORY7 = Util.toFontText(Medias.create(Folder.TEXT, LANG, Folder.EXTRO, "story7.txt"));
    private static final Animation GLOW = new Animation(Animation.DEFAULT_NAME, 1, 4, 0.15, true, true);

    private final Sprite credits = Drawable.loadSprite(Medias.create(Folder.EXTRO, "part4", "credits.png"));
    private final Sprite[] pics = new Sprite[2];
    private final SpriteFont font = Drawable.loadSpriteFont(Medias.create(Folder.SPRITE, "font.png"),
                                                            Medias.create(Folder.SPRITE, "fontdata.xml"),
                                                            12,
                                                            12);
    private final SpriteAnimated amulet = Drawable.loadSpriteAnimated(Medias.create(Folder.EXTRO,
                                                                                    "part4",
                                                                                    "amulet.png"),
                                                                      2,
                                                                      2);
    private final Tick tick = new Tick();
    private final Audio audio;
    private final boolean alternative;
    private final int textX = getWidth() / 2 - 124;
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
     * @param audio The audio reference.
     * @param alternative The alternative end.
     */
    public Part4(Context context, Audio audio, Boolean alternative)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context));

        this.audio = audio;
        this.alternative = Boolean.TRUE.equals(alternative);

        final Services services = new Services();
        services.add(context);
        services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_CUSTOM)));
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        info = new AppInfo(this::getFps, services);

        if (this.alternative)
        {
            audioAlternative = AudioFactory.loadAudio(Music.EXTRO_ALTERNATIVE);
            audioAlternative.setVolume(Settings.getInstance().getVolumeMusic());
            load(Part5.class, audioAlternative, alternative);
        }
        else
        {
            load(Credits.class, audio, alternative);
        }

        tick.start();

        setSystemCursorVisible(false);
    }

    @Override
    public void load()
    {
        credits.load();
        credits.prepare();
        credits.setOrigin(Origin.MIDDLE);
        credits.setLocation(getWidth() / 2, getHeight() / 2);

        for (int i = 0; i < pics.length; i++)
        {
            pics[i] = Drawable.loadSprite(Medias.create(Folder.EXTRO, "part4", "pic" + i + ".png"));
            pics[i].load();
            pics[i].prepare();
        }

        pics[0].setLocation(getWidth() / 2 - 114, 12);
        pics[1].setLocation(getWidth() / 2 - 20, 62);

        font.load();
        font.prepare();

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
        tick.update(extrp);

        if (tick.elapsed() < 235)
        {
            alphaBack += 6.0;
        }
        if (alternativeMusic && tick.elapsed() > 3050)
        {
            alphaBack -= 6.0;
        }
        alphaBack = UtilMath.clamp(alphaBack, 0.0, 255.0);

        if (alternative)
        {
            amulet.update(extrp);
        }

        if (alternative && tick.elapsed() > 980 && !alternativeMusic)
        {
            alternativeMusic = true;
            audio.stop();
            audioAlternative.play();
        }

        if (!alternative && tick.elapsed() > 980 || alternativeMusic && tick.elapsed() > 3200)
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
        credits.render(g);

        if (alternative)
        {
            amulet.render(g);

            if (tick.elapsed() >= 1330)
            {
                g.setColor(Constant.ALPHAS_BLACK[128]);
                g.drawRect(0, 0, getWidth(), getHeight(), true);
                pics[0].render(g);
            }
            if (tick.elapsed() >= 2220)
            {
                pics[1].render(g);
            }
        }

        // Render texts
        if (tick.elapsed() > 180 && tick.elapsed() < 1330)
        {
            font.draw(g, textX, 22, Align.LEFT, STORY4);

            if (alternativeMusic)
            {
                font.draw(g, textX, 74, Align.LEFT, STORY5);

                if (!played)
                {
                    played = true;
                    amulet.play(GLOW);
                }
            }
        }
        else if (alternativeMusic && tick.elapsed() >= 1330 && tick.elapsed() < 2220)
        {
            font.draw(g, textX, 172, Align.LEFT, STORY6);
        }
        else if (alternativeMusic && tick.elapsed() >= 2220 && tick.elapsed() < 3100)
        {
            font.draw(g, textX, 172, Align.LEFT, STORY7);
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
        }
    }
}
