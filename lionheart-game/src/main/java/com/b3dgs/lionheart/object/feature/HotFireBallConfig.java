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
 * HotFireBall configuration.
 */
public final class HotFireBallConfig
{
    /** Hurtable node name. */
    public static final String NODE_HOTFIREBALL = "hotfireball";
    /** Delay attribute name. */
    private static final String ATT_DELAY = "delay";
    /** Count attribute name. */
    private static final String ATT_COUNT = "count";
    /** Level attribute name. */
    private static final String ATT_LEVEL = "level";
    /** Horizontal force attribute name. */
    private static final String ATT_VX = "vx";
    /** Vertical force attribute name. */
    private static final String ATT_VY = "vy";

    /**
     * Imports the config from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static HotFireBallConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getChild(NODE_HOTFIREBALL));
    }

    /**
     * Imports the config from root.
     * 
     * @param root The patrol node reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static HotFireBallConfig imports(XmlReader root)
    {
        Check.notNull(root);

        return new HotFireBallConfig(root);
    }

    /** Fire delay. */
    private final int delay;
    /** Fire count. */
    private final int count;
    /** Level count. */
    private final int level;
    /** Horizontal force. */
    private final double vx;
    /** Vertical force. */
    private final double vy;

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be null).
     */
    private HotFireBallConfig(XmlReader root)
    {
        super();

        delay = root.readInteger(ATT_DELAY);
        count = root.readInteger(ATT_COUNT);
        level = root.readInteger(ATT_LEVEL);
        vx = root.readDouble(ATT_VX);
        vy = root.readDouble(ATT_VY);
    }

    /**
     * Get the fire delay.
     * 
     * @return The fire delay.
     */
    public int getDelay()
    {
        return delay;
    }

    /**
     * Get the fire count.
     * 
     * @return The fire count.
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Get the level.
     * 
     * @return The level.
     */
    public int getLevel()
    {
        return level;
    }

    /**
     * Get the horizontal force.
     * 
     * @return The horizontal force.
     */
    public double getVx()
    {
        return vx;
    }

    /**
     * Get the vertical force.
     * 
     * @return The vertical force.
     */
    public double getVy()
    {
        return vy;
    }
}
