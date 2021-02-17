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
package com.b3dgs.lionheart;

import java.util.List;
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
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionheart.object.feature.Canon2AirshipConfig;
import com.b3dgs.lionheart.object.feature.CanonConfig;
import com.b3dgs.lionheart.object.feature.Dragon1Config;
import com.b3dgs.lionheart.object.feature.Dragon2Config;
import com.b3dgs.lionheart.object.feature.GeyzerConfig;
import com.b3dgs.lionheart.object.feature.HotFireBallConfig;
import com.b3dgs.lionheart.object.feature.PatrolConfig;
import com.b3dgs.lionheart.object.feature.RotatingConfig;
import com.b3dgs.lionheart.object.feature.SpikeConfig;

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
    /** Mirror attribute name. */
    private static final String ATT_MIRROR = "mirror";
    /** Secret attribute name. */
    private static final String ATT_SECRET = "secret";
    /** Next attribute name. */
    private static final String ATT_NEXT = "next";

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
    private final Coord spawn;
    /** Patrols configuration. */
    private final List<PatrolConfig> patrols;
    /** Spike configuration. */
    private final Optional<SpikeConfig> spike;
    /** Canon1 configuration. */
    private final Optional<CanonConfig> canon;
    /** Rotating configuration. */
    private final Optional<RotatingConfig> rotating;
    /** HotFireBall configuration. */
    private final Optional<HotFireBallConfig> hotFireBall;
    /** Geyzer configuration. */
    private final Optional<GeyzerConfig> geyzer;
    /** Dragon1 configuration. */
    private final Optional<Dragon1Config> dragon1;
    /** Dragon2 configuration. */
    private final Optional<Dragon2Config> dragon2;
    /** Canon2 configuration. */
    private final Optional<Canon2AirshipConfig> canon2;
    /** Jump configuration. */
    private final int jump;
    /** Mirror configuration. */
    private final Optional<Boolean> mirror;
    /** Secret configuration. */
    private final Optional<Boolean> secret;
    /** Next stage. */
    private final Optional<String> next;

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

        spawn = new Coord(root.readDouble(ATT_RESPAWN_TX), root.readDouble(ATT_RESPAWN_TY));

        patrols = PatrolConfig.imports(root.getChildren(PatrolConfig.NODE_PATROL));
        spike = root.getChildOptional(SpikeConfig.NODE_SPIKE).map(SpikeConfig::imports);
        canon = root.getChildOptional(CanonConfig.NODE_CANON).map(CanonConfig::imports);
        rotating = root.getChildOptional(RotatingConfig.NODE_ROTATING).map(RotatingConfig::imports);
        hotFireBall = root.getChildOptional(HotFireBallConfig.NODE_HOTFIREBALL).map(HotFireBallConfig::imports);
        geyzer = root.getChildOptional(GeyzerConfig.NODE_GEYZER).map(GeyzerConfig::imports);
        dragon1 = root.getChildOptional(Dragon1Config.NODE_DRAGON1).map(Dragon1Config::imports);
        dragon2 = root.getChildOptional(Dragon2Config.NODE_DRAGON2).map(Dragon2Config::imports);
        canon2 = root.getChildOptional(Canon2AirshipConfig.NODE_CANON2).map(Canon2AirshipConfig::imports);
        jump = root.readInteger(0, ATT_JUMP);
        mirror = root.readBooleanOptional(ATT_MIRROR);
        secret = root.readBooleanOptional(ATT_SECRET);
        next = root.readStringOptional(ATT_NEXT);
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
     * Get raster index.
     * 
     * @return The raster index.
     */
    public Integer getRaster()
    {
        return Integer.valueOf((int) Math.round(spawn.getY() / 2.0 - size.getHeight() / 16.0) + 1);
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
     * Get the patrols configuration.
     * 
     * @return The patrols configuration.
     */
    public List<PatrolConfig> getPatrols()
    {
        return patrols;
    }

    /**
     * Get the spike configuration.
     * 
     * @return The spike configuration.
     */
    public Optional<SpikeConfig> getSpike()
    {
        return spike;
    }

    /**
     * Get the canon configuration.
     * 
     * @return The canon configuration.
     */
    public Optional<CanonConfig> getCanon()
    {
        return canon;
    }

    /**
     * Get the rotating configuration.
     * 
     * @return The rotating configuration.
     */
    public Optional<RotatingConfig> getRotating()
    {
        return rotating;
    }

    /**
     * Get the hot fire ball configuration.
     * 
     * @return The hot fire ball configuration.
     */
    public Optional<HotFireBallConfig> getHotFireBall()
    {
        return hotFireBall;
    }

    /**
     * Get the geyzer configuration.
     * 
     * @return The geyzer configuration.
     */
    public Optional<GeyzerConfig> getGeyzer()
    {
        return geyzer;
    }

    /**
     * Get the dragon1 configuration.
     * 
     * @return The dragon1 configuration.
     */
    public Optional<Dragon1Config> getDragon1()
    {
        return dragon1;
    }

    /**
     * Get the dragon2 configuration.
     * 
     * @return The dragon2 configuration.
     */
    public Optional<Dragon2Config> getDragon2()
    {
        return dragon2;
    }

    /**
     * Get the canon2 configuration.
     * 
     * @return The canon2 configuration.
     */
    public Optional<Canon2AirshipConfig> getCanon2()
    {
        return canon2;
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

    /**
     * Get the mirror flag.
     * 
     * @return The mirror flag.
     */
    public Optional<Boolean> getMirror()
    {
        return mirror;
    }

    /**
     * Get the secret flag.
     * 
     * @return The secret flag.
     */
    public Optional<Boolean> getSecret()
    {
        return secret;
    }

    /**
     * Get the next stage.
     * 
     * @return The next stage.
     */
    public Optional<String> getNext()
    {
        return next;
    }
}
