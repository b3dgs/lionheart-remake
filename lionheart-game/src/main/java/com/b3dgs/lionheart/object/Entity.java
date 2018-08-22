/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Locale;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.MirrorableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.body.BodyModel;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.state.State;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableModel;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;

/**
 * Entity base representation.
 */
public final class Entity extends FeaturableModel
{
    /**
     * Create an entity.
     * 
     * @param services The services reference.
     * @param setup The setup used.
     * @throws LionEngineException If error.
     */
    public Entity(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);

        addFeature(new LayerableModel(5));

        final Mirrorable mirrorable = addFeatureAndGet(new MirrorableModel());
        final TileCollidable tileCollidable = addFeatureAndGet(new TileCollidableModel(services, setup));

        addFeature(new CollidableModel(services, setup));

        final Body body = getFeature(BodyModel.class);
        tileCollidable.addListener((tile, axis) ->
        {
            if (Axis.Y == axis)
            {
                body.resetGravity();
            }
        });

        final EntityModel model = getFeature(EntityModel.class);

        final StateHandler stateHandler = addFeatureAndGet(new StateHandler(setup)
        {
            @Override
            protected String getAnimationName(Class<? extends State> state)
            {
                return state.getSimpleName().replace("State", "").toLowerCase(Locale.ENGLISH);
            }
        });
        stateHandler.changeState(StateIdle.class);

        final SpriteAnimated surface = model.getSurface();
        final Force movement = model.getMovement();
        final Viewer viewer = services.get(Viewer.class);

        final Transformable transformable = getFeature(TransformableModel.class);
        addFeature(new RefreshableModel(extrp ->
        {
            stateHandler.update(extrp);
            mirrorable.update(extrp);
            movement.update(extrp);
            body.update(extrp);
            tileCollidable.update(extrp);
            surface.setLocation(viewer, transformable);
            surface.setMirror(mirrorable.getMirror());
            surface.update(extrp);
        }));

        addFeature(new DisplayableModel(g -> surface.render(g)));
    }
}
