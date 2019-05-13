/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListenerVoid;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.Routine;
import com.b3dgs.lionheart.object.state.StateDie;
import com.b3dgs.lionheart.object.state.StateHurt;

/**
 * Hurtable feature implementation.
 */
@FeatureInterface
public final class Hurtable extends FeatureModel
                            implements Routine, CollidableListener, TileCollidableListener, Recyclable
{
    private static final double HURT_JUMP_FORCE = 3.5;
    private static final long HURT_RECOVER_TICK = 70L;

    private final Force hurtForce = new Force();
    private final Tick recover = new Tick();
    private final CollidableListener hurt;
    private final TileCollidableListener hurtTile;

    private CollidableListener current;
    private TileCollidableListener currentTile;

    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private EntityModel model;
    @FeatureGet private Stats stats;

    /**
     * Create hurtable.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Hurtable(Services services, SetupSurfaceRastered setup)
    {
        super();

        final Spawner spawner = services.get(Spawner.class);
        final HurtableConfig config = HurtableConfig.imports(setup);

        hurt = (collidable, with, by) ->
        {
            if (recover.elapsed(HURT_RECOVER_TICK)
                && Double.compare(hurtForce.getDirectionHorizontal(), 0.0) == 0
                && by.getName().startsWith(Constant.ANIM_PREFIX_ATTACK))
            {
                if (stats.applyDamages(collidable.getFeature(Stats.class).getDamages()))
                {
                    spawner.spawn(config.getEffect(), transformable);
                    identifiable.destroy();
                    current = CollidableListenerVoid.getInstance();
                }
                if (model.getMovement().isDecreasingHorizontal())
                {
                    mirrorable.mirror(Mirror.NONE);
                }
                stateHandler.changeState(StateHurt.class);
                final int side = UtilMath.getSign(transformable.getX()
                                                  - collidable.getFeature(Transformable.class).getX());
                hurtForce.setDirection(1.8 * side, 0.0);
                recover.restart();
            }
            if (recover.elapsed(HURT_RECOVER_TICK) && with.getName().startsWith(Constant.ANIM_PREFIX_BODY))
            {
                if (stats.applyDamages(collidable.getFeature(Stats.class).getDamages()))
                {
                    stateHandler.changeState(StateDie.class);
                }
                else
                {
                    stateHandler.changeState(StateHurt.class);
                    hurtJump();
                }
                recover.restart();
            }
        };
        hurtTile = (result, tile) ->
        {
            if (recover.elapsed(HURT_RECOVER_TICK)
                && Double.compare(hurtForce.getDirectionVertical(), 0.0) == 0
                && result.startWith("spike"))
            {
                model.getMovement().setDirection(DirectionNone.INSTANCE);
                model.getMovement().setDestination(0.0, 0.0);
                if (stats.applyDamages(1))
                {
                    stateHandler.changeState(StateDie.class);
                }
                else
                {
                    stateHandler.changeState(StateHurt.class);
                    hurtJump();
                }
                recover.restart();
            }
        };
        hurtForce.setDestination(0.0, 0.0);
        hurtForce.setSensibility(0.1);
        hurtForce.setVelocity(0.5);

        recycle();
    }

    /**
     * Force jump on hurt.
     */
    private void hurtJump()
    {
        model.getJump().setSensibility(0.1);
        model.getJump().setVelocity(0.18);
        model.getJump().setDestination(0.0, 0.0);
        model.getJump().setDirection(0.0, HURT_JUMP_FORCE);
        model.getJump().setDirectionMaximum(new Force(0.0, HURT_JUMP_FORCE));
    }

    /**
     * Check if hurting.
     * 
     * @return <code>true</code> if hurting, <code>false</code> else.
     */
    public boolean isHurting()
    {
        return model.getJump().getDirectionVertical() > 0
               || model.getMovement().isIncreasingHorizontal() && mirrorable.getMirror() == Mirror.NONE
               || model.getMovement().isDecreasingHorizontal() && mirrorable.getMirror() == Mirror.HORIZONTAL;
    }

    @Override
    public void update(double extrp)
    {
        hurtForce.update(extrp);
        recover.update(extrp);
        model.getMovement().addDirection(extrp, hurtForce);
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        current.notifyCollided(collidable, with, by);
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        currentTile.notifyTileCollided(result, category);
    }

    @Override
    public void recycle()
    {
        current = hurt;
        currentTile = hurtTile;
        recover.restart();
        recover.set(HURT_RECOVER_TICK);
    }
}
