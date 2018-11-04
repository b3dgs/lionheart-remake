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
package com.b3dgs.lionheart.object;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;

/**
 * Glue collision implementation.
 */
public final class Glue implements Updatable, CollidableListener
{
    private final Transformable reference;
    private final TransformY transformY;
    private final GlueListener listener;

    private double referenceY;
    private boolean first = true;
    private Transformable other;
    private int offsetY;
    private boolean collide;
    private boolean glue;
    private boolean started;

    /**
     * Create glue component.
     * 
     * @param reference The glue reference.
     * @param transformY The transform Y function.
     * @param listener The listener callback.
     */
    public Glue(Transformable reference, TransformY transformY, GlueListener listener)
    {
        super();

        this.reference = reference;
        this.transformY = transformY;
        this.listener = listener;
    }

    /**
     * Set glue activation.
     * 
     * @param glue <code>true</code> to enable glue, <code>false</code> else.
     */
    public void setGlue(boolean glue)
    {
        this.glue = glue;
    }

    @Override
    public void update(double extrp)
    {
        reference.teleportY(referenceY - transformY.transformY());

        if (!collide && started)
        {
            listener.notifyEnd(other);
            started = false;
        }
        else if (glue && collide)
        {
            if (Double.compare(other.getFeature(EntityModel.class).getInput().getVerticalDirection(), 0.0) <= 0)
            {
                other.getFeature(Body.class).resetGravity();
                other.teleportY(reference.getY() + offsetY);
            }
        }

        collide = false;
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision collision)
    {
        other = collidable.getFeature(Transformable.class);
        if (Double.compare(other.getY(), other.getOldY()) <= 0)
        {
            collide = true;
            offsetY = collision.getOffsetY();
            other.getFeature(Body.class).resetGravity();
            other.teleportY(reference.getY() + offsetY);

            if (!started)
            {
                listener.notifyStart(other);
                started = true;
            }
            if (first)
            {
                referenceY = reference.getY();
                first = false;
            }
        }
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
