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
package com.b3dgs.lionheart.entity.scenery;

import java.io.IOException;

import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.game.Movement;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.EntityState;
import com.b3dgs.lionheart.entity.State;
import com.b3dgs.lionheart.entity.player.Valdyn;
import com.b3dgs.lionheart.purview.ascend.AscendableModel;
import com.b3dgs.lionheart.purview.ascend.AscendableServices;
import com.b3dgs.lionheart.purview.ascend.AscendableUsedServices;
import com.b3dgs.lionheart.purview.patrol.Patrol;
import com.b3dgs.lionheart.purview.patrol.PatrolSide;
import com.b3dgs.lionheart.purview.patrol.PatrollerModel;
import com.b3dgs.lionheart.purview.patrol.PatrollerServices;
import com.b3dgs.lionheart.purview.patrol.PatrollerUsedServices;

/**
 * Beetle base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class EntitySceneryPatroller
        extends EntityScenery
        implements PatrollerUsedServices, PatrollerServices, AscendableUsedServices
{
    /** Movement force. */
    private final Movement movement;
    /** Patrollable model. */
    private final PatrollerServices patroller;
    /** Ascendable model. */
    private final AscendableServices ascendable;
    /** Movement max speed. */
    private double movementSpeedMax;

    /**
     * @see Entity#Entity(SetupSurfaceRasteredGame)
     */
    protected EntitySceneryPatroller(SetupSurfaceRasteredGame setup)
    {
        super(setup);
        movement = new Movement();
        patroller = new PatrollerModel(this);
        ascendable = new AscendableModel(this);
        setDirections(movement);
    }

    /**
     * Check if patrol end has been reached.
     */
    protected void checkPatrolEnd()
    {
        final State state = status.getState();
        if (state == EntityState.TURN)
        {
            resetMovement();
            updatePatrolEnd();
        }
    }

    /**
     * Update the patrol when end has been reached.
     */
    private void updatePatrolEnd()
    {
        if (getAnimState() == AnimState.FINISHED)
        {
            final int side = patroller.getSide();
            setSide(-side);
            if (getPatrolType() == Patrol.HORIZONTAL)
            {
                mirror(side >= 0);
                setMovementForce(movementSpeedMax * side, 0.0);
                teleportX(getLocationIntX() + side);
            }
            else if (getPatrolType() == Patrol.VERTICAL)
            {
                mirror(side < 0);
                setMovementForce(0.0, movementSpeedMax * side);
                teleportY(getLocationIntY() + side);
            }
        }
    }

    /**
     * Reset the movement to zero.
     */
    protected final void resetMovement()
    {
        movement.reset();
    }

    /*
     * Preparable
     */

    @Override
    public void prepare()
    {
        patroller.prepare();
    }

    /*
     * EntityScenery
     */

    @Override
    public void checkCollision(Valdyn player)
    {
        if (!status.isState(EntityState.TURN))
        {
            ascendable.checkAscendBy(player, player.getCollisionLeg());
        }
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
    protected void handleActions(double extrp)
    {
        checkPatrolEnd();
        super.handleActions(extrp);
    }

    @Override
    protected void handleMovements(double extrp)
    {
        movement.update(extrp);
        super.handleMovements(extrp);
    }

    /*
     * Patrollable
     */

    @Override
    public final void setMovementForce(double fh, double fv)
    {
        movement.setForce(fh, fv);
        movement.setDirectionToReach(fh, fv);
    }

    @Override
    public final void setMovementSpeedMax(double speed)
    {
        movementSpeedMax = speed;
    }

    @Override
    public final double getMovementSpeedMax()
    {
        return movementSpeedMax;
    }

    /*
     * Patroller
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
