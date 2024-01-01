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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
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
public enum Music implements Media
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
    private final Media media = Medias.create(Folder.MUSIC, name().toLowerCase(Locale.ENGLISH) + Extension.MUSIC);

    @Override
    public String getPath()
    {
        return media.getPath();
    }

    @Override
    public String getParentPath()
    {
        return media.getParentPath();
    }

    @Override
    public File getFile()
    {
        return media.getFile();
    }

    @Override
    public URL getUrl()
    {
        return media.getUrl();
    }

    @Override
    public Collection<Media> getMedias()
    {
        return media.getMedias();
    }

    @Override
    public InputStream getInputStream()
    {
        return media.getInputStream();
    }

    @Override
    public OutputStream getOutputStream()
    {
        return media.getOutputStream();
    }

    @Override
    public boolean exists()
    {
        return media.exists();
    }

    @Override
    public boolean isJar()
    {
        return media.isJar();
    }

    @Override
    public String getName()
    {
        return media.getName();
    }
}
