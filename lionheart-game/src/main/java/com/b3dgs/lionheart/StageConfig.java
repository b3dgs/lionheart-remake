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
import java.util.List;
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

    /** Entities node name. */
    public static final String NODE_ENTITIES = "entities";

    /** Stage picture attribute name. */
    private static final String ATT_STAGE_PIC = "pic";
    /** Stage text attribute name. */
    private static final String ATT_STAGE_TEXT = "text";
    /** File attribute name. */
    private static final String ATT_FILE = "file";

    /** Music node name. */
    private static final String NODE_MUSIC = "music";

    /** Map node name. */
    private static final String NODE_MAP = "map";
    /** Lines per raster attribute name. */
    private static final String ATT_MAP_LINES_PER_RASTER = "linesPerRaster";
    /** Raster line offset attribute name. */
    private static final String ATT_MAP_RASTER_LINE_OFFSET = "rasterLineOffset";

    /** Raster folder node name. */
    private static final String NODE_RASTER = "raster";
    /** Raster folder attribute name. */
    private static final String ATT_RASTER_FOLDER = "folder";

    /** Background node name. */
    private static final String NODE_BACKGROUND = "background";
    /** Background type attribute name. */
    private static final String ATT_BACKGROUND_TYPE = "type";

    /** Checkpoints node name. */
    private static final String NODE_CHECKPOINTS = "checkpoints";
    /** Checkpoint node name. */
    private static final String NODE_CHECKPOINT = "checkpoint";
    /** Checkpoint tile x attribute name. */
    private static final String ATT_CHECKPOINT_TX = "tx";
    /** Checkpoint tile y attribute name. */
    private static final String ATT_CHECKPOINT_TY = "ty";
    /** Checkpoint next stage attribute name. */
    private static final String ATT_CHECKPOINT_NEXT = "next";
    /** Spawn tile x attribute name. */
    private static final String ATT_SPAWN_TX = "stx";
    /** Spawn tile y attribute name. */
    private static final String ATT_SPAWN_TY = "sty";

    /** Boss node name. */
    private static final String NODE_BOSS = "boss";
    /** Boss spawn tile x attribute name. */
    private static final String ATT_BOSS_TSX = "tsx";
    /** Boss spawn tile y attribute name. */
    private static final String ATT_BOSS_TSY = "tsy";

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

    /** Picture file. */
    private final Optional<Media> pic;
    /** Text. */
    private final Optional<String> text;
    /** Music file. */
    private final Media music;
    /** Map level file. */
    private final Media mapFile;
    /** Lines per raster. */
    private final int linesPerRaster;
    /** Raster line offset. */
    private final int rasterLineOffset;
    /** Map raster folder. */
    private final Optional<String> rasterFolder;
    /** Background type. */
    private final BackgroundType background;
    /** Foreground config. */
    private final ForegroundConfig foreground;
    /** Boss tile. */
    private final Optional<Coord> boss;
    /** Boss spawn tile. */
    private final Optional<Coord> bossSpawn;
    /** Boss next stage. */
    private final Optional<String> bossNext;
    /** Checkpoints tiles. */
    private final List<Checkpoint> checkpoints = new ArrayList<>();
    /** Entities configuration. */
    private final List<EntityConfig> entities = new ArrayList<>();

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

        pic = configurer.getMediaOptional(ATT_STAGE_PIC);
        text = configurer.getStringOptional(ATT_STAGE_TEXT);

        music = configurer.getMedia(ATT_FILE, NODE_MUSIC);

        mapFile = configurer.getMedia(ATT_FILE, NODE_MAP);
        linesPerRaster = configurer.getInteger(2, ATT_MAP_LINES_PER_RASTER, NODE_MAP);
        rasterLineOffset = configurer.getInteger(1, ATT_MAP_RASTER_LINE_OFFSET, NODE_MAP);

        rasterFolder = configurer.getStringOptional(ATT_RASTER_FOLDER, NODE_RASTER);

        background = configurer.getEnum(BackgroundType.class, ATT_BACKGROUND_TYPE, NODE_BACKGROUND);
        foreground = ForegroundConfig.imports(configurer);

        if (configurer.hasAttribute(ATT_CHECKPOINT_TX, NODE_BOSS)
            && configurer.hasAttribute(ATT_CHECKPOINT_TY, NODE_BOSS))
        {
            boss = Optional.of(new Coord(configurer.getDouble(ATT_CHECKPOINT_TX, NODE_BOSS),
                                         configurer.getDouble(ATT_CHECKPOINT_TY, NODE_BOSS)));
            bossSpawn = Optional.of(new Coord(configurer.getDouble(ATT_BOSS_TSX, NODE_BOSS),
                                              configurer.getDouble(ATT_BOSS_TSY, NODE_BOSS)));
            bossNext = configurer.getStringOptional(ATT_CHECKPOINT_NEXT, NODE_BOSS);
        }
        else
        {
            boss = Optional.empty();
            bossSpawn = Optional.empty();
            bossNext = Optional.empty();
        }

        configurer.getChildren(NODE_CHECKPOINT, NODE_CHECKPOINTS).forEach(this::addCheckpoints);

        configurer.getChildren(EntityConfig.NODE_ENTITY, NODE_ENTITIES).forEach(this::addEntity);
    }

    /**
     * Add checkpoint from configuration.
     * 
     * @param checkpoint The checkpoint configuration.
     */
    private void addCheckpoints(XmlReader checkpoint)
    {
        final Optional<Coord> spawn;
        if (checkpoint.hasAttribute(ATT_SPAWN_TX) && checkpoint.hasAttribute(ATT_SPAWN_TY))
        {
            spawn = Optional.of(new Coord(checkpoint.getDouble(ATT_SPAWN_TX), checkpoint.getDouble(ATT_SPAWN_TY)));
        }
        else
        {
            spawn = Optional.empty();
        }
        checkpoints.add(new Checkpoint(checkpoint.getDouble(ATT_CHECKPOINT_TX),
                                       checkpoint.getDouble(ATT_CHECKPOINT_TY),
                                       checkpoint.getStringOptional(ATT_CHECKPOINT_NEXT),
                                       spawn));
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
     * Get the picture.
     * 
     * @return The picture.
     */
    public Optional<Media> getPic()
    {
        return pic;
    }

    /**
     * Get the text.
     * 
     * @return The text.
     */
    public Optional<String> getText()
    {
        return text;
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
     * Get the lines per raster.
     * 
     * @return The lines per raster.
     */
    public int getLinesPerRaster()
    {
        return linesPerRaster;
    }

    /**
     * Get the raster line offset.
     * 
     * @return The raster line offset.
     */
    public int getRasterLineOffset()
    {
        return rasterLineOffset;
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
     * Get the respawn locations.
     * 
     * @return The respawn locations.
     */
    public List<Checkpoint> getCheckpoints()
    {
        return checkpoints;
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
     * Get the boss spawn location.
     * 
     * @return The boss spawn location.
     */
    public Optional<Coord> getBossSpawn()
    {
        return bossSpawn;
    }

    /**
     * Get the boss next stage.
     * 
     * @return The next stage.
     */
    public Optional<String> getBossNext()
    {
        return bossNext;
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
