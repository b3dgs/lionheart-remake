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

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.editor.UtilSwt;
import com.b3dgs.lionengine.editor.world.ObjectSelectionListener;
import com.b3dgs.lionengine.game.ObjectGame;
import com.b3dgs.lionheart.entity.Patrol;
import com.b3dgs.lionheart.entity.Patroller;

/**
 * Element properties part.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PropertiesPart
        implements ObjectSelectionListener
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.properties";

    /**
     * Create the patrol properties.
     * 
     * @param patroller The patroller reference.
     * @param parent The parent reference.
     */
    private static void createPatrol(Patroller patroller, Composite parent)
    {
        final Composite patrol = new Composite(parent, SWT.NONE);
        patrol.setLayout(new GridLayout(1, false));

        final Combo type = UtilSwt.createCombo(parent, Patrol.values());
        type.setText(patroller.getPatrolType().name());
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
     * ObjectSelectionListener
     */

    @Override
    public void notifyObjectSelected(ObjectGame object)
    {
        if (object instanceof Patroller)
        {
            createPatrol((Patroller) object, properties);
        }
    }

    @Override
    public void notifyObjectsSelected(Collection<ObjectGame> objects)
    {
        // Nothing to do
    }
}
