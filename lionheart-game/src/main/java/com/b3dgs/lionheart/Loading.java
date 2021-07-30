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
package com.b3dgs.lionheart;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.graphic.engine.LoopUnlocked;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.intro.Intro;
import com.b3dgs.lionheart.landscape.BackgroundType;

/**
 * Loading screen.
 */
public final class Loading extends Sequence
{
    private static final int MIN_HEIGHT = 180;
    private static final int MAX_WIDTH = 320;
    private static final int MARGIN_WIDTH = 0;

    private final Progress progress = new Progress(getWidth(), getHeight());
    private final BackgroundType[] backgrounds = BackgroundType.values();
    private final Image loading = Drawable.loadImage(Medias.create(Folder.SPRITE, "logo.png"));
    private final int max;

    private int current = -1;

    /**
     * Constructor.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public Loading(Context context)
    {
        super(context, Util.getResolution(context, MIN_HEIGHT, MAX_WIDTH, MARGIN_WIDTH), new LoopUnlocked());

        max = Settings.getInstance().getRasterCheck() ? backgrounds.length - 1 : current;

        setSystemCursorVisible(false);
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
        if (current < max)
        {
            if (current > -1)
            {
                Util.run(backgrounds[current]);
                final int percent = (current + 2) * 100 / max;
                progress.setPercent(percent);
            }
            current = UtilMath.clamp(current + 1, 0, max);
        }
        else
        {
            if (Constant.DEBUG)
            {
                end(Scene.class, Stage.STAGE1, Constant.INIT_DEBUG);
            }
            else
            {
                end(Intro.class);
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
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        loading.dispose();
    }
}
