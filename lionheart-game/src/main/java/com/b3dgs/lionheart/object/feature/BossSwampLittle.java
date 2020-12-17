/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionheart.constant.Anim;

/**
 * Boss Swamp 2 little feature implementation.
 * <ol>
 * <li>Walk in player direction and continues straight on.</li>
 * </ol>
 */
@FeatureInterface
public final class BossSwampLittle extends FeatureModel implements Routine, Recyclable
{
    private static final double SPEED_X = 1.0;

    private final Transformable player = services.get(SwordShade.class).getFeature(Transformable.class);
    private final Animation walk;

    private double vx;
    private boolean init;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Animatable animatable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BossSwampLittle(Services services, Setup setup)
    {
        super(services, setup);

        walk = AnimationConfig.imports(setup).getAnimation(Anim.WALK);
    }

    @Override
    public void update(double extrp)
    {
        if (init)
        {
            vx = transformable.getX() > player.getX() ? -SPEED_X : SPEED_X;
            if (vx > 0)
            {
                mirrorable.mirror(Mirror.HORIZONTAL);
            }
            init = false;
        }
        transformable.moveLocationX(extrp, vx);
    }

    @Override
    public void recycle()
    {
        init = true;
        animatable.stop();
        animatable.play(walk);
    }
}
