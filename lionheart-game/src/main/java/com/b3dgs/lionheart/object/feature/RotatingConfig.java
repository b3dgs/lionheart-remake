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
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Rotating Platform configuration.
 */
public final class RotatingConfig implements XmlSaver
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
    /** Angle offset attribute name. */
    public static final String ATT_OFFSET = "offset";
    /** Amplitude attribute name. */
    public static final String ATT_AMPLITUDE = "amplitude";
    /** Controlled attribute name. */
    public static final String ATT_CONTROLLED = "controlled";
    /** Back attribute name. */
    public static final String ATT_BACK = "back";

    /** Extremity reference. */
    private final String extremity;
    /** Ring reference. */
    private final String ring;
    /** Length value. */
    private final int length;
    /** Speed value. */
    private final double speed;
    /** Offset value. */
    private final int offset;
    /** Amplitude value. */
    private final int amplitude;
    /** Controlled flag. */
    private final boolean controlled;
    /** Controlled flag. */
    private final int back;

    /**
     * Create blank configuration.
     */
    public RotatingConfig()
    {
        super();

        extremity = "";
        ring = "";
        length = 4;
        speed = 1.0;
        offset = 0;
        amplitude = 0;
        controlled = false;
        back = -1;
    }

    /**
     * Create configuration.
     * 
     * @param extremity The extremity object.
     * @param ring The ring object.
     * @param length The ring length.
     * @param speed The speed value.
     * @param offset The angle offset value.
     * @param amplitude The rotating amplitude value.
     * @param controlled The controlled flag.
     * @param back The back value.
     */
    public RotatingConfig(String extremity,
                          String ring,
                          int length,
                          double speed,
                          int offset,
                          int amplitude,
                          boolean controlled,
                          int back)
    {
        super();
        this.extremity = extremity;
        this.ring = ring;
        this.length = length;
        this.speed = speed;
        this.offset = offset;
        this.amplitude = amplitude;
        this.controlled = controlled;
        this.back = back;
    }

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be null).
     */
    public RotatingConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        final XmlReader node = root.getChild(NODE_ROTATING);
        extremity = node.getString(ATT_EXTREMITY);
        ring = node.getString(ATT_RING);
        length = node.getInteger(4, ATT_LENGTH);
        speed = node.getDouble(1.0, ATT_SPEED);
        offset = node.getInteger(0, ATT_OFFSET);
        amplitude = node.getInteger(0, ATT_AMPLITUDE);
        controlled = node.getBoolean(false, ATT_CONTROLLED);
        back = node.getInteger(-1, ATT_BACK);
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

    /**
     * Get the offset.
     * 
     * @return The offset.
     */
    public double getOffset()
    {
        return offset;
    }

    /**
     * Get the angle amplitude.
     * 
     * @return The angle amplitude.
     */
    public int getAmplitude()
    {
        return amplitude;
    }

    /**
     * Check if is controlled.
     * 
     * @return The controlled flag.
     */
    public boolean isControlled()
    {
        return controlled;
    }

    /**
     * Get angle back.
     * 
     * @return The angle back.
     */
    public int getBack()
    {
        return back;
    }

    @Override
    public void save(Xml root)
    {
        final Xml node = root.createChild(NODE_ROTATING);
        node.writeString(ATT_EXTREMITY, extremity);
        node.writeString(ATT_RING, ring);
        node.writeInteger(ATT_LENGTH, length);
        node.writeDouble(ATT_SPEED, speed);
        node.writeInteger(ATT_OFFSET, offset);
        node.writeInteger(ATT_AMPLITUDE, amplitude);
        node.writeBoolean(ATT_CONTROLLED, controlled);
        node.writeInteger(ATT_BACK, back);
    }

    private static void add(StringBuilder builder, String name, int value)
    {
        builder.append(name).append(Constant.DOUBLE_DOT).append(value).append(Constant.SPACE);
    }

    private static void add(StringBuilder builder, String name, double value)
    {
        builder.append(name).append(Constant.DOUBLE_DOT).append(value).append(Constant.SPACE);
    }

    private static void add(StringBuilder builder, String name, boolean value)
    {
        builder.append(name).append(Constant.DOUBLE_DOT).append(value).append(Constant.SPACE);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Rotating [");
        add(builder, ATT_LENGTH, length);
        add(builder, ATT_SPEED, speed);
        add(builder, ATT_OFFSET, offset);
        add(builder, ATT_AMPLITUDE, amplitude);
        add(builder, ATT_CONTROLLED, controlled);
        add(builder, ATT_BACK, back);
        builder.append("]");
        return builder.toString();
    }
}
