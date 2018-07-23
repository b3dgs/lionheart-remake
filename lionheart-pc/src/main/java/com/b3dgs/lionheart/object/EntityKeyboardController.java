/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object;

import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.io.InputDeviceDirectional;

/**
 * Entity updating implementation.
 */
class EntityKeyboardController extends FeatureModel
{
    private final InputDeviceDirectional input;
    
    @FeatureGet private EntityModel model;
    
    /**
     * Create updater.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    EntityKeyboardController(Services services, SetupSurfaceRastered setup)
    {
        super();
        
        input = services.get(InputDeviceDirectional.class);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);
        
        model.setInput(input);
    }
}
