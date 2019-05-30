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
import com.b3dgs.lionengine.AnimatorFrameListener;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.object.state.StateIdle;

/**
 * Effect feature implementation.
 * <ol>
 * <li>Listen to animation state changes.</li>
 * <li>On {@link AnimState#FINISHED}, destroy and reset.</li>
 * </ol>
 */
@FeatureInterface
public final class Effect extends FeatureModel implements Recyclable
{
    private static final String NODE_SFX = "sfx";

    private final boolean sfx;

    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private StateHandler stateHandler;

    /**
     * Create effect.
     * 
     * @param setup The setup reference.
     */
    public Effect(Setup setup)
    {
        super();

        sfx = setup.hasNode(NODE_SFX);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        animatable.addListener(new AnimatorFrameListener()
        {
            @Override
            public void notifyAnimState(AnimState state)
            {
                if (AnimState.FINISHED == state)
                {
                    identifiable.destroy();
                }
            }

            @Override
            public void notifyAnimFrame(int frame)
            {
                if (sfx && (frame - 1) % 4 == 0)
                {
                    Sfx.playRandomExplode();
                }
            }
        });
    }

    @Override
    public void recycle()
    {
        stateHandler.changeState(StateIdle.class);
    }
}
