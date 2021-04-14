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
import com.b3dgs.lionengine.UtilRandom;
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
    /** Menu select. */
    MENU_SELECT,
    /** Valdyn sword attack. */
    VALDYN_SWORD,
    /** Valdyn hurt. */
    VALDYN_HURT,
    /** Valdyn die. */
    VALDYN_DIE,
    /** Item potion little taken. */
    ITEM_POTIONLITTLE,
    /** Item potion big taken. */
    ITEM_POTIONBIG,
    /** Item taken other. */
    ITEM_TAKEN,
    /** Scenery dragon tongue. */
    SCENERY_DRAGON,
    /** Scenery turning. */
    SCENERY_TURNING,
    /** Scenery turning cube. */
    SCENERY_TURNINGCUBE,
    /** Scenery rotating platform. */
    SCENERY_ROTATINGPLATFORM,
    /** Scenery spike. */
    SCENERY_SPIKE,
    /** Scenery melting platform. */
    SCENERY_MELTINGPLATFORM,
    /** Scenery hot fire ball. */
    SCENERY_HOTFIREBALL,
    /** Scenery fire ball. */
    SCENERY_FIREBALL,
    /** Scenery geyzer. */
    SCENERY_GEYZER,
    /** Scenery geyzer platform. */
    SCENERY_GEYZERPLATFORM,
    /** Monster hurt. */
    MONSTER_HURT,
    /** Monster grasshopper. */
    MONSTER_GRASSHOPER,
    /** Monster land. */
    MONSTER_LAND,
    /** Monster spider. */
    MONSTER_SPIDER,
    /** Monster canon1 fire. */
    MONSTER_CANON1,
    /** Monster canon2 fire. */
    MONSTER_CANON2,
    /** Monster canon3 fire. */
    MONSTER_CANON3,
    /** Monster goblin rise. */
    MONSTER_GOBELIN,
    /** Monster executioner hurt. */
    MONSTER_EXECUTIONER_HURT,
    /** Monster executioner attack. */
    MONSTER_EXECUTIONER_ATTACK,
    /** Monster dragon ball. */
    MONSTER_DRAGONBALL,
    /** Monster frog. */
    MONSTER_FROG,
    /** Monster wizard. */
    MONSTER_WIZARD,
    /** Effect explode 1. */
    EFFECT_EXPLODE1,
    /** Effect explode 2. */
    EFFECT_EXPLODE2,
    /** Effect explode 3. */
    EFFECT_EXPLODE3,
    /** Projectile flower. */
    PROJECTILE_FLOWER,
    /** Projectile fly. */
    PROJECTILE_FLY,
    /** Projectile canon 2 bounce. */
    PROJECTILE_BULLET2,
    /** Boss 1 hurt. */
    BOSS1_HURT,
    /** Boss 1 bowl. */
    BOSS1_BOWL,
    /** Boss 2. */
    BOSS2,
    /** Boss 3 jump. */
    BOSS3_JUMP,
    /** Boss 3 hurt. */
    BOSS3_HURT,
    /** Boss flyer. */
    BOSS_FLYER,
    /** Boss daemon fire. */
    BOSS_DAEMON_FIRE,
    /** Boss daemon land. */
    BOSS_DAEMON_LAND,
    /** Boss norka platform. */
    BOSS_NORKA_PLATFORM,
    /** Boss norka fire. */
    BOSS_NORKA_FIRE,
    /** Boss flyer. */
    BOSS_NORKA_HURT;

    private static boolean cached;

    /**
     * Cache sfx start.
     */
    public static void cacheStart()
    {
        if (Settings.getInstance().getVolumeSfx() > 0 && !cached)
        {
            for (final Sfx sfx : Sfx.values())
            {
                sfx.audio.setVolume(0);
                sfx.play();
            }
        }
    }

    /**
     * Cache sfx end.
     */
    public static void cacheEnd()
    {
        final int volume = Settings.getInstance().getVolumeSfx();
        if (volume > 0 && !cached)
        {
            for (final Sfx sfx : Sfx.values())
            {
                sfx.audio.await();
                sfx.audio.setVolume(volume);
            }
            cached = true;
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
            EFFECT_EXPLODE1.play();
        }
        else if (id == 1)
        {
            EFFECT_EXPLODE2.play();
        }
        else if (id == 2)
        {
            EFFECT_EXPLODE3.play();
        }
    }

    /** Audio handler. */
    private final Audio audio;

    /**
     * Create Sfx.
     */
    Sfx()
    {
        final Media media = Medias.create(Folder.SFX, name().toLowerCase(Locale.ENGLISH) + Extension.SFX);
        audio = AudioFactory.loadAudio(media);
    }

    /**
     * Play sound.
     */
    public void play()
    {
        if (Settings.getInstance().getVolumeSfx() > 0)
        {
            audio.play();
        }
    }

    /**
     * Stop sound.
     */
    public void stop()
    {
        audio.stop();
    }
}
