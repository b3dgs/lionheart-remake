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

import java.util.Locale;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionheart.constant.Extension;
import com.b3dgs.lionheart.constant.Folder;

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
    /** Item potion full. */
    ITEM_POTION_FULL,
    /** Item talisment. */
    ITEM_TAKEN,
    /** Monster hurt. */
    MONSTER_HURT,
    /** Explode 1. */
    EXPLODE1,
    /** Explode 2. */
    EXPLODE2,
    /** Explode 3. */
    EXPLODE3,
    /** Spike. */
    SPIKE,
    /** Enemy flower. */
    ENEMY_FLOWER,
    /** Enemy throws fly. */
    ENEMY_INSEKT,
    /** Grasshopper jump. */
    GRASSHOPPER_JUMP;

    /**
     * Cache sfx.
     */
    public static void cache()
    {
        if (!Constant.AUDIO_MUTE)
        {
            for (final Sfx sfx : Sfx.values())
            {
                sfx.audio.setVolume(0);
                sfx.play();
            }
            try
            {
                Thread.sleep(com.b3dgs.lionengine.Constant.HUNDRED / 2);
            }
            catch (final InterruptedException exception)
            {
                Verbose.exception(exception);
            }
            for (final Sfx sfx : Sfx.values())
            {
                sfx.audio.stop();
                sfx.audio.setVolume(50);
            }
        }
    }

    /**
     * Play a random explode sound.
     */
    public static void playRandomExplode()
    {
        final int id = UtilRandom.getRandomInteger(2);
        if (id == 0)
        {
            EXPLODE1.play();
        }
        else if (id == 1)
        {
            EXPLODE2.play();
        }
        else if (id == 2)
        {
            EXPLODE3.play();
        }
    }

    /** Audio handler. */
    private final Audio audio;

    /**
     * Create Sfx.
     */
    Sfx()
    {
        final Media media = Medias.create(Folder.SOUNDS, name().toLowerCase(Locale.ENGLISH) + Extension.SFX);
        audio = AudioFactory.loadAudio(media);
    }

    /**
     * Play sound.
     */
    public void play()
    {
        if (!Constant.AUDIO_MUTE)
        {
            audio.play();
        }
    }
}
