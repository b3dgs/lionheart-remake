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
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Rotating Platform configuration.
 */
public final class RotatingConfig
{
    /** Rotating node name. */
    public static final String NODE_ROTATING = "rotating";
    /** Extremity attribute name. */
    public static final String ATT_EXTREMITY = "extremity";
    /** Ring attribute name. */
    public static final String ATT_RING = "ring";
    /** Length attribute name. */
    public static final String ATT_LENGTH = "length";
    /** Speed attribute name. */
    public static final String ATT_SPEED = "speed";

    /**
     * Imports the config from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static RotatingConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getChild(NODE_ROTATING));
    }

    /**
     * Imports the config from root.
     * 
     * @param root The node reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static RotatingConfig imports(XmlReader root)
    {
        Check.notNull(root);

        return new RotatingConfig(root);
    }

    /** Extremity reference. */
    private final String extremity;
    /** Ring reference. */
    private final String ring;
    /** Length value. */
    private final int length;
    /** Speed value. */
    private final double speed;

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be null).
     */
    private RotatingConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        extremity = root.readString(ATT_EXTREMITY);
        ring = root.readString(ATT_RING);
        length = root.readInteger(4, ATT_LENGTH);
        speed = root.readDouble(1.0, ATT_SPEED);
    }

    /**
     * Get the extremity.
     * 
     * @return The extremity.
     */
    public String getExtremity()
    {
        return extremity;
    }

    /**
     * Get the ring.
     * 
     * @return The ring.
     */
    public String getRing()
    {
        return ring;
    }

    /**
     * Get the length.
     * 
     * @return The length.
     */
    public int getLength()
    {
        return length;
    }

    /**
     * Get the speed.
     * 
     * @return The speed.
     */
    public double getSpeed()
    {
        return speed;
    }
}
