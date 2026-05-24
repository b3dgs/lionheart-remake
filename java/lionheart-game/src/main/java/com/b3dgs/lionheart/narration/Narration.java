/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.narration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.RenderableVoid;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.FadeSide;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Time;
import com.b3dgs.lionheart.Util;

/**
 * Narration base implementation.
 */
public abstract class Narration extends Sequence implements NarrationFactory
{
    /** Current time. */
    protected final Time time = new Time(getRate());
    /** Device controller reference. */
    protected final DeviceController device;
    /** Device cursor. */
    protected final DeviceController deviceCursor;
    /** Current audio. */
    protected final Audio audio;
    private final Deque<Action> toAdd = new ArrayDeque<>();
    private final Deque<Action> toRemove = new ArrayDeque<>();
    private final List<Action> currents = new ArrayList<>();
    private final AppInfo info;

    private double alpha;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param resolution The resolution reference.
     * @param audio The audio reference.
     */
    protected Narration(Context context, Resolution resolution, Audio audio)
    {
        super(context, resolution, Util.getLoop(context.getConfig().output()));

        this.audio = audio;
        audio.setVolume(Settings.getInstance().getVolumeMusic());

        final Services services = new Services();
        services.add(context);
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_DEFAULT)));
        device.setVisible(false);

        final Media mediaCursor = Medias.create(Constant.INPUT_FILE_CURSOR);
        deviceCursor = DeviceControllerConfig.create(services, mediaCursor);

        info = new AppInfo(this::getFps, services);

        setSystemCursorVisible(false);
        Util.setFilter(this, context, resolution, 2);
    }

    @Override
    public void setSource(Resolution resolution)
    {
        super.setSource(resolution);

        Util.setFilter(this, context, resolution, 2);
    }

    @Override
    public void add(int startMs, FadeSide side, double speed)
    {
        toAdd.add(new Action(startMs, -1, new Fade(side, speed)));
    }

    @Override
    public void add(int startMs, com.b3dgs.lionengine.game.Action action)
    {
        toAdd.add(new Action(startMs, startMs, new Step()
        {
            @Override
            public void init()
            {
                action.execute();
            }
        }));
    }

    @Override
    public void add(int startMs, int endMs, Step step)
    {
        toAdd.add(new Action(startMs, endMs, step));
    }

    @Override
    public void add(int startMs, int endMs, Updatable updater)
    {
        toAdd.add(new Action(startMs, endMs, new StepDelegate(updater, RenderableVoid.getInstance())));
    }

    @Override
    public void add(int startMs, int endMs, Renderable renderer)
    {
        toAdd.add(new Action(startMs, endMs, new StepDelegate(UpdatableVoid.getInstance(), renderer)));
    }

    @Override
    public void add(int startMs, int endMs, Updatable updater, Renderable renderer)
    {
        toAdd.add(new Action(startMs, endMs, new StepDelegate(updater, renderer)));
    }

    @Override
    public void add(int timeStartMs, int timeEndMs, SpriteFont font, int x1, int y1, int x2, String... texts)
    {
        add(timeStartMs, timeEndMs, font, x1, y1, x2, Align.LEFT, texts);
    }

    @Override
    public void add(int timeStartMs,
                    int timeEndMs,
                    SpriteFont font,
                    int x1,
                    int y1,
                    int x2,
                    Align align,
                    String... texts)
    {
        final TextData text = new TextData(time, font, timeStartMs, timeEndMs, x1, y1, x2, align, texts);
        add(text.getStartMs(), text.getEndMs() + 3000, text, text);
    }

    @Override
    public void add(int startMs, Updatable updater, Renderable renderer)
    {
        add(startMs, startMs, updater, renderer);
    }

    /**
     * Get wide depending on factor.
     * 
     * @param context The context reference.
     * @return The wide factor.
     */
    protected double getWideFactor(Context context)
    {
        final Resolution output = context.getConfig().output();
        final double factor = getHeight() / (double) output.height();
        return Math.floor(output.width() * factor) / Constant.RESOLUTION.width();
    }

    /**
     * Get alpha value.
     * 
     * @return The alpha value.
     */
    protected int getAlpha()
    {
        return (int) Math.floor(alpha);
    }

    /**
     * Render fade.
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
    protected void onLoaded(double extrp, Graphic g)
    {
        super.onLoaded(extrp, g);

        audio.play();
        time.start();
    }

    @Override
    public void update(double extrp)
    {
        time.update(extrp);
        device.update(extrp);
        deviceCursor.update(extrp);
        info.update(extrp);

        checkNextActions();

        final int n = currents.size();
        for (int i = 0; i < n; i++)
        {
            final Action action = currents.get(i);
            action.update(extrp);

            if (action.endMs() > 0 && time.isAfter(action.endMs()) || action.isDone())
            {
                toRemove.add(action);
            }
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        final int n = currents.size();
        for (int i = 0; i < n; i++)
        {
            final Action action = currents.get(i);
            action.render(g);
        }
        renderFade(g);
        checkDoneActions();
        info.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        super.onTerminated(hasNextSequence);

        if (!hasNextSequence)
        {
            audio.stop();
            Engine.terminate();
        }
    }

    private void checkNextActions()
    {
        do
        {
            final Action next = toAdd.peekFirst();
            if (next != null)
            {
                if (time.isBefore(next.startMs))
                {
                    break;
                }
                if (time.isAfter(next.startMs))
                {
                    final Action action = toAdd.pollFirst();
                    currents.add(action);
                    action.init();
                }
            }
        }
        while (!toAdd.isEmpty());
    }

    private void checkDoneActions()
    {
        Action remove = null;
        while ((remove = toRemove.poll()) != null)
        {
            currents.remove(remove);
        }
    }

    private static class StepDelegate implements Step
    {
        private final Updatable updater;
        private final Renderable renderer;

        public StepDelegate(Updatable updater, Renderable renderer)
        {
            this.updater = updater;
            this.renderer = renderer;
        }

        @Override
        public void update(double extrp)
        {
            updater.update(extrp);
        }

        @Override
        public void render(Graphic g)
        {
            renderer.render(g);
        }
    }

    private record Action(int startMs, int endMs, Step step) implements Step
    {
        @Override
        public void init()
        {
            step.init();
        }

        @Override
        public void update(double extrp)
        {
            step.update(extrp);
        }

        @Override
        public void render(Graphic g)
        {
            step.render(g);
        }
    }

    /**
     * Fade wrapper.
     */
    protected final class Fade implements Step
    {
        private final FadeSide side;
        private final double speed;

        private double a;

        /**
         * Create fade.
         * 
         * @param side The side fade.
         * @param speed The speed fade.
         */
        public Fade(FadeSide side, double speed)
        {
            this.side = side;
            this.speed = speed;
        }

        @Override
        public void init()
        {
            a = side == FadeSide.IN ? 255 : 0;
        }

        @Override
        public void update(double extrp)
        {
            final double s = side == FadeSide.IN ? -speed : speed;
            a = UtilMath.clamp(a + s * extrp, 0.0, 255.0);
            alpha = a;
        }

        @Override
        public boolean isDone()
        {
            final int v = (int) a;
            return v == 0 && side == FadeSide.IN || v == 255 && side == FadeSide.OUT;
        }
    }
}
