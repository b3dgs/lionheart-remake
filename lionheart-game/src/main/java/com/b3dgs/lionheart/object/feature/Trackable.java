/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Shape;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Mover;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * Represents something that can be tracked.
 */
@FeatureInterface
public class Trackable extends FeatureModel implements Mover, Shape
{
    private final Transformable transformable;

    /**
     * Create feature.
     * <p>
     * The {@link Configurer} can provide a valid {@link SizeConfig}.
     * </p>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Trackable(Services services, Setup setup, Transformable transformable)
    {
        super(services, setup);

        this.transformable = transformable;
    }

    @Override
    public void backup()
    {
        transformable.backup();
    }

    @Override
    public void moveLocation(double extrp, Direction direction, Direction... directions)
    {
        transformable.moveLocation(extrp, direction, directions);
    }

    @Override
    public void moveLocationX(double extrp, double vx)
    {
        transformable.moveLocationX(extrp, vx);
    }

    @Override
    public void moveLocationY(double extrp, double vy)
    {
        transformable.moveLocationY(extrp, vy);
    }

    @Override
    public void moveLocation(double extrp, double vx, double vy)
    {
        transformable.moveLocation(extrp, vx, vy);
    }

    @Override
    public void teleport(double x, double y)
    {
        transformable.teleport(x, y);
    }

    @Override
    public void teleportX(double x)
    {
        transformable.teleportX(x);
    }

    @Override
    public void teleportY(double y)
    {
        transformable.teleportY(y);
    }

    @Override
    public double getX()
    {
        return transformable.getX();
    }

    @Override
    public double getY()
    {
        return transformable.getY();
    }

    @Override
    public double getOldX()
    {
        return transformable.getOldX();
    }

    @Override
    public double getOldY()
    {
        return transformable.getOldY();
    }

    @Override
    public void setLocation(double x, double y)
    {
        transformable.setLocation(x, y);
    }

    @Override
    public void setLocationX(double x)
    {
        transformable.setLocationX(x);
    }

    @Override
    public void setLocationY(double y)
    {
        transformable.setLocationY(y);
    }

    @Override
    public int getWidth()
    {
        return transformable.getWidth();
    }

    @Override
    public int getHeight()
    {
        return transformable.getHeight();
    }
}
