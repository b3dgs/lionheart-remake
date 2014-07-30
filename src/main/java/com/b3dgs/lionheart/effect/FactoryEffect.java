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
import com.b3dgs.lionengine.game.purview.Fabricable;
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
     * @param type The effect type.
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void startEffect(Class<? extends Effect> type, int x, int y)
    {
        final Effect effect = create(type);
        effect.start(x, y);
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
    protected SetupSurfaceRasteredGame createSetup(Class<? extends Fabricable> type, Media config)
    {
        final Media raster;
        if (AppLionheart.RASTER_ENABLED)
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
