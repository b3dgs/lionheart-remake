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

import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.swt.Mouse;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.palette.PalettePart;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.geom.Point;

/**
 * World view renderer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class WorldViewRenderer
        extends com.b3dgs.lionengine.editor.world.WorldViewRenderer
{
    /**
     * Render a single checkpoint.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     * @param map The map reference.
     * @param point The checkpoint to render.
     */
    private static void renderCheckpointsBox(Graphic g, CameraGame camera, MapTile<?> map, Point point)
    {
        g.drawRect(camera.getViewpointX(point.getX()), camera.getViewpointY(point.getY()) - map.getTileHeight(),
                map.getTileWidth(), map.getTileHeight(), true);
    }

    /**
     * Constructor.
     * 
     * @param partService The part service.
     * @param parent The parent container.
     */
    public WorldViewRenderer(Composite parent, EPartService partService)
    {
        super(parent, partService);
        final PropertiesPart part = UtilEclipse.getPart(partService, PropertiesPart.ID, PropertiesPart.class);
        addListenerObject(part);
    }

    /**
     * Render all checkpoints.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     * @param map The map reference.
     */
    private void renderCheckpoints(Graphic g, CameraGame camera, MapTile<?> map)
    {
        final PalettePart part = UtilEclipse.getPart(partService, PalettePart.ID, PalettePart.class);
        final CheckpointsView view = part.getPaletteView(CheckpointsView.ID, CheckpointsView.class);

        g.setColor(ColorRgba.GREEN);
        WorldViewRenderer.renderCheckpointsBox(g, camera, map, view.getStart());

        g.setColor(ColorRgba.RED);
        WorldViewRenderer.renderCheckpointsBox(g, camera, map, view.getEnd());

        g.setColor(ColorRgba.YELLOW);
        for (final Point point : view.getCheckpoints())
        {
            WorldViewRenderer.renderCheckpointsBox(g, camera, map, point);
        }
    }

    /*
     * WorldViewRenderer
     */

    @Override
    protected void updatePalettePointer(PalettePart part, int mx, int my)
    {
        super.updatePalettePointer(part, mx, my);
        if (part.getActivePaletteId() == CheckpointsView.ID)
        {
            final MapTile<?> map = model.getMap();
            final CameraGame camera = model.getCamera();
            final Point tile = Tools.getMouseTile(map, camera, mx, my);

            final CheckpointsView view = part.getPaletteView(CheckpointsView.class);
            final CheckpointType type = view.getType();
            final int click = getClick();
            if (type == CheckpointType.START)
            {
                view.setStart(tile);
            }
            else if (type == CheckpointType.END)
            {
                view.setEnd(tile);
            }
            else if (type == CheckpointType.PLACE)
            {
                if (click == Mouse.LEFT)
                {
                    view.addCheckpoint(tile);
                }
                else if (click == Mouse.RIGHT)
                {
                    view.removeCheckpoint(tile);
                }
            }
        }
    }

    @Override
    protected void renderMap(Graphic g, CameraGame camera, MapTile<?> map, int areaX, int areaY)
    {
        super.renderMap(g, camera, map, areaX, areaY);
        renderCheckpoints(g, camera, map);
    }
}
