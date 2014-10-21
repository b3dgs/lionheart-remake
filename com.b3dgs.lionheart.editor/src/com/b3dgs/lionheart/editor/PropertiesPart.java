/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.editor;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.b3dgs.lionengine.editor.properties.PropertiesListener;
import com.b3dgs.lionengine.game.configurer.Configurer;

/**
 * Element properties part.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PropertiesPart
        implements PropertiesListener
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.properties";

    /**
     * Create the patrol properties.
     * 
     * @param configurer The configurer reference.
     * @param parent The parent reference.
     */
    private static void createPatrol(Configurer configurer, Composite parent)
    {
        final Composite patrol = new Composite(parent, SWT.NONE);
        patrol.setLayout(new GridLayout(1, false));
    }

    /** The properties area. */
    private Composite properties;

    /**
     * Create the composite.
     * 
     * @param parent The parent reference.
     */
    @PostConstruct
    public void createComposite(Composite parent)
    {
        properties = new Composite(parent, SWT.NONE);
        properties.setLayout(new GridLayout(1, false));
    }

    /*
     * PropertiesListener
     */

    @Override
    public void setInput(Configurer configurer)
    {
        for (final Control control : properties.getChildren())
        {
            control.dispose();
        }
        if (configurer != null)
        {
            createPatrol(configurer, properties);
        }
    }
}
