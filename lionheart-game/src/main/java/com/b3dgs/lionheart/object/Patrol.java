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

import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionheart.InputDeviceControlVoid;

/**
 * Patrol feature implementation.
 */
public final class Patrol extends FeatureModel implements Routine
{
    private final Tick change = new Tick();
    private double direction = 0.3;

    @FeatureGet private EntityModel model;

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        model.setInput(new InputDeviceControlVoid()
        {
            @Override
            public double getHorizontalDirection()
            {
                return direction;
            }
        });
        change.start();
    }

    @Override
    public void update(double extrp)
    {
        change.update(extrp);
        if (change.elapsed(100L))
        {
            direction = -direction;
            change.restart();
        }
    }
}
