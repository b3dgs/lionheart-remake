/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.entity.monster;

import java.io.IOException;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.effect.ExplodeBig;
import com.b3dgs.lionheart.effect.FactoryEffect;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.EntityMover;
import com.b3dgs.lionheart.entity.EntityState;
import com.b3dgs.lionheart.entity.State;
import com.b3dgs.lionheart.entity.player.Valdyn;
import com.b3dgs.lionheart.purview.Hitable;
import com.b3dgs.lionheart.purview.patrol.Patrol;
import com.b3dgs.lionheart.purview.patrol.PatrolSide;
import com.b3dgs.lionheart.purview.patrol.PatrollerModel;
import com.b3dgs.lionheart.purview.patrol.PatrollerServices;
import com.b3dgs.lionheart.purview.patrol.PatrollerUsedServices;

/**
 * Monster base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class EntityMonster
        extends EntityMover
        implements PatrollerUsedServices, PatrollerServices, Hitable
{
    /** Hurt max time. */
    private static final int HURT_TIME = 200;
    /** Hurt timer. */
    private final Timing timerHurt;
    /** Life. */
    private final Alterable life;
    /** Patrol model. */
    private final PatrollerServices patroller;

    /** Effect factory. */
    private FactoryEffect factoryEffect;

    /**
     * @see Entity#Entity(SetupSurfaceRasteredGame)
     */
    public EntityMonster(SetupSurfaceRasteredGame setup)
    {
        super(setup);
        final Configurer configurer = setup.getConfigurer();
        life = new Alterable(configurer.getInteger("normal", "life"));
        timerHurt = new Timing();
        patroller = new PatrollerModel(this);
    }

    /**
     * Called to update with another entity.
     * 
     * @param entity The entity to update with.
     */
    public void update(Entity entity)
    {
        // Nothing by default
    }

    /**
     * Called when entity is hit once.
     * 
     * @param entity The entity that hit.
     */
    protected void onHitBy(Entity entity)
    {
        // Nothing by default
    }

    /**
     * Check if hurt time has been started.
     * 
     * @return <code>true</code> if started, <code>false</code> else.
     */
    protected boolean isHurtStarted()
    {
        return timerHurt.isStarted();
    }

    /*
     * EntityMover
     */

    @Override
    public void prepare(ContextGame context)
    {
        super.prepare(context);
        factoryEffect = context.getService(FactoryEffect.class);
    }

    @Override
    public void prepare()
    {
        patroller.prepare();
        movement.setDirectionToReach(movement.getDirectionHorizontal(), movement.getDirectionVertical());
        life.fill();
    }

    @Override
    public void save(FileWriting file) throws IOException
    {
        super.save(file);
        patroller.save(file);
    }

    @Override
    public void load(FileReading file) throws IOException
    {
        super.load(file);
        patroller.load(file);
    }

    @Override
    public void checkCollision(Valdyn player)
    {
        update(player);
        if (player.collide(this))
        {
            player.hitBy(this);
        }
        if (player.getCollisionAttack().collide(this))
        {
            player.hitThat(this);
            hitBy(player);
        }
    }

    @Override
    protected void updateActions()
    {
        if (timerHurt.isStarted() && timerHurt.elapsed(EntityMonster.HURT_TIME))
        {
            timerHurt.stop();
        }
        final State state = status.getState();
        if (state == EntityState.TURN)
        {
            if (getAnimState() == AnimState.FINISHED)
            {
                final int side = getSide();
                setSide(-side);
                mirror(side < 0);
                if (getPatrolType() == Patrol.HORIZONTAL)
                {
                    setMovementForce(getMovementSpeedMax() * side, 0.0);
                    movement.setDirectionToReach(getMovementSpeedMax() * side, 0.0);
                    teleportX(getLocationIntX() + side);
                }
                else if (getPatrolType() == Patrol.VERTICAL)
                {
                    setMovementForce(0.0, getMovementSpeedMax() * side);
                    movement.setDirectionToReach(getMovementSpeedMax() * side, 0.0);
                    teleportY(getLocationIntY() + side);
                }
            }
            else
            {
                movement.reset();
            }
        }
        else if (state == EntityState.WALK)
        {
            if (getPatrolType() == Patrol.HORIZONTAL)
            {
                final int x = getLocationIntX();
                if (x > getPositionMax())
                {
                    teleportX(getPositionMax());
                }
                if (x < getPositionMin())
                {
                    teleportX(getPositionMin());
                }
            }
            else if (getPatrolType() == Patrol.VERTICAL)
            {
                final int y = getLocationIntY();
                if (y > getPositionMax())
                {
                    teleportY(getPositionMax());
                }
                if (y < getPositionMin())
                {
                    teleportY(getPositionMin());
                }
            }
        }
    }

    @Override
    protected void updateStates()
    {
        boolean willTurn = false;
        if (getPatrolType() == Patrol.HORIZONTAL)
        {
            final int x = getLocationIntX();
            willTurn = x == getPositionMin() || x == getPositionMax();
        }
        else if (getPatrolType() == Patrol.VERTICAL)
        {
            final int y = getLocationIntY();
            willTurn = y == getPositionMin() || y == getPositionMax();
        }

        final double diffHorizontal = getDiffHorizontal();
        final double diffVertical = getDiffVertical();
        if (hasPatrol() && willTurn)
        {
            status.setState(EntityState.TURN);
        }
        else if (diffHorizontal != 0.0 || diffVertical != 0.0)
        {
            status.setState(EntityState.WALK);
        }
        else
        {
            status.setState(EntityState.IDLE);
        }
    }

    @Override
    protected void updateDead()
    {
        factoryEffect.startEffect(ExplodeBig.MEDIA, getDeathLocation(), -getCollisionData().getWidth() / 2 - 5, -5);
        Sfx.EXPLODE.play();
        destroy();
    }

    @Override
    protected void handleAnimations(double extrp)
    {
        final State state = status.getState();
        if (state == EntityState.WALK)
        {
            final double speed = Math.abs(getHorizontalForce()) / 3;
            setAnimSpeed(speed);
        }
        super.handleAnimations(extrp);
    }

    /*
     * Hitable
     */

    @Override
    public void hitBy(Entity entity)
    {
        if (!timerHurt.isStarted())
        {
            timerHurt.start();
            life.decrease(1);
            if (life.isEmpty())
            {
                kill();
            }
            else
            {
                Sfx.MONSTER_HURT.play();
                onHitBy(entity);
            }
        }
    }

    /*
     * PatrollerUsedServices
     */

    @Override
    public final void setMovementForce(double fh, double fv)
    {
        movement.setForce(fh, fv);
    }

    /*
     * PatrollerServices
     */

    @Override
    public final void enableMovement(Patrol type)
    {
        patroller.enableMovement(type);
    }

    @Override
    public final void setSide(int side)
    {
        patroller.setSide(side);
    }

    @Override
    public final void setPatrolType(Patrol movement)
    {
        patroller.setPatrolType(movement);
    }

    @Override
    public final void setFirstMove(PatrolSide firstMove)
    {
        patroller.setFirstMove(firstMove);
    }

    @Override
    public final void setMoveSpeed(int speed)
    {
        patroller.setMoveSpeed(speed);
    }

    @Override
    public final void setPatrolLeft(int left)
    {
        patroller.setPatrolLeft(left);
    }

    @Override
    public final void setPatrolRight(int right)
    {
        patroller.setPatrolRight(right);
    }

    @Override
    public final int getSide()
    {
        return patroller.getSide();
    }

    @Override
    public final Patrol getPatrolType()
    {
        return patroller.getPatrolType();
    }

    @Override
    public final PatrolSide getFirstMove()
    {
        return patroller.getFirstMove();
    }

    @Override
    public final int getMoveSpeed()
    {
        return patroller.getMoveSpeed();
    }

    @Override
    public final int getPatrolLeft()
    {
        return patroller.getPatrolLeft();
    }

    @Override
    public final int getPatrolRight()
    {
        return patroller.getPatrolRight();
    }

    @Override
    public final int getPositionMin()
    {
        return patroller.getPositionMin();
    }

    @Override
    public final int getPositionMax()
    {
        return patroller.getPositionMax();
    }

    @Override
    public final boolean hasPatrol()
    {
        return patroller.hasPatrol();
    }

    @Override
    public final boolean isPatrolEnabled()
    {
        return patroller.isPatrolEnabled();
    }

    @Override
    public final boolean isPatrolEnabled(Patrol type)
    {
        return patroller.isPatrolEnabled(type);
    }
}
