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
    public static final String ATT_DELAY_FIRST = "delayFirst";
    /** Start delay attribute name. */
    public static final String ATT_DELAY_START = "delayStart";
    /** Down delay attribute name. */
    public static final String ATT_DELAY_DOWN = "delayDown";
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
     * Create config.
     * 
     * @param root The root configuration (must not be null).
     */
    public GeyzerConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        final XmlReader node = root.getChild(NODE_GEYZER);
        delayFirst = node.getInteger(0, ATT_DELAY_FIRST);
        delayStart = node.getInteger(ATT_DELAY_START);
        delayDown = node.getInteger(ATT_DELAY_DOWN);
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
        node.writeInteger(ATT_DELAY_FIRST, delayFirst);
        node.writeInteger(ATT_DELAY_START, delayStart);
        node.writeInteger(ATT_DELAY_DOWN, delayDown);
        node.writeInteger(ATT_HEIGHT, height);
    }
}
