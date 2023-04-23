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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionheart.landscape.BackgroundType;
import com.b3dgs.lionheart.landscape.ForegroundConfig;
import com.b3dgs.lionheart.landscape.ForegroundType;

/**
 * Test {@link StageConfig}.
 */
final class StageConfigTest
{
    /**
     * Test import with all data.
     */
    @Test
    void testImportFull()
    {
        Medias.setLoadFromJar(StageConfigTest.class);

        final Xml root = new Xml(StageConfig.NODE_STAGE);
        root.writeString(StageConfig.ATT_STAGE_PIC, "pic");
        root.writeString(StageConfig.ATT_STAGE_TEXT, "txt");
        root.writeBoolean(StageConfig.ATT_RELOAD, true);
        root.writeInteger(StageConfig.ATT_RELOAD_MIN_X, 1);
        root.writeInteger(StageConfig.ATT_RELOAD_MAX_X, 2);

        root.createChild(StageConfig.NODE_MUSIC).writeString(StageConfig.ATT_FILE, "mus");

        final Xml map = root.createChild(StageConfig.NODE_MAP);
        map.writeString(StageConfig.ATT_FILE, "map");
        map.writeInteger(StageConfig.ATT_MAP_LINES_PER_RASTER, 3);
        map.writeInteger(StageConfig.ATT_MAP_RASTER_LINE_OFFSET, 4);

        root.createChild(StageConfig.NODE_RASTER).writeString(StageConfig.ATT_RASTER_FOLDER, "raster");

        root.createChild(StageConfig.NODE_BACKGROUND)
            .writeEnum(StageConfig.ATT_BACKGROUND_TYPE, BackgroundType.SWAMP_DUSK);

        final Xml foreground = root.createChild(ForegroundConfig.NODE_FOREGROUND);
        foreground.writeEnum(ForegroundConfig.ATT_FOREGROUND_TYPE, ForegroundType.WATER);
        foreground.writeInteger(ForegroundConfig.ATT_WATER_DEPTH, 1);
        foreground.writeDouble(ForegroundConfig.ATT_WATER_DEPTH_SPEED, 2.0);
        foreground.writeInteger(ForegroundConfig.ATT_WATER_OFFSET, 3);
        foreground.writeDouble(ForegroundConfig.ATT_WATER_SPEED, 4.0);
        foreground.writeBoolean(ForegroundConfig.ATT_WATER_EFFECT, false);
        foreground.writeInteger(ForegroundConfig.ATT_WATER_RAISE, 5);
        foreground.writeInteger(ForegroundConfig.ATT_WIDTH_MAX, 6);

        final Xml entities = root.createChild(StageConfig.NODE_ENTITIES);
        Xml entity = entities.createChild(EntityConfig.NODE_ENTITY);
        entity.writeString(EntityConfig.ATT_FILE, "entity/Bee.xml");
        entity.writeDouble(EntityConfig.ATT_TX, 1.0);
        entity.writeDouble(EntityConfig.ATT_TY, 2.0);

        final Xml spawns = root.createChild(StageConfig.NODE_SPAWNS);
        final Xml spawn = spawns.createChild(SpawnConfig.NODE_SPAWN);
        spawn.writeInteger(SpawnConfig.ATT_DELAY, 1);

        entity = entities.createChild(EntityConfig.NODE_ENTITY);
        entity.writeString(EntityConfig.ATT_FILE, "entity/Bee.xml");
        entity.writeDouble(EntityConfig.ATT_TX, 1.0);
        entity.writeDouble(EntityConfig.ATT_TY, 2.0);
        spawn.add(entity);

        final Xml checkpoints = root.createChild(StageConfig.NODE_CHECKPOINTS);
        final Xml checkpoint = checkpoints.createChild(StageConfig.NODE_CHECKPOINT);
        checkpoint.writeDouble(StageConfig.ATT_CHECKPOINT_TX, 5.0);
        checkpoint.writeDouble(StageConfig.ATT_CHECKPOINT_TY, 6.0);
        checkpoint.writeDouble(StageConfig.ATT_SPAWN_TX, 7.0);
        checkpoint.writeDouble(StageConfig.ATT_SPAWN_TY, 8.0);
        checkpoint.writeString(StageConfig.ATT_CHECKPOINT_NEXT, "next");

        final Xml checkpoint2 = checkpoints.createChild(StageConfig.NODE_CHECKPOINT);
        checkpoint2.writeDouble(StageConfig.ATT_CHECKPOINT_TX, 50.0);
        checkpoint2.writeDouble(StageConfig.ATT_CHECKPOINT_TY, 60.0);

        final Xml boss = root.createChild(StageConfig.NODE_BOSS);
        boss.writeDouble(StageConfig.ATT_CHECKPOINT_TX, 9.0);
        boss.writeDouble(StageConfig.ATT_CHECKPOINT_TY, 10.0);
        boss.writeDouble(StageConfig.ATT_BOSS_TSX, 11.0);
        boss.writeDouble(StageConfig.ATT_BOSS_TSY, 12.0);
        boss.writeString(StageConfig.ATT_CHECKPOINT_NEXT, "nextBoss");

        final StageConfig config = StageConfig.imports(root);

        assertEquals("pic", config.getPic().get().getPath());
        assertEquals("txt", config.getText().get());
        assertTrue(config.getReload());
        assertEquals(1, config.getReloadMinX());
        assertEquals(2, config.getReloadMaxX());
        assertEquals("mus", config.getMusic().getPath());
        assertEquals("map", config.getMapFile().getPath());
        assertEquals(3, config.getLinesPerRaster());
        assertEquals(4, config.getRasterLineOffset());
        assertEquals("raster", config.getRasterFolder().get());
        assertEquals(BackgroundType.SWAMP_DUSK, config.getBackground());

        final ForegroundConfig f = config.getForeground();
        assertEquals(ForegroundType.WATER, f.getType());
        assertEquals(1, f.getWaterDepth().getAsInt());
        assertEquals(2.0, f.getWaterDepthSpeed().getAsDouble());
        assertEquals(3, f.getWaterOffset().getAsInt());
        assertEquals(4.0, f.getWaterSpeed().getAsDouble());
        assertFalse(f.getWaterEffect());
        assertEquals(5, f.getWaterRaise());
        assertEquals(6, f.getWidthMax().getAsInt());

        assertEquals(1, config.getSpawns().iterator().next().getDelay());

        final MapTile m = new MapTileGame();
        m.create(16, 16, 4, 4);

        EntityConfig e = config.getSpawns().iterator().next().getEntities().iterator().next();
        assertEquals("entity/Bee.xml", e.getMedia().getPath());
        assertEquals(16.0, e.getSpawnX(m));
        assertEquals(16.0, e.getSpawnY(m));

        e = config.getEntities().iterator().next();
        assertEquals("entity/Bee.xml", e.getMedia().getPath());
        assertEquals(16.0, e.getSpawnX(m));
        assertEquals(16.0, e.getSpawnY(m));

        final Checkpoint c = config.getCheckpoints().iterator().next();
        assertEquals(5.0, c.getTx());
        assertEquals(6.0, c.getTy());
        assertEquals(7.0, c.getSpawn().get().getX());
        assertEquals(8.0, c.getSpawn().get().getY());
        assertEquals("next", c.getNext().get());

        final Checkpoint c2 = config.getCheckpoints().get(1);
        assertEquals(50.0, c2.getTx());
        assertEquals(60.0, c2.getTy());
        assertFalse(c2.getSpawn().isPresent());
        assertFalse(c2.getNext().isPresent());

        assertEquals(9.0, config.getBoss().get().getX());
        assertEquals(10.0, config.getBoss().get().getY());
        assertEquals(11.0, config.getBossSpawn().get().getX());
        assertEquals(12.0, config.getBossSpawn().get().getY());
        assertEquals("nextBoss", config.getBossNext().get());
    }

    /**
     * Test import with minimum data.
     */
    @Test
    void testImportMinimum()
    {
        Medias.setLoadFromJar(StageConfigTest.class);

        final Xml root = new Xml(StageConfig.NODE_STAGE);

        root.createChild(StageConfig.NODE_MUSIC).writeString(StageConfig.ATT_FILE, "mus");

        final Xml map = root.createChild(StageConfig.NODE_MAP);
        map.writeString(StageConfig.ATT_FILE, "map");

        root.createChild(StageConfig.NODE_BACKGROUND)
            .writeEnum(StageConfig.ATT_BACKGROUND_TYPE, BackgroundType.SWAMP_DUSK);

        final Xml foreground = root.createChild(ForegroundConfig.NODE_FOREGROUND);
        foreground.writeEnum(ForegroundConfig.ATT_FOREGROUND_TYPE, ForegroundType.WATER);

        final StageConfig config = StageConfig.imports(root);

        assertFalse(config.getPic().isPresent());
        assertFalse(config.getText().isPresent());
        assertFalse(config.getReload());
        assertEquals(0, config.getReloadMinX());
        assertEquals(Integer.MAX_VALUE, config.getReloadMaxX());
        assertEquals("mus", config.getMusic().getPath());
        assertEquals("map", config.getMapFile().getPath());
        assertEquals(2, config.getLinesPerRaster());
        assertEquals(1, config.getRasterLineOffset());
        assertFalse(config.getRasterFolder().isPresent());
        assertEquals(BackgroundType.SWAMP_DUSK, config.getBackground());

        final ForegroundConfig f = config.getForeground();
        assertEquals(ForegroundType.WATER, f.getType());
        assertFalse(f.getWaterDepth().isPresent());
        assertFalse(f.getWaterDepthSpeed().isPresent());
        assertFalse(f.getWaterOffset().isPresent());
        assertFalse(f.getWaterSpeed().isPresent());
        assertTrue(f.getWaterEffect());
        assertEquals(0, f.getWaterRaise());
        assertFalse(f.getWidthMax().isPresent());

        assertTrue(config.getCheckpoints().isEmpty());

        assertFalse(config.getBoss().isPresent());
        assertFalse(config.getBossSpawn().isPresent());
        assertFalse(config.getBossNext().isPresent());
    }
}
