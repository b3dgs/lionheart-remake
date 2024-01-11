/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.state.StateFall;

/**
 * Glue feature implementation.
 */
@FeatureInterface
public final class Glue extends FeatureModel implements RoutineUpdate, CollidableListener, Recyclable
{
    private static final String NODE = "glue";
    private static final String ATT_FORCE = "force";

    private final Transformable transformable;
    private final Collidable collidable;

    private final Collection<GlueListener> listeners = new ArrayList<>();
    private final boolean force = setup.getBoolean(false, ATT_FORCE, NODE);

    private Transform transformX;
    private Transform transformY;
    private double referenceX;
    private double referenceY;
    private boolean first;
    private Transformable other;
    private int offsetY;
    private boolean collide;
    private boolean glue;
    private boolean started;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @param collidable The collidable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Glue(Services services, Setup setup, Transformable transformable, Collidable collidable)
    {
        super(services, setup);

        this.transformable = transformable;
        this.collidable = collidable;
    }

    /**
     * Add glue listener.
     * 
     * @param listener The listener reference.
     */
    public void addListener(GlueListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Set the horizontal transform.
     * 
     * @param transformX The horizontal transform.
     */
    public void setTransformX(Transform transformX)
    {
        this.transformX = transformX;
    }

    /**
     * Set the vertical transform.
     * 
     * @param transformY The vertical transform.
     */
    public void setTransformY(Transform transformY)
    {
        this.transformY = transformY;
    }

    /**
     * Set glue state.
     * 
     * @param glue <code>true</code> to enable, <code>false</code> to disable.
     */
    public void setGlue(boolean glue)
    {
        this.glue = glue;
    }

    /**
     * Start glue.
     */
    public void start()
    {
        if (first)
        {
            referenceX = transformable.getX();
            referenceY = transformable.getY();
            first = false;
        }
        if (!started)
        {
            listeners.forEach(l -> l.notifyStart(other));
            started = true;
        }
    }

    /**
     * Stop glue.
     */
    public void stop()
    {
        listeners.forEach(l -> l.notifyEnd(other));
        started = false;
    }

    @Override
    public void update(double extrp)
    {
        if (!first)
        {
            if (transformX != null)
            {
                transformable.teleportX(referenceX - transformX.transform());
            }
            if (transformY != null)
            {
                transformable.teleportY(referenceY - transformY.transform());
            }
        }

        if (!collide && started)
        {
            stop();
        }
        else if (glue && collide)
        {
            other.moveLocationX(1.0, transformable.getX() - transformable.getOldX());
            other.getFeature(Body.class).resetGravity();
            other.teleportY(transformable.getY() + offsetY);
        }
        if (!collidable.isEnabled() && other != null && Double.compare(other.getY(), other.getOldY()) == 0)
        {
            other.getFeature(StateHandler.class).changeState(StateFall.class);
        }

        collide = false;
        other = null;
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (with.getName().startsWith(CollisionName.GROUND)
            && by.getName().startsWith(Anim.LEG)
            && (!by.getName().contains(Anim.ATTACK_FALL) || force))
        {
            other = collidable.getFeature(Transformable.class);
            if (!collide
                && !other.getFeature(EntityModel.class).isIgnoreGlue()
                && (Double.compare(other.getY(), other.getOldY()) <= 0
                    && Double.compare(other.getFeature(EntityModel.class).getJump().getDirectionVertical(), 0.0) <= 0
                    || force && Double.compare(transformable.getY(), transformable.getOldY()) != 0))
            {
                collide = true;
                offsetY = with.getOffsetY();
                other.getFeature(Body.class).resetGravity();
                other.teleportY(transformable.getY() + offsetY);

                start();
            }
        }
        else if (with.getName().startsWith(CollisionName.BODY) && by.getName().startsWith(Anim.ATTACK_FALL))
        {
            other = null;
        }
    }

    @Override
    public void recycle()
    {
        transformX = null;
        transformY = null;
        referenceX = 0.0;
        referenceY = 0.0;
        other = null;
        first = true;
        offsetY = 0;
        glue = true;
        collide = false;
        started = false;
    }

    /**
     * Represents the vertical transformation.
     */
    public interface Transform
    {
        /**
         * Get the current vertical transformation.
         * 
         * @return The vertical transformation.
         */
        double transform();
    }

    /**
     * Listen to glue events.
     */
    public interface GlueListener
    {
        /**
         * Notify when glue started.
         * 
         * @param transformable The glued transformable.
         */
        void notifyStart(Transformable transformable);

        /**
         * Notify when glue ended.
         * 
         * @param transformable The unglued transformable.
         */
        void notifyEnd(Transformable transformable);
    }
}
