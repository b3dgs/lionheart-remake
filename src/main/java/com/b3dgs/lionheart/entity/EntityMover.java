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
package com.b3dgs.lionheart.entity;

import java.util.EnumMap;

import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Movement;
import com.b3dgs.lionengine.game.platform.CollisionTileCategory;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionheart.map.Tile;
import com.b3dgs.lionheart.map.TileCollision;

/**
 * Abstract entity base implementation designed to move around the map.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class EntityMover
        extends Entity
{
    /** Entity actions. */
    protected final EnumMap<EntityAction, Boolean> actions;
    /** Movement force. */
    protected final Movement movement;
    /** Movement jump force. */
    protected final Force jumpForce;
    /** Jump max force. */
    protected final double jumpHeightMax;
    /** Gravity max. */
    protected final double gravityMax;
    /** Extra gravity force. */
    private final Force extraGravityForce;
    /** Forces list used. */
    private final Force[] forces;
    /** Movement max speed. */
    protected double movementSpeedMax;

    /**
     * @see Entity#Entity(SetupEntity)
     */
    public EntityMover(SetupEntity setup)
    {
        super(setup);
        movement = new Movement();
        actions = new EnumMap<>(EntityAction.class);
        extraGravityForce = new Force();
        jumpForce = new Force();
        final Configurable configurable = setup.getConfigurable();
        jumpHeightMax = configurable.getDouble("heightMax", "data", "jump");
        gravityMax = configurable.getDouble("gravityMax", "data");
        movement.setVelocity(0.2);
        movement.setSensibility(0.4);
        forces = new Force[]
        {
                jumpForce, extraGravityForce, movement.getForce()
        };
        setMass(configurable.getDouble("mass", "data"));
        setGravityMax(gravityMax);
        addCollisionTile(EntityCollisionTileCategory.GROUND_CENTER, 0, 0);
    }

    /**
     * Update the actions.
     * 
     * @see EntityAction
     */
    protected abstract void updateActions();

    /**
     * Check vertical axis.
     * 
     * @param y The y location.
     * @param collision The collision type.
     * @return <code>true</code> if collision applied, <code>false</code> else.
     */
    public boolean checkCollisionVertical(Double y, EntityCollisionTile collision)
    {
        if (applyVerticalCollision(y))
        {
            resetGravity();
            jumpForce.setForce(Force.ZERO);
            status.setCollision(collision);
            return true;
        }
        return false;
    }

    /**
     * Check if entity can jump.
     * 
     * @return <code>true</code> if can jump, <code>false</code> else.
     */
    public boolean canJump()
    {
        return isOnGround();
    }

    /**
     * Check if entity is jumping.
     * 
     * @return <code>true</code> if jumping, <code>false</code> else.
     */
    public boolean isJumping()
    {
        return !isOnGround() && getLocationY() > getLocationOldY();
    }

    /**
     * Check if entity is falling.
     * 
     * @return <code>true</code> if falling, <code>false</code> else.
     */
    public boolean isFalling()
    {
        return !isOnGround() && getLocationY() < getLocationOldY();
    }

    /**
     * Check if entity is on ground.
     * 
     * @return <code>true</code> if on ground, <code>false</code> else.
     */
    public boolean isOnGround()
    {
        return status.getCollision() == EntityCollisionTile.GROUND;
    }

    /**
     * Check if the specified action is enabled.
     * 
     * @param action The action to check.
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    public boolean isEnabled(EntityAction action)
    {
        return actions.get(action).booleanValue();
    }

    /**
     * Check vertical axis.
     * 
     * @param tile The tile collision.
     * @return <code>true</code> if collision occurred, <code>false</code> else.
     */
    protected boolean checkCollisionVertical(Tile tile)
    {
        if (tile != null)
        {
            final Double y = tile.getCollisionY(this);
            checkCollisionVertical(y, EntityCollisionTile.GROUND);
            return true;
        }
        return false;
    }

    /**
     * Check horizontal axis.
     * 
     * @param category The collision category.
     * @return The tile found.
     */
    protected Tile checkCollisionHorizontal(CollisionTileCategory<TileCollision> category)
    {
        final Tile tile = getCollisionTile(map, category);
        if (tile != null)
        {
            final Double x = tile.getCollisionX(this);
            if (applyHorizontalCollision(x))
            {
                movement.reset();
            }
        }
        return tile;
    }

    /**
     * Get the horizontal force.
     * 
     * @return The horizontal force.
     */
    protected double getHorizontalForce()
    {
        return movement.getForce().getForceHorizontal();
    }

    /**
     * Check the map limit and apply collision if necessary.
     */
    private void checkMapLimit()
    {
        final int limitLeft = 0;
        if (getLocationX() < limitLeft)
        {
            teleportX(limitLeft);
            movement.reset();
        }
        final int limitRight = map.getWidthInTile() * map.getTileWidth();
        if (getLocationX() > limitRight)
        {
            teleportX(limitRight);
            movement.reset();
        }
    }

    /*
     * Entity
     */

    @Override
    public void kill()
    {
        super.kill();
        movement.reset();
    }

    @Override
    public void respawn()
    {
        super.respawn();
        movement.reset();
    }

    @Override
    protected void updateCollisions()
    {
        checkMapLimit();
        status.setCollision(EntityCollisionTile.NONE);
        // Vertical collision
        if (getDiffVertical() < 0 || isOnGround())
        {
            final Tile tile = getCollisionTile(map, EntityCollisionTileCategory.GROUND_CENTER);
            checkCollisionVertical(tile);
        }
    }

    @Override
    protected void handleActions(double extrp)
    {
        if (!isDead())
        {
            updateActions();
        }
        super.handleActions(extrp);
    }

    @Override
    protected void handleMovements(double extrp)
    {
        movement.update(extrp);
        super.handleMovements(extrp);
    }

    @Override
    protected Force[] getForces()
    {
        return forces;
    }
}
