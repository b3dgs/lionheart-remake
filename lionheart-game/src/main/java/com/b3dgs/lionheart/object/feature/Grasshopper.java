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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.io.DeviceControllerVoid;
import com.b3dgs.lionheart.RasterType;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.state.StateFall;
import com.b3dgs.lionheart.object.state.StateJump;

/**
 * Grasshopper feature implementation.
 * <ol>
 * <li>Follow player until defined distance.</li>
 * <li>Fire fly on delay.</li>
 * </ol>
 */
@FeatureInterface
public final class Grasshopper extends FeatureModel implements Routine, CollidableListener
{
    private final MapTile map = services.get(MapTile.class);
    private final Camera camera = services.get(Camera.class);
    private final Trackable target = services.getOptional(Trackable.class).orElse(null);

    private final Transformable transformable;
    private final Mirrorable mirrorable;
    private final Launcher launcher;
    private final StateHandler handler;

    private double move;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @param mirrorable The mirrorable feature.
     * @param rasterable The rasterable feature.
     * @param launcher The launcher feature.
     * @param model The model feature.
     * @param handler The handler feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Grasshopper(Services services,
                       Setup setup,
                       Transformable transformable,
                       Mirrorable mirrorable,
                       Rasterable rasterable,
                       Launcher launcher,
                       EntityModel model,
                       StateHandler handler)
    {
        super(services, setup);

        this.transformable = transformable;
        this.mirrorable = mirrorable;
        this.launcher = launcher;
        this.handler = handler;

        model.setInput(new DeviceControllerVoid()
        {
            @Override
            public double getHorizontalDirection()
            {
                return move;
            }
        });
        if (RasterType.CACHE == Settings.getInstance().getRaster())
        {
            launcher.addListener(l -> l.ifIs(Rasterable.class,
                                             r -> r.setRaster(true, rasterable.getMedia().get(), map.getTileHeight())));
        }
    }

    @Override
    public void update(double extrp)
    {
        if (target != null && camera.isViewable(transformable, 64, 16))
        {
            if (target.getX() - transformable.getX() > 100)
            {
                move = 1.0;
            }
            else if (target.getX() - transformable.getX() < -112)
            {
                move = -1.0;
            }
            else if (!handler.isState(StateJump.class) && !handler.isState(StateFall.class))
            {
                move = 0.0;

                if (transformable.getX() > target.getX())
                {
                    mirrorable.mirror(Mirror.HORIZONTAL);
                }
                else
                {
                    mirrorable.mirror(Mirror.NONE);
                }

                launcher.fire();
            }
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (CollisionName.COLL_SIGH.equals(with.getName()) && collidable.hasFeature(Trackable.class))
        {
            // FIXME target = collidable.getFeature(Trackable.class);
        }
    }
}
