/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;

/**
 * Stats feature implementation.
 */
@FeatureInterface
public final class Stats extends FeatureModel
{
    private final Alterable life;
    private final Alterable health;

    /**
     * Create stats.
     * 
     * @param setup The setup reference.
     */
    public Stats(SetupSurfaceRastered setup)
    {
        super();

        final StatsConfig config = StatsConfig.imports(setup);
        life = new Alterable(config.getLife());
        health = new Alterable(config.getHealth());
        health.fill();
    }

    /**
     * Apply config.
     * 
     * @param config The config to apply.
     */
    public void apply(TakeableConfig config)
    {
        life.increase(config.getLife());
        health.increase(config.getHealth());
    }

    /**
     * Get the current life.
     * 
     * @return The current life.
     */
    public int getLife()
    {
        return life.getCurrent();
    }

    /**
     * Get the current health.
     * 
     * @return The current health.
     */
    public int getHealth()
    {
        return health.getCurrent();
    }
}
