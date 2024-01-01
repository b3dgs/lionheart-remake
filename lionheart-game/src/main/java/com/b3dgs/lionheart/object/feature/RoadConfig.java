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
 * Road configuration.
 */
public final class RoadConfig implements XmlSaver
{
    /** Config node name. */
    public static final String NODE_ROAD = "road";
    /** Road attribute name. */
    public static final String ATT_START = "start";
    /** Offset attribute name. */
    public static final String ATT_OFFSET = "offset";

    /** Road start. */
    private final int start;
    /** Road offset. */
    private final int offset;

    /**
     * Create blank configuration.
     */
    public RoadConfig()
    {
        super();

        start = 0;
        offset = 0;
    }

    /**
     * Create configuration.
     * 
     * @param start The road start.
     * @param offset The road offset.
     */
    public RoadConfig(int start, int offset)
    {
        super();

        this.start = start;
        this.offset = offset;
    }

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be <code>null</code>).
     */
    public RoadConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        final XmlReader node = root.getChild(NODE_ROAD);
        start = node.getInteger(ATT_START);
        offset = node.getInteger(ATT_OFFSET);
    }

    /**
     * Get the road start.
     * 
     * @return The road start.
     */
    public int getStart()
    {
        return start;
    }

    /**
     * Get the road offset.
     * 
     * @return The road offset.
     */
    public int getOffset()
    {
        return offset;
    }

    @Override
    public void save(Xml root)
    {
        final Xml node = root.createChild(NODE_ROAD);
        node.writeInteger(ATT_START, start);
        node.writeInteger(ATT_OFFSET, offset);
    }

    private static void add(StringBuilder builder, String name, int value)
    {
        builder.append(name).append(Constant.DOUBLE_DOT).append(value).append(Constant.SPACE);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Road [ ");
        add(builder, ATT_START, start);
        add(builder, ATT_OFFSET, offset);
        builder.append("]");
        return builder.toString();
    }
}
