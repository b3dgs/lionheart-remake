/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionheart.Constant;

/**
 * Stats configuration.
 */
public final class StatsConfig
{
    /** Stats node name. */
    private static final String NODE_STATS = "stats";
    /** Health attribute name. */
    private static final String ATT_HEALTH = "health";
    /** Life attribute name. */
    private static final String ATT_LIFE = "life";

    /**
     * Imports from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The data.
     * @throws LionEngineException If unable to read node.
     */
    public static StatsConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        final int health = configurer.getIntegerDefault(0, ATT_HEALTH, NODE_STATS);
        final int life = configurer.getIntegerDefault(0, ATT_LIFE, NODE_STATS);

        return new StatsConfig(health, life);
    }

    /** Health. */
    private final int health;
    /** Life. */
    private final int life;

    /**
     * Create config.
     * 
     * @param health The health (between 0 and {@link Constant#STATS_MAX_HEALTH} included).
     * @param life The life (between 0 and {@link Constant#STATS_MAX_LIFE} included).
     * @throws LionEngineException If invalid arguments.
     */
    private StatsConfig(int health, int life)
    {
        super();

        Check.superiorOrEqual(health, 0);
        Check.inferiorOrEqual(health, Constant.STATS_MAX_HEALTH);

        Check.superiorOrEqual(life, 0);
        Check.inferiorOrEqual(life, Constant.STATS_MAX_LIFE);

        this.health = health < 0 ? Integer.MAX_VALUE : health;
        this.life = life;
    }

    /**
     * Get the health.
     * 
     * @return The health.
     */
    public int getHealth()
    {
        return health;
    }

    /**
     * Get the life.
     * 
     * @return The life.
     */
    public int getLife()
    {
        return life;
    }
}
