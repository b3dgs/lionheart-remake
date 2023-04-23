/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.game.Configurer;

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
    /** Effect offset X name. */
    private static final String ATT_OFFSET_X = "offsetX";
    /** Move backward attribute name. */
    private static final String ATT_BACKWARD = "backward";
    /** Interrupt movement attribute name. */
    private static final String ATT_INTERRUPT = "interrupt";
    /** Persist on death attribute name. */
    private static final String ATT_PERSIST = "persist";
    /** Fall before death attribute name. */
    private static final String ATT_FALL = "fall";
    /** Hurt sfx attribute name. */
    private static final String ATT_SFX = "sfx";
    /** Boss attribute name. */
    private static final String ATT_BOSS = "boss";

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
            final Optional<Media> effect = configurer.getMediaOptional(ATT_EFFECT, NODE_HURTABLE);
            final int offsetX = configurer.getInteger(0, ATT_OFFSET_X, NODE_HURTABLE);
            final OptionalDouble backward = configurer.getDoubleOptional(ATT_BACKWARD, NODE_HURTABLE);
            final boolean interrupt = configurer.getBoolean(true, ATT_INTERRUPT, NODE_HURTABLE);
            final boolean persist = configurer.getBoolean(false, ATT_PERSIST, NODE_HURTABLE);
            final boolean fall = configurer.getBoolean(false, ATT_FALL, NODE_HURTABLE);
            final Optional<String> sfx = configurer.getStringOptional(ATT_SFX, NODE_HURTABLE);
            final boolean boss = configurer.getBoolean(false, ATT_BOSS, NODE_HURTABLE);

            return new HurtableConfig(frame, effect, offsetX, backward, interrupt, persist, fall, sfx, boss);
        }
        return new HurtableConfig(OptionalInt.empty(),
                                  Optional.empty(),
                                  0,
                                  OptionalDouble.empty(),
                                  true,
                                  false,
                                  false,
                                  Optional.empty(),
                                  false);
    }

    /** Frame hurt. */
    private final OptionalInt frame;
    /** Effect media. */
    private final Optional<Media> effect;
    /** Offset X. */
    private final int offsetX;
    /** Move backward force. */
    private final OptionalDouble backward;
    /** Interrupt movement flag. */
    private final boolean interrupt;
    /** Persist on death flag. */
    private final boolean persist;
    /** Fall before death flag. */
    private final boolean fall;
    /** Effect media. */
    private final Optional<String> sfx;
    /** Boss flag. */
    private final boolean boss;

    /**
     * Create config.
     * 
     * @param frame The hurt frame.
     * @param effect The effect media (can be <code>null</code>).
     * @param offsetX The horizontal offset.
     * @param backward The move backward force.
     * @param interrupt The interrupt flag.
     * @param persist The persist flag.
     * @param fall The fall before death flag.
     * @param sfx The media sfx.
     * @param boss The boss flag.
     */
    private HurtableConfig(OptionalInt frame,
                           Optional<Media> effect,
                           int offsetX,
                           OptionalDouble backward,
                           boolean interrupt,
                           boolean persist,
                           boolean fall,
                           Optional<String> sfx,
                           boolean boss)
    {
        super();

        this.frame = frame;
        this.effect = effect;
        this.offsetX = offsetX;
        this.backward = backward;
        this.interrupt = interrupt;
        this.persist = persist;
        this.fall = fall;
        this.sfx = sfx;
        this.boss = boss;
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
    public Optional<Media> getEffect()
    {
        return effect;
    }

    /**
     * Get the horizontal offset.
     * 
     * @return The horizontal offset.
     */
    public int getOffsetX()
    {
        return offsetX;
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
     * Get the interrupt flag.
     * 
     * @return The interrupt flag.
     */
    public boolean hasInterrupt()
    {
        return interrupt;
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

    /**
     * Get the boss flag.
     * 
     * @return The boss flag.
     */
    public boolean hasBoss()
    {
        return boss;
    }
}
