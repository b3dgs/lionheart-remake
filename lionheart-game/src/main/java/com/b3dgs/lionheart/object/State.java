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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.state.StateAbstract;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.io.InputDeviceControl;
import com.b3dgs.lionengine.io.InputDeviceControlDelegate;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.CollisionName;

/**
 * Base state with animation implementation.
 */
public abstract class State extends StateAbstract
{
    /** Model reference. */
    protected final EntityModel model;
    /** Animatable reference. */
    protected final Animatable animatable;
    /** Transformable reference. */
    protected final Transformable transformable;
    /** Body reference. */
    protected final Body body;
    /** Mirrorable reference. */
    protected final Mirrorable mirrorable;
    /** Tile collidable reference. */
    protected final TileCollidable tileCollidable;
    /** Collidable reference. */
    protected final Collidable collidable;
    /** Rasterable reference. */
    protected final Rasterable rasterable;
    /** State animation data. */
    protected final Animation animation;
    /** Movement reference. */
    protected final Force movement;
    /** Jump reference. */
    protected final Force jump;
    /** Input device control. */
    protected final InputDeviceControl control;
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
        super();

        this.model = model;
        this.animation = animation;
        movement = model.getMovement();
        jump = model.getJump();
        animatable = model.getFeature(Animatable.class);
        transformable = model.getFeature(Transformable.class);
        body = model.getFeature(Body.class);
        mirrorable = model.getFeature(Mirrorable.class);
        tileCollidable = model.getFeature(TileCollidable.class);
        collidable = model.getFeature(Collidable.class);
        rasterable = model.getFeature(Rasterable.class);
        control = new InputDeviceControlDelegate(model::getInput);

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
    }

    /**
     * Called when a tile collision occurred on vertical axis with leg.
     * 
     * @param result The collided tile.
     * @param category The collided axis.
     */
    protected void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        if (!result.startWithY(CollisionName.LIANA) && !result.startWithY(CollisionName.SPIKE))
        {
            collideY.set(true);
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
     * Check if is anim state.
     * 
     * @param state The expected anim state.
     * @return <code>true</code> if is state, <code>false</code> else.
     */
    protected final boolean is(AnimState state)
    {
        return animatable.is(state);
    }

    /**
     * Check if is current mirror state.
     * 
     * @param mirror The expected mirror to be.
     * @return <code>true</code> if is mirror, <code>false</code> else.
     */
    protected final boolean is(Mirror mirror)
    {
        return mirrorable.is(mirror);
    }

    /**
     * Check if going nowhere.
     * 
     * @return <code>true</code> if not going to move, <code>false</code> else.
     */
    protected final boolean isGoNone()
    {
        return Double.compare(control.getHorizontalDirection(), 0.0) == 0;
    }

    /**
     * Check if going horizontally in any way.
     * 
     * @return <code>true</code> if going to left or right, <code>false</code> else.
     */
    protected final boolean isGoHorizontal()
    {
        return Double.compare(control.getHorizontalDirection(), 0.0) != 0;
    }

    /**
     * Check if going left.
     * 
     * @return <code>true</code> if going to left, <code>false</code> else.
     */
    protected final boolean isGoLeft()
    {
        return Double.compare(control.getHorizontalDirection(), 0.0) < 0;
    }

    /**
     * Check if going right.
     * 
     * @return <code>true</code> if going to right, <code>false</code> else.
     */
    protected final boolean isGoRight()
    {
        return Double.compare(control.getHorizontalDirection(), 0.0) > 0;
    }

    /**
     * Check if going vertically in any way.
     * 
     * @return <code>true</code> if going to up or down, <code>false</code> else.
     */
    protected final boolean isGoVertical()
    {
        return Double.compare(control.getVerticalDirection(), 0.0) != 0;
    }

    /**
     * Check if going up.
     * 
     * @return <code>true</code> if going to up, <code>false</code> else.
     */
    protected final boolean isGoUp()
    {
        return Double.compare(control.getVerticalDirection(), 0.0) > 0;
    }

    /**
     * Check if going down.
     * 
     * @return <code>true</code> if going to down, <code>false</code> else.
     */
    protected final boolean isGoDown()
    {
        return Double.compare(control.getVerticalDirection(), 0.0) < 0;
    }

    /**
     * Check if going up one time.
     * 
     * @return <code>true</code> if going to up, <code>false</code> else.
     */
    protected final boolean isGoUpOnce()
    {
        return control.isUpButtonOnce();
    }

    /**
     * Check if going down one time.
     * 
     * @return <code>true</code> if going to down, <code>false</code> else.
     */
    protected final boolean isGoDownOnce()
    {
        return control.isDownButtonOnce();
    }

    /**
     * Check if going left once.
     * 
     * @return <code>true</code> if going to left, <code>false</code> else.
     */
    protected final boolean isGoLeftOnce()
    {
        return control.isLeftButtonOnce();
    }

    /**
     * Check if going right once.
     * 
     * @return <code>true</code> if going to right, <code>false</code> else.
     */
    protected final boolean isGoRightOnce()
    {
        return control.isRightButtonOnce();
    }

    /**
     * Check if fire button is enabled.
     * 
     * @return <code>true</code> if active, <code>false</code> else.
     */
    protected final boolean isFire()
    {
        return control.isFireButton(Constant.FIRE1);
    }

    /**
     * Check if fire button once.
     * 
     * @return <code>true</code> if active, <code>false</code> else.
     */
    protected final boolean isFireOnce()
    {
        return control.isFireButtonOnce(Constant.FIRE1);
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
