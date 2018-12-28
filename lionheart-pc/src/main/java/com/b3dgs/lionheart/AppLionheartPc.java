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

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.awt.graphic.EngineAwt;
import com.b3dgs.lionengine.graphic.engine.Loader;

/**
 * Program starts here.
 */
public final class AppLionheartPc
{
    /**
     * Main function.
     * 
     * @param args The arguments (none).
     */
    public static void main(String[] args) // CHECKSTYLE IGNORE LINE: TrailingComment|UncommentedMain
    {
        EngineAwt.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, AppLionheartPc.class);
        Loader.start(Config.windowed(Constant.DEFAULT_RESOLUTION.get3x()), Loading.class);
    }

    /**
     * Private constructor.
     */
    private AppLionheartPc()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
