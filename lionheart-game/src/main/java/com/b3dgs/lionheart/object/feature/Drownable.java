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

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.Routine;
import com.b3dgs.lionheart.object.state.StateDie;
import com.b3dgs.lionheart.object.state.StateIdle;

/**
 * Drownable feature implementation.
 */
@FeatureInterface
public class Drownable extends FeatureModel implements Routine, Recyclable
{
    private final int DROWN_START_Y = -10;
    private final int DROWN_END_Y = -60;
    private final Updatable start;
    private final Updatable end;
    private Updatable current;

    @FeatureGet private Transformable transformable;
    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private EntityModel model;
    @FeatureGet private Body body;

    /**
     * Create drownable.
     */
    public Drownable()
    {
        super();

        end = extrp ->
        {
            if (transformable.getY() < DROWN_END_Y)
            {
                stateHandler.changeState(StateIdle.class);
                recycle();
            }
        };
        start = extrp ->
        {
            if (transformable.getY() < DROWN_START_Y)
            {
                current = end;
                stateHandler.changeState(StateDie.class);
            }
        };
    }

    @Override
    public void update(double extrp)
    {
        current.update(extrp);
    }

    @Override
    public void recycle()
    {
        current = start;
    }
}
