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

import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategoryConfig;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.InputDeviceControl;
import com.b3dgs.lionheart.InputDeviceControlVoid;

/**
 * Entity model implementation.
 */
final class EntityModel extends FeatureModel
{
    private static final double GRAVITY = 5.5;
    private static final double GRAVITY_MAX = 6.5;

    private final Force movement = new Force();
    private final Force jump = new Force();
    private final boolean hasGravity;

    private final SourceResolutionProvider source;
    private InputDeviceControl input = InputDeviceControlVoid.getInstance();

    @FeatureGet private Body body;
    @FeatureGet private Rasterable rasterable;
    @FeatureGet private Animatable animatable;

    /**
     * Create model.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public EntityModel(Services services, Setup setup)
    {
        super();

        source = services.get(SourceResolutionProvider.class);
        hasGravity = setup.hasNode(CollisionCategoryConfig.NODE_CATEGORY);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        rasterable.setOrigin(Origin.CENTER_BOTTOM);
        if (hasGravity)
        {
            body.setGravity(GRAVITY);
            body.setGravityMax(GRAVITY_MAX);
            body.setVectors(movement, jump);
            body.setDesiredFps(source.getRate());
        }
        else
        {
            body.setGravity(0.0);
        }
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
     * @return THe jump force.
     */
    public Force getJump()
    {
        return jump;
    }

    /**
     * Get the animator.
     * 
     * @return The animator.
     */
    public Animator getAnimator()
    {
        return animatable;
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
}
