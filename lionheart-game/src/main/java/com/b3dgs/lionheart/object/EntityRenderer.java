/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.game.feature.Displayable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routines;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Entity rendering implementation.
 */
public final class EntityRenderer extends FeatureModel implements Displayable
{
    @FeatureGet private Collidable collidable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Rasterable rasterable;
    @FeatureGet private EntityModel model;
    @FeatureGet private Routines routines;

    @Override
    public void render(Graphic g)
    {
        if (model.isVisible())
        {
            rasterable.render(g);

            g.setColor(ColorRgba.GREEN);
            collidable.render(g);

            routines.render(g);
        }
    }
}
