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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Takeable configuration.
 */
public final class TakeableConfig
{
    /** Takeable node name. */
    private static final String NODE_TAKEABLE = "takeable";
    /** Effect attribute name. */
    private static final String ATT_EFFECT = "effect";
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
    public static TakeableConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        final Media effect = Medias.create(configurer.getString(ATT_EFFECT, NODE_TAKEABLE));
        final int health = configurer.getIntegerDefault(0, ATT_HEALTH, NODE_TAKEABLE);
        final int life = configurer.getIntegerDefault(0, ATT_LIFE, NODE_TAKEABLE);

        return new TakeableConfig(effect, health, life);
    }

    /** Effect media. */
    private final Media effect;
    /** Health modifier. */
    private final int health;
    /** Life modifier. */
    private final int life;

    /**
     * Create config.
     * 
     * @param effect The effect media.
     * @param health The health modifier.
     * @param life The life modifier.
     */
    private TakeableConfig(Media effect, int health, int life)
    {
        super();

        this.effect = effect;
        this.health = health < 0 ? Integer.MAX_VALUE : health;
        this.life = life;
    }

    /**
     * Get the effect media.
     * @return The effect media.
     */
    public Media getEffect()
    {
        return effect;
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
     * Get the life modifier.
     * 
     * @return The life modifier.
     */
    public int getLife()
    {
        return life;
    }
}
