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

import java.util.Locale;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionheart.constant.Extension;
import com.b3dgs.lionheart.constant.Folder;

/**
 * List of available musics.
 * <p>
 * Music file name is enum name in lower case.
 * </p>
 */
public enum Music
{
    /** Swamp music. */
    SWAMP,
    /** Spider cave music. */
    SPIDERCAVE,
    /** Ancient Town music. */
    ANCIENTTOWN,
    /** Lava music. */
    LAVA,
    /** Airship music. */
    AIRSHIP,
    /** Dragonfly music. */
    DRAGONFLY,
    /** Tower music. */
    TOWER,
    /** Secret music. */
    SECRET,
    /** Intro music. */
    INTRO,
    /** Boss music. */
    BOSS,
    /** Boss win music. */
    BOSS_WIN,
    /** Secret amulet. */
    SECRET_WIN,
    /** Norka music. */
    NORKA,
    /** Norka win music. */
    NORKA_WIN,
    /** Extro music. */
    EXTRO,
    /** Extro alternative music. */
    EXTRO_ALTERNATIVE,
    /** Credits music. */
    CREDITS;

    /** The associated media. */
    private final Media media;

    /**
     * Create music.
     */
    Music()
    {
        media = Medias.create(Folder.MUSICS, name().toLowerCase(Locale.ENGLISH) + Extension.MUSIC);
    }

    /**
     * Get the music media.
     * 
     * @return The music media.
     */
    public Media get()
    {
        return media;
    }
}
