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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.editor.UtilSwt;
import com.b3dgs.lionengine.editor.world.ObjectSelectionListener;
import com.b3dgs.lionengine.game.ObjectGame;
import com.b3dgs.lionheart.purview.patrol.Patrol;
import com.b3dgs.lionheart.purview.patrol.PatrolSide;
import com.b3dgs.lionheart.purview.patrol.PatrollerServices;

/**
 * Map object properties part.
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
    private static void createPatrol(PatrollerServices patroller, Composite parent)
    {
        final Group group = new Group(parent, SWT.NONE);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        group.setLayout(new GridLayout(1, false));
        group.setText("Patrol");

        createPatrolType(patroller, group);
        createPatrolSide(patroller, group);
        createPatrolSpeed(patroller, group);
        createPatrolPosition(patroller, group);
    }

    /**
     * Create the patrol type chooser.
     * 
     * @param patroller The patroller reference.
     * @param parent The composite reference.
     */
    private static void createPatrolType(final PatrollerServices patroller, Composite parent)
    {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        composite.setLayout(new GridLayout(2, false));

        final Label speedLabel = new Label(composite, SWT.NONE);
        speedLabel.setText("Type: ");

        final List<Patrol> patrols = new ArrayList<>();
        for (final Patrol current : Patrol.values())
        {
            if (patroller.isPatrolEnabled(current))
            {
                patrols.add(current);
            }
        }
        final Combo type = UtilSwt.createCombo(composite, patrols.toArray(new Patrol[patrols.size()]));
        final Patrol patrol = patroller.getPatrolType();
        type.setText(UtilConversion.toTitleCase(patrol.name()));
        type.setData(patrol);
        type.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent event)
            {
                patroller.setPatrolType((Patrol) type.getData());
            }
        });
    }

    /**
     * Create the patrol side chooser.
     * 
     * @param patroller The patroller reference.
     * @param parent The composite reference.
     */
    private static void createPatrolSide(final PatrollerServices patroller, Composite parent)
    {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        composite.setLayout(new GridLayout(2, false));

        final Label speedLabel = new Label(composite, SWT.NONE);
        speedLabel.setText("Side: ");

        final Combo side = UtilSwt.createCombo(composite, PatrolSide.values());
        side.setText(UtilConversion.toTitleCase(patroller.getFirstMove().name()));
        side.setData(patroller.getFirstMove());
        side.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent event)
            {
                patroller.setFirstMove((PatrolSide) side.getData());
            }
        });
    }

    /**
     * Create the patrol speed text.
     * 
     * @param patroller The patroller reference.
     * @param parent The composite reference.
     */
    private static void createPatrolSpeed(final PatrollerServices patroller, Composite parent)
    {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        composite.setLayout(new GridLayout(2, false));

        final Label speedLabel = new Label(composite, SWT.NONE);
        speedLabel.setText("Speed: ");

        final Text speed = new Text(composite, SWT.SINGLE);
        speed.setTextLimit(6);
        speed.setLayoutData(new GridData(48, 16));
        speed.setText(String.valueOf(patroller.getMoveSpeed()));
        speed.addVerifyListener(createVerify(speed, "[0-9]+"));
        speed.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent event)
            {
                patroller.setMoveSpeed(Integer.parseInt(speed.getText()));
            }
        });
    }

    /**
     * Create the patrol position range text.
     * 
     * @param patroller The patroller reference.
     * @param parent The composite reference.
     */
    private static void createPatrolPosition(final PatrollerServices patroller, Composite parent)
    {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        composite.setLayout(new GridLayout(2, false));

        final Label minLabel = new Label(composite, SWT.NONE);
        minLabel.setText("Min: ");

        final Text min = new Text(composite, SWT.SINGLE);
        min.setTextLimit(6);
        min.setLayoutData(new GridData(48, 16));
        min.setText(String.valueOf(patroller.getPatrolLeft()));
        min.addVerifyListener(createVerify(min, "[0-9]+"));
        min.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent event)
            {
                patroller.setPatrolLeft(Integer.parseInt(min.getText()));
            }
        });

        final Label maxLabel = new Label(composite, SWT.NONE);
        maxLabel.setText("Max: ");

        final Text max = new Text(composite, SWT.SINGLE);
        max.setTextLimit(6);
        max.setLayoutData(new GridData(48, 16));
        max.setText(String.valueOf(patroller.getPatrolRight()));
        max.addVerifyListener(createVerify(max, "[0-9]+"));
        max.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent event)
            {
                patroller.setPatrolRight(Integer.parseInt(max.getText()));
            }
        });
    }

    /**
     * Create a verify listener.
     * 
     * @param text The text to verify.
     * @param match The expected match.
     * @return The verify listener.
     */
    private static VerifyListener createVerify(final Text text, final String match)
    {
        return new VerifyListener()
        {
            @Override
            public void verifyText(VerifyEvent event)
            {
                final String init = text.getText();
                final String newText = init.substring(0, event.start) + event.text + init.substring(event.end);
                event.doit = newText.matches(match) || newText.isEmpty();
            }
        };
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

    /**
     * Clear the properties.
     */
    private void clear()
    {
        if (properties != null)
        {
            for (final Control control : properties.getChildren())
            {
                control.dispose();
            }
        }
    }

    /*
     * ObjectSelectionListener
     */

    @Override
    public void notifyObjectSelected(ObjectGame object)
    {
        clear();
        if (properties != null)
        {
            if (object instanceof PatrollerServices)
            {
                createPatrol((PatrollerServices) object, properties);
            }
            properties.layout(true, true);
        }
    }

    @Override
    public void notifyObjectsSelected(Collection<ObjectGame> objects)
    {
        clear();
    }
}
