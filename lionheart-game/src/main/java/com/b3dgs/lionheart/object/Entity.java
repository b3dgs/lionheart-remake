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

import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.body.BodyModel;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableModel;
import com.b3dgs.lionengine.game.state.StateAnimationBased;
import com.b3dgs.lionengine.game.state.StateFactory;
import com.b3dgs.lionengine.game.state.StateHandler;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.SpriteAnimated;

/**
 * Entity base representation.
 */
public abstract class Entity extends FeaturableModel implements Updatable, Renderable
{
    /** Surface. */
    public final SpriteAnimated surface;
    /** Movement force. */
    public final Force movement = new Force();
    /** Jump force. */
    public final Force jump = new Force();
    /** Transformable reference. */
    private final Transformable transformable = addFeatureAndGet(new TransformableModel());
    /** Body reference. */
    private final Body body = addFeatureAndGet(new BodyModel());
    /** Tile collidable reference. */
    private final TileCollidable tileCollidable;
    /** State factory. */
    private final StateFactory stateFactory = new StateFactory();
    /** States handler. */
    private final StateHandler stateHandler = new StateHandler(stateFactory);
    /** Viewer reference. */
    private final Viewer viewer;

    /**
     * Create an entity.
     * 
     * @param services The services reference.
     * @param setup The setup used.
     * @throws LionEngineException If error.
     */
    public Entity(Services services, SetupSurfaceRastered setup)
    {
        super();

        final FramesConfig frames = FramesConfig.imports(setup);
        surface = Drawable.loadSpriteAnimated(setup.getSurface(), frames.getHorizontal(), frames.getVertical());

        tileCollidable = addFeatureAndGet(new TileCollidableModel(services, setup));

        body.setMass(2.0);
        body.setGravityMax(7.0);
        body.setDesiredFps(60);
        body.setVectors(movement, jump);

        viewer = services.get(Viewer.class);

        StateAnimationBased.Util.loadStates(EntityState.values(), stateFactory, this, setup);

        stateHandler.addInput(getInput());
        stateHandler.changeState(EntityState.IDLE);
    }

    /**
     * Get the input device used.
     * 
     * @return The input device used.
     */
    protected abstract InputDevice getInput();

    @Override
    public void update(double extrp)
    {
        body.update(extrp);
        tileCollidable.update(extrp);
        surface.setLocation(viewer, transformable);
    }

    @Override
    public void render(Graphic g)
    {
        surface.render(g);
    }
}
