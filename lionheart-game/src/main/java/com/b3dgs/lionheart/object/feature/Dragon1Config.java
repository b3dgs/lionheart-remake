/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * Dragon1 configuration.
 */
public final class Dragon1Config implements XmlSaver
{
    /** Config node name. */
    public static final String NODE_DRAGON1 = "dragon1";
    /** Fired count attribute name. */
    public static final String ATT_FIRED_COUNT = "firedCount";

    /** Fire delay. */
    private final int firedCount;

    /**
     * Create blank configuration.
     */
    public Dragon1Config()
    {
        super();

        firedCount = 0;
    }

    /**
     * Create configuration.
     * 
     * @param firedCount The fired count value.
     */
    public Dragon1Config(int firedCount)
    {
        super();

        this.firedCount = firedCount;
    }

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be <code>null</code>).
     */
    public Dragon1Config(XmlReader root)
    {
        super();

        Check.notNull(root);

        final XmlReader node = root.getChild(NODE_DRAGON1);
        firedCount = node.getInteger(ATT_FIRED_COUNT);
    }

    /**
     * Get the fired count.
     * 
     * @return The fired count.
     */
    public int getFiredCount()
    {
        return firedCount;
    }

    @Override
    public void save(Xml root)
    {
        final Xml node = root.createChild(NODE_DRAGON1);
        node.writeInteger(ATT_FIRED_COUNT, firedCount);
    }

    private static void add(StringBuilder builder, String name, int value)
    {
        builder.append(name).append(Constant.DOUBLE_DOT).append(value).append(Constant.SPACE);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Dragon1 [ ");
        add(builder, ATT_FIRED_COUNT, firedCount);
        builder.append("]");
        return builder.toString();
    }
}
