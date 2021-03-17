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

import com.b3dgs.lionengine.Check;

/**
 * Init configuration.
 */
public final class InitConfig
{
    /** Health max. */
    private final int healthMax;
    /** Talisment value. */
    private final int talisment;
    /** Life value. */
    private final int life;
    /** Sword level. */
    private final int sword;
    /** Amulet flag. */
    private final boolean amulet;
    /** Cheats flag. */
    private final boolean cheats;

    /**
     * Create config.
     * 
     * @param healthMax The health (between 0 and {@link Constant#STATS_MAX_HEALTH} included).
     * @param talisment The Talisment modifier (between 0 and {@link Constant#STATS_MAX_TALISMENT} included).
     * @param life The life (between 0 and {@link Constant#STATS_MAX_LIFE} included).
     * @param sword The sword level (between 0 and {@link Constant#STATS_MAX_SWORD} included).
     * @param amulet The amulet flag.
     * @param cheats The cheats flag.
     */
    public InitConfig(int healthMax, int talisment, int life, int sword, boolean amulet, boolean cheats)
    {
        super();

        Check.superiorOrEqual(healthMax, 0);
        Check.inferiorOrEqual(healthMax, Constant.STATS_MAX_HEALTH);

        Check.superiorOrEqual(talisment, 0);
        Check.inferiorOrEqual(talisment, Constant.STATS_MAX_TALISMENT);

        Check.superiorOrEqual(life, 0);
        Check.inferiorOrEqual(life, Constant.STATS_MAX_LIFE);

        Check.superiorOrEqual(sword, 0);
        Check.inferiorOrEqual(sword, Constant.STATS_MAX_SWORD);

        this.healthMax = healthMax;
        this.talisment = talisment;
        this.life = life;
        this.sword = sword;
        this.amulet = amulet;
        this.cheats = cheats;
    }

    /**
     * Get the health modifier.
     * 
     * @return The health modifier.
     */
    public int getHealthMax()
    {
        return healthMax;
    }

    /**
     * Get the talisment modifier.
     * 
     * @return The talisment modifier.
     */
    public int getTalisment()
    {
        return talisment;
    }

    /**
     * Get the life modifier.
     * 
     * @return The life modifier.
     */
    public int getLife()
    {
        return life;
    }

    /**
     * Get the sword modifier.
     * 
     * @return The sword modifier.
     */
    public int getSword()
    {
        return sword;
    }

    /**
     * Check if is amulet.
     * 
     * @return <code>true</code> if amulet, <code>false</code> else.
     */
    public boolean isAmulet()
    {
        return amulet;
    }

    /**
     * Check if is cheats.
     * 
     * @return <code>true</code> if cheats, <code>false</code> else.
     */
    public boolean isCheats()
    {
        return cheats;
    }
}
