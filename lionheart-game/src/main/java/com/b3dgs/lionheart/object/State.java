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
package com.b3dgs.lionheart.object;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.helper.StateHelper;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.CollisionName;

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
                if (category.getName().startsWith(CollisionName.LEG))
                {
                    onCollideLeg(result, category);
                }
                else if (category.getName().startsWith(CollisionName.HAND))
                {
                    onCollideHand(result, category);
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
        collideXright.set(result.contains(CollisionName.STEEP_LEFT) || result.contains(CollisionName.SPIKE_LEFT));
        collideXleft.set(result.contains(CollisionName.STEEP_RIGHT) || result.contains(CollisionName.SPIKE_RIGHT));

        if (movement.getDirectionHorizontal() < 0
            && (result.startWithX(CollisionName.STEEP_RIGHT) || result.startWithX(CollisionName.SPIKE_RIGHT))
            || movement.getDirectionHorizontal() > 0
               && (result.startWithX(CollisionName.STEEP_LEFT) || result.startWithX(CollisionName.SPIKE_LEFT)))
        {
            tileCollidable.apply(result);
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
            && !(result.startWithX(CollisionName.SPIKE) && jump.getDirectionVertical() > 0))
        {
            collideY.set(true);
            tileCollidable.apply(result);
            body.resetGravity();
        }
    }

    /**
     * Called when a tile collision occurred on vertical axis with hand.
     * 
     * @param result The collided tile.
     * @param category The collided axis.
     */
    protected void onCollideHand(CollisionResult result, CollisionCategory category)
    {
        // Nothing by default
    }

    /**
     * Called when a collision occurred with another {@link Collidable}. Does nothing by default.
     * 
     * @param collidable The collidable reference.
     * @param with The collision collided with (source).
     * @param by The collision collided by (other).
     */
    protected void onCollided(Collidable collidable, Collision with, Collision by)
    {
        // Nothing by default
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
        return isFire(Constant.FIRE1);
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
    }
}
