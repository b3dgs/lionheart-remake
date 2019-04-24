/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionheart.Constant;

/**
 * Turning feature implementation.
 */
@FeatureInterface
public final class Turning extends FeatureModel implements Routine
{
    private static final int DELAY = 100;

    private final Tick tick = new Tick();

    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private Collidable collidable;

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        stateHandler.addListener((from, to) ->
        {
            collidable.setEnabled(!Constant.ANIM_NAME_TURN.equals(Entity.getAnimationName(to)));
        });
        tick.start();
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsed(DELAY))
        {
            stateHandler.changeState(StateTurn.class);
            tick.restart();
        }
    }
}
