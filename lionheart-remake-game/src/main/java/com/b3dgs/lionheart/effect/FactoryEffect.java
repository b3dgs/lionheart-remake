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
package com.b3dgs.lionheart.effect;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionheart.AppLionheart;
import com.b3dgs.lionheart.landscape.LandscapeType;

/**
 * Factory effect implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FactoryEffect
        extends FactoryObjectGame<SetupSurfaceRasteredGame>
{
    /** Handler effect reference. */
    private HandlerEffect handlerEffect;
    /** Landscape used. */
    private LandscapeType landscape;

    /**
     * Constructor.
     */
    public FactoryEffect()
    {
        super(AppLionheart.EFFECTS_DIR);
    }

    /**
     * Start an effect an the specified location.
     * 
     * @param config The effect config.
     * @param coord The coord location.
     * @param offsetX The horizontal offset.
     * @param offsetY The vertical offset.
     */
    public void startEffect(Media config, Coord coord, int offsetX, int offsetY)
    {
        final Effect effect = create(config);
        effect.start((int) coord.getX() + offsetX, (int) coord.getY() + offsetY);
        handlerEffect.add(effect);
    }

    /**
     * Set the handler effect type used.
     * 
     * @param handlerEffect The handler effect type used.
     */
    public void setHandlerEffect(HandlerEffect handlerEffect)
    {
        this.handlerEffect = handlerEffect;
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
    protected SetupSurfaceRasteredGame createSetup(Media config)
    {
        final Media raster;
        if (AppLionheart.ENABLE_RASTER)
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
