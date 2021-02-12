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
 * Dragon1 configuration.
 */
public final class Dragon1Config
{
    /** Config node name. */
    public static final String NODE_DRAGON1 = "dragon1";
    /** Fired count attribute name. */
    public static final String ATT_FIRED_COUNT = "firedCount";

    /**
     * Imports the config from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static Dragon1Config imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getChild(NODE_DRAGON1));
    }

    /**
     * Imports the config from root.
     * 
     * @param root The patrol node reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static Dragon1Config imports(XmlReader root)
    {
        Check.notNull(root);

        return new Dragon1Config(root);
    }

    /** Fire delay. */
    private final int firedCount;

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be null).
     */
    private Dragon1Config(XmlReader root)
    {
        super();

        Check.notNull(root);

        firedCount = root.readInteger(ATT_FIRED_COUNT);
    }

    /**
     * Get the fired count.
     * 
     * @return The fired count.
     */
    public int getFiredCount()
    {
        return firedCount;
    }
}
