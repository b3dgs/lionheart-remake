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
package com.b3dgs.lionheart.constant;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFolder;

/**
 * Folder constants.
 */
public final class Folder
{
    /** Sprites folder. */
    public static final String SPRITES = "sprite";
    /** Menu folder. */
    public static final String MENU = "menu";
    /** Extro folder. */
    public static final String EXTRO = "extro";
    /** Backgrounds folder. */
    public static final String BACKGROUNDS = "background";
    /** Backgrounds folder. */
    public static final String FOREGROUNDS = "foreground";
    /** Entities folder. */
    public static final String ENTITIES = "entity";
    /** Boss folder. */
    public static final String BOSS = UtilFolder.getPathSeparator(Medias.getSeparator(), ENTITIES, "boss");
    /** Items folder. */
    public static final String ITEMS = UtilFolder.getPathSeparator(Medias.getSeparator(), ENTITIES, "item");
    /** Monsters folder. */
    public static final String MONSTERS = UtilFolder.getPathSeparator(Medias.getSeparator(), ENTITIES, "monster");
    /** Sceneries folder. */
    public static final String SCENERIES = UtilFolder.getPathSeparator(Medias.getSeparator(), ENTITIES, "scenery");
    /** Players folder. */
    public static final String PLAYERS = UtilFolder.getPathSeparator(Medias.getSeparator(), ENTITIES, "player");
    /** Effects folder. */
    public static final String EFFECTS = "effect";
    /** Projectiles folder. */
    public static final String PROJECTILES = "projectile";
    /** Levels folder. */
    public static final String LEVELS = "levels";
    /** Stages folder. */
    public static final String STAGES = "stage";
    /** Sounds folder. */
    public static final String SOUNDS = "sfx";
    /** Musics folder. */
    public static final String MUSICS = "music";

    /**
     * Private constructor.
     */
    private Folder()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
