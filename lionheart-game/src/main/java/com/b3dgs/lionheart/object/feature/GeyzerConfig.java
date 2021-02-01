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
 * Geyzer configuration.
 */
public final class GeyzerConfig
{
    /** Config node name. */
    public static final String NODE_GEYZER = "geyzer";
    /** Start delay attribute name. */
    public static final String ATT_DELAY_START = "delayStart";
    /** Down delay attribute name. */
    public static final String ATT_DELAY_DOWN = "delayDown";
    /** Height attribute name. */
    public static final String ATT_HEIGHT = "height";

    /**
     * Imports the config from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static GeyzerConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getChild(NODE_GEYZER));
    }

    /**
     * Imports the config from root.
     * 
     * @param root The patrol node reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static GeyzerConfig imports(XmlReader root)
    {
        Check.notNull(root);

        return new GeyzerConfig(root);
    }

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
    private GeyzerConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        delayStart = root.readInteger(ATT_DELAY_START);
        delayDown = root.readInteger(ATT_DELAY_DOWN);
        height = root.readInteger(ATT_HEIGHT);
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
}
