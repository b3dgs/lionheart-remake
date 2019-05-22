/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionheart;

import java.util.Locale;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;

/**
 * List of available sounds fx.
 * <p>
 * Sound file name is enum name in lower case.
 * </p>
 */
public enum Sfx
{
    /** Sword attack. */
    VALDYN_ATTACK,
    /** Valdyn hurt. */
    VALDYN_HURT,
    /** Valdyn die. */
    VALDYN_DIE,
    /** Item potion little. */
    ITEM_POTION,
    /** Item talisment. */
    ITEM_TAKEN,
    /** Monster hurt. */
    MONSTER_HURT;

    /** Audio handler. */
    private final Audio audio;

    /**
     * Create Sfx.
     */
    Sfx()
    {
        audio = AudioFactory.loadAudio(get());
    }

    /**
     * Get the music media.
     * 
     * @return The music media.
     */
    public Media get()
    {
        return Medias.create(Constant.FOLDER_SOUNDS, name().toLowerCase(Locale.ENGLISH) + Constant.EXTENSION_SFX);
    }

    /**
     * Play sound.
     */
    public void play()
    {
        audio.play();
    }
}
