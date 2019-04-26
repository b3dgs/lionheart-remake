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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionheart.object.Routine;
import com.b3dgs.lionheart.object.state.StateIdle;

/**
 * Effect feature implementation.
 */
@FeatureInterface
public final class Effect extends FeatureModel implements Routine, Recyclable
{
    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private StateHandler stateHandler;

    @Override
    public void update(double extrp)
    {
        if (animatable.getAnimState() == AnimState.FINISHED)
        {
            identifiable.destroy();
        }
    }

    @Override
    public void recycle()
    {
        stateHandler.changeState(StateIdle.class);
    }
}
