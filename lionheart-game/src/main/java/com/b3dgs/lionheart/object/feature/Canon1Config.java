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
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Canon1 configuration.
 */
public final class Canon1Config
{
    /** Config node name. */
    public static final String NODE_CANON1 = "canon1";
    /** Fire delay attribute name. */
    public static final String ATT_FIRE_DELAY = "fireDelay";
    /** Fire horizontal force attribute name. */
    public static final String ATT_VX = "vx";
    /** Fire vertical force attribute name. */
    public static final String ATT_VY = "vy";

    /**
     * Imports the config from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static Canon1Config imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getChild(NODE_CANON1));
    }

    /**
     * Imports the config from root.
     * 
     * @param root The patrol node reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static Canon1Config imports(XmlReader root)
    {
        Check.notNull(root);

        return new Canon1Config(root);
    }

    /** Fire delay. */
    private final int fireDelay;
    /** Fire horizontal force. */
    private final double vx;
    /** Fire vertical force. */
    private final double vy;

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be null).
     */
    private Canon1Config(XmlReader root)
    {
        super();

        Check.notNull(root);

        fireDelay = root.readInteger(ATT_FIRE_DELAY);
        vx = root.readDouble(ATT_VX);
        vy = root.readDouble(ATT_VY);
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
     * Get the fire horizontal speed.
     * 
     * @return The fire horizontal speed.
     */
    public double getVx()
    {
        return vx;
    }

    /**
     * Get the fire vertical speed.
     * 
     * @return The fire vertical speed.
     */
    public double getVy()
    {
        return vy;
    }
}
