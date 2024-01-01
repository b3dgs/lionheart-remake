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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
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
public final class NorkaWalk extends FeatureModel implements Routine, Recyclable
{
    private final Spawner spawner = services.get(Spawner.class);

    private final Transformable transformable;
    private final Animatable animatable;
    private final Identifiable identifiable;

    private final Animation idle;
    private final Animation walk;

    private Updatable phase;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @param animatable The animatable feature.
     * @param identifiable The identifiable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public NorkaWalk(Services services,
                     Setup setup,
                     Transformable transformable,
                     Animatable animatable,
                     Identifiable identifiable)
    {
        super(services, setup);

        this.transformable = transformable;
        this.animatable = animatable;
        this.identifiable = identifiable;

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation(Anim.IDLE);
        walk = config.getAnimation(Anim.WALK);
    }

    /**
     * Update raise phase from chair.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateRaise(double extrp)
    {
        if (animatable.is(AnimState.FINISHED))
        {
            animatable.play(walk);
            phase = this::updateWalk;
        }
    }

    /**
     * Update walk phase until front.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateWalk(double extrp)
    {
        transformable.moveLocationY(extrp, -0.15);
        if (transformable.getY() < 80)
        {
            spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "NorkaTransform.xml"), 210, 76);
            identifiable.destroy();
        }
    }

    @Override
    public void update(double extrp)
    {
        phase.update(extrp);
    }

    @Override
    public void recycle()
    {
        phase = this::updateRaise;
        animatable.play(idle);
    }
}
