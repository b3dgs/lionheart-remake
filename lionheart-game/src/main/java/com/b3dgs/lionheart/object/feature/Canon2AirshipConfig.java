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
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Canon2 Airship configuration.
 */
public final class Canon2AirshipConfig implements XmlSaver
{
    /** Config node name. */
    public static final String NODE_CANON2 = "canon2";
    /** Fire delay attribute name. */
    public static final String ATT_FIRE_DELAY = "fireDelay";
    /** Stay delay attribute name. */
    public static final String ATT_STAY_DELAY = "stayDelay";

    /** Fire delay. */
    private final int fireDelay;
    /** Stay delay. */
    private final int stayDelay;

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public Canon2AirshipConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        final XmlReader node = root.getChild(NODE_CANON2);
        fireDelay = node.readInteger(ATT_FIRE_DELAY);
        stayDelay = node.readInteger(ATT_STAY_DELAY);
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

        final Xml node = root.createChild(NODE_CANON2);
        node.writeInteger(ATT_FIRE_DELAY, fireDelay);
        node.writeInteger(ATT_STAY_DELAY, stayDelay);
    }
}
