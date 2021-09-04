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
 * List of hard stages.
 */
public enum StageHard implements Media
{
    /** First swamp stage. */
    STAGE1,
    /** First spider cave stage. */
    STAGE2,
    /** Second swamp stage. */
    STAGE3,
    /** Second spider cave stage. */
    STAGE4,
    /** Third swamp stage. */
    STAGE5,
    /** First ancient town stage. */
    STAGE6,
    /** Second ancient town stage. */
    STAGE7,
    /** Third ancient town stage. */
    STAGE8,
    /** Lava stage. */
    STAGE9,
    /** Airship hard stage. */
    STAGE11,
    /** Dragonfly hard stage. */
    STAGE12,
    /** Tower hard stage. */
    STAGE13,
    /** Norka hard stage. */
    STAGE14,
    /** Secret hard stage. */
    STAGE10;

    /** Level file. */
    private final Media file = Medias.create(Folder.STAGE,
                                             Folder.ORIGINAL,
                                             name().toLowerCase(Locale.ENGLISH) + "_hard" + Extension.STAGE);

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
    public String getName()
    {
        return file.getName();
    }
}
