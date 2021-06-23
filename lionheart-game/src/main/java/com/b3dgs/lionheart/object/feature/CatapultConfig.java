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
 * Catapult configuration.
 */
public final class CatapultConfig
{
    /** Pillar node name. */
    public static final String NODE_CATAPULT = "catapult";
    /** Horizontal speed attribute name. */
    public static final String ATT_VX = "vx";
    /** Vertical speed attribute name. */
    public static final String ATT_VY = "vy";

    /**
     * Imports the config from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static CatapultConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getChild(NODE_CATAPULT));
    }

    /**
     * Imports the config from root.
     * 
     * @param root The patrol node reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static CatapultConfig imports(XmlReader root)
    {
        Check.notNull(root);

        return new CatapultConfig(root.readDouble(ATT_VX), root.readDouble(ATT_VY));
    }

    /** Horizontal speed. */
    private final double vx;
    /** Vertical speed. */
    private final double vy;

    /**
     * Create config.
     * 
     * @param vx The horizontal speed value.
     * @param vy The vertical speed value.
     */
    public CatapultConfig(double vx, double vy)
    {
        super();

        this.vx = vx;
        this.vy = vy;
    }

    /**
     * Get the horizontal speed.
     * 
     * @return The horizontal speed.
     */
    public double getVx()
    {
        return vx;
    }

    /**
     * Get the vertical speed.
     * 
     * @return The vertical speed.
     */
    public double getVy()
    {
        return vy;
    }
}
