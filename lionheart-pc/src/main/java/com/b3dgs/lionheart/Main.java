/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.awt.graphic.EngineAwt;

/**
 * Main entry.
 */
public class Main
{
    /**
     * Main function.
     * <p>
     * No arguments to start with launcher.
     * </p>
     * <p>
     * Arguments to start game without launcher:
     * </p>
     * {@link AppLionheart#main(String[])}
     * 
     * @param args The arguments.
     */
    public static void main(String[] args)
    {
        Tools.disableAutoScale();

        EngineAwt.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, AppLionheart.class);

        if (args.length > 0 || new File(Medias.getResourcesDirectory(), Settings.FILENAME).exists())
        {
            AppLionheart.main(args);
        }
        else
        {
            Launcher.main(args);
        }
    }
}
