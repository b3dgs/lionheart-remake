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
package com.b3dgs.lionheart.entity;

import java.io.IOException;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionheart.AppLionheart;
import com.b3dgs.lionheart.entity.player.Valdyn;
import com.b3dgs.lionheart.landscape.LandscapeType;

/**
 * Handle the entity creation by containing all necessary object for their instantiation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FactoryEntity
        extends FactoryObjectGame<SetupSurfaceRasteredGame>
{
    /** Landscape used. */
    private LandscapeType landscape;
    /** Raster enabled. */
    private boolean hasRaster;

    /**
     * Constructor.
     */
    public FactoryEntity()
    {
        super(AppLionheart.ENTITIES_DIR);
    }

    /**
     * Create an entity from a file loaded.
     * 
     * @param file The file loaded.
     * @return The created entity.
     * @throws IOException If error.
     */
    public Entity createEntity(FileReading file) throws IOException
    {
        final String path = file.readString();
        final Media config = Core.MEDIA.create(path);
        return create(config);
    }

    /**
     * Set the landscape type used.
     * 
     * @param landscape The landscape type used.
     */
    public void setLandscape(LandscapeType landscape)
    {
        this.landscape = landscape;
    }

    /**
     * Set the raster state.
     * 
     * @param hasRaster <code>true</code> to use raster, <code>false</code> else.
     */
    public void setRaster(boolean hasRaster)
    {
        this.hasRaster = hasRaster;
    }

    /*
     * FactoryObjectGame
     */

    @Override
    protected SetupSurfaceRasteredGame createSetup(Media config)
    {
        final Media raster;
        if (hasRaster && !config.getPath().equals(Valdyn.MEDIA.getPath()))
        {
            raster = Core.MEDIA.create(AppLionheart.RASTERS_DIR, landscape.getRaster());
        }
        else
        {
            raster = null;
        }
        return new SetupSurfaceRasteredGame(config, false, raster, false);
    }
}
