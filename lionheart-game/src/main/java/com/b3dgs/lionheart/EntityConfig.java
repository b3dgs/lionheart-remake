/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Optional;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.OriginConfig;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.CoordTile;
import com.b3dgs.lionheart.object.feature.PatrolConfig;

/**
 * Entity configuration.
 */
public final class EntityConfig
{
    /** Entity node name. */
    public static final String NODE_ENTITY = "entity";
    /** Configuration file attribute name. */
    private static final String ATT_FILE = "file";
    /** Spawn tile x attribute name. */
    private static final String ATT_RESPAWN_TX = "tx";
    /** Spawn tile y attribute name. */
    private static final String ATT_RESPAWN_TY = "ty";
    /** Jump attribute name. */
    private static final String ATT_JUMP = "jump";

    /**
     * Imports the config from configurer.
     * 
     * @param root The root reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static EntityConfig imports(XmlReader root)
    {
        Check.notNull(root);

        return new EntityConfig(root);
    }

    /** Configuration file. */
    private final Media media;
    /** Size config. */
    private final SizeConfig size;
    /** Origin. */
    private final Origin origin;
    /** Spawn tile. */
    private final CoordTile spawn;
    /** Patrol configuration. */
    private Optional<PatrolConfig> patrol;
    /** Jump configuration. */
    private final int jump;

    /**
     * Create config.
     * 
     * @param root The configurer reference (must not be <code>null</code>).
     * @throws LionEngineException If unable to read node.
     */
    private EntityConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        media = Medias.create(root.readString(ATT_FILE));

        final Configurer configurer = new Configurer(media);
        size = SizeConfig.imports(configurer);
        origin = OriginConfig.imports(configurer);

        spawn = new CoordTile(root.readInteger(ATT_RESPAWN_TX), root.readInteger(ATT_RESPAWN_TY));

        if (root.hasChild(PatrolConfig.NODE_PATROL))
        {
            patrol = Optional.of(PatrolConfig.imports(root.getChild(PatrolConfig.NODE_PATROL)));
        }
        else
        {
            patrol = Optional.empty();
        }
        jump = root.readInteger(0, ATT_JUMP);
    }

    /**
     * Get the media.
     * 
     * @return The media.
     */
    public Media getMedia()
    {
        return media;
    }

    /**
     * Get the spawn location.
     * 
     * @param map The map reference.
     * @return The spawn location.
     */
    public double getSpawnX(MapTile map)
    {
        return origin.getX(spawn.getX() * map.getTileWidth(), size.getWidth()) + map.getTileWidth();
    }

    /**
     * Get the spawn location.
     * 
     * @param map The map reference.
     * @return The spawn location.
     */
    public double getSpawnY(MapTile map)
    {
        return origin.getY(spawn.getY() * map.getTileHeight(), size.getHeight()) + map.getTileHeight();
    }

    /**
     * Get the spawn raw location.
     * 
     * @param map The map reference.
     * @return The spawn location.
     */
    public double getSpawnRawX(MapTile map)
    {
        return spawn.getX() * map.getTileWidth();
    }

    /**
     * Get the spawn raw location.
     * 
     * @param map The map reference.
     * @return The spawn location.
     */
    public double getSpawnRawY(MapTile map)
    {
        return spawn.getY() * map.getTileHeight();
    }

    /**
     * Get the patrol configuration.
     * 
     * @return The patrol configuration.
     */
    public Optional<PatrolConfig> getPatrol()
    {
        return patrol;
    }

    /**
     * Get the jump tick.
     * 
     * @return The jump tick.
     */
    public int getJump()
    {
        return jump;
    }
}
