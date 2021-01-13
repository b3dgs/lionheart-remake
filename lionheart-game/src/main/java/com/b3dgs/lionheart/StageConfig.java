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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionheart.landscape.BackgroundType;
import com.b3dgs.lionheart.landscape.ForegroundConfig;

/**
 * Stage configuration.
 */
public final class StageConfig
{
    /** Stage node name. */
    public static final String NODE_STAGE = "stage";
    /** Stage next attribute name. */
    private static final String ATT_STAGE_NEXT_FILE = "next";

    /** Music node name. */
    private static final String NODE_MUSIC = "music";
    /** Music file attribute name. */
    private static final String ATT_MUSIC_FILE = "file";

    /** Map node name. */
    private static final String NODE_MAP = "map";
    /** Map file attribute name. */
    private static final String ATT_MAP_FILE = "file";

    /** Raster folder node name. */
    private static final String NODE_RASTER = "raster";
    /** Raster folder attribute name. */
    private static final String ATT_RASTER_FOLDER = "folder";

    /** Background node name. */
    private static final String NODE_BACKGROUND = "background";
    /** Background type attribute name. */
    private static final String ATT_BACKGROUND_TYPE = "type";

    /** Checkpoint node name. */
    private static final String NODE_CHECKPOINT = "checkpoint";
    /** Checkpoint starting tile x attribute name. */
    private static final String ATT_CHECKPOINT_START_TX = "startTx";
    /** Checkpoint starting tile y attribute name. */
    private static final String ATT_CHECKPOINT_START_TY = "startTy";
    /** Checkpoint ending tile x attribute name. */
    private static final String ATT_CHECKPOINT_END_TX = "endTx";
    /** Checkpoint ending tile y attribute name. */
    private static final String ATT_CHECKPOINT_END_TY = "endTy";

    /** Respawn node name. */
    private static final String NODE_RESPAWN = "respawn";
    /** Respawn tile x attribute name. */
    private static final String ATT_RESPAWN_TX = "tx";
    /** Respawn tile y attribute name. */
    private static final String ATT_RESPAWN_TY = "ty";

    /** Boss node name. */
    private static final String NODE_BOSS = "boss";
    /** Boss tile x attribute name. */
    private static final String ATT_BOSS_TX = "tx";
    /** Boss tile y attribute name. */
    private static final String ATT_BOSS_TY = "ty";

    /** Entities node name. */
    private static final String NODE_ENTITIES = "entities";

    /**
     * Imports the config from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static StageConfig imports(Configurer configurer)
    {
        return new StageConfig(configurer);
    }

    /** Next stage file. */
    private final Media nextStage;
    /** Music file. */
    private final Media music;
    /** Map level file. */
    private final Media mapFile;
    /** Map raster folder. */
    private final Optional<String> rasterFolder;
    /** Background type. */
    private final BackgroundType background;
    /** Foreground config. */
    private final ForegroundConfig foreground;
    /** Starting tile. */
    private final Coord start;
    /** Ending tile. */
    private final Optional<Coord> end;
    /** Boss tile. */
    private final Optional<Coord> boss;
    /** Respawn tiles. */
    private final Collection<Coord> respawns = new ArrayList<>();
    /** Entities configuration. */
    private final Collection<EntityConfig> entities = new ArrayList<>();

    /**
     * Create config.
     * 
     * @param configurer The configurer reference.
     * @throws LionEngineException If unable to read node.
     */
    private StageConfig(Configurer configurer)
    {
        super();

        Check.notNull(configurer);

        nextStage = configurer.getMedia(ATT_STAGE_NEXT_FILE);

        music = configurer.getMedia(ATT_MUSIC_FILE, NODE_MUSIC);

        mapFile = configurer.getMedia(ATT_MAP_FILE, NODE_MAP);

        rasterFolder = configurer.getStringOptional(ATT_RASTER_FOLDER, NODE_RASTER);

        background = configurer.getEnum(BackgroundType.class, ATT_BACKGROUND_TYPE, NODE_BACKGROUND);
        foreground = ForegroundConfig.imports(configurer);

        start = new Coord(configurer.getInteger(ATT_CHECKPOINT_START_TX, NODE_CHECKPOINT),
                          configurer.getInteger(ATT_CHECKPOINT_START_TY, NODE_CHECKPOINT));

        if (configurer.hasAttribute(ATT_CHECKPOINT_END_TX, NODE_CHECKPOINT)
            && configurer.hasAttribute(ATT_CHECKPOINT_END_TY, NODE_CHECKPOINT))
        {
            end = Optional.of(new Coord(configurer.getInteger(ATT_CHECKPOINT_END_TX, NODE_CHECKPOINT),
                                        configurer.getInteger(ATT_CHECKPOINT_END_TY, NODE_CHECKPOINT)));
        }
        else
        {
            end = Optional.empty();
        }

        if (configurer.hasAttribute(ATT_BOSS_TX, NODE_BOSS) && configurer.hasAttribute(ATT_BOSS_TY, NODE_BOSS))
        {
            boss = Optional.of(new Coord(configurer.getInteger(ATT_BOSS_TX, NODE_BOSS),
                                         configurer.getInteger(ATT_BOSS_TY, NODE_BOSS)));
        }
        else
        {
            boss = Optional.empty();
        }

        configurer.getChildren(NODE_RESPAWN, NODE_CHECKPOINT).forEach(this::addRespawn);

        configurer.getChildren(EntityConfig.NODE_ENTITY, NODE_ENTITIES).forEach(this::addEntity);
    }

    /**
     * Add respawn from configuration.
     * 
     * @param respawn The respawn configuration.
     */
    private void addRespawn(XmlReader respawn)
    {
        respawns.add(new Coord(respawn.readInteger(ATT_RESPAWN_TX), respawn.readInteger(ATT_RESPAWN_TY)));
    }

    /**
     * Add entity from configuration.
     * 
     * @param entity The entity configuration.
     */
    private void addEntity(XmlReader entity)
    {
        entities.add(EntityConfig.imports(entity));
    }

    /**
     * Get the next stage.
     * 
     * @return The next stage.
     */
    public Media getNextStage()
    {
        return nextStage;
    }

    /**
     * Get the music.
     * 
     * @return The music.
     */
    public Media getMusic()
    {
        return music;
    }

    /**
     * Get the map file.
     * 
     * @return The map file.
     */
    public Media getMapFile()
    {
        return mapFile;
    }

    /**
     * Get the raster folder.
     * 
     * @return The raster folder.
     */
    public Optional<String> getRasterFolder()
    {
        return rasterFolder;
    }

    /**
     * Get the background type.
     * 
     * @return The background type.
     */
    public BackgroundType getBackground()
    {
        return background;
    }

    /**
     * Get the foreground type.
     * 
     * @return The foreground type.
     */
    public ForegroundConfig getForeground()
    {
        return foreground;
    }

    /**
     * Get the start location.
     * 
     * @return The start location.
     */
    public Coord getStart()
    {
        return start;
    }

    /**
     * Get the end location.
     * 
     * @return The end location.
     */
    public Optional<Coord> getEnd()
    {
        return end;
    }

    /**
     * Get the respawn locations.
     * 
     * @return The respawn locations.
     */
    public Collection<Coord> getRespawns()
    {
        return respawns;
    }

    /**
     * Get the boss location.
     * 
     * @return The boss location.
     */
    public Optional<Coord> getBoss()
    {
        return boss;
    }

    /**
     * Get the entities.
     * 
     * @return The entities.
     */
    public Collection<EntityConfig> getEntities()
    {
        return entities;
    }
}
