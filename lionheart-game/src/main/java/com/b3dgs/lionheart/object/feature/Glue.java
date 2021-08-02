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
package com.b3dgs.lionheart.object.feature;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
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
public final class Glue extends FeatureModel implements Routine, Recyclable, CollidableListener
{
    private static final String NODE = "glue";
    private static final String ATT_FORCE = "force";

    private final Collection<GlueListener> listeners = new ArrayList<>();
    private final boolean force = setup.getBoolean(false, ATT_FORCE, NODE);

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
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Glue(Services services, Setup setup)
    {
        super(services, setup);
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
            other.getFeature(Body.class).resetGravity();
            other.teleportY(reference.getY() + offsetY);
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
        if (with.getName().startsWith(CollisionName.GROUND) && by.getName().startsWith(Anim.LEG))
        {
            other = collidable.getFeature(Transformable.class);
            if (!collide
                && (Double.compare(other.getY(), other.getOldY()) <= 0
                    && Double.compare(other.getFeature(EntityModel.class).getJump().getDirectionVertical(), 0.0) <= 0
                    || force))
            {
                collide = true;
                offsetY = with.getOffsetY();
                other.getFeature(Body.class).resetGravity();
                other.teleportY(reference.getY() + offsetY);

                start();
            }
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
