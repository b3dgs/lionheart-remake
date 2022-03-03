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

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.LoopUnlocked;
import com.b3dgs.lionengine.graphic.engine.Sequence;

/**
 * Black screen.
 */
public final class SceneBlack extends Sequence
{
    private final InitConfig init;

    /**
     * Constructor.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @param init The initial config.
     * @throws LionEngineException If invalid argument.
     */
    public SceneBlack(Context context, InitConfig init)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context), new LoopUnlocked());

        this.init = init;

        setSystemCursorVisible(false);
    }

    @Override
    public void load()
    {
        // Nothing to load
    }

    @Override
    public void update(double extrp)
    {
        final StageConfig config = StageConfig.imports(new Configurer(init.getStage()));
        if (config.getPic().isPresent() && !init.getSpawn().isPresent())
        {
            end(ScenePicture.class, init, config.getPic().get(), config.getText().get());
        }
        else
        {
            end(Scene.class, init);
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());
    }
}
