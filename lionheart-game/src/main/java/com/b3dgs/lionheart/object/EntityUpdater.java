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

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionheart.object.state.StateHurt;
import com.b3dgs.lionheart.object.state.StateSlide;

/**
 * Entity updating implementation.
 */
final class EntityUpdater extends FeatureModel implements Refreshable
{
    private final int DESTROY_Y = -100;

    private final List<Routine> routines = new ArrayList<>();
    private final Force movement;
    private final Force jump;

    private int routinesCount;

    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Body body;
    @FeatureGet private StateHandler state;
    @FeatureGet private Transformable transformable;
    @FeatureGet private TileCollidable tileCollidable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Rasterable rasterable;

    /**
     * Create updater.
     * 
     * @param services The services reference.
     * @param model The model reference.
     */
    public EntityUpdater(Services services, EntityModel model)
    {
        super();

        movement = model.getMovement();
        jump = model.getJump();
    }

    /**
     * Update mirror depending of current mirror and movement.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateMirror(double extrp)
    {
        if (!state.isState(StateHurt.class) && !state.isState(StateSlide.class))
        {
            if (mirrorable.getMirror() == Mirror.NONE && movement.getDirectionHorizontal() < 0.0)
            {
                mirrorable.mirror(Mirror.HORIZONTAL);
            }
            else if (mirrorable.getMirror() == Mirror.HORIZONTAL && movement.getDirectionHorizontal() > 0.0)
            {
                mirrorable.mirror(Mirror.NONE);
            }
        }
        mirrorable.update(extrp);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        for (final Feature feature : provider.getFeatures())
        {
            if (feature instanceof Routine)
            {
                routines.add((Routine) feature);
            }
        }
        routinesCount = routines.size();
    }

    @Override
    public void update(double extrp)
    {
        for (int i = 0; i < routinesCount; i++)
        {
            routines.get(i).update(extrp);
        }
        state.update(extrp);
        jump.update(extrp);
        movement.update(extrp);
        transformable.moveLocation(extrp, body, movement, jump);
        tileCollidable.update(extrp);
        state.postUpdate();

        updateMirror(extrp);
        animatable.update(extrp);
        rasterable.update(extrp);

        if (transformable.getY() < DESTROY_Y)
        {
            identifiable.destroy();
        }
    }
}
