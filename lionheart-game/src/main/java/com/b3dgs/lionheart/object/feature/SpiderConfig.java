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
 * Spider configuration.
 */
public final class SpiderConfig
{
    /** Spider node name. */
    public static final String NODE_SPIDER = "spider";
    /** Follow attribute name. */
    public static final String ATT_FOLLOW = "follow";

    /**
     * Imports the config from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static SpiderConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getChild(NODE_SPIDER));
    }

    /**
     * Imports the config from root.
     * 
     * @param root The patrol node reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static SpiderConfig imports(XmlReader root)
    {
        Check.notNull(root);

        return new SpiderConfig(root);
    }

    /** Follow flag. */
    private final boolean follow;

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be null).
     */
    private SpiderConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        follow = root.readBoolean(true, ATT_FOLLOW);
    }

    /**
     * Get the follow flag.
     * 
     * @return The follow flag.
     */
    public boolean getFollow()
    {
        return follow;
    }
}
