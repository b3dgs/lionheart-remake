/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.XmlReader;

/**
 * Spawn configuration.
 */
public final class SpawnConfig
{
    /** Spawn node name. */
    public static final String NODE_SPAWN = "spawn";
    /** Delay in millisecond attribute name. */
    public static final String ATT_DELAY = "delay";

    /**
     * Imports the config from configurer.
     * 
     * @param root The root reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static SpawnConfig imports(XmlReader root)
    {
        Check.notNull(root);

        return new SpawnConfig(root);
    }

    /** The delay milli. */
    private final int delay;
    /** Associated entities. */
    private final List<EntityConfig> entities = new ArrayList<>();

    /**
     * Create config.
     * 
     * @param root The configurer reference (must not be <code>null</code>).
     * @throws LionEngineException If unable to read node.
     */
    private SpawnConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        delay = root.getInteger(ATT_DELAY);
        root.getChildren(EntityConfig.NODE_ENTITY).forEach(entity -> entities.add(EntityConfig.imports(entity)));
    }

    /**
     * Get the delay.
     * 
     * @return The delay in milli.
     */
    public int getDelay()
    {
        return delay;
    }

    /**
     * Get the entities.
     * 
     * @return The entities.
     */
    public List<EntityConfig> getEntities()
    {
        return entities;
    }
}
