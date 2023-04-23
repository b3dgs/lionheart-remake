/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.intro.Intro;
import com.b3dgs.lionheart.landscape.BackgroundType;
import com.b3dgs.lionheart.menu.Menu;

/**
 * Loading screen.
 */
public final class Loading extends Sequence
{
    private final Timing timing = new Timing();
    private final Progress progress = new Progress(getWidth(), getHeight());
    private final BackgroundType[] backgrounds = BackgroundType.values();
    private final Image image = Drawable.loadImage(Medias.create(Folder.SPRITE, "logo.png"));
    private final GameConfig config;
    private final boolean direct;
    private final int max;

    private boolean load;
    private int current = -1;
    private double alpha = Settings.getInstance().isFlagDebug() ? 0 : 255;
    private int alphaSpeed = Settings.getInstance().isFlagDebug() ? 256 : 10;

    /**
     * Constructor.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @param config The config reference (must not be <code>null</code>).
     * @param direct <code>true</code> for direct start, <code>false</code> to show menu.
     * @throws LionEngineException If invalid argument.
     */
    public Loading(Context context, GameConfig config, Boolean direct)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context), Util.getLoop(context.getConfig().getOutput()));

        timing.start();
        this.config = config;
        this.direct = direct.booleanValue();
        max = Settings.getInstance().isRasterCheck() ? backgrounds.length - 1 : current;

        setSystemCursorVisible(false);
        Util.setFilter(this, context, Util.getResolution(Constant.RESOLUTION, context), 2);
    }

    /**
     * Load next scene.
     */
    private void loadNext()
    {
        if (Settings.getInstance().isFlagDebug())
        {
            if (config.getInit() == null)
            {
                load(Scene.class,
                     config.with(new InitConfig(Stage.STAGE1,
                                                Constant.STATS_MAX_HEART - 1,
                                                Constant.STATS_MAX_LIFE - 1,
                                                Difficulty.NORMAL)));
            }
            else
            {
                load(Scene.class, config);
            }
        }
        else if (direct)
        {
            if (config.getType().is(GameType.STORY))
            {
                load(Intro.class, config);
            }
            else
            {
                load(Scene.class, config);
            }
        }
        else
        {
            load(Menu.class, config);
        }
    }

    @Override
    public void load()
    {
        Sfx.cache(Sfx.MENU_SELECT);
        Sfx.cacheStart();

        image.load();
        image.prepare();
        image.setOrigin(Origin.MIDDLE);
        image.setLocation(getWidth() / 2.0, getHeight() / 2.0);
    }

    @Override
    public void update(double extrp)
    {
        timing.update(extrp);

        if (!load && timing.elapsed(com.b3dgs.lionengine.Constant.THOUSAND / 2))
        {
            alpha -= alphaSpeed * extrp;
            if (alpha < 0)
            {
                alpha = 0;
                load = true;
                loadNext();
                timing.restart();
            }
        }
        else if (current < max)
        {
            if (current > -1)
            {
                Util.run(backgrounds[current]);
                final int percent = (current + 2) * 100 / max;
                progress.setPercent(percent);
            }
            current = UtilMath.clamp(current + 1, 0, max);
        }
        else if (timing.elapsed(com.b3dgs.lionengine.Constant.THOUSAND))
        {
            alpha += alphaSpeed * extrp;

            if (alpha > 255)
            {
                alpha = 255;
                alphaSpeed = 0;
                timing.addAction(this::end, 0);
            }
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        image.render(g);

        if (current < max)
        {
            progress.render(g);
        }

        if (alpha < 256)
        {
            g.setColor(Constant.ALPHAS_BLACK[(int) Math.floor(alpha)]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
            g.setColor(ColorRgba.BLACK);
        }
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        super.onTerminated(hasNextSequence);

        image.dispose();
    }
}
