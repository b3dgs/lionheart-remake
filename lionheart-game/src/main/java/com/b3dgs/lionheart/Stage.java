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

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionheart.constant.Extension;
import com.b3dgs.lionheart.constant.Folder;

/**
 * List of stages.
 */
public enum Stage implements Media
{
    /** First swamp stage. */
    STAGE_1,
    /** First spider cave stage. */
    STAGE_2,
    /** Second swamp stage. */
    STAGE_3,
    /** Second spider cave stage. */
    STAGE_4,
    /** Third swamp stage. */
    STAGE_5,
    /** First ancient town stage. */
    STAGE_6,
    /** Second ancient town stage. */
    STAGE_7,
    /** Third ancient town stage. */
    STAGE_8,
    /** Lava stage. */
    STAGE_9,
    /** Airship stage. */
    STAGE_11,
    /** Dragonfly stage. */
    STAGE_12,
    /** Tower stage. */
    STAGE_13,
    /** Norka stage. */
    STAGE_14,
    /** Secret stage. */
    STAGE_10;

    /** Level file. */
    private final Media file = Medias.create(Folder.STAGE,
                                             name().toLowerCase(Locale.ENGLISH)
                                                   .replace(Constant.UNDERSCORE, Constant.EMPTY_STRING)
                                                           + Extension.STAGE);

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
