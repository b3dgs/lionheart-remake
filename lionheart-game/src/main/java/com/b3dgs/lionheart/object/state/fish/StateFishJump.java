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
package com.b3dgs.lionheart.object.state.fish;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.io.InputDeviceControlVoid;
import com.b3dgs.lionheart.MapTileWater;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;

/**
 * Jump fish state implementation.
 */
final class StateFishJump extends State
{
    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateFishJump(EntityModel model, Animation animation)
    {
        super(model, animation);

        final MapTileWater water = model.getServices().get(MapTileWater.class);

        addTransition(StateFishAttack.class,
                      () -> jump.getDirectionVertical() < 1.0
                            || transformable.getY() > water.getCurrent() + transformable.getHeight() + 64);
    }

    @Override
    public void enter()
    {
        super.enter();

        model.setInput(new InputDeviceControlVoid()
        {
            @Override
            public double getVerticalDirection()
            {
                return 1.0;
            }
        });

        jump.setVelocity(0.05);
        jump.setSensibility(0.5);
        jump.setDirection(0.0, 3.2);
    }

    @Override
    public void update(double extrp)
    {
        body.resetGravity();
    }

    @Override
    public void exit()
    {
        super.exit();

        model.setInput(InputDeviceControlVoid.getInstance());
    }
}
