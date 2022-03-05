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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.geom.Coord;

/**
 * Init configuration.
 */
public final class InitConfig
{
    /** Stage. */
    private final Media stage;
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
    /** Remaining credits. */
    private final int credits;
    /** Difficulty. */
    private final Difficulty difficulty;
    /** Cheats flag. */
    private final boolean cheats;
    /** Spawn tile. */
    private final Optional<Coord> spawn;

    /**
     * Create first config.
     * 
     * @param stage The stage to play.
     * @param healthMax The health (between 0 and {@link Constant#STATS_MAX_HEALTH} included).
     * @param life The life (between 0 and {@link Constant#STATS_MAX_LIFE} included).
     * @param difficulty The difficulty.
     */
    public InitConfig(Media stage, int healthMax, int life, Difficulty difficulty)
    {
        this(stage, healthMax, 0, life, 0, false, Constant.CREDITS, difficulty, false, Optional.empty());
    }

    /**
     * Create inherited config.
     * 
     * @param stage The stage to play.
     * @param healthMax The health (between 0 and {@link Constant#STATS_MAX_HEALTH} included).
     * @param talisment The Talisment modifier (between 0 and {@link Constant#STATS_MAX_TALISMENT} included).
     * @param life The life (between 0 and {@link Constant#STATS_MAX_LIFE} included).
     * @param sword The sword level (between 0 and {@link Constant#STATS_MAX_SWORD} excluded).
     * @param amulet The amulet flag.
     * @param credits The credits value.
     * @param difficulty The difficulty.
     * @param cheats The cheats flag.
     * @param spawn The spawn tile.
     */
    public InitConfig(Media stage,
                      int healthMax,
                      int talisment,
                      int life,
                      int sword,
                      boolean amulet,
                      int credits,
                      Difficulty difficulty,
                      boolean cheats,
                      Optional<Coord> spawn)
    {
        super();

        Check.notNull(stage);

        Check.superiorOrEqual(healthMax, 0);
        Check.inferiorOrEqual(healthMax, Constant.STATS_MAX_HEALTH);

        Check.superiorOrEqual(talisment, 0);
        Check.inferiorOrEqual(talisment, Constant.STATS_MAX_TALISMENT);

        Check.superiorOrEqual(life, 0);
        Check.inferiorOrEqual(life, Constant.STATS_MAX_LIFE);

        Check.superiorOrEqual(sword, 0);
        Check.inferiorOrEqual(sword, Constant.STATS_MAX_SWORD);

        Check.superiorOrEqual(credits, 0);

        this.stage = stage;
        this.healthMax = healthMax;
        this.talisment = talisment;
        this.life = life;
        this.sword = sword;
        this.credits = credits;
        this.amulet = amulet;
        this.difficulty = difficulty;
        this.cheats = cheats;
        this.spawn = spawn;
    }

    /**
     * Get the stage.
     * 
     * @return The associated stage.
     */
    public Media getStage()
    {
        return stage;
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
     * Get the credits modifier.
     * 
     * @return The credits modifier.
     */
    public int getCredits()
    {
        return credits;
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
     * Get the difficulty.
     * 
     * @return The difficulty.
     */
    public Difficulty getDifficulty()
    {
        return difficulty;
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

    /**
     * Get spawn tile.
     * 
     * @return The spawn tile.
     */
    public Optional<Coord> getSpawn()
    {
        return spawn;
    }
}
