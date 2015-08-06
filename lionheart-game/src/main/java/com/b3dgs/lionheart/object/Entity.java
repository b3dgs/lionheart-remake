/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.InputDevice;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.configurer.ConfigFrames;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.state.StateAnimationBased;
import com.b3dgs.lionengine.game.state.StateFactory;
import com.b3dgs.lionengine.game.state.StateHandler;
import com.b3dgs.lionengine.game.trait.body.Body;
import com.b3dgs.lionengine.game.trait.body.BodyModel;
import com.b3dgs.lionengine.game.trait.collidable.TileCollidable;
import com.b3dgs.lionengine.game.trait.collidable.TileCollidableModel;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.trait.transformable.TransformableModel;

/**
 * Entity base representation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Entity extends ObjectGame implements Updatable, Renderable
{
    /** Surface. */
    public final SpriteAnimated surface;
    /** Movement force. */
    public final Force movement = new Force();
    /** Jump force. */
    public final Force jump = new Force();
    /** Transformable trait. */
    private final Transformable transformable = addTrait(new TransformableModel());
    /** Body trait. */
    private final Body body = addTrait(new BodyModel());
    /** Tile collidable trait. */
    private final TileCollidable tileCollidable = addTrait(new TileCollidableModel());
    /** State factory. */
    private final StateFactory stateFactory = new StateFactory();
    /** States handler. */
    private final StateHandler stateHandler = new StateHandler(stateFactory);
    /** Viewer reference. */
    private final Viewer viewer;

    /**
     * Create an entity.
     * 
     * @param setup The setup used.
     * @param services The services reference.
     * @throws LionEngineException If error.
     */
    public Entity(SetupSurfaceRastered setup, Services services) throws LionEngineException
    {
        super(setup, services);

        final ConfigFrames frames = ConfigFrames.create(getConfigurer());
        surface = Drawable.loadSpriteAnimated(setup.getSurface(), frames.getHorizontal(), frames.getVertical());

        body.setMass(2.0);
        body.setGravityMax(7.0);
        body.setDesiredFps(60);
        body.setVectors(movement, jump);

        viewer = services.get(Viewer.class);
    }

    /**
     * Get the input device used.
     * 
     * @return The input device used.
     */
    protected abstract InputDevice getInput();

    @Override
    protected void onPrepared()
    {
        StateAnimationBased.Util.loadStates(EntityState.values(), stateFactory, this);

        stateHandler.addInput(getInput());
        stateHandler.start(EntityState.IDLE);
    }

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
