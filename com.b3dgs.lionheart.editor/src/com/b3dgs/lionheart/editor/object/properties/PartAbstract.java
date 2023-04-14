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
package com.b3dgs.lionheart.editor.object.properties;

import java.util.Locale;

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

import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.editor.utility.Focusable;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.editor.utility.UtilTree;
import com.b3dgs.lionengine.editor.utility.control.UtilSwt;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionheart.editor.Activator;
import com.b3dgs.lionheart.object.Editable;

/**
 * Element properties part.
 * 
 * @param <C> The configuration type.
 * @param <E> The editable feature type.
 */
public class PartAbstract<C, E extends Editable<C>> implements Focusable, PropertiesFeature
{
    /** Id. */
    protected static final String ID_PREFIX = Activator.PLUGIN_ID + ".part.properties.";

    private final String id;
    private final String menuId;
    private final Class<C> config;
    private final Class<? extends EditorAbstract<C>> editor;

    private Tree tree;
    private E editable;

    /**
     * Create part.
     * 
     * @param config The configuration class.
     * @param type The property class type.
     * @param editor The editor class.
     */
    protected PartAbstract(Class<C> config, Class<E> type, Class<? extends EditorAbstract<C>> editor)
    {
        super();

        this.config = config;
        this.editor = editor;
        id = ID_PREFIX + type.getSimpleName().toLowerCase(Locale.ENGLISH);
        menuId = id + ".menu";
    }

    /**
     * Check if config exists.
     * 
     * @return <code>true</code> if exists, <code>false</code> else.
     */
    public boolean exists()
    {
        return editable.getConfig() != null;
    }

    /**
     * Enable configuration.
     */
    public void enable()
    {
        if (editable.getConfig() == null)
        {
            final TreeItem item = new TreeItem(tree, SWT.NONE);
            try
            {
                editable.setConfig(config.getDeclaredConstructor().newInstance());
                item.setText(editable.getConfig().toString());
            }
            catch (final ReflectiveOperationException exception)
            {
                Verbose.exception(exception);
            }
        }
    }

    /**
     * Disable configuration.
     */
    public void disable()
    {
        if (tree.getSelectionCount() > 0)
        {
            final TreeItem item = tree.getSelection()[0];
            editable.setConfig(null);
            item.dispose();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void load(Feature feature)
    {
        UtilPart.getMPart(id).setVisible(true);
        for (final TreeItem item : tree.getItems())
        {
            item.setData(null);
            item.dispose();
        }

        editable = (E) feature;
        if (editable.getConfig() == null)
        {
            enable();
        }
        else
        {
            final TreeItem item = new TreeItem(tree, SWT.NONE);
            item.setText(editable.getConfig().toString());
        }
        UtilPart.bringToTop(id);
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

        menuService.registerContextMenu(tree, menuId);
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
                    try
                    {
                        final EditorAbstract<C> dialog = editor.getDeclaredConstructor(Composite.class, config)
                                                               .newInstance(tree, editable.getConfig());
                        dialog.create();
                        dialog.openAndWait();
                        dialog.getOutput().ifPresent(c ->
                        {
                            editable.setConfig(c);
                            item.setText(c.toString());
                        });
                    }
                    catch (final ReflectiveOperationException exception)
                    {
                        Verbose.exception(exception);
                    }
                }
            }
        });
    }
}
