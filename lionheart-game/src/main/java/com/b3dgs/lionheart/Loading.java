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
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.intro.Intro;

/**
 * Loading screen.
 */
public final class Loading extends Sequence
{
    private static final int MIN_HEIGHT = 180;
    private static final int MAX_WIDTH = 320;
    private static final int MARGIN_WIDTH = 0;

    private final Image loading = Drawable.loadImage(Medias.create(Folder.SPRITE, "logo.png"));

    /**
     * Constructor.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public Loading(Context context)
    {
        super(context, Util.getResolution(context, MIN_HEIGHT, MAX_WIDTH, MARGIN_WIDTH));

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
        if (Constant.DEBUG)
        {
            end(Scene.class, StageHard.STAGE1, Constant.INIT_DEBUG);
        }
        else
        {
            end(Intro.class);
        }
    }

    @Override
    public void render(Graphic g)
    {
        loading.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        loading.dispose();
    }
}
