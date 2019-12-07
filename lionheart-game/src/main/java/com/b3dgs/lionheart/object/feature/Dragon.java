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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilMath;
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
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionheart.Sfx;

/**
 * Dragon feature implementation.
 * <ol>
 * <li>Point player.</li>
 * <li>Raise on close distance.</li>
 * <li>Throw line during delay.</li>
 * </ol>
 */
@FeatureInterface
public final class Dragon extends FeatureModel implements Routine, Recyclable
{
    private static final int THROW_DISTANCE = 128;

    private final Transformable track;
    private final Animation raise;
    private final Animation open;
    private final Animation close;
    private final Animation hide;
    private Updatable current;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Stats stats;
    @FeatureGet private StateHandler stateHandler;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Dragon(Services services, Setup setup)
    {
        super(services, setup);

        track = services.get(SwordShade.class).getFeature(Transformable.class);

        final AnimationConfig config = AnimationConfig.imports(setup);
        raise = config.getAnimation("raise");
        open = config.getAnimation("open");
        close = config.getAnimation("close");
        hide = config.getAnimation("hide");
    }

    /**
     * Update check distance.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateCheck(double extrp)
    {
        if (animatable.is(AnimState.FINISHED) && UtilMath.getDistance(track, transformable) < THROW_DISTANCE)
        {
            animatable.play(raise);
            current = this::updateRaise;
        }
    }

    /**
     * Update raise animation.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateRaise(double extrp)
    {
        if (animatable.is(AnimState.FINISHED))
        {
            animatable.play(open);
            Sfx.ENEMY_FLOWER.play();
            current = this::updateThrow;
        }
    }

    /**
     * Update throw.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateThrow(double extrp)
    {
        if (animatable.is(AnimState.FINISHED) && UtilMath.getDistance(track, transformable) > THROW_DISTANCE)
        {
            final int frame = animatable.getFrame();
            animatable.play(close);
            animatable.setFrame(frame);
            current = this::updateClose;
        }
    }

    /**
     * Update close.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateClose(double extrp)
    {
        if (animatable.is(AnimState.FINISHED))
        {
            final int frame = animatable.getFrame();
            animatable.play(hide);
            animatable.setFrame(frame);
            current = this::updateCheck;
        }
    }

    @Override
    public void update(double extrp)
    {
        current.update(extrp);
    }

    @Override
    public void recycle()
    {
        current = this::updateCheck;
    }
}
