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

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Routines;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionheart.object.feature.Patrol;
import com.b3dgs.lionheart.object.state.StateHurt;
import com.b3dgs.lionheart.object.state.StateSlide;

/**
 * Entity updating implementation.
 */
final class EntityUpdater extends FeatureModel implements Refreshable
{
    private static final int DESTROY_Y = -100;

    private final Force movement;
    private final Force jump;

    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Body body;
    @FeatureGet private StateHandler state;
    @FeatureGet private Transformable transformable;
    @FeatureGet private TileCollidable tileCollidable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Rasterable rasterable;
    @FeatureGet private Routines routines;

    /**
     * Create feature.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param model The model reference.
     */
    EntityUpdater(Services services, Setup setup, EntityModel model)
    {
        super(services, setup);

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
        if (hasFeature(Patrol.class) == true) {
            mirrorable.update(extrp);
            return;
        }

        if (state.isState(StateHurt.class) == true) {
            mirrorable.update(extrp);
            return;
        }

        if (state.isState(StateSlide.class) == true) {
            mirrorable.update(extrp);
            return;
        }

        if (mirrorable.is(Mirror.NONE) && movement.getDirectionHorizontal() < 0.0) {
            mirrorable.mirror(Mirror.HORIZONTAL);
        } else if (mirrorable.is(Mirror.HORIZONTAL) && movement.getDirectionHorizontal() > 0.0) {
            mirrorable.mirror(Mirror.NONE);
        }

        mirrorable.update(extrp);
    }

    @Override
    public void update(double extrp)
    {
        routines.update(extrp);
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
