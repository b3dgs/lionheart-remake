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
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Pillar configuration.
 */
public final class PillarConfig implements XmlSaver
{
    /** Pillar node name. */
    public static final String NODE_PILLARD = "pillar";
    /** Delay attribute name. */
    public static final String ATT_DELAY_MS = "delay";

    /** Delay. */
    private final int delay;

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public PillarConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        delay = root.getInteger(ATT_DELAY_MS);
    }

    /**
     * Create config.
     * 
     * @param delay The delay value.
     */
    public PillarConfig(int delay)
    {
        super();

        this.delay = delay;
    }

    /**
     * Get the delay.
     * 
     * @return The delay.
     */
    public int getDelay()
    {
        return delay;
    }

    @Override
    public void save(Xml root)
    {
        root.writeInteger(ATT_DELAY_MS, delay);
    }
}
