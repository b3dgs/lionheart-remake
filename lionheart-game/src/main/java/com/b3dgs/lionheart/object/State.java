/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.function.BooleanSupplier;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.helper.StateHelper;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.GameConfig;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.feature.Glue;
import com.b3dgs.lionheart.object.feature.Stats;

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
    /** Win flag. */
    private final BooleanSupplier win;
    /** One button flag. */
    private final boolean oneButton;

    /** Object collide flag. */
    private boolean collObject;
    private double oldY;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    protected State(EntityModel model, Animation animation)
    {
        super(model, animation);

        oneButton = model.getServices().get(GameConfig.class).isOneButton();
        movement = model.getMovement();
        jump = model.getJump();
        if (model.hasFeature(Stats.class))
        {
            win = model.getFeature(Stats.class)::hasWin;
        }
        else
        {
            win = () -> false;
        }
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
        if (result.endWithX(CollisionName.LEFT_VERTICAL))
        {
            collideXright.set(true);
        }
        if (result.endWithX(CollisionName.RIGHT_VERTICAL))
        {
            collideXleft.set(true);
        }

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
            && !result.startWithY(CollisionName.GRIP)
            && Double.compare(transformable.getY(), transformable.getOldY()) <= 0)
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
        if (result.startWithY(CollisionName.GRIP)
            && Double.compare(transformable.getY(), transformable.getOldY()) <= 0
            && !isGoDown())
        {
            grip.set(true);
            tileCollidable.apply(result);
            body.resetGravity();
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
        if (Double.compare(transformable.getY(), transformable.getOldY()) > 0)
        {
            jump.zero();
        }
    }

    /**
     * Update collided with glue.
     * 
     * @param collidable The collidable reference.
     * @param with The collision collided with (source).
     * @param by The collision collided by (other).
     */
    private void updateCollidedGlue(Collidable collidable, Collision with, Collision by)
    {
        if (collidable.hasFeature(Glue.class))
        {
            if (!model.isIgnoreGlue()
                && with.getName().startsWith(Anim.LEG)
                && by.getName().startsWith(CollisionName.GROUND))
            {
                collideY.set(true);
                collObject = true;
            }
            else if (with.getName().startsWith(CollisionName.GRIP) && by.getName().startsWith(CollisionName.GRIP))
            {
                grip.set(true);
            }
        }
    }

    /**
     * Check if collided on vertical.
     * 
     * @param by The collision collided by (other).
     * @return <code>true</code> if vertical collided, <code>false</code> else.
     */
    private boolean isCollidedVertical(Collision by)
    {
        return movement.getDirectionHorizontal() < 0 && by.getName().contains(CollisionName.RIGHT_VERTICAL)
               || movement.getDirectionHorizontal() > 0 && by.getName().contains(CollisionName.LEFT_VERTICAL);
    }

    /**
     * Check if fire button is enabled.
     * 
     * @return <code>true</code> if active, <code>false</code> else.
     */
    protected final boolean isFire()
    {
        return isFire(DeviceMapping.ATTACK);
    }

    /**
     * Check if fire button once.
     * 
     * @return <code>true</code> if active, <code>false</code> else.
     */
    protected final boolean isFireOnce()
    {
        return isFireOnce(DeviceMapping.ATTACK);
    }

    /**
     * Check if button up pressed.
     * 
     * @return <code>true</code> if up, <code>false</code> else.
     */
    protected final boolean isButtonUp()
    {
        return oneButton ? isGoUp() || isFireOnce(DeviceMapping.JUMP) : isFireOnce(DeviceMapping.JUMP);
    }

    /**
     * Check if button up pressed once.
     * 
     * @return <code>true</code> if up, <code>false</code> else.
     */
    protected final boolean isButtonUpOnce()
    {
        return oneButton ? isGoUp() || isFireOnce(DeviceMapping.JUMP) : isFireOnce(DeviceMapping.JUMP);
    }

    /**
     * Check win flag.
     * 
     * @return The win flag.
     */
    protected final boolean hasWin()
    {
        return win.getAsBoolean();
    }

    @Override
    public void enter()
    {
        animatable.play(animation);
        collideX.set(false);
        collideXright.set(false);
        collideXleft.set(false);
        collideY.set(false);
        collideYleft.set(false);
        collideYright.set(false);
        grip.set(false);
        collObject = false;
        steep.reset();
        liana.reset();
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
    public void exit()
    {
        super.exit();

        model.setIgnoreGlue(false);
        collObject = false;
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
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
            if ((!collObject || transformable.getY() < oldY)
                && !liana.is()
                && category.getName().startsWith(CollisionName.LEG))
            {
                model.setIgnoreGlue(true);
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
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        updateCollidedGlue(collidable, with, by);

        if (with.getName().contains(CollisionName.BODY) && isCollidedVertical(by))
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

        collObject = false;
        oldY = transformable.getY();
    }

}
