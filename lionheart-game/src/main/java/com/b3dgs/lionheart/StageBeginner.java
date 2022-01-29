/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * List of beginner stages.
 */
public enum StageBeginner implements Media
{
    /** Swamp stage. */
    STAGE1,
    /** Spider cave stage. */
    STAGE2,
    /** Underworld 1 stage. */
    STAGE3,
    /** Underworld 2 stage. */
    STAGE4,
    /** Underworld 3 stage. */
    STAGE5,
    /** Ancient Town stage. */
    STAGE6,
    /** Lava stage. */
    STAGE7,
    /** Secret stage. */
    STAGE8,
    /** Airship stage. */
    STAGE9,
    /** Dragonfly stage. */
    STAGE10,
    /** Tower stage. */
    STAGE11,
    /** Norka stage. */
    STAGE12;

    /** Level file. */
    private final Media file = Medias.create(Folder.STAGE,
                                             Folder.BEGINNER,
                                             name().toLowerCase(Locale.ENGLISH) + Extension.STAGE);

    @Override
    public String getPath()
    {
        return file.getPath();
    }

    @Override
    public String getParentPath()
    {
        return file.getParentPath();
    }

    @Override
    public File getFile()
    {
        return file.getFile();
    }

    @Override
    public URL getUrl()
    {
        return file.getUrl();
    }

    @Override
    public Collection<Media> getMedias()
    {
        return file.getMedias();
    }

    @Override
    public InputStream getInputStream()
    {
        return file.getInputStream();
    }

    @Override
    public OutputStream getOutputStream()
    {
        return file.getOutputStream();
    }

    @Override
    public boolean exists()
    {
        return file.exists();
    }

    @Override
    public boolean isJar()
    {
        return file.isJar();
    }

    @Override
    public String getName()
    {
        return file.getName();
    }
}
