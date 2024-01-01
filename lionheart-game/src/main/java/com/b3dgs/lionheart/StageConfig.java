/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.AttributesReader;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.XmlReader;
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
    /** Spawns node name. */
    public static final String NODE_SPAWNS = "spawns";

    /** Stage picture attribute name. */
    public static final String ATT_STAGE_PIC = "pic";
    /** Stage text attribute name. */
    public static final String ATT_STAGE_TEXT = "text";
    /** File attribute name. */
    public static final String ATT_FILE = "file";
    /** Reload attribute name. */
    public static final String ATT_RELOAD = "reload";
    /** Reload minimum horizontal location attribute name. */
    public static final String ATT_RELOAD_MIN_X = "reloadMinX";
    /** Reload maximum horizontal location attribute name. */
    public static final String ATT_RELOAD_MAX_X = "reloadMaxX";

    /** Music node name. */
    public static final String NODE_MUSIC = "music";

    /** Map node name. */
    public static final String NODE_MAP = "map";
    /** Lines per raster attribute name. */
    public static final String ATT_MAP_LINES_PER_RASTER = "linesPerRaster";
    /** Raster line offset attribute name. */
    public static final String ATT_MAP_RASTER_LINE_OFFSET = "rasterLineOffset";

    /** Raster folder node name. */
    public static final String NODE_RASTER = "raster";
    /** Raster folder attribute name. */
    public static final String ATT_RASTER_FOLDER = "folder";

    /** Background node name. */
    public static final String NODE_BACKGROUND = "background";
    /** Background type attribute name. */
    public static final String ATT_BACKGROUND_TYPE = "type";

    /** Checkpoints node name. */
    public static final String NODE_CHECKPOINTS = "checkpoints";
    /** Checkpoint node name. */
    public static final String NODE_CHECKPOINT = "checkpoint";
    /** Checkpoint tile x attribute name. */
    public static final String ATT_CHECKPOINT_TX = "tx";
    /** Checkpoint tile y attribute name. */
    public static final String ATT_CHECKPOINT_TY = "ty";
    /** Checkpoint next stage attribute name. */
    public static final String ATT_CHECKPOINT_NEXT = "next";
    /** Spawn tile x attribute name. */
    public static final String ATT_SPAWN_TX = "stx";
    /** Spawn tile y attribute name. */
    public static final String ATT_SPAWN_TY = "sty";

    /** Boss node name. */
    public static final String NODE_BOSS = "boss";
    /** Boss spawn tile x attribute name. */
    public static final String ATT_BOSS_TSX = "tsx";
    /** Boss spawn tile y attribute name. */
    public static final String ATT_BOSS_TSY = "tsy";

    /**
     * Imports the config from configurer.
     * 
     * @param root The configurer reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static StageConfig imports(AttributesReader root)
    {
        return new StageConfig(root);
    }

    /** Picture file. */
    private final Optional<Media> pic;
    /** Text. */
    private final Optional<String> text;
    /** Music file. */
    private final Media music;
    /** Reload flag. */
    private final boolean reload;
    /** Reload min X. */
    private final int reloadMinX;
    /** Reload max X. */
    private final int reloadMaxX;
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
    /** Spawns configuration. */
    private final List<SpawnConfig> spawns = new ArrayList<>();

    /**
     * Create config.
     * 
     * @param root The configurer reference.
     * @throws LionEngineException If unable to read node.
     */
    private StageConfig(AttributesReader root)
    {
        super();

        Check.notNull(root);

        pic = root.getMediaOptional(ATT_STAGE_PIC);
        text = root.getStringOptional(ATT_STAGE_TEXT);
        reload = root.getBoolean(false, ATT_RELOAD);
        reloadMinX = root.getInteger(0, ATT_RELOAD_MIN_X);
        reloadMaxX = root.getInteger(Integer.MAX_VALUE, ATT_RELOAD_MAX_X);

        music = root.getMedia(ATT_FILE, NODE_MUSIC);

        mapFile = root.getMedia(ATT_FILE, NODE_MAP);
        linesPerRaster = root.getInteger(2, ATT_MAP_LINES_PER_RASTER, NODE_MAP);
        rasterLineOffset = root.getInteger(1, ATT_MAP_RASTER_LINE_OFFSET, NODE_MAP);

        rasterFolder = root.getStringOptional(ATT_RASTER_FOLDER, NODE_RASTER);

        background = root.getEnum(BackgroundType.class, ATT_BACKGROUND_TYPE, NODE_BACKGROUND);
        foreground = ForegroundConfig.imports(root);

        if (root.hasAttribute(ATT_CHECKPOINT_TX, NODE_BOSS) && root.hasAttribute(ATT_CHECKPOINT_TY, NODE_BOSS))
        {
            boss = Optional.of(new Coord(root.getDouble(ATT_CHECKPOINT_TX, NODE_BOSS),
                                         root.getDouble(ATT_CHECKPOINT_TY, NODE_BOSS)));
            bossSpawn = Optional.of(new Coord(root.getDouble(ATT_BOSS_TSX, NODE_BOSS),
                                              root.getDouble(ATT_BOSS_TSY, NODE_BOSS)));
            bossNext = root.getStringOptional(ATT_CHECKPOINT_NEXT, NODE_BOSS);
        }
        else
        {
            boss = Optional.empty();
            bossSpawn = Optional.empty();
            bossNext = Optional.empty();
        }

        root.getChildren(NODE_CHECKPOINT, NODE_CHECKPOINTS).forEach(this::addCheckpoints);

        root.getChildren(EntityConfig.NODE_ENTITY, NODE_ENTITIES).forEach(this::addEntity);

        root.getChildren(SpawnConfig.NODE_SPAWN, NODE_SPAWNS).forEach(this::addSpawn);
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
     * Add spawns from configuration.
     * 
     * @param spawn The spawn configuration.
     */
    private void addSpawn(XmlReader spawn)
    {
        spawns.add(SpawnConfig.imports(spawn));
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
     * Get the reload flag.
     * 
     * @return The reload flag.
     */
    public boolean getReload()
    {
        return reload;
    }

    /**
     * Get the reload min X.
     * 
     * @return The reload min X.
     */
    public int getReloadMinX()
    {
        return reloadMinX;
    }

    /**
     * Get the reload max X.
     * 
     * @return The reload max X.
     */
    public int getReloadMaxX()
    {
        return reloadMaxX;
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

    /**
     * Get the spawns.
     * 
     * @return The spawns.
     */
    public List<SpawnConfig> getSpawns()
    {
        return spawns;
    }
}
