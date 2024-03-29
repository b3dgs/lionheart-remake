/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.landscape;

import java.util.OptionalDouble;
import java.util.OptionalInt;

import com.b3dgs.lionengine.AttributesReader;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Foreground configuration.
 */
public final class ForegroundConfig
{
    /** Entity node name. */
    public static final String NODE_FOREGROUND = "foreground";
    /** Foreground type attribute name. */
    public static final String ATT_FOREGROUND_TYPE = "type";
    /** Water depth attribute name. */
    public static final String ATT_WATER_DEPTH = "waterDepth";
    /** Water depth speed attribute name. */
    public static final String ATT_WATER_DEPTH_SPEED = "waterDepthSpeed";
    /** Water offset attribute name. */
    public static final String ATT_WATER_OFFSET = "waterOffset";
    /** Water speed attribute name. */
    public static final String ATT_WATER_SPEED = "waterSpeed";
    /** Water effect attribute name. */
    public static final String ATT_WATER_EFFECT = "waterEffect";
    /** Water raise attribute name. */
    public static final String ATT_WATER_RAISE = "raise";
    /** Width limit. */
    public static final String ATT_WIDTH_MAX = "widthMax";

    /**
     * Imports the config from configurer.
     * 
     * @param root The root reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static ForegroundConfig imports(AttributesReader root)
    {
        Check.notNull(root);

        return new ForegroundConfig(root);
    }

    /** Foreground type. */
    private final ForegroundType type;
    /** Water depth value. */
    private final OptionalInt waterDepth;
    /** Water offset value. */
    private final OptionalInt waterOffset;
    /** Water depth speed. */
    private final OptionalDouble waterDepthSpeed;
    /** Water speed value. */
    private final OptionalDouble waterSpeed;
    /** Water effect value. */
    private final boolean waterEffect;
    /** Water raise flag. */
    private final int waterRaise;
    /** Width max value. */
    private final OptionalInt widthMax;

    /**
     * Create config.
     * 
     * @param root The configurer reference (must not be <code>null</code>).
     * @throws LionEngineException If unable to read node.
     */
    private ForegroundConfig(AttributesReader root)
    {
        super();

        Check.notNull(root);

        type = root.getEnum(ForegroundType.class, ATT_FOREGROUND_TYPE, NODE_FOREGROUND);
        waterDepth = root.getIntegerOptional(ATT_WATER_DEPTH, NODE_FOREGROUND);
        waterOffset = root.getIntegerOptional(ATT_WATER_OFFSET, NODE_FOREGROUND);
        waterDepthSpeed = root.getDoubleOptional(ATT_WATER_DEPTH_SPEED, NODE_FOREGROUND);
        waterSpeed = root.getDoubleOptional(ATT_WATER_SPEED, NODE_FOREGROUND);
        waterEffect = root.getBoolean(true, ATT_WATER_EFFECT, NODE_FOREGROUND);
        waterRaise = root.getInteger(0, ATT_WATER_RAISE, NODE_FOREGROUND);
        widthMax = root.getIntegerOptional(ATT_WIDTH_MAX, NODE_FOREGROUND);
    }

    /**
     * Get the foreground type.
     * 
     * @return The foreground type.
     */
    public ForegroundType getType()
    {
        return type;
    }

    /**
     * Get the water depth.
     * 
     * @return The water depth.
     */
    public OptionalInt getWaterDepth()
    {
        return waterDepth;
    }

    /**
     * Get the water depth speed.
     * 
     * @return The water depth speed.
     */
    public OptionalDouble getWaterDepthSpeed()
    {
        return waterDepthSpeed;
    }

    /**
     * Get the water offset.
     * 
     * @return The water offset.
     */
    public OptionalInt getWaterOffset()
    {
        return waterOffset;
    }

    /**
     * Get the water speed.
     * 
     * @return The water speed.
     */
    public OptionalDouble getWaterSpeed()
    {
        return waterSpeed;
    }

    /**
     * Get the water effect.
     * 
     * @return The water effect.
     */
    public boolean getWaterEffect()
    {
        return waterEffect;
    }

    /**
     * Get the water raise value.
     * 
     * @return The water raise value.
     */
    public int getWaterRaise()
    {
        return waterRaise;
    }

    /**
     * Get the width max value.
     * 
     * @return The width max value.
     */
    public OptionalInt getWidthMax()
    {
        return widthMax;
    }

}
