/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.object.state.StateIdle;
import com.b3dgs.lionheart.object.state.StatePatrol;

/**
 * Bird feature implementation.
 * <ol>
 * <li>Fly vertical on patrol.</li>
 * <li>Hurt player on top, can be hit to be in platform mode.</li>
 * <li>Player can walk over in platform mode.</li>
 * <li>Move back to patrol after delay.</li>
 * </ol>
 */
@FeatureInterface
public final class Bird extends Turning implements CollidableListener
{
    private final Tick tick = new Tick();

    private @FeatureGet Animatable animatable;
    private @FeatureGet Collidable collidable;
    private @FeatureGet StateHandler stateHandler;
    private @FeatureGet Glue glue;

    private boolean hit;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Bird(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        tick.update(extrp);
        if (hit && tick.elapsed(200))
        {
            hit = false;
            stateHandler.changeState(StatePatrol.class);
            glue.setGlue(false);
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (by.getName().startsWith(Anim.ATTACK))
        {
            stateHandler.changeState(StateIdle.class);
            glue.setGlue(true);
            animatable.stop();
            tick.restart();
            hit = true;
        }
    }

    @Override
    public void recycle()
    {
        super.recycle();

        tick.restart();
        hit = false;
    }
}
