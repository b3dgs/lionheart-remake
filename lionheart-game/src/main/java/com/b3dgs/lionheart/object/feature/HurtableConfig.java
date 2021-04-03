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

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

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
    /** Frame attribute name. */
    private static final String ATT_FRAME = "frame";
    /** Effect attribute name (can be <code>null</code>). */
    private static final String ATT_EFFECT = "effect";
    /** Move backward attribute name. */
    private static final String ATT_BACKWARD = "backward";
    /** Persist on death attribute name. */
    private static final String ATT_PERSIST = "persist";
    /** Fall before death attribute name. */
    private static final String ATT_FALL = "fall";
    /** Hurt sfx attribute name. */
    private static final String ATT_SFX = "sfx";

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
            final OptionalInt frame = configurer.getIntegerOptional(ATT_FRAME, NODE_HURTABLE);
            final String effect = configurer.getStringDefault(null, ATT_EFFECT, NODE_HURTABLE);
            final OptionalDouble backward = configurer.getDoubleOptional(ATT_BACKWARD, NODE_HURTABLE);
            final boolean persist = configurer.getBooleanDefault(false, ATT_PERSIST, NODE_HURTABLE);
            final boolean fall = configurer.getBooleanDefault(false, ATT_FALL, NODE_HURTABLE);
            final Optional<String> sfx = configurer.getStringOptional(ATT_SFX, NODE_HURTABLE);

            return new HurtableConfig(frame, effect, backward, persist, fall, sfx);
        }
        return new HurtableConfig(OptionalInt.empty(), null, OptionalDouble.empty(), false, false, Optional.empty());
    }

    /** Frame hurt. */
    private final OptionalInt frame;
    /** Effect media. */
    private final Media effect;
    /** Move backward force. */
    private final OptionalDouble backward;
    /** Persist on death flag. */
    private final boolean persist;
    /** Fall before death flag. */
    private final boolean fall;
    /** Effect media. */
    private final Optional<String> sfx;

    /**
     * Create config.
     * 
     * @param frame The hurt frame.
     * @param effect The effect media (can be <code>null</code>).
     * @param backward The move backward force.
     * @param persist The persist flag.
     * @param fall The fall before death flag.
     * @param sfx The media sfx.
     */
    private HurtableConfig(OptionalInt frame,
                           String effect,
                           OptionalDouble backward,
                           boolean persist,
                           boolean fall,
                           Optional<String> sfx)
    {
        super();

        this.frame = frame;
        if (effect != null)
        {
            this.effect = Medias.create(Folder.EFFECT, effect + Factory.FILE_DATA_DOT_EXTENSION);
        }
        else
        {
            this.effect = null;
        }
        this.backward = backward;
        this.persist = persist;
        this.fall = fall;
        this.sfx = sfx;
    }

    /**
     * Get the hurt frame.
     * 
     * @return The hurt frame.
     */
    public OptionalInt getFrame()
    {
        return frame;
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
     * Get the backward force.
     * 
     * @return The backward force.
     */
    public OptionalDouble getBackward()
    {
        return backward;
    }

    /**
     * Get the persist flag.
     * 
     * @return The persist flag.
     */
    public boolean hasPersist()
    {
        return persist;
    }

    /**
     * Get the fall flag.
     * 
     * @return The fall flag.
     */
    public boolean hasFall()
    {
        return fall;
    }

    /**
     * Get the sfx media.
     * 
     * @return The sfx media, <code>null</code> if not defined.
     */
    public Optional<String> getSfx()
    {
        return sfx;
    }
}
