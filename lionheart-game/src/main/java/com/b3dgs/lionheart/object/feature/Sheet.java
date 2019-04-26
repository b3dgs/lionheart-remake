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

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionheart.object.Routine;
import com.b3dgs.lionheart.object.feature.Glue.GlueListener;

/**
 * Sheet feature implementation.
 */
@FeatureInterface
public final class Sheet extends FeatureModel implements Routine
{
    private static final double CURVE_FORCE = 8.0;
    private static final double CURVE_SPEED = 6.0;

    private boolean start;
    private boolean done;
    private double curve;
    private boolean abord;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Glue glue;

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        glue.addListener(new GlueListener()
        {
            @Override
            public void notifyStart(Transformable transformable)
            {
                start = true;
                done = false;
                glue.setGlue(true);
                abord = false;
            }

            @Override
            public void notifyEnd(Transformable transformable)
            {
                glue.setGlue(false);
                abord = true;
            }
        });
        glue.setTransformY(() -> UtilMath.sin(curve) * CURVE_FORCE);
    }

    @Override
    public void update(double extrp)
    {
        if (start)
        {
            if (curve < 0 || curve > 180)
            {
                curve = 0.0;
                done = true;
            }
            if (!done)
            {
                if (abord && Double.compare(curve, 90) < 0)
                {
                    curve -= CURVE_SPEED;
                }
                else
                {
                    curve += CURVE_SPEED;
                }
            }
        }
    }
}
