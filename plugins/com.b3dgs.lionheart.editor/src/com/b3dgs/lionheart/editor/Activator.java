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
package com.b3dgs.lionheart.editor;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.b3dgs.lionengine.Version;
import com.b3dgs.lionheart.Constant;

/**
 * Plugin activator.
 */
public class Activator implements BundleActivator
{
    /** Plugin name. */
    public static final String PLUGIN_NAME = Constant.PROGRAM_NAME + " Editor";
    /** Plugin version. */
    public static final Version PLUGIN_VERSION = Constant.PROGRAM_VERSION;
    /** Plugin website. */
    public static final String PLUGIN_WEBSITE = "https://www.b3dgs.com";
    /** Plugin ID. */
    public static final String PLUGIN_ID = "com.b3dgs.lionheart.editor";

    /**
     * Constructor.
     */
    public Activator()
    {
        super();
    }

    @Override
    public void start(BundleContext bundleContext) throws Exception
    {
        // Nothing to do
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception
    {
        // Nothing to do
    }
}
