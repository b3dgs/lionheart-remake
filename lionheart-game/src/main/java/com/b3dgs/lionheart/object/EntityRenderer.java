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

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.Displayable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Entity rendering implementation.
 */
public final class EntityRenderer extends FeatureModel implements Displayable, Recyclable
{
    private final List<Routine> routines = new ArrayList<>();

    private int routinesCount;
    private boolean visible;

    @FeatureGet private Collidable collidable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Rasterable rasterable;

    /**
     * Load routines.
     */
    public void loadRoutines()
    {
        for (final Feature feature : getFeatures())
        {
            if (feature instanceof Routine)
            {
                routines.add((Routine) feature);
            }
        }
        routinesCount = routines.size();
    }

    /**
     * Set visibility state.
     * 
     * @param visible <code>true</code> if visible, <code>false</code> else.
     */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    @Override
    public void render(Graphic g)
    {
        if (visible)
        {
            rasterable.render(g);
        }

        g.setColor(ColorRgba.GREEN);
        collidable.render(g);

        for (int i = 0; i < routinesCount; i++)
        {
            routines.get(i).render(g);
        }
    }

    @Override
    public void recycle()
    {
        visible = true;
    }
}
