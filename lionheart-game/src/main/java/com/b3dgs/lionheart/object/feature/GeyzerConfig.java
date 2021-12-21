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
 * Geyzer configuration.
 */
public final class GeyzerConfig implements XmlSaver
{
    /** Config node name. */
    public static final String NODE_GEYZER = "geyzer";
    /** First delay attribute name. */
    public static final String ATT_FIRST_DELAY_MS = "delayFirst";
    /** Start delay attribute name. */
    public static final String ATT_START_DELAY_MS = "delayStart";
    /** Down delay attribute name. */
    public static final String ATT_DOWN_DELAY_MS = "delayDown";
    /** Height attribute name. */
    public static final String ATT_HEIGHT = "height";

    /** First delay. */
    private final int delayFirst;
    /** Start delay. */
    private final int delayStart;
    /** Down delay. */
    private final int delayDown;
    /** Max height. */
    private final int height;

    /**
     * Create blank config.
     */
    public GeyzerConfig()
    {
        super();

        delayFirst = 0;
        delayStart = 0;
        delayDown = 0;
        height = 0;
    }

    /**
     * Create config.
     * 
     * @param delayFirst The delay first.
     * @param delayStart The delay start.
     * @param delayDown The delay down.
     * @param height The maximum height.
     */
    public GeyzerConfig(int delayFirst, int delayStart, int delayDown, int height)
    {
        super();

        this.delayFirst = delayFirst;
        this.delayStart = delayStart;
        this.delayDown = delayDown;
        this.height = height;
    }

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be null).
     */
    public GeyzerConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        final XmlReader node = root.getChild(NODE_GEYZER);
        delayFirst = node.getInteger(0, ATT_FIRST_DELAY_MS);
        delayStart = node.getInteger(ATT_START_DELAY_MS);
        delayDown = node.getInteger(ATT_DOWN_DELAY_MS);
        height = node.getInteger(ATT_HEIGHT);
    }

    /**
     * Get the first delay.
     * 
     * @return The first delay.
     */
    public int getDelayFirst()
    {
        return delayFirst;
    }

    /**
     * Get the start delay.
     * 
     * @return The start delay.
     */
    public int getDelayStart()
    {
        return delayStart;
    }

    /**
     * Get the down delay.
     * 
     * @return The down delay.
     */
    public int getDelayDown()
    {
        return delayDown;
    }

    /**
     * Get the height.
     * 
     * @return The height.
     */
    public int getHeight()
    {
        return height;
    }

    @Override
    public void save(Xml root)
    {
        final Xml node = root.createChild(NODE_GEYZER);
        node.writeInteger(ATT_FIRST_DELAY_MS, delayFirst);
        node.writeInteger(ATT_START_DELAY_MS, delayStart);
        node.writeInteger(ATT_DOWN_DELAY_MS, delayDown);
        node.writeInteger(ATT_HEIGHT, height);
    }

    private static void add(StringBuilder builder, String name, int value)
    {
        builder.append(name).append(Constant.DOUBLE_DOT).append(value).append(Constant.SPACE);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Geyzer [ ");
        add(builder, ATT_FIRST_DELAY_MS, delayFirst);
        add(builder, ATT_START_DELAY_MS, delayStart);
        add(builder, ATT_DOWN_DELAY_MS, delayDown);
        add(builder, ATT_HEIGHT, height);
        builder.append("]");
        return builder.toString();
    }
}
