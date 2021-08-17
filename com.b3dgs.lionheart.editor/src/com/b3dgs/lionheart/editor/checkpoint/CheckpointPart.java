/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.editor.checkpoint;

import java.util.Optional;
import java.util.OptionalInt;

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

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.editor.utility.Focusable;
import com.b3dgs.lionengine.editor.utility.UtilTree;
import com.b3dgs.lionengine.editor.utility.control.UtilSwt;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.view.WorldPart;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionheart.Checkpoint;
import com.b3dgs.lionheart.StageConfig;
import com.b3dgs.lionheart.editor.Activator;

/**
 * Element properties part.
 */
public class CheckpointPart implements Focusable
{
    /** Id. */
    public static final String ID = Activator.PLUGIN_ID + ".part.checkpoint";
    /** Menu. */
    public static final String MENU = ID + ".menu";

    private final Checkpoints checkpoints = WorldModel.INSTANCE.getServices().get(Checkpoints.class);
    private Tree tree;

    /**
     * Create part.
     */
    public CheckpointPart()
    {
        super();
    }

    /**
     * Select index.
     * 
     * @param index The index number.
     */
    public void select(int index)
    {
        tree.select(tree.getItem(index));
    }

    /**
     * Set checkpoint.
     * 
     * @param index The index number.
     * @param checkpoint The checkpoint reference.
     */
    public void set(int index, Checkpoint checkpoint)
    {
        checkpoints.set(index, checkpoint);
        tree.getItem(index).setText(checkpoint.toString());
    }

    /**
     * Get current selection.
     * 
     * @return The current selection.
     */
    public OptionalInt getSelection()
    {
        final TreeItem[] items = tree.getSelection();
        if (items.length > 0)
        {
            final TreeItem item = items[0];
            return OptionalInt.of(((Integer) item.getData()).intValue());
        }
        return OptionalInt.empty();
    }

    /**
     * Add new item.
     */
    public void add()
    {
        final TreeItem item = new TreeItem(tree, SWT.NONE);
        final Camera camera = WorldModel.INSTANCE.getCamera();
        final MapTile map = WorldModel.INSTANCE.getMap();
        final Checkpoint patrol = new Checkpoint(UtilMath.getRounded((camera.getX() + camera.getWidth() / 2)
                                                                     / map.getTileWidth(),
                                                                     map.getTileWidth()),
                                                 UtilMath.getRounded((camera.getY() + camera.getHeight() / 2)
                                                                     / map.getTileHeight(),
                                                                     map.getTileHeight()),
                                                 Optional.empty(),
                                                 Optional.empty());
        item.setText(patrol.toString());

        updateIndexes();
    }

    /**
     * Remove selected item.
     */
    public void remove()
    {
        if (tree.getSelectionCount() > 0)
        {
            final TreeItem item = tree.getSelection()[0];
            checkpoints.remove(((Integer) item.getData()).intValue());
            item.setData(null);
            item.dispose();
        }
        updateIndexes();
    }

    /**
     * Load checkpoints.
     * 
     * @param stage The stage configuration.
     */
    public void load(StageConfig stage)
    {
        checkpoints.removeAll();
        for (final TreeItem item : tree.getItems())
        {
            item.setData(null);
            item.dispose();
        }

        int i = 0;
        for (final Checkpoint checkpoint : stage.getCheckpoints())
        {
            checkpoints.add(checkpoint);

            final TreeItem item = new TreeItem(tree, SWT.NONE);
            item.setData(Integer.valueOf(i++));
            item.setText(checkpoint.toString());
        }
    }

    /**
     * Update items.
     */
    private void updateIndexes()
    {
        int i = 0;
        for (final Checkpoint checkpoint : checkpoints)
        {
            final TreeItem item = tree.getItem(i);
            item.setData(Integer.valueOf(i++));
            item.setText(checkpoint.toString());
        }
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
        addListeners(menuService);
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
     * 
     * @param menuService The menu service reference.
     */
    private void addListeners(EMenuService menuService)
    {
        tree.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDown(MouseEvent e)
            {
                final TreeItem[] items = tree.getSelection();
                if (items.length > 0)
                {
                    final TreeItem item = items[0];
                    final Checkpoint checkpoint = checkpoints.get(((Integer) item.getData()).intValue());
                    final MapTile map = WorldModel.INSTANCE.getMap();
                    final Camera camera = WorldModel.INSTANCE.getCamera();
                    camera.teleport(UtilMath.getRounded(checkpoint.getTx() * map.getTileWidth() - camera.getWidth() / 2,
                                                        map.getTileWidth()),
                                    UtilMath.getRounded(checkpoint.getTy() * map.getTileHeight()
                                                        - camera.getHeight() / 2,
                                                        map.getTileHeight()));
                    final WorldPart worldPart = WorldModel.INSTANCE.getServices().get(WorldPart.class);
                    worldPart.update();
                }
            }

            @Override
            public void mouseDoubleClick(MouseEvent event)
            {
                final TreeItem[] items = tree.getSelection();
                if (items.length > 0)
                {
                    final TreeItem item = items[0];
                    final int index = ((Integer) item.getData()).intValue();
                    final CheckpointEditor editor = new CheckpointEditor(tree, checkpoints.get(index));
                    editor.create();
                    editor.openAndWait();
                    editor.getOutput().ifPresent(c ->
                    {
                        item.setText(c.toString());
                        checkpoints.set(((Integer) item.getData()).intValue(), c);
                    });
                }
            }
        });
    }
}
