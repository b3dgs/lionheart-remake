/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Locale;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.CameraTracker;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.state.State;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.helper.EntityModelHelper;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.feature.Patrol;
import com.b3dgs.lionheart.object.state.StateHurt;
import com.b3dgs.lionheart.object.state.StateLianaSlide;
import com.b3dgs.lionheart.object.state.StateSlide;

/**
 * Entity model implementation.
 */
@FeatureInterface
public final class EntityModel extends EntityModelHelper implements Routine
{
    private static final int DESTROY_Y = -100;
    private static final String NODE_DATA = "data";
    private static final int PREFIX = State.class.getSimpleName().length();

    /**
     * Get animation name from state class.
     * 
     * @param state The state class.
     * @return The animation name.
     */
    public static String getAnimationName(Class<? extends State> state)
    {
        return state.getSimpleName().substring(PREFIX).toLowerCase(Locale.ENGLISH);
    }

    private final Force movement = new Force();
    private final Force jump = new Force();
    private final Camera camera = services.get(Camera.class);
    private final MapTile map = services.get(MapTile.class);
    private final CameraTracker tracker = services.get(CameraTracker.class);
    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final boolean hasGravity = setup.hasNode(NODE_DATA);

    @FeatureGet private Body body;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private StateHandler state;
    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Rasterable rasterable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public EntityModel(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        movement.setVelocity(0.1);
        movement.setSensibility(0.01);

        if (hasGravity)
        {
            body.setGravity(Constant.GRAVITY);
            body.setGravityMax(Constant.GRAVITY);
            body.setDesiredFps(source.getRate());
        }
        else
        {
            body.setGravity(0.0);
        }

        jump.setSensibility(0.1);
        jump.setVelocity(0.18);
        jump.setDestination(0.0, 0.0);
    }

    /**
     * Update mirror depending of current mirror and movement.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateMirror(double extrp)
    {
        if (!hasFeature(Patrol.class)
            && !state.isState(StateHurt.class)
            && !state.isState(StateSlide.class)
            && !state.isState(StateLianaSlide.class))
        {
            if (mirrorable.is(Mirror.NONE) && movement.getDirectionHorizontal() < 0.0)
            {
                mirrorable.mirror(Mirror.HORIZONTAL);
            }
            else if (mirrorable.is(Mirror.HORIZONTAL) && movement.getDirectionHorizontal() > 0.0)
            {
                mirrorable.mirror(Mirror.NONE);
            }
        }
        mirrorable.update(extrp);
    }

    @Override
    public void update(double extrp)
    {
        jump.update(extrp);
        movement.update(extrp);
        transformable.moveLocation(extrp, body, movement, jump);
        updateMirror(extrp);

        if (transformable.getY() < DESTROY_Y)
        {
            identifiable.destroy();
        }
    }

    /**
     * Set the visible flag.
     * 
     * @param visible <code>true</code> if visible, <code>false</code> else.
     */
    public void setVisible(boolean visible)
    {
        rasterable.setVisibility(visible);
    }

    /**
     * Get the camera reference.
     * 
     * @return The camera reference.
     */
    public Camera getCamera()
    {
        return camera;
    }

    /**
     * Get the map reference.
     * 
     * @return The map reference.
     */
    public MapTile getMap()
    {
        return map;
    }

    /**
     * Get the camera tracker reference.
     * 
     * @return The camera tracker reference.
     */
    public CameraTracker getTracker()
    {
        return tracker;
    }

    /**
     * Get the movement force.
     * 
     * @return The movement force.
     */
    public Force getMovement()
    {
        return movement;
    }

    /**
     * Get the jump force.
     * 
     * @return The jump force.
     */
    public Force getJump()
    {
        return jump;
    }

    /**
     * Check if has gravity.
     * 
     * @return <code>true</code> if has gravity, <code>false</code> else.
     */
    public boolean hasGravity()
    {
        return hasGravity;
    }
}
