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
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Hurtable configuration.
 */
public final class HurtableConfig
{
    /** Hurtable node name. */
    private static final String NODE_HURTABLE = "hurtable";
    /** Effect attribute name (can be <code>null</code>). */
    private static final String ATT_EFFECT = "effect";
    /** Move backward attribute name. */
    private static final String ATT_BACKWARD = "backward";

    /**
     * Imports from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The data.
     * @throws LionEngineException If unable to read node.
     */
    public static HurtableConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        if (configurer.hasNode(NODE_HURTABLE))
        {
            final String effect = configurer.getStringDefault(null, ATT_EFFECT, NODE_HURTABLE);
            final boolean backward = configurer.getBooleanDefault(false, ATT_BACKWARD, NODE_HURTABLE);

            return new HurtableConfig(effect, backward);
        }
        return new HurtableConfig(null, false);
    }

    /** Effect media. */
    private final Media effect;
    /** Move backward flag. */
    private final boolean backward;

    /**
     * Create config.
     * 
     * @param effect The effect media (can be <code>null</code>).
     * @param backward The move backward flag.
     */
    private HurtableConfig(String effect, boolean backward)
    {
        super();

        if (effect != null)
        {
            this.effect = Medias.create(Folder.EFFECTS, effect + Factory.FILE_DATA_DOT_EXTENSION);
        }
        else
        {
            this.effect = null;
        }
        this.backward = backward;
    }

    /**
     * Get the effect media.
     * 
     * @return The effect media, <code>null</code> if not defined.
     */
    public Media getEffect()
    {
        return effect;
    }

    /**
     * Get the backward flag.
     * 
     * @return The backward flag.
     */
    public boolean hasBackward()
    {
        return backward;
    }
}
