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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionheart.constant.Extension;
import com.b3dgs.lionheart.constant.Folder;

/**
 * List of stages with their file.
 */
public enum Stage
{
    /** First swamp stage. */
    STAGE_1("stage1"),
    /** First spider cave stage. */
    STAGE_2("stage2"),
    /** Second swamp stage. */
    STAGE_3("stage3"),
    /** Second spider cave stage. */
    STAGE_4("stage4"),
    /** Third swamp stage. */
    STAGE_5("stage5"),
    /** First ancient town stage. */
    STAGE_6("stage6"),
    /** Second ancient town stage. */
    STAGE_7("stage7"),
    /** Third ancient town stage. */
    STAGE_8("stage8"),
    /** Lava stage. */
    STAGE_9("stage9"),
    /** Secret stage. */
    STAGE_10("stage10"),
    /** Airship stage. */
    STAGE_11("stage11"),
    /** Dragonfly stage. */
    STAGE_12("stage12"),
    /** Tower stage. */
    STAGE_13("stage13"),
    /** Norka stage. */
    STAGE_14("stage14");

    /** Level file. */
    private final Media file;

    /**
     * Create the stage.
     * 
     * @param file The stage file name without extension.
     */
    Stage(String file)
    {
        this.file = Medias.create(Folder.STAGES, file + Extension.STAGE);
    }

    /**
     * Get the stage data file.
     * 
     * @return The stage data file.
     */
    public Media getFile()
    {
        return file;
    }
}
