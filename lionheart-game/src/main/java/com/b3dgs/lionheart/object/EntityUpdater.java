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

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;

/**
 * Entity updating implementation.
 */
final class EntityUpdater extends FeatureModel implements Refreshable, TileCollidableListener
{
    /**
     * Update mirror depending of current mirror and movement.
     * 
     * @param mirrorable The mirrorable reference.
     * @param movement The movement force reference.
     */
    private static void updateMirror(Mirrorable mirrorable, Force movement)
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

    private final Force movement;
    private final Force jump;
    private final SpriteAnimated surface;
    private final Viewer viewer;

    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Body body;
    @FeatureGet private StateHandler state;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Collidable collidable;
    @FeatureGet private TileCollidable tileCollidable;

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
        surface = model.getSurface();
        viewer = services.get(Viewer.class);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        collidable.setOrigin(Origin.CENTER_BOTTOM);
        transformable.teleport(80, 32);
    }

    @Override
    public void update(double extrp)
    {
        state.update(extrp);
        movement.update(extrp);
        updateMirror(mirrorable, movement);
        mirrorable.update(extrp);
        jump.update(extrp);
        body.update(extrp);
        tileCollidable.update(extrp);

        if (transformable.getY() < 0)
        {
            transformable.teleportY(80);
            body.resetGravity();
        }

        surface.setLocation(viewer, transformable);
        surface.setMirror(mirrorable.getMirror());
        surface.update(extrp);
    }

    @Override
    public void notifyTileCollided(Tile tile, Axis axis)
    {
        if (Axis.Y == axis && transformable.getY() < transformable.getOldY())
        {
            body.resetGravity();
            jump.setDirection(DirectionNone.INSTANCE);
        }
    }
}
