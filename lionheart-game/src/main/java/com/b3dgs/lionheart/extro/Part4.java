/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.RenderableVoid;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.GameConfig;
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
    private static final int FADE_SPEED = 5;

    private static final int STORY_1 = 3;
    private static final int STORY_2 = 4;
    private static final int STORY_3 = 5;
    private static final int STORY_4 = 7;
    private static final int STORY_5 = 8;

    private static final int AMULET_X_OFFSET = -48;
    private static final int AMULET_Y = 152;
    private static final int AMULET_GLOW_COUNT = 5;

    private static final String PART4_FOLDER = "part4";

    private static final String FILE_AMULET = "amulet.png";

    private static final int TIME_STORY2_MS = 89_100;
    private static final int TIME_END_MS = 103_000;
    private static final int TIME_STORY4_MS = 108_200;
    private static final int TIME_STORY5_MS = 123_400;
    private static final int TIME_FADE_OUT_MS = 138_600;

    /** Device controller reference. */
    final DeviceController device;
    /** Alpha speed. */
    int alphaSpeed = FADE_SPEED;

    private final Stories stories = new Stories(getWidth(), getHeight());
    private final Animation glow = new Animation(Animation.DEFAULT_NAME, 1, 4, 0.15, true, false);
    private final SpriteAnimated amulet = Drawable.loadSpriteAnimated(Medias.create(Folder.EXTRO,
                                                                                    PART4_FOLDER,
                                                                                    FILE_AMULET),
                                                                      2,
                                                                      2);
    private final Time time;
    private final Audio audio;
    private final Audio audioAlternative;
    private final boolean alternative;
    private final AppInfo info;

    private Updatable updaterAmulet = UpdatableVoid.getInstance();
    private Updatable updaterFade = this::updateFadeIn;
    private Updatable updaterStories = this::updateStory2;

    private Renderable rendererFade = this::renderFade;

    private double alpha = 255.0;
    private int glowed;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param config The game config reference.
     * @param time The time reference.
     * @param audio The audio reference.
     * @param alternative The alternative end.
     */
    public Part4(Context context, GameConfig config, Time time, Audio audio, Boolean alternative)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context), Util.getLoop(context.getConfig().getOutput()));

        this.time = time;
        this.audio = audio;
        this.alternative = Boolean.TRUE.equals(alternative);

        final Services services = new Services();
        services.add(context);
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_DEFAULT)));
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        info = new AppInfo(this::getFps, services);

        if (this.alternative)
        {
            audioAlternative = AudioFactory.loadAudio(Music.EXTRO_ALTERNATIVE);
            audioAlternative.setVolume(Settings.getInstance().getVolumeMusic());
            load(Part5.class, config, time, audioAlternative, alternative);
        }
        else
        {
            audioAlternative = null;
            load(Credits.class, config, time, audio, alternative);
        }

        setSystemCursorVisible(false);
        Util.setFilter(this, context, Util.getResolution(Constant.RESOLUTION, context), 2);
    }

    /**
     * Update fade in effect.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeIn(double extrp)
    {
        alpha -= alphaSpeed * extrp;

        if (getAlpha() < 0)
        {
            alpha = 0.0;
            updaterFade = this::updateEnd;
            rendererFade = RenderableVoid.getInstance();
        }
    }

    /**
     * Update fade out time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOutInit(double extrp)
    {
        if (time.isAfter(TIME_FADE_OUT_MS))
        {
            updaterFade = this::updateFadeOut;
            rendererFade = this::renderFade;
        }
    }

    /**
     * Update fade out effect.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOut(double extrp)
    {
        alpha += alphaSpeed * extrp;

        if (getAlpha() > 255)
        {
            alpha = 255.0;
            end();
            updaterFade = UpdatableVoid.getInstance();
        }
    }

    /**
     * Update end.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateEnd(double extrp)
    {
        if (time.isAfter(TIME_END_MS))
        {
            if (alternative)
            {
                audio.stop();
                audioAlternative.play();
                stories.setStory(STORY_3);
                amulet.play(glow);
                updaterAmulet = this::updateAmulet;
                updaterFade = this::updateFadeOutInit;
            }
            else
            {
                end();
            }
        }
    }

    /**
     * Update story 2 or jump to alternative story.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateStory2(double extrp)
    {
        if (time.isAfter(TIME_STORY2_MS))
        {
            stories.setStory(STORY_2);

            if (alternative)
            {
                updaterStories = this::updateStory4;
            }
            else
            {
                updaterStories = UpdatableVoid.getInstance();
            }
        }
    }

    /**
     * Update story 4.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateStory4(double extrp)
    {
        if (time.isAfter(TIME_STORY4_MS))
        {
            stories.setStory(STORY_4);
            updaterStories = this::updateStory5;
        }
    }

    /**
     * Update story 5.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateStory5(double extrp)
    {
        if (time.isAfter(TIME_STORY5_MS))
        {
            stories.setStory(STORY_5);
            updaterStories = UpdatableVoid.getInstance();
        }
    }

    /**
     * Update amulet routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateAmulet(double extrp)
    {
        amulet.update(extrp);

        if (amulet.getAnimState() == AnimState.FINISHED)
        {
            glowed++;

            if (glowed < AMULET_GLOW_COUNT)
            {
                amulet.play(glow);
            }
        }
    }

    /**
     * Get alpha value.
     * 
     * @return The alpha value.
     */
    private int getAlpha()
    {
        return (int) Math.floor(alpha);
    }

    /**
     * Render fade effect.
     * 
     * @param g The graphic output.
     */
    private void renderFade(Graphic g)
    {
        final int a = getAlpha();
        if (a > 0)
        {
            g.setColor(Constant.ALPHAS_BLACK[a]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
            g.setColor(ColorRgba.BLACK);
        }
    }

    @Override
    public void load()
    {
        stories.load();
        stories.setStory(STORY_1);
        stories.setStart(STORY_1);

        amulet.load();
        amulet.prepare();
        amulet.setLocation(getWidth() / 2 + AMULET_X_OFFSET, AMULET_Y);
    }

    @Override
    public void update(double extrp)
    {
        time.update(extrp);
        updaterFade.update(extrp);
        updaterAmulet.update(extrp);
        updaterStories.update(extrp);
        info.update(extrp);

        if (device.isFiredOnce(DeviceMapping.FORCE_EXIT))
        {
            end(null);
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        if (time.isBefore(TIME_STORY4_MS))
        {
            stories.render(g);
        }
        if (alternative)
        {
            amulet.render(g);
            if (time.isAfter(TIME_STORY4_MS))
            {
                stories.render(g);
            }
        }

        rendererFade.render(g);
        info.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        super.onTerminated(hasNextSequence);

        stories.dispose();
        amulet.dispose();

        if (!hasNextSequence)
        {
            audio.stop();
            Engine.terminate();
        }
    }
}
