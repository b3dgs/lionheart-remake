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
    private final GameConfig config;

    /**
     * Constructor.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @param config The game config (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public SceneBlack(Context context, GameConfig config)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context), new LoopUnlocked());

        this.config = config;

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
        final StageConfig stage = StageConfig.imports(new Configurer(config.getInit().getStage()));
        if (stage.getPic().isPresent() && !config.getInit().getSpawn().isPresent())
        {
            end(ScenePicture.class, config, stage.getPic().get(), stage.getText().get());
        }
        else
        {
            end(Scene.class, config);
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());
    }
}
