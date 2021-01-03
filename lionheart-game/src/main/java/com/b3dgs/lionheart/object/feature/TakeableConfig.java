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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Sfx;

/**
 * Takeable configuration.
 */
public final class TakeableConfig
{
    /** Takeable node name. */
    private static final String NODE_TAKEABLE = "takeable";
    /** Effect attribute name. */
    private static final String ATT_EFFECT = "effect";
    /** Sound attribute name. */
    private static final String ATT_SFX = "sfx";
    /** Health attribute name. */
    private static final String ATT_HEALTH = "health";
    /** Talisment attribute name. */
    private static final String ATT_TALISMENT = "talisment";
    /** Life attribute name. */
    private static final String ATT_LIFE = "life";
    /** Sword attribute name. */
    private static final String ATT_SWORD = "sword";

    /**
     * Imports from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The data.
     * @throws LionEngineException If unable to read node.
     */
    public static TakeableConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        final Media effect = Medias.create(configurer.getString(ATT_EFFECT, NODE_TAKEABLE));
        final String sfx = configurer.getStringDefault(null, ATT_SFX, NODE_TAKEABLE);
        final int health = configurer.getIntegerDefault(0, ATT_HEALTH, NODE_TAKEABLE);
        final int talisment = configurer.getIntegerDefault(0, ATT_TALISMENT, NODE_TAKEABLE);
        final int life = configurer.getIntegerDefault(0, ATT_LIFE, NODE_TAKEABLE);
        final int sword = configurer.getIntegerDefault(0, ATT_SWORD, NODE_TAKEABLE);

        return new TakeableConfig(effect, sfx, health, talisment, life, sword);
    }

    /** Effect media. */
    private final Media effect;
    /** Effect sound. */
    private final Sfx sfx;
    /** Health modifier. */
    private final int health;
    /** Talisment modifier. */
    private final int talisment;
    /** Life modifier. */
    private final int life;
    /** Sword modifier. */
    private final int sword;

    /**
     * Create config.
     * 
     * @param effect The effect media (must not be <code>null</code>).
     * @param sfx The sound type (<code>null</code> if none).
     * @param health The health (between 0 and {@link Constant#STATS_MAX_HEALTH} included).
     * @param talisment The Talisment modifier (between 0 and {@link Constant#STATS_MAX_TALISMENT} included).
     * @param life The life (between 0 and {@link Constant#STATS_MAX_LIFE} included).
     * @param sword The sword level (between 0 and {@link Constant#STATS_MAX_SWORD} included).
     */
    private TakeableConfig(Media effect, String sfx, int health, int talisment, int life, int sword)
    {
        super();

        Check.superiorOrEqual(health, 0);
        Check.inferiorOrEqual(health, Constant.STATS_MAX_HEALTH);

        Check.superiorOrEqual(talisment, 0);
        Check.inferiorOrEqual(talisment, Constant.STATS_MAX_TALISMENT);

        Check.superiorOrEqual(life, 0);
        Check.inferiorOrEqual(life, Constant.STATS_MAX_LIFE);

        Check.superiorOrEqual(sword, 0);
        Check.inferiorOrEqual(sword, Constant.STATS_MAX_SWORD);

        this.effect = effect;
        this.sfx = Sfx.valueOf(sfx);
        this.health = health < 0 ? Integer.MAX_VALUE : health;
        this.talisment = talisment;
        this.life = life;
        this.sword = sword;
    }

    /**
     * Get the effect media.
     * 
     * @return The effect media.
     */
    public Media getEffect()
    {
        return effect;
    }

    /**
     * Get the effect sound.
     * 
     * @return The effect sound.
     */
    public Sfx getSfx()
    {
        return sfx;
    }

    /**
     * Get the health modifier.
     * 
     * @return The health modifier.
     */
    public int getHealth()
    {
        return health;
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
}
