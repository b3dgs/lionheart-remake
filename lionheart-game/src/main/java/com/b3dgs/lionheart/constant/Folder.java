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

/**
 * Folder constants.
 */
public final class Folder
{
    /** Sprite folder. */
    public static final String SPRITE = "sprite";
    /** Intro folder. */
    public static final String INTRO = "intro";
    /** Extro folder. */
    public static final String EXTRO = "extro";
    /** Background folder. */
    public static final String BACKGROUND = "background";
    /** Background folder. */
    public static final String FOREGROUND = "foreground";
    /** Hero folder. */
    public static final String HERO = "hero";
    /** Entity folder. */
    public static final String ENTITY = "entity";
    /** Limb folder. */
    public static final String LIMB = "limb";
    /** Boss folder. */
    public static final String BOSS = "boss";
    /** Effect folder. */
    public static final String EFFECT = "effect";
    /** Projectile folder. */
    public static final String PROJECTILE = "projectile";
    /** Level folder. */
    public static final String LEVEL = "level";
    /** Stage folder. */
    public static final String STAGE = "stage";
    /** Stage original folder. */
    public static final String ORIGINAL = "original";
    /** Stage veteran folder. */
    public static final String VETERAN = "veteran";
    /** Stage beginner folder. */
    public static final String BEGINNER = "beginner";
    /** Text folder. */
    public static final String TEXT = "text";
    /** Sfx folder. */
    public static final String SFX = "sfx";
    /** Music folder. */
    public static final String MUSIC = "music";
    /** Menu folder. */
    public static final String MENU = "menu";
    /** Raster folder. */
    public static final String RASTER = "raster";

    /**
     * Private constructor.
     */
    private Folder()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
