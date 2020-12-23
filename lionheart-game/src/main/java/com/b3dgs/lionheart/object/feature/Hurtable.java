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

import java.util.OptionalInt;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListenerVoid;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.state.StateDie;
import com.b3dgs.lionheart.object.state.StateHurt;

/**
 * Hurtable feature implementation.
 * <p>
 * Represents something that can be hurt by attack.
 * </p>
 */
@FeatureInterface
public final class Hurtable extends FeatureModel
                            implements Routine, CollidableListener, TileCollidableListener, Recyclable
{
    private static final long HURT_RECOVER_ATTACK_TICK = 20L;
    private static final long HURT_RECOVER_BODY_TICK = 120L;
    private static final long HURT_FLICKER_TICK_DURATION = 120L;
    private static final long HURT_FLICKER_TICK_SWITCH = 8;
    private static final int SPIKE_DAMAGES = 1;

    private final Force hurtForce = new Force();
    private final Tick recover = new Tick();
    private final Tick flicker = new Tick();
    private final double hurtForceValue;
    private final Spawner spawner = services.get(Spawner.class);
    private final Media effect;
    private final OptionalInt frame;
    private final boolean persist;
    private final boolean fall;

    private CollidableListener currentCollide;
    private TileCollidableListener currentTile;
    private Updatable flickerCurrent;
    private double oldGravity;
    private boolean enabled;

    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Body body;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private EntityModel model;
    @FeatureGet private Stats stats;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Hurtable(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);

        final HurtableConfig config = HurtableConfig.imports(setup);
        effect = config.getEffect();
        persist = config.hasPersist();
        fall = config.hasFall();

        if (config.hasBackward())
        {
            hurtForce.setDestination(0.0, 0.0);
            hurtForce.setSensibility(0.1);
            hurtForce.setVelocity(0.5);
            hurtForceValue = 1.8;
        }
        else
        {
            hurtForceValue = 0.0;
        }
        frame = config.getFrame();
    }

    /**
     * Set the enabled flag.
     * 
     * @param enabled The enabled flag.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Check if hurting.
     * 
     * @return <code>true</code> if hurting, <code>false</code> else.
     */
    public boolean isHurting()
    {
        return !recover.elapsed(HURT_RECOVER_ATTACK_TICK);
    }

    /**
     * Get the hurt frame.
     * 
     * @return The hurt frame.
     */
    public OptionalInt getFrame()
    {
        return frame;
    }

    /**
     * Update collidable checking.
     * 
     * @param collidable The collidable reference.
     * @param with The collision with.
     * @param by The collision by.
     */
    private void updateCollide(Collidable collidable, Collision with, Collision by)
    {
        if (enabled
            && collidable.getGroup() == Constant.COLL_GROUP_PLAYER
            && recover.elapsed(HURT_RECOVER_ATTACK_TICK)
            && Double.compare(hurtForce.getDirectionHorizontal(), 0.0) == 0
            && by.getName().startsWith(Anim.ATTACK))
        {
            updateCollideAttack(collidable);
        }
        if (collidable.getGroup() != Constant.COLL_GROUP_PLAYER
            && recover.elapsed(HURT_RECOVER_BODY_TICK)
            && with.getName().startsWith(Anim.BODY)
            && by.getName().startsWith(Anim.ATTACK))
        {
            updateCollideBody(collidable);
        }
    }

    /**
     * Update collide with sword.
     * 
     * @param collidable The collidable reference.
     */
    private void updateCollideAttack(Collidable collidable)
    {
        Sfx.MONSTER_HURT.play();
        stateHandler.changeState(StateHurt.class);
        if (stats.getHealthMax() > 0 && stats.applyDamages(collidable.getFeature(Stats.class).getDamages()))
        {
            if (fall)
            {
                body.setGravityMax(oldGravity);
            }
            currentCollide = CollidableListenerVoid.getInstance();
        }
        if (model.getMovement().isDecreasingHorizontal())
        {
            mirrorable.mirror(Mirror.NONE);
        }

        if (stats.getHealth() > 0)
        {
            final int side = UtilMath.getSign(transformable.getX() - collidable.getFeature(Transformable.class).getX());
            hurtForce.setDirection(hurtForceValue * side, 0.0);
        }
        else if (hasFeature(Patrol.class))
        {
            getFeature(Patrol.class).stop();
        }
        recover.restart();
    }

    /**
     * Update collide with body.
     * 
     * @param collidable The collidable reference.
     */
    private void updateCollideBody(Collidable collidable)
    {
        if (stats.applyDamages(collidable.getFeature(Stats.class).getDamages()))
        {
            Sfx.VALDYN_DIE.play();
            stateHandler.changeState(StateDie.class);
        }
        else
        {
            Sfx.VALDYN_HURT.play();
            stateHandler.changeState(StateHurt.class);
            hurtJump();
        }
        recover.restart();
    }

    /**
     * Update tile collision checking.
     * 
     * @param result The collision result.
     * @param category The category reference.
     */
    private void updateTile(CollisionResult result, CollisionCategory category)
    {
        if (!hasFeature(Patrol.class)
            && recover.elapsed(HURT_RECOVER_BODY_TICK)
            && (category.getAxis() == Axis.Y && result.contains(CollisionName.SPIKE)
                || category.getName().equals(CollisionName.KNEE_CENTER) && result.contains(CollisionName.SPIKE)))
        {
            if (stats.applyDamages(SPIKE_DAMAGES))
            {
                Sfx.VALDYN_DIE.play();
                stateHandler.changeState(StateDie.class);
            }
            else
            {
                Sfx.VALDYN_HURT.play();
                stateHandler.changeState(StateHurt.class);
                hurtJump();
            }
            recover.restart();
        }
        if (fall)
        {
            spawner.spawn(effect, transformable);
            identifiable.destroy();
        }
    }

    /**
     * Force jump on hurt.
     */
    private void hurtJump()
    {
        flicker.restart();
        flickerCurrent = this::updateFlicker;
    }

    /**
     * Update flicker effect on hurt.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFlicker(double extrp)
    {
        flicker.update(extrp);
        if (!stateHandler.isState(StateHurt.class))
        {
            model.setVisible(flicker.elapsed() % HURT_FLICKER_TICK_SWITCH < HURT_FLICKER_TICK_SWITCH / 2);
        }
        if (flicker.elapsed(HURT_FLICKER_TICK_DURATION))
        {
            flickerCurrent = UpdatableVoid.getInstance();
            model.setVisible(true);
        }
    }

    /**
     * Spawn effect and destroy.
     */
    public void kill()
    {
        if (!fall)
        {
            if (effect != null)
            {
                spawner.spawn(effect, transformable);
            }
            if (!persist)
            {
                identifiable.destroy();
            }
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        if (fall)
        {
            oldGravity = body.getGravityMax();
            body.setGravityMax(0.0);
        }
    }

    @Override
    public void update(double extrp)
    {
        hurtForce.update(extrp);
        recover.update(extrp);
        flickerCurrent.update(extrp);
        model.getMovement().addDirection(extrp, hurtForce);
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        currentCollide.notifyCollided(collidable, with, by);
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        currentTile.notifyTileCollided(result, category);
    }

    @Override
    public void recycle()
    {
        currentCollide = this::updateCollide;
        currentTile = this::updateTile;
        flickerCurrent = UpdatableVoid.getInstance();
        enabled = true;
        recover.restart();
        recover.set(HURT_RECOVER_BODY_TICK);
    }
}
