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
 * Jumper configuration.
 */
public final class JumperConfig implements XmlSaver
{
    /** Config node name. */
    public static final String NODE_JUMPER = "jumper";
    /** Tick attribute name. */
    public static final String ATT_TICK = "tick";

    /** Jump tick. */
    private final int tick;

    /**
     * Create blank configuration.
     */
    public JumperConfig()
    {
        super();

        tick = 0;
    }

    /**
     * Create configuration.
     * 
     * @param tick The jump tick.
     */
    public JumperConfig(int tick)
    {
        super();

        this.tick = tick;
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

        tick = root.getInteger(0, ATT_TICK, NODE_JUMPER);
    }

    /**
     * Get the jump tick.
     * 
     * @return The jump tick.
     */
    public int getTick()
    {
        return tick;
    }

    @Override
    public void save(Xml root)
    {
        if (tick > 0)
        {
            final Xml node = root.createChild(NODE_JUMPER);
            node.writeInteger(ATT_TICK, tick);
        }
    }

    private static void add(StringBuilder builder, String name, int value)
    {
        builder.append(name).append(Constant.DOUBLE_DOT).append(value).append(Constant.SPACE);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Jumper [ ");
        add(builder, ATT_TICK, tick);
        builder.append("]");
        return builder.toString();
    }
}
