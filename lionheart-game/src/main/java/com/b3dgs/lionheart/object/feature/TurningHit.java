/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.AnimationConfig;
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

/**
 * Turning hit feature implementation.
 * <ol>
 * <li>Wait for a delay before start shaking.</li>
 * <li>Shake a defined number of times.</li>
 * <li>Wait for a delay before start rotating.</li>
 * <li>Rotate one time with disabled collision.</li>
 * <li>Once hit on rotated, enable collision and go to step 1.</li>
 * </ol>
 */
@FeatureInterface
public final class TurningHit extends Turning implements CollidableListener
{
    private final Animation idle;

    @FeatureGet private Animatable animatable;
    @FeatureGet private StateHandler stateHandler;

    private boolean stopped;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public TurningHit(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation(Anim.IDLE);
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        if (stopped && animatable.getFrameAnim() == idle.getFirst())
        {
            startIdle();
            resetDelay();
            stateHandler.changeState(StateIdle.class);
            stopped = false;
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (by.getName().startsWith(Anim.ATTACK))
        {
            stopped = true;
        }
    }

    @Override
    public void recycle()
    {
        super.recycle();

        startTurn();
        stopped = false;
    }
}
