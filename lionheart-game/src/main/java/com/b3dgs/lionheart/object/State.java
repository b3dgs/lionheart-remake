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
package com.b3dgs.lionheart.object;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.helper.StateHelper;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.feature.Glue;

/**
 * Base state with animation implementation.
 */
public abstract class State extends StateHelper<EntityModel>
{
    /** Movement reference. */
    protected final Force movement;
    /** Jump reference. */
    protected final Force jump;
    /** Horizontal collision flag. */
    protected final AtomicBoolean collideX = new AtomicBoolean();
    /** Horizontal collision flag on left. */
    protected final AtomicBoolean collideXleft = new AtomicBoolean();
    /** Horizontal collision flag on right. */
    protected final AtomicBoolean collideXright = new AtomicBoolean();
    /** Vertical collision flag. */
    protected final AtomicBoolean collideY = new AtomicBoolean();
    /** Vertical left collision flag. */
    protected final AtomicBoolean collideYleft = new AtomicBoolean();
    /** Vertical right collision flag. */
    protected final AtomicBoolean collideYright = new AtomicBoolean();
    /** Grip collision flag. */
    protected final AtomicBoolean grip = new AtomicBoolean();

    /** Steep gameplay. */
    protected final GameplaySteep steep = new GameplaySteep();
    /** Liana gameplay. */
    protected final GameplayLiana liana = new GameplayLiana();
    /** Tile collidable listener. */
    private final TileCollidableListener listenerTileCollidable;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    protected State(EntityModel model, Animation animation)
    {
        super(model, animation);

        movement = model.getMovement();
        jump = model.getJump();

        listenerTileCollidable = (result, category) ->
        {
            if (Axis.X == category.getAxis())
            {
                if (category.getName().startsWith(CollisionName.KNEE))
                {
                    onCollideKnee(result, category);
                }
            }
            else if (Axis.Y == category.getAxis())
            {
                if (!liana.is() && category.getName().startsWith(CollisionName.LEG))
                {
                    onCollideLeg(result, category);
                }
                else if (category.getName().startsWith(CollisionName.HAND))
                {
                    onCollideHand(result, category);
                }
                else if (category.getName().startsWith(CollisionName.HEAD))
                {
                    onCollideHead(result, category);
                }
            }
        };
    }

    /**
     * Called when a tile collision occurred on horizontal axis with knee.
     * 
     * @param result The collided tile.
     * @param category The collided axis.
     */
    protected void onCollideKnee(CollisionResult result, CollisionCategory category)
    {
        collideX.set(true);
        collideXright.set(result.endWithX(CollisionName.LEFT_VERTICAL));
        collideXleft.set(result.endWithX(CollisionName.RIGHT_VERTICAL));

        if (movement.getDirectionHorizontal() < 0 && result.endWithX(CollisionName.RIGHT_VERTICAL)
            || movement.getDirectionHorizontal() > 0 && result.endWithX(CollisionName.LEFT_VERTICAL))
        {
            tileCollidable.apply(result);
            movement.zero();
        }
        if (category.getName().equals(CollisionName.KNEE_CENTER) && result.contains(CollisionName.SPIKE))
        {
            transformable.teleportX(transformable.getOldX());
            movement.zero();
        }
    }

    /**
     * Called when a tile collision occurred on vertical axis with leg.
     * 
     * @param result The collided tile.
     * @param category The collided axis.
     */
    protected void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        if (!result.startWithY(CollisionName.LIANA)
            && !result.startWithY(CollisionName.SPIKE)
            && jump.getDirectionVertical() < 0.1)
        {
            collideY.set(true);
            if (category.getName().endsWith(CollisionName.LEFT))
            {
                collideYleft.set(true);
            }
            else if (category.getName().endsWith(CollisionName.RIGHT))
            {
                collideYright.set(true);
            }
            tileCollidable.apply(result);
            body.resetGravity();
        }
        steep.onCollideLeg(result, category);
    }

    /**
     * Called when a tile collision occurred on vertical axis with hand.
     * 
     * @param result The collided tile.
     * @param category The collided axis.
     */
    protected void onCollideHand(CollisionResult result, CollisionCategory category)
    {
        liana.onCollideHand(result, category);
        if (result.startWithY(CollisionName.GRIP))
        {
            grip.set(true);
        }
    }

    /**
     * Called when a tile collision occurred on vertical axis with head.
     * 
     * @param result The collided tile.
     * @param category The collided axis.
     */
    protected void onCollideHead(CollisionResult result, CollisionCategory category)
    {
        jump.zero();
    }

    /**
     * Called when a collision occurred with another {@link Collidable}.
     * 
     * @param collidable The collidable reference.
     * @param with The collision collided with (source).
     * @param by The collision collided by (other).
     */
    protected void onCollided(Collidable collidable, Collision with, Collision by)
    {
        if (collidable.hasFeature(Glue.class)
            && with.getName().startsWith(Anim.LEG)
            && by.getName().startsWith(CollisionName.GROUND))
        {
            collideY.set(true);
        }

        if (with.getName().contains(CollisionName.BODY)
            && (movement.getDirectionHorizontal() < 0 && by.getName().contains(CollisionName.RIGHT_VERTICAL)
                || movement.getDirectionHorizontal() > 0 && by.getName().contains(CollisionName.LEFT_VERTICAL)))
        {
            final Transformable other = collidable.getFeature(Transformable.class);
            collideX.set(true);
            if (by.getName().contains(CollisionName.LEFT_VERTICAL))
            {
                transformable.teleportX(other.getX() + by.getOffsetX() - with.getWidth() / 2);
                collideXright.set(true);
            }
            if (by.getName().contains(CollisionName.RIGHT_VERTICAL))
            {
                transformable.teleportX(other.getX() + by.getOffsetX() + with.getWidth() / 2);
                collideXleft.set(true);
            }
            movement.zero();
        }
    }

    /**
     * Check if fire button is enabled.
     * 
     * @return <code>true</code> if active, <code>false</code> else.
     */
    protected final boolean isFire()
    {
        return isFire(Constant.FIRE1);
    }

    /**
     * Check if fire button once.
     * 
     * @return <code>true</code> if active, <code>false</code> else.
     */
    protected final boolean isFireOnce()
    {
        return isFireOnce(Constant.FIRE1);
    }

    @Override
    public void enter()
    {
        animatable.play(animation);
        tileCollidable.addListener(listenerTileCollidable);
        collidable.addListener(this::onCollided);
        collideX.set(false);
        collideXright.set(false);
        collideXleft.set(false);
        collideY.set(false);
        collideYleft.set(false);
        collideYright.set(false);
        grip.set(false);
        steep.reset();
        liana.reset();
    }

    @Override
    public void exit()
    {
        tileCollidable.removeListener(listenerTileCollidable);
        collidable.removeListener(this::onCollided);
    }

    /**
     * {@inheritDoc} Does nothing by default.
     */
    @Override
    public void update(double extrp)
    {
        // Nothing by default
    }

    @Override
    protected void postUpdate()
    {
        collideX.set(false);
        collideXright.set(false);
        collideXleft.set(false);

        collideY.set(false);
        collideYleft.set(false);
        collideYright.set(false);

        grip.set(false);
        steep.reset();
        liana.reset();
    }
}
