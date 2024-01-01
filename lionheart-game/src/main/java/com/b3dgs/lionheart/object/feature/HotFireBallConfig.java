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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * HotFireBall configuration.
 */
public final class HotFireBallConfig implements XmlSaver
{
    /** Hurtable node name. */
    public static final String NODE_HOTFIREBALL = "hotfireball";
    /** Delay attribute name. */
    public static final String ATT_DELAY_MS = "delay";
    /** Count attribute name. */
    public static final String ATT_COUNT = "count";
    /** Level attribute name. */
    public static final String ATT_LEVEL = "level";
    /** Horizontal force attribute name. */
    public static final String ATT_VX = "vx";
    /** Vertical force attribute name. */
    public static final String ATT_VY = "vy";

    /** Fire delay. */
    private final int delay;
    /** Fire count. */
    private final int count;
    /** Level count. */
    private final int level;
    /** Horizontal force. */
    private final double vx;
    /** Vertical force. */
    private final double vy;

    /**
     * Create blank configuration.
     */
    public HotFireBallConfig()
    {
        super();

        delay = 0;
        count = 0;
        level = 0;
        vx = 0;
        vy = 0;
    }

    /**
     * Create configuration.
     * 
     * @param delay The delay value.
     * @param count The count number.
     * @param level The fire type.
     * @param vx The horizontal force.
     * @param vy The vertical force.
     */
    public HotFireBallConfig(int delay, int count, int level, double vx, double vy)
    {
        super();

        this.delay = delay;
        this.count = count;
        this.level = level;
        this.vx = vx;
        this.vy = vy;
    }

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be <code>null</code>).
     */
    public HotFireBallConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        final XmlReader node = root.getChild(NODE_HOTFIREBALL);
        delay = node.getInteger(ATT_DELAY_MS);
        count = node.getInteger(ATT_COUNT);
        level = node.getInteger(ATT_LEVEL);
        vx = node.getDouble(ATT_VX);
        vy = node.getDouble(ATT_VY);
    }

    /**
     * Get the fire delay.
     * 
     * @return The fire delay.
     */
    public int getDelay()
    {
        return delay;
    }

    /**
     * Get the fire count.
     * 
     * @return The fire count.
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Get the level.
     * 
     * @return The level.
     */
    public int getLevel()
    {
        return level;
    }

    /**
     * Get the horizontal force.
     * 
     * @return The horizontal force.
     */
    public double getVx()
    {
        return vx;
    }

    /**
     * Get the vertical force.
     * 
     * @return The vertical force.
     */
    public double getVy()
    {
        return vy;
    }

    @Override
    public void save(Xml root)
    {
        final Xml node = root.createChild(NODE_HOTFIREBALL);
        node.writeInteger(ATT_DELAY_MS, delay);
        node.writeInteger(ATT_COUNT, count);
        node.writeInteger(ATT_LEVEL, level);
        node.writeDouble(ATT_VX, vx);
        node.writeDouble(ATT_VY, vy);
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
        builder.append("HotFireBall [ ");
        add(builder, ATT_DELAY_MS, delay);
        add(builder, ATT_COUNT, count);
        add(builder, ATT_LEVEL, level);
        add(builder, ATT_VX, vx);
        add(builder, ATT_VY, vy);
        builder.append("]");
        return builder.toString();
    }
}
