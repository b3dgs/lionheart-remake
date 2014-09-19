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

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * Represents the checkpoints list, including starting and ending points.
 * 
 * @author Pierre-Alexandre
 */
public class CheckpointsPart
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.checkpoints";

    /**
     * Create the composite.
     * 
     * @param parent The parent reference.
     * @param menuService The menu service reference.
     */
    @PostConstruct
    public void createComposite(Composite parent, EMenuService menuService)
    {
        final Group infos = new Group(parent, SWT.NONE);
        infos.setLayout(new GridLayout(1, false));
        infos.setText(Messages.Checkpoints_Infos);

        final Composite buttons = new Composite(parent, SWT.NONE);
        buttons.setLayout(new GridLayout(1, false));

        final Button placeStart = new Button(parent, SWT.TOGGLE);
        placeStart.setText(Messages.Checkpoints_Start);
        placeStart.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                // TODO set palette to start point selection
            }
        });

        final Button placeEnd = new Button(parent, SWT.TOGGLE);
        placeEnd.setText(Messages.Checkpoints_End);
        placeEnd.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                // TODO set palette to end point selection
            }
        });

        final Button placeCheckpoint = new Button(parent, SWT.TOGGLE);
        placeCheckpoint.setText(Messages.Checkpoints_Place);
        placeCheckpoint.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                // TODO set palette to checkpoints selection
            }
        });
    }

    /**
     * Set the focus.
     */
    @Focus
    public void setFocus()
    {
        // Nothing to do
    }
}
