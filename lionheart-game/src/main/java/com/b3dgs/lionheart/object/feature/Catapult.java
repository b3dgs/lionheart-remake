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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionheart.constant.Anim;

/**
 * Catapult feature implementation.
 * <p>
 * Fire on hit.
 * </p>
 */
@FeatureInterface
public final class Catapult extends FeatureModel implements Routine, Recyclable, CollidableListener
{
    private final Viewer viewer = services.get(Viewer.class);

    private final Animation idle;
    private final Animation fire;

    private Direction vector = DirectionNone.INSTANCE;
    private boolean fired;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Launcher launcher;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Catapult(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation(Anim.IDLE);
        fire = config.getAnimation(Anim.ATTACK);
    }

    /**
     * Load configuration.
     * 
     * @param config The configuration reference.
     */
    public void load(CatapultConfig config)
    {
        vector = new Force(config.getVx(), config.getVy());
    }

    @Override
    public void update(double extrp)
    {
        if (fired && !viewer.isViewable(transformable, 0, 0))
        {
            fired = false;
            animatable.play(idle);
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (!fired && with.getName().startsWith(Anim.BODY) && by.getName().startsWith(Anim.ATTACK))
        {
            fired = true;
            animatable.play(fire);
            launcher.fire(vector);
        }
    }

    @Override
    public void recycle()
    {
        animatable.play(idle);
        fired = false;
    }
}
