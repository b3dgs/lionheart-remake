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

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.io.InputDeviceControlVoid;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.state.StateFall;
import com.b3dgs.lionheart.object.state.StateJump;
import com.b3dgs.lionheart.object.state.StatePatrol;
import com.b3dgs.lionheart.object.state.StatePatrolCeil;
import com.b3dgs.lionheart.object.state.StateTurn;

/**
 * Patrol feature implementation.
 * <p>
 * Move loop from starting position to defined amplitude with specified speed.
 * </p>
 */
@FeatureInterface
public final class Patrol extends FeatureModel
                          implements Routine, TileCollidableListener, CollidableListener, Recyclable
{
    private final List<PatrolConfig> patrols = new ArrayList<>();
    private final Transformable player = services.get(SwordShade.class).getFeature(Transformable.class);
    private final AnimationConfig anim;

    private int currentIndex;
    private double sh;
    private double sv;
    private int amplitude;
    private int offset;
    private boolean coll;
    private int proximity;
    private int animOffset;
    private int skip;

    private double startX;
    private double startY;

    private Updatable checker;
    private boolean enabled = true;
    private boolean first = true;
    private double idle;

    @FeatureGet private EntityModel model;
    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Rasterable rasterable;
    @FeatureGet private Animatable animatable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Patrol(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);

        patrols.addAll(PatrolConfig.imports(setup));

        anim = AnimationConfig.imports(setup);
    }

    /**
     * Load patrol configuration.
     * 
     * @param patrols The configuration patrols.
     */
    public void load(List<PatrolConfig> patrols)
    {
        if (!patrols.isEmpty())
        {
            this.patrols.addAll(patrols);
            loadNextPatrol();
        }
    }

    /**
     * Stop patrol.
     */
    public void stop()
    {
        sh = 0.0;
        sv = 0.0;
    }

    /**
     * Load next available patrol.
     */
    private void loadNextPatrol()
    {
        currentIndex++;
        if (currentIndex >= patrols.size())
        {
            currentIndex = 0;
        }
        final PatrolConfig config = patrols.get(currentIndex);

        config.getSh().ifPresent(h -> sh = h);
        config.getSv().ifPresent(v -> sv = v);
        config.getAmplitude().ifPresent(a -> amplitude = a);
        config.getOffset().ifPresent(o -> offset = o);
        config.getColl().ifPresent(c -> coll = c.booleanValue());
        config.getAnimOffset().ifPresent(o -> animOffset = o);
        config.getProximity().ifPresent(p ->
        {
            proximity = p;
            enabled = false;
        });

        checkAmplitude();
        applyMirror();

        first = true;
        startX = 0;
        startY = 0;

        if (rasterable != null)
        {
            rasterable.setAnimOffset(animOffset);
        }
    }

    /**
     * Perform mirror computation if required.
     */
    public void applyMirror()
    {
        if (mirrorable != null
            && enabled
            && (patrols.isEmpty()
                || currentIndex > -1 && patrols.get(currentIndex).getMirror().orElse(Boolean.TRUE).booleanValue()))
        {
            if (mirrorable.is(Mirror.NONE))
            {
                if (sh < 0)
                {
                    mirrorable.mirror(Mirror.HORIZONTAL);
                }
                else if (sv < 0)
                {
                    mirrorable.mirror(Mirror.VERTICAL);
                }
            }
            else if (mirrorable.is(Mirror.HORIZONTAL) && sh > 0 || mirrorable.is(Mirror.VERTICAL) && sv > 0)
            {
                mirrorable.mirror(Mirror.NONE);
            }
        }
    }

    /**
     * Check amplitude movement.
     */
    private void checkAmplitude()
    {
        if (amplitude == 0)
        {
            checker = UpdatableVoid.getInstance();
        }
        else
        {
            checker = extrp ->
            {
                if (skip == 0
                    && (Double.compare(sh, 0.0) != 0 && Math.abs(startX - transformable.getX()) > amplitude
                        || Double.compare(sv, 0.0) != 0 && Math.abs(startY - transformable.getY()) > amplitude)
                    || skip == 2)
                {
                    if (sh > 0)
                    {
                        transformable.teleportX(startX + amplitude);
                    }
                    else if (sh < 0)
                    {
                        transformable.teleportX(startX - amplitude);
                    }
                    if (sv > 0)
                    {
                        transformable.teleportY(startY + amplitude);
                    }
                    else if (sv < 0)
                    {
                        transformable.teleportY(startY - amplitude);
                    }
                    model.getMovement().zero();

                    if (Double.compare(sh, 0.0) != 0)
                    {
                        sh = -sh;
                    }
                    if (Double.compare(sv, 0.0) != 0)
                    {
                        sv = -sv;
                    }
                    if (anim.hasAnimation(Anim.TURN))
                    {
                        stateHandler.changeState(StateTurn.class);
                    }
                    else
                    {
                        applyMirror();
                    }
                    if (patrols.size() > 1)
                    {
                        loadNextPatrol();
                    }
                    else
                    {
                        startX = transformable.getX();
                        startY = transformable.getY();
                    }
                    skip = 0;
                }
                if (amplitude < 0 && !stateHandler.isState(StateTurn.class))
                {
                    skip++;
                }
            };
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        stateHandler.addListener((from, to) ->
        {
            collidable.setEnabled(!coll || !Anim.TURN.equals(EntityModel.getAnimationName(to)));
        });
        model.setInput(new InputDeviceControlVoid()
        {
            @Override
            public double getHorizontalDirection()
            {
                return enabled && stateHandler.isState(StatePatrol.class) ? sh : 0.0;
            }

            @Override
            public double getVerticalDirection()
            {
                return enabled ? sv : 0.0;
            }
        });
        model.getMovement().setVelocity(1.0);

        applyMirror();
        mirrorable.update(1.0);

        rasterable.setAnimOffset(animOffset);
    }

    @Override
    public void update(double extrp)
    {
        if (first)
        {
            first = false;
            startX = transformable.getX();
            startY = transformable.getY();
            if (offset > 0)
            {
                transformable.teleportX(startX + offset);
            }
        }
        if (!enabled)
        {
            if (Math.abs(transformable.getX() - player.getX()) < proximity)
            {
                enabled = true;
            }
            else
            {
                idle = UtilMath.wrapDouble(idle + 0.15, 0, 360);
                transformable.teleportY(startY + Math.sin(idle) * 2.0);
            }
        }
        if (!stateHandler.isState(StateJump.class) || stateHandler.isState(StateFall.class))
        {
            checker.update(extrp);
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (by.getName().startsWith(CollisionName.SPIKE))
        {
            sh = -sh;
            transformable.teleportX(transformable.getOldX() + sh);
        }
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        if (category.getAxis() == Axis.X && result.startWithX(CollisionName.STEEP))
        {
            sh = -sh;
            transformable.teleportX(transformable.getOldX() + sh);
        }
        if (category.getAxis() == Axis.Y && result.containsY(CollisionName.HORIZONTAL))
        {
            stateHandler.changeState(StatePatrolCeil.class);
            transformable.teleportY(result.getY().doubleValue() - 1.0);
        }
    }

    @Override
    public void recycle()
    {
        currentIndex = -1;
        startX = 0.0;
        startY = 0.0;
        checker = UpdatableVoid.getInstance();
        enabled = true;
        first = true;
        idle = 0.0;
        skip = 0;
        stateHandler.changeState(StatePatrol.class);
    }
}
