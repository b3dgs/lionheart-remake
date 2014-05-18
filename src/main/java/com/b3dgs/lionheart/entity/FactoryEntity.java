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
import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionheart.AppLionheart;
import com.b3dgs.lionheart.CategoryType;
import com.b3dgs.lionheart.Level;
import com.b3dgs.lionheart.ThemeType;
import com.b3dgs.lionheart.entity.player.Valdyn;
import com.b3dgs.lionheart.landscape.LandscapeType;

/**
 * Handle the entity creation by containing all necessary object for their instantiation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FactoryEntity
        extends FactoryObjectGame<SetupEntity, Entity>
{
    /** Unknown entity error message. */
    public static final String UNKNOWN_ENTITY_ERROR = "Unknown entity: ";

    /** Level used. */
    protected Level level;
    /** Landscape used. */
    protected LandscapeType landscape;

    /**
     * Constructor.
     */
    public FactoryEntity()
    {
        super(AppLionheart.ENTITIES_DIR);
    }

    /**
     * Constructor.
     * 
     * @param folder The folder name.
     */
    protected FactoryEntity(String folder)
    {
        super(UtilFile.getPath(AppLionheart.ENTITIES_DIR, folder));
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
        final String name = file.readString();
        final Class<? extends Entity> type = EntityType.CONVERTER.getType(name);

        return create(type);
    }

    /**
     * Set the level used.
     * 
     * @param level The level used.
     */
    public void setLevel(Level level)
    {
        this.level = level;
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

    /*
     * FactoryObjectGame
     */

    @Override
    protected SetupEntity createSetup(Class<? extends Entity> type, Media config)
    {
        final CategoryType category = CategoryType.getType(type);
        final ThemeType theme = ThemeType.getType(type);
        final List<String> path = new ArrayList<>(1);
        path.add(folder);
        path.add(category.getPath());
        if (theme != ThemeType.NONE)
        {
            path.add(theme.getPath());
        }
        path.add(type.getSimpleName() + ".xml");

        final Media media = Core.MEDIA.create(path.toArray(new String[path.size()]));

        final Media raster;
        if (AppLionheart.RASTER_ENABLED && type != Valdyn.class)
        {
            raster = Core.MEDIA.create(AppLionheart.RASTERS_DIR, landscape.getRaster());
        }
        else
        {
            raster = null;
        }
        return new SetupEntity(media, raster, false, level);
    }
}
