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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
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
import com.b3dgs.lionheart.object.state.StateTurn;

/**
 * Patrol feature implementation.
 * <p>
 * Move loop from starting position to defined amplitude with specified speed.
 * </p>
 */
@FeatureInterface
public final class Patrol extends FeatureModel implements Routine, TileCollidableListener, CollidableListener
{
    private final AnimationConfig anim;

    private double sh;
    private double sv;
    private int amplitude;
    private boolean coll;

    private Updatable checker;
    private double moved;

    @FeatureGet private EntityModel model;
    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Transformable transformable;

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

        final PatrolConfig config = PatrolConfig.imports(setup);
        load(config);

        anim = AnimationConfig.imports(setup);
    }

    /**
     * Load patrol configuration.
     * 
     * @param config The configuration media.
     */
    public void load(PatrolConfig config)
    {
        config.getSh().ifPresent(v -> sh = v);
        config.getSv().ifPresent(v -> sv = v);
        config.getAmplitude().ifPresent(a -> amplitude = a);
        config.getColl().ifPresent(c -> coll = c.booleanValue());

        checkAmplitude();
        if (mirrorable != null)
        {
            applyMirror();
        }
    }

    /**
     * Perform mirror computation if required.
     */
    public void applyMirror()
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

    /**
     * Check amplitude movement.
     */
    private void checkAmplitude()
    {
        if (amplitude > 0)
        {
            checker = extrp ->
            {
                if (Math.abs(moved) > amplitude)
                {
                    moved = 0.0;
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
                }
            };
        }
        else
        {
            checker = UpdatableVoid.getInstance();
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        stateHandler.changeState(StatePatrol.class);
        stateHandler.addListener((from, to) ->
        {
            collidable.setEnabled(!coll || !Anim.TURN.equals(EntityModel.getAnimationName(to)));
        });
        model.setInput(new InputDeviceControlVoid()
        {
            @Override
            public double getHorizontalDirection()
            {
                return sh;
            }

            @Override
            public double getVerticalDirection()
            {
                return sv;
            }
        });

        applyMirror();
        mirrorable.update(1.0);
    }

    @Override
    public void update(double extrp)
    {
        if (!stateHandler.isState(StateJump.class) || stateHandler.isState(StateFall.class))
        {
            checker.update(extrp);
            moved += model.getMovement().getDirectionHorizontal() + model.getMovement().getDirectionVertical();
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
        if (result.startWithX(CollisionName.STEEP))
        {
            sh = -sh;
            transformable.teleportX(transformable.getOldX() + sh);
        }
    }
}
