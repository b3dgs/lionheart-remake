/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionheart.MapTileWater;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;

/**
 * Fall fish state implementation.
 */
public final class StateFishFall extends State
{
    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateFishFall(EntityModel model, Animation animation)
    {
        super(model, animation);

        final MapTileWater water = model.getServices().get(MapTileWater.class);

        addTransition(StateFishJump.class,
                      () -> transformable.getY() < water.getCurrent() - 80 && collidable.isEnabled());
    }

    @Override
    public void enter()
    {
        super.enter();

        jump.setVelocity(0.06);
        jump.setSensibility(0.5);
        jump.setDestination(0.0, -2.0);
    }
}
