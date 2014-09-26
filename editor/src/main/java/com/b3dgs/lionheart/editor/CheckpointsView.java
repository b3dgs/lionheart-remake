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
import java.util.List;

import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.palette.PaletteView;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.editor.world.WorldViewPart;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Point;

/**
 * Represents the checkpoints list, including starting and ending points.
 * 
 * @author Pierre-Alexandre
 */
public final class CheckpointsView
        implements PaletteView
{
    /** ID. */
    public static final String ID = "checkpoints";

    /** Part service. */
    final EPartService partService;
    /** Starting point. */
    private final Point start;
    /** Ending point. */
    private final Point end;
    /** Current checkpoints. */
    private final List<Point> checkpoints;
    /** Current checkpoint type. */
    CheckpointType type;
    /** Checkpoints list viewer. */
    ListViewer viewer;

    /**
     * Constructor.
     * 
     * @param partService The part service.
     */
    public CheckpointsView(EPartService partService)
    {
        this.partService = partService;
        start = Geom.createPoint();
        end = Geom.createPoint();
        checkpoints = new ArrayList<>();
    }

    /**
     * Add a checkpoint.
     * 
     * @param point The selected tile point.
     */
    public void addCheckpoint(Point point)
    {
        checkpoints.add(point);
        viewer.add(new CheckpointElement(point));
    }

    /**
     * Remove a checkpoint.
     * 
     * @param point The selected tile point.
     */
    public void removeCheckpoint(Point point)
    {
        final int size = checkpoints.size();
        final List<Point> toRemove = new ArrayList<>();
        for (final Point current : checkpoints)
        {
            if (point.getX() == current.getX() && point.getY() == current.getY())
            {
                toRemove.add(current);
            }
        }
        final List<CheckpointElement> toRemoveElement = new ArrayList<>();
        for (int i = 0; i < size; i++)
        {
            final CheckpointElement element = (CheckpointElement) viewer.getElementAt(i);
            final Point current = element.getCheckpoint();
            if (toRemove.contains(current))
            {
                toRemoveElement.add(element);
            }
        }
        checkpoints.removeAll(toRemove);
        viewer.remove(toRemoveElement.toArray());
        toRemove.clear();
        toRemoveElement.clear();
    }

    /**
     * Set the starting point.
     * 
     * @param point The starting point.
     */
    public void setStart(Point point)
    {
        start.set(point.getX(), point.getY());
    }

    /**
     * Set the ending point.
     * 
     * @param point The ending point.
     */
    public void setEnd(Point point)
    {
        end.set(point.getX(), point.getY());
    }

    /**
     * Get the current checkpoint type.
     * 
     * @return The current checkpoint type.
     */
    public CheckpointType getType()
    {
        return type;
    }

    /**
     * Get the starting point.
     * 
     * @return The starting point.
     */
    public Point getStart()
    {
        return start;
    }

    /**
     * Get the ending point.
     * 
     * @return The ending point.
     */
    public Point getEnd()
    {
        return end;
    }

    /**
     * Get the checkpoints list.
     * 
     * @return The checkpoints list.
     */
    public List<Point> getCheckpoints()
    {
        return checkpoints;
    }

    /*
     * PaletteView
     */

    @Override
    public void create(Composite parent)
    {
        parent.setLayout(new GridLayout(1, false));

        final Composite buttons = new Composite(parent, SWT.NONE);
        buttons.setLayout(new GridLayout(1, false));

        final Button placeStart = new Button(buttons, SWT.TOGGLE);
        placeStart.setText(Messages.Checkpoints_Start);

        final Button placeEnd = new Button(buttons, SWT.TOGGLE);
        placeEnd.setText(Messages.Checkpoints_End);

        final Button placeCheckpoint = new Button(buttons, SWT.TOGGLE);
        placeCheckpoint.setText(Messages.Checkpoints_Place);

        placeStart.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                if (placeStart.getSelection())
                {
                    placeEnd.setSelection(false);
                    placeCheckpoint.setSelection(false);
                    type = CheckpointType.START;
                }
                else
                {
                    type = null;
                }
            }
        });
        placeEnd.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                if (placeEnd.getSelection())
                {
                    placeStart.setSelection(false);
                    placeCheckpoint.setSelection(false);
                    type = CheckpointType.END;
                }
                else
                {
                    type = null;
                }
            }
        });
        placeCheckpoint.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                if (placeCheckpoint.getSelection())
                {
                    placeStart.setSelection(false);
                    placeEnd.setSelection(false);
                    type = CheckpointType.PLACE;
                }
                else
                {
                    type = null;
                }
            }
        });

        final Group infos = new Group(parent, SWT.NONE);
        infos.setLayout(new GridLayout(1, false));
        infos.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        infos.setText(Messages.Checkpoints_Infos);

        viewer = new ListViewer(infos, SWT.BORDER | SWT.V_SCROLL);
        viewer.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer.addDoubleClickListener(new IDoubleClickListener()
        {
            @Override
            public void doubleClick(DoubleClickEvent event)
            {
                final ISelection selection = viewer.getSelection();
                if (selection instanceof StructuredSelection)
                {
                    final Object object = ((StructuredSelection) selection).getFirstElement();
                    if (object instanceof CheckpointElement)
                    {
                        final Point point = ((CheckpointElement) object).getCheckpoint();
                        final CameraGame camera = WorldViewModel.INSTANCE.getCamera();
                        final MapTile<?> map = WorldViewModel.INSTANCE.getMap();
                        final int x = UtilMath.getRounded(point.getX() - camera.getViewWidth() / 2, map.getTileWidth());
                        final int y = UtilMath.getRounded(point.getY() - camera.getViewHeight() / 2,
                                map.getTileHeight());
                        camera.setLocation(x, y);

                        final WorldViewPart view = UtilEclipse.getPart(partService, WorldViewPart.ID,
                                WorldViewPart.class);
                        view.update();
                    }
                }
            }
        });
    }

    @Override
    public String getId()
    {
        return CheckpointsView.ID;
    }
}
