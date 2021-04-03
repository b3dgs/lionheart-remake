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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionheart.constant.Anim;

/**
 * Norka walk feature implementation.
 * <ol>
 * <li>Move down and transform.</li>
 * </ol>
 */
@FeatureInterface
public final class NorkaTransform extends FeatureModel implements Routine, Recyclable
{
    private final Animation idle;

    private final Spawner spawner = services.get(Spawner.class);

    private int max;
    private Updatable phase;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Identifiable identifiable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public NorkaTransform(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation(Anim.IDLE);
    }

    /**
     * Update transform phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransform(double extrp)
    {
        if (animatable.is(AnimState.FINISHED))
        {
            max++;
            if (max >= 5)
            {
                max = idle.getLast();
            }
            if (animatable.getFrame() == idle.getLast())
            {
                phase = UpdatableVoid.getInstance();
                spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "Norka.xml"), 208, 88);
                identifiable.destroy();
            }
            else
            {
                animatable.play(getAnim());
            }
        }
    }

    private Animation getAnim()
    {
        final double speed;
        final boolean reversed;
        if (max == idle.getLast())
        {
            speed = 0.25;
            reversed = false;
        }
        else
        {
            speed = idle.getSpeed();
            reversed = idle.hasReverse();
        }
        return new Animation(idle.getName(), idle.getFirst(), max, speed, reversed, false);
    }

    @Override
    public void update(double extrp)
    {
        phase.update(extrp);
    }

    @Override
    public void recycle()
    {
        phase = this::updateTransform;
        max = 2;
        animatable.play(getAnim());
    }
}
