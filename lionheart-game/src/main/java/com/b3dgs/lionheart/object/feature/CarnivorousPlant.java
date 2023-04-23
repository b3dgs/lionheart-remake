/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.state.StateBitten;

/**
 * Carnivorous plant feature implementation.
 * <p>
 * Add support to random bite animation and kill on top collide.
 * </p>
 */
@FeatureInterface
public final class CarnivorousPlant extends FeatureModel implements Routine, Recyclable, CollidableListener
{
    private static final int BITE_DELAY_MS = 1500;

    private final Tick tick = new Tick();
    private final Animation bite;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);

    @FeatureGet private Animatable animatable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public CarnivorousPlant(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        bite = config.getAnimation(Anim.ATTACK);
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        if (animatable.getAnimState() == AnimState.FINISHED && tick.elapsedTime(source.getRate(), BITE_DELAY_MS))
        {
            if (UtilRandom.getRandomBoolean())
            {
                animatable.play(bite);
            }
            tick.restart();
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        final Transformable transformable = collidable.getFeature(Transformable.class);
        if (CollisionName.BITE.equals(with.getName())
            && by.getName().contains(CollisionName.BODY)
            && transformable.getY() < transformable.getOldY())
        {
            collidable.getFeature(StateHandler.class).changeState(StateBitten.class);
        }
    }

    @Override
    public void recycle()
    {
        tick.restart();
        animatable.play(bite);
    }
}
