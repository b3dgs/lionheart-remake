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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.AnimatorFrameListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.object.state.StatePatrol;
import com.b3dgs.lionheart.object.state.executioner.StateExecutionerAttackPrepare;
import com.b3dgs.lionheart.object.state.executioner.StateExecutionerDefense;

/**
 * Executioner feature implementation.
 * <ol>
 * <li>Spawn wall behind.</li>
 * <li>Move wall down on death.</li>
 * </ol>
 */
@FeatureInterface
public final class Executioner extends FeatureModel implements Routine, Recyclable
{
    /** Maximum attack 1 distance. */
    public static final int ATTACK1_DISTANCE_MAX = 96;
    /** Maximum attack 2 distance. */
    public static final int ATTACK2_DISTANCE_MAX = 32;
    private static final int ATTACK1_DISTANCE_MIN = 80;
    private static final int DEFENSE_DISTANCE = 64;

    private final Trackable target = services.get(Trackable.class);
    private final Spawner spawner = services.get(Spawner.class);

    private boolean first;
    private Animation fall;
    private Animatable wall;

    @FeatureGet private Animatable animatable;
    @FeatureGet private Rasterable rasterable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Hurtable hurtable;
    @FeatureGet private Stats stats;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Executioner(Services services, Setup setup)
    {
        super(services, setup);
    }

    /**
     * Check if player on sight.
     * 
     * @return <code>true</code> if on sight, <code>false</code> else.
     */
    private boolean isOnSight()
    {
        return target.getX() < transformable.getX() && mirrorable.is(Mirror.HORIZONTAL)
               || target.getX() > transformable.getX() && mirrorable.is(Mirror.NONE);
    }

    /**
     * Check attack range.
     * 
     * @return <code>true</code> if on range, <code>false</code> else.
     */
    private boolean isAttackRange()
    {
        final double dist = getPlayerDistance();
        return dist < ATTACK2_DISTANCE_MAX || UtilMath.isBetween(dist, ATTACK1_DISTANCE_MIN, ATTACK1_DISTANCE_MAX);
    }

    /**
     * Get distance with player.
     * 
     * @return The player distance.
     */
    private double getPlayerDistance()
    {
        return Math.abs(target.getX() - transformable.getX());
    }

    @Override
    public void update(double extrp)
    {
        if (first)
        {
            final Transformable t = wall.getFeature(Transformable.class);
            t.teleport(transformable.getX() + 48.0, transformable.getY());
            t.check(true);
            first = false;
        }
        else
        {
            if (isOnSight())
            {
                if (stateHandler.isState(StatePatrol.class))
                {
                    if (isAttackRange())
                    {
                        collidable.setEnabled(true);
                        stateHandler.changeState(StateExecutionerAttackPrepare.class);
                    }
                    else
                    {
                        if (target.getFeature(Animatable.class).getAnim().getName().startsWith(Anim.ATTACK)
                            && target.getFeature(Mirrorable.class).getMirror() != mirrorable.getMirror()
                            && getPlayerDistance() < DEFENSE_DISTANCE)
                        {
                            collidable.setEnabled(false);
                            stateHandler.changeState(StateExecutionerDefense.class);
                        }
                    }
                }
            }
            else
            {
                collidable.setEnabled(true);
            }
            if (stats.getHealth() == 0 && wall.is(AnimState.FINISHED))
            {
                wall.play(fall);
                hurtable.kill(true);
            }
        }
    }

    @Override
    public void recycle()
    {
        if (!Settings.isEditor())
        {
            final Featurable featurable = spawner.spawn(Medias.create(Folder.LIMB, "ancienttown", "Wall.xml"),
                                                        -100.0,
                                                        -100.0);
            wall = featurable.getFeature(Animatable.class);
            wall.addListener((AnimatorFrameListener) s ->
            {
                if (s == fall.getLast())
                {
                    wall.getFeature(Identifiable.class).destroy();
                }
            });

            final AnimationConfig config = AnimationConfig.imports(new Configurer(featurable.getMedia()));
            fall = config.getAnimation(Anim.FALL);
        }
        first = true;
    }
}
