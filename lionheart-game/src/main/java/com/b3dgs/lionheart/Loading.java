/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Optional;

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
    private final Image loading = Drawable.loadImage(Medias.create(Folder.SPRITE, "logo.png"));
    private final GameConfig config;
    private final int max;

    private boolean load;
    private int current = -1;
    private int alpha = 255;
    private int alphaSpeed = 10;

    /**
     * Constructor.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @param config The config reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public Loading(Context context, GameConfig config)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context));

        timing.start();
        this.config = config;
        max = Settings.getInstance().getRasterCheck() ? backgrounds.length - 1 : current;

        setSystemCursorVisible(false);
        Util.setFilter(this);
    }

    /**
     * Load next scene.
     */
    private void loadNext()
    {
        if (Settings.getInstance().getFlagDebug())
        {
            load(Scene.class,
                 config.with(new InitConfig(Medias.create(Folder.STAGE, Folder.STORY, Folder.ORIGINAL, "stage1.xml"),
                                            Constant.STATS_MAX_HEART - 1,
                                            Constant.STATS_MAX_TALISMENT - 1,
                                            Constant.STATS_MAX_LIFE - 1,
                                            0,
                                            true,
                                            Constant.CREDITS,
                                            Difficulty.NORMAL,
                                            true,
                                            Optional.empty())));
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

        loading.load();
        loading.prepare();
        loading.setOrigin(Origin.MIDDLE);
        loading.setLocation(getWidth() / 2.0, getHeight() / 2.0);
    }

    @Override
    public void update(double extrp)
    {
        timing.update(extrp);

        if (!load && timing.elapsed(com.b3dgs.lionengine.Constant.THOUSAND / 2))
        {
            alpha -= alphaSpeed;
            if (alpha < 0)
            {
                alpha = 0;
                load = true;
                timing.restart();
                loadNext();
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
            alpha += alphaSpeed;

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

        loading.render(g);

        if (current < max)
        {
            progress.render(g);
        }

        if (alpha < 256)
        {
            g.setColor(Constant.ALPHAS_BLACK[alpha]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
            g.setColor(ColorRgba.BLACK);
        }
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        loading.dispose();
    }
}
