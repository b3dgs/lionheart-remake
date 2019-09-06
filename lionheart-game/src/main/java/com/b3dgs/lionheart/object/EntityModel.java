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

import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.io.InputDeviceControl;
import com.b3dgs.lionengine.io.InputDeviceControlVoid;
import com.b3dgs.lionheart.Constant;

/**
 * Entity model implementation.
 */
@FeatureInterface
public final class EntityModel extends FeatureModel
{
    private static final String NODE_DATA = "data";

    private final Force movement = new Force();
    private final Force jump = new Force();
    private final Camera camera;
    private final MapTile map;
    private final boolean hasGravity;

    private final SourceResolutionProvider source;
    private InputDeviceControl input = InputDeviceControlVoid.getInstance();
    private boolean visible = true;

    @FeatureGet private Body body;

    /**
     * Create model.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public EntityModel(Services services, Setup setup)
    {
        super(services, setup);

        camera = services.get(Camera.class);
        map = services.get(MapTile.class);
        source = services.get(SourceResolutionProvider.class);
        hasGravity = setup.hasNode(NODE_DATA);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

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
     * Set the input used.
     * 
     * @param input The input used (if <code>null</code>, {@link InputDeviceControlVoid#getInstance} is used).
     */
    public void setInput(InputDeviceControl input)
    {
        if (input == null)
        {
            this.input = InputDeviceControlVoid.getInstance();
        }
        else
        {
            this.input = input;
        }
    }

    /**
     * Set the visible flag.
     * 
     * @param visible <code>true</code> if visible, <code>false</code> else.
     */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    /**
     * Check visible flag.
     * 
     * @return <code>true</code> if visible, <code>false</code> else.
     */
    public boolean isVisible()
    {
        return visible;
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
     * Get the input used.
     * 
     * @return The input used.
     */
    public InputDeviceControl getInput()
    {
        return input;
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
