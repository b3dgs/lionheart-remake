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
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Canon2 Airship configuration.
 */
public final class LaserAirshipConfig implements XmlSaver
{
    /** Config node name. */
    public static final String NODE_LASER = "laser";
    /** Fire delay attribute name. */
    public static final String ATT_FIRE_DELAY_MS = "fireDelay";
    /** Stay delay attribute name. */
    public static final String ATT_STAY_DELAY_MS = "stayDelay";

    /** Fire delay. */
    private final int fireDelay;
    /** Stay delay. */
    private final int stayDelay;

    /**
     * Create blank configuration.
     */
    public LaserAirshipConfig()
    {
        super();

        fireDelay = 0;
        stayDelay = 0;
    }

    /**
     * Create config.
     * 
     * @param fireDelay The fire delay.
     * @param stayDelay The stay delay.
     */
    public LaserAirshipConfig(int fireDelay, int stayDelay)
    {
        super();

        this.fireDelay = fireDelay;
        this.stayDelay = stayDelay;
    }

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public LaserAirshipConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        final XmlReader node = root.getChild(NODE_LASER);
        fireDelay = node.getInteger(ATT_FIRE_DELAY_MS);
        stayDelay = node.getInteger(ATT_STAY_DELAY_MS);
    }

    /**
     * Get the fire delay.
     * 
     * @return The fire delay.
     */
    public int getFireDelay()
    {
        return fireDelay;
    }

    /**
     * Get the stay delay.
     * 
     * @return The stay delay.
     */
    public int getStayDelay()
    {
        return stayDelay;
    }

    @Override
    public void save(Xml root)
    {
        Check.notNull(root);

        final Xml node = root.createChild(NODE_LASER);
        node.writeInteger(ATT_FIRE_DELAY_MS, fireDelay);
        node.writeInteger(ATT_STAY_DELAY_MS, stayDelay);
    }

    private static void add(StringBuilder builder, String name, int value)
    {
        builder.append(name).append(Constant.DOUBLE_DOT).append(value).append(Constant.SPACE);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Laser [ ");
        add(builder, ATT_FIRE_DELAY_MS, fireDelay);
        add(builder, ATT_STAY_DELAY_MS, stayDelay);
        builder.append("]");
        return builder.toString();
    }
}
