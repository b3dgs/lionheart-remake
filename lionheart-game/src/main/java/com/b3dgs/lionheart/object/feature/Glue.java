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

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.Routine;
import com.b3dgs.lionheart.object.state.StateFall;

/**
 * Glue feature implementation.
 */
@FeatureInterface
public final class Glue extends FeatureModel implements Routine, Recyclable, CollidableListener
{
    private final Collection<GlueListener> listeners = new ArrayList<>();

    private TransformY transformY;
    private double referenceY;
    private boolean first;
    private Transformable other;
    private int offsetY;
    private boolean collide;
    private boolean glue;
    private boolean started;

    @FeatureGet private Transformable reference;
    @FeatureGet private Collidable collidable;

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
     * Set the vertical transform.
     * 
     * @param transformY The vertical transform.
     */
    public void setTransformY(TransformY transformY)
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
            referenceY = reference.getY();
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
        if (!first && transformY != null)
        {
            reference.teleportY(referenceY - transformY.transformY());
        }

        if (!collide && started)
        {
            stop();
        }
        else if (glue && collide)
        {
            other.moveLocationX(extrp, reference.getX() - reference.getOldX());
            if (Double.compare(other.getFeature(EntityModel.class).getInput().getVerticalDirection(), 0.0) <= 0)
            {
                other.getFeature(Body.class).resetGravity();
                other.teleportY(reference.getY() + offsetY);
            }
        }
        if (!collidable.isEnabled() && other != null)
        {
            if (Double.compare(other.getY(), other.getOldY()) == 0)
            {
                other.getFeature(StateHandler.class).changeState(StateFall.class);
            }
        }

        collide = false;
        other = null;
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        other = collidable.getFeature(Transformable.class);
        if (Double.compare(other.getY(), other.getOldY()) <= 0 && by.getName().startsWith(Constant.ANIM_PREFIX_LEG))
        {
            collide = true;
            offsetY = with.getOffsetY();
            other.getFeature(Body.class).resetGravity();
            other.teleportY(reference.getY() + offsetY);

            start();
        }
    }

    @Override
    public void recycle()
    {
        transformY = null;
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
    public interface TransformY
    {
        /**
         * Get the current vertical transformation.
         * 
         * @return The vertical transformation.
         */
        double transformY();
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
