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

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionheart.object.Glue.GlueListener;

/**
 * Sheet feature implementation.
 */
public final class Sheet extends FeatureModel implements Routine, CollidableListener
{
    private static final double CURVE_FORCE = 8.0;

    private Glue glue;
    private boolean start;
    private boolean done;
    private double curve;

    @FeatureGet private Transformable transformable;

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        glue = new Glue(transformable, this::getCurve, new GlueListener()
        {
            @Override
            public void notifyStart(Transformable transformable)
            {
                start = true;
                done = false;
                glue.setGlue(true);
            }

            @Override
            public void notifyEnd(Transformable transformable)
            {
                glue.setGlue(false);
            }
        });
    }

    /**
     * Get current curve value.
     * 
     * @return The current curve value.
     */
    public double getCurve()
    {
        return UtilMath.sin(curve) * CURVE_FORCE;
    }

    @Override
    public void update(double extrp)
    {
        if (start)
        {
            glue.update(extrp);
            if (curve > 180)
            {
                curve = 0.0;
                done = true;
            }
            if (!done)
            {
                curve += 6;
            }
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision collision)
    {
        glue.notifyCollided(collidable, collision);
    }
}
