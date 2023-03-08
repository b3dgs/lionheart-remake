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

import java.io.IOException;

import com.b3dgs.lionengine.Verbose;

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
     * <ul>
     * <li>custom lionheart.properties [string]</li>
     * <li>input.xml [string]</li>
     * <li>{@link GameType} [string])</li>
     * <li>Stage [string])</li>
     * <li>{@link Difficulty} [string])</li>
     * <li>players number [int]</li>
     * </ul>
     * 
     * @param args The arguments.
     * @throws IOException If error.
     */
    public static void main(String[] args) throws IOException
    {
        try
        {
            System.setProperty("sun.java2d.uiScale", "1.0");
        }
        catch (final SecurityException exception)
        {
            Verbose.exception(exception);
        }

        if (args.length > 0)
        {
            AppLionheart.main(args);
        }
        else
        {
            Launcher.main(args);
        }
    }
}
