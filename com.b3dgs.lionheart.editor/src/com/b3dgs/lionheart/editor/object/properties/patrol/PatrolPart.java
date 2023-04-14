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
package com.b3dgs.lionheart.editor.object.properties.patrol;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.utility.Focusable;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.editor.utility.UtilTree;
import com.b3dgs.lionengine.editor.utility.control.UtilSwt;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionheart.editor.Activator;
import com.b3dgs.lionheart.editor.object.properties.PropertiesFeature;
import com.b3dgs.lionheart.object.feature.PatrolConfig;
import com.b3dgs.lionheart.object.feature.Patrols;

/**
 * Element properties part.
 */
public class PatrolPart implements Focusable, PropertiesFeature
{
    /** Id. */
    public static final String ID = Activator.PLUGIN_ID + ".part.properties.patrol";
    /** Menu. */
    public static final String MENU = ID + ".menu";

    private Tree tree;
    private Patrols patrols;

    /**
     * Create part.
     */
    public PatrolPart()
    {
        super();
    }

    /**
     * Add new item.
     */
    public void add()
    {
        final TreeItem item = new TreeItem(tree, SWT.NONE);
        final PatrolConfig patrol = new PatrolConfig();
        patrols.get().add(patrol);
        item.setText(patrol.toString());
        item.setData(Integer.valueOf(patrols.get().size() - 1));
    }

    /**
     * Remove selected item.
     */
    public void remove()
    {
        if (tree.getSelectionCount() > 0)
        {
            final TreeItem item = tree.getSelection()[0];
            patrols.get().remove(((Integer) item.getData()).intValue());
            item.setData(null);
            item.dispose();

            int i = 0;
            for (final PatrolConfig patrol : patrols.get())
            {
                item.setText(patrol.toString());
                item.setData(Integer.valueOf(i));
                i++;
            }
        }
    }

    @Override
    public void load(Feature feature)
    {
        UtilPart.getMPart(PatrolPart.ID).setVisible(true);
        for (final TreeItem item : tree.getItems())
        {
            item.setData(null);
            item.dispose();
        }

        patrols = (Patrols) feature;
        int i = 0;
        for (final PatrolConfig patrol : patrols.get())
        {
            final TreeItem item = new TreeItem(tree, SWT.NONE);
            item.setText(patrol.toString());
            item.setData(Integer.valueOf(i));
            i++;
        }
        UtilPart.bringToTop(ID);
    }

    /**
     * Create the composite.
     * 
     * @param parent The parent reference.
     * @param menuService The menu service reference.
     */
    @PostConstruct
    public void createComposite(Composite parent, EMenuService menuService)
    {
        tree = new Tree(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        tree.setLayout(new FillLayout());
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tree.setHeaderVisible(false);
        tree.addMouseTrackListener(UtilSwt.createFocusListener(this));

        final Listener listener = UtilTree.createAutosizeListener();
        tree.addListener(SWT.Collapse, listener);
        tree.addListener(SWT.Expand, listener);

        menuService.registerContextMenu(tree, MENU);
        addListeners();
    }

    /**
     * Set the focus.
     */
    @Override
    @Focus
    public void focus()
    {
        tree.setFocus();
    }

    /**
     * Add mouse tree listener.
     */
    private void addListeners()
    {
        tree.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDoubleClick(MouseEvent event)
            {
                final TreeItem[] items = tree.getSelection();
                if (items.length > 0)
                {
                    final TreeItem item = items[0];
                    final PatrolEditor editor = new PatrolEditor(tree,
                                                                 patrols.get(((Integer) item.getData()).intValue()));
                    editor.create();
                    editor.openAndWait();
                    editor.getOutput().ifPresent(c ->
                    {
                        item.setText(c.toString());
                        patrols.get().set(((Integer) item.getData()).intValue(), c);
                    });
                }
            }
        });
    }
}
