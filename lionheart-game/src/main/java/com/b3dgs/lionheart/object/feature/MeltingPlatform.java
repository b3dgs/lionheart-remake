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
import com.b3dgs.lionengine.AnimatorFrameListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
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
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;

/**
 * MeltingPlatform feature implementation.
 * <ol>
 * <li>Listen to animation state changes.</li>
 * <li>On {@link AnimState#FINISHED}, destroy and reset.</li>
 * </ol>
 */
@FeatureInterface
public final class MeltingPlatform extends FeatureModel implements Recyclable, CollidableListener, Routine
{
    private final Animation idle;
    private final Animation fall;
    private final Viewer viewer = services.get(Viewer.class);

    @FeatureGet private Animatable animatable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Rasterable rasterable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public MeltingPlatform(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation(Anim.IDLE);
        fall = config.getAnimation(Anim.FALL);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        animatable.addListener((AnimatorFrameListener) frame ->
        {
            if (frame == fall.getLast())
            {
                collidable.setEnabled(false);
                rasterable.setVisibility(false);
            }
        });
    }

    @Override
    public void update(double extrp)
    {
        if (!collidable.isEnabled() && !viewer.isViewable(transformable, viewer.getWidth() / 2, viewer.getHeight() / 2))
        {
            recycle();
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (animatable.is(AnimState.FINISHED) && by.getName().startsWith(CollisionName.LEG))
        {
            animatable.play(fall);
            Sfx.SCENERY_MELTINGPLATFORM.play();
        }
    }

    @Override
    public void recycle()
    {
        animatable.play(idle);
        collidable.setEnabled(true);
        rasterable.setVisibility(true);
    }
}
