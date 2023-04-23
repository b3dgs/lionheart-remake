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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Jumper configuration.
 */
public final class JumperConfig implements XmlSaver
{
    /** Config node name. */
    public static final String NODE_JUMPER = "jumper";
    /** Delay attribute name. */
    public static final String ATT_DELAY_MS = "delay";
    /** Horizontal force attribute name. */
    public static final String ATT_H = "h";

    /** Jump delay. */
    private final int delay;
    /** Jump horizontal force. */
    private final double h;

    /**
     * Create blank configuration.
     */
    public JumperConfig()
    {
        super();

        delay = 0;
        h = 1.3;
    }

    /**
     * Create configuration.
     * 
     * @param delay The jump delay.
     * @param h The horizontal force.
     */
    public JumperConfig(int delay, double h)
    {
        super();

        this.delay = delay;
        this.h = h;
    }

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be <code>null</code>).
     */
    public JumperConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        delay = root.getInteger(0, ATT_DELAY_MS, NODE_JUMPER);
        h = root.getDouble(1.3, ATT_H, NODE_JUMPER);
    }

    /**
     * Get the jump delay.
     * 
     * @return The jump delay.
     */
    public int getDelay()
    {
        return delay;
    }

    /**
     * Get the jump horizontal force.
     * 
     * @return The jump horizontal force.
     */
    public double getH()
    {
        return h;
    }

    @Override
    public void save(Xml root)
    {
        if (delay > 0)
        {
            final Xml node = root.createChild(NODE_JUMPER);
            node.writeInteger(ATT_DELAY_MS, delay);
            node.writeDouble(ATT_H, h);
        }
    }

    private static void add(StringBuilder builder, String name, int value)
    {
        builder.append(name).append(Constant.DOUBLE_DOT).append(value).append(Constant.SPACE);
    }

    private static void add(StringBuilder builder, String name, double value)
    {
        builder.append(name).append(Constant.DOUBLE_DOT).append(value).append(Constant.SPACE);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Jumper [ ");
        add(builder, ATT_DELAY_MS, delay);
        add(builder, ATT_H, h);
        builder.append("]");
        return builder.toString();
    }
}
