/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Setup entity definition.
 */
public class SetupEntity extends SetupSurfaceRastered
{
    /** Shade node name. */
    public static final String NODE_SHADE = "shade";

    private final ImageBuffer shade;

    /**
     * Create setup.
     * 
     * @param config The config media.
     * @throws LionEngineException If error when opening the media or invalid raster file.
     */
    public SetupEntity(Media config)
    {
        this(config, null);
    }

    /**
     * Create setup.
     * 
     * @param config The config media.
     * @param rasterMedia The raster media.
     * @throws LionEngineException If error when opening the media or invalid raster file.
     */
    public SetupEntity(Media config, Media rasterMedia)
    {
        super(config, rasterMedia);

        final Media media = Medias.create(getSurfaceFile().getParentPath(),
                                          getSurfaceFile().getName().replace(".", "_shade."));
        if (media.exists())
        {
            shade = Graphics.getImageBuffer(media);
        }
        else
        {
            shade = null;
        }
    }

    /**
     * Get shade image.
     * 
     * @return The shade image.
     */
    public ImageBuffer getShade()
    {
        return shade;
    }
}
