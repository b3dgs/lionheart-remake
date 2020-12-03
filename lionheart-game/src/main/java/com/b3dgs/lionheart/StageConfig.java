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

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.CoordTile;
import com.b3dgs.lionheart.landscape.BackgroundType;
import com.b3dgs.lionheart.landscape.ForegroundType;

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

    /** Foreground node name. */
    private static final String NODE_FOREGROUND = "foreground";
    /** Foreground type attribute name. */
    private static final String ATT_FOREGROUND_TYPE = "type";

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
    private final String rasterFolder;
    /** Background type. */
    private final BackgroundType background;
    /** Foreground type. */
    private final ForegroundType foreground;
    /** Starting tile. */
    private final CoordTile tileStart;
    /** Ending tile. */
    private final CoordTile tileEnd;
    /** Respawn tiles. */
    private final Collection<CoordTile> tileRespawn = new ArrayList<>();
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

        rasterFolder = configurer.getString(ATT_RASTER_FOLDER, NODE_RASTER);

        background = configurer.getEnum(BackgroundType.class, ATT_BACKGROUND_TYPE, NODE_BACKGROUND);
        foreground = configurer.getEnum(ForegroundType.class, ATT_FOREGROUND_TYPE, NODE_FOREGROUND);

        tileStart = new CoordTile(configurer.getInteger(ATT_CHECKPOINT_START_TX, NODE_CHECKPOINT),
                                  configurer.getInteger(ATT_CHECKPOINT_START_TY, NODE_CHECKPOINT));

        tileEnd = new CoordTile(configurer.getInteger(ATT_CHECKPOINT_END_TX, NODE_CHECKPOINT),
                                configurer.getInteger(ATT_CHECKPOINT_END_TY, NODE_CHECKPOINT));

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
        tileRespawn.add(new CoordTile(respawn.readInteger(ATT_RESPAWN_TX), respawn.readInteger(ATT_RESPAWN_TY)));
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
    public String getRasterFolder()
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
    public ForegroundType getForeground()
    {
        return foreground;
    }

    /**
     * Get the tile start.
     * 
     * @return The tile start.
     */
    public CoordTile getTileStart()
    {
        return tileStart;
    }

    /**
     * Get the tile end.
     * 
     * @return The tile end.
     */
    public CoordTile getTileEnd()
    {
        return tileEnd;
    }

    /**
     * Get the tiles respawn.
     * 
     * @return The tiles respawn.
     */
    public Collection<CoordTile> getTileRespawn()
    {
        return tileRespawn;
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
