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
 * Animal configuration.
 */
public final class AnimalConfig implements XmlSaver
{
    /** Config node name. */
    public static final String NODE_ANIMAL = "animal";
    /** Boat attribute name. */
    public static final String ATT_BOAT = "boat";

    /** Boat range. */
    private final int boat;

    /**
     * Create blank configuration.
     */
    public AnimalConfig()
    {
        super();

        boat = 0;
    }

    /**
     * Create configuration.
     * 
     * @param boat The boat range.
     */
    public AnimalConfig(int boat)
    {
        super();

        this.boat = boat;
    }

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be <code>null</code>).
     */
    public AnimalConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        final XmlReader node = root.getChild(NODE_ANIMAL);
        boat = node.getInteger(ATT_BOAT);
    }

    /**
     * Get the boat range.
     * 
     * @return The boat range.
     */
    public int getBoat()
    {
        return boat;
    }

    @Override
    public void save(Xml root)
    {
        final Xml node = root.createChild(NODE_ANIMAL);
        node.writeInteger(ATT_BOAT, boat);
    }

    private static void add(StringBuilder builder, String name, int value)
    {
        builder.append(name).append(Constant.DOUBLE_DOT).append(value).append(Constant.SPACE);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Animal [ ");
        add(builder, ATT_BOAT, boat);
        builder.append("]");
        return builder.toString();
    }
}
