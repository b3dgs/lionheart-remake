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
package com.b3dgs.lionheart;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.FeaturableAbstract;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileSurface;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewer;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

/**
 * Map tile water layer.
 */
public class MapTileWater extends FeaturableAbstract implements Renderable
{
    private static final int MAX_HEIGHT = 81;

    /** Top. */
    private final MapTileWater top;
    /** Camera reference. */
    private final Viewer viewer;
    /** Map tile surface. */
    private final MapTileSurface map;
    /** Tiles water. */
    private SpriteTiled[] tiles;
    /** The water height. */
    private int waterHeight;
    /** Disabled. */
    private boolean disabled;

    /**
     * Create feature.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link MapTileSurface}</li>
     * <li>{@link Viewer}</li>
     * </ul>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public MapTileWater(Services services)
    {
        this(services, false);
    }

    /**
     * Create feature.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link MapTileSurface}</li>
     * <li>{@link Viewer}</li>
     * </ul>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param bottom The bottom flag.
     * @throws LionEngineException If invalid argument.
     */
    public MapTileWater(Services services, boolean bottom)
    {
        super();

        map = services.get(MapTile.class).getFeature(MapTileSurface.class);
        viewer = services.get(Viewer.class);

        if (bottom)
        {
            top = services.get(MapTileWater.class);
        }
        else
        {
            top = null;
        }

        addFeature(new DisplayableModel(this::render));
    }

    /**
     * Create water map.
     * 
     * @param folder The raster folder.
     */
    public void create(String folder)
    {
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        tiles = new SpriteTiled[th];
        for (int i = 0; i < tiles.length; i++)
        {
            tiles[i] = Drawable.loadSpriteTiled(Medias.create(folder, "water", i + ".png"), tw, th);
            tiles[i].load();
            tiles[i].prepare();
        }
    }

    /**
     * Set the water height.
     * 
     * @param waterHeight The water height.
     */
    public void setWaterHeight(int waterHeight)
    {
        this.waterHeight = waterHeight;

        if (!disabled && waterHeight < 0)
        {
            final MapTileViewer viewer = map.getFeature(MapTileViewer.class);
            map.getFeature(MapTileViewer.class).clear();
            map.getFeature(MapTileViewer.class).addRenderer(viewer);

            disabled = true;
        }
    }

    /**
     * Get current water level.
     * 
     * @return The current water level.
     */
    public int getCurrent()
    {
        return waterHeight;
    }

    @Override
    public void render(Graphic g)
    {
        if (top != null)
        {
            waterHeight = UtilMath.clamp(top.getCurrent(), 0, MAX_HEIGHT);
        }
        if (waterHeight > 1)
        {
            final double viewY = viewer.getY() + viewer.getScreenHeight();
            final double viewX = viewer.getX();

            final int vtx = map.getInTileX(viewer);
            final int vtx2 = vtx + (int) Math.ceil(viewer.getWidth() / (double) map.getTileWidth()) + 1;
            int ty;
            final int max = (int) Math.floor((waterHeight - 2) / (double) map.getTileHeight());

            for (ty = 0; ty < max; ty++)
            {
                final SpriteTiled water = tiles[tiles.length - 1];
                for (int tx = vtx; tx < vtx2; tx++)
                {
                    final Tile tile = map.getTile(tx, ty);
                    if (tile != null)
                    {
                        final int x = (int) Math.round(tile.getX() - viewX);
                        final int y = (int) Math.round(-tile.getY() + viewY - tile.getHeight());
                        water.setLocation(x, y);
                        water.setTile(tile.getNumber());
                        water.render(g);
                    }
                }
            }

            final SpriteTiled water = tiles[(waterHeight - 2) % map.getTileHeight()];
            for (int tx = vtx; tx < vtx2; tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                if (tile != null)
                {
                    final int x = (int) Math.round(tile.getX() - viewX);
                    final int y = (int) Math.round(-tile.getY() + viewY - tile.getHeight());
                    water.setLocation(x, y);
                    water.setTile(tile.getNumber());
                    water.render(g);
                }
            }
        }
    }

    @Override
    public Media getMedia()
    {
        return null;
    }
}
