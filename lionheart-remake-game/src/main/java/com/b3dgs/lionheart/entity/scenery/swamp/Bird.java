/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.entity.scenery.swamp;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionengine.game.purview.Collidable;
import com.b3dgs.lionengine.game.purview.model.CollidableModel;
import com.b3dgs.lionheart.CategoryType;
import com.b3dgs.lionheart.ThemeType;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.EntityCollision;
import com.b3dgs.lionheart.entity.EntityMover;
import com.b3dgs.lionheart.entity.EntityState;
import com.b3dgs.lionheart.entity.Patrol;
import com.b3dgs.lionheart.entity.player.ValdynState;
import com.b3dgs.lionheart.entity.scenery.EntitySceneryPatroller;

/**
 * Bird implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Bird
        extends EntitySceneryPatroller
{
    /** Class media. */
    public static final Media MEDIA = Entity.getConfig(CategoryType.SCENERY, ThemeType.SWAMP, Bird.class);

    /** Top surface. */
    private final Collidable top;
    /** Hit timer. */
    private final Timing hit;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Bird(SetupSurfaceRasteredGame setup)
    {
        super(setup);
        hit = new Timing();
        top = new CollidableModel(this);
        top.setCollision(collisions.get(EntityCollision.TOP));
        enableMovement(Patrol.VERTICAL);
    }

    /**
     * Check the patrol end.
     */
    private void check()
    {
        movement.reset();
        setSide(-getSide());
        setMovementForce(0.0, movementSpeedMax * getSide());
        teleportY(getLocationIntY() + getSide());
    }

    /*
     * EntityScenery
     */

    @Override
    public void updateCollision()
    {
        super.updateCollision();
        top.updateCollision();
    }

    @Override
    public void renderCollision(Graphic g, CameraGame camera)
    {
        top.renderCollision(g, camera);
    }

    @Override
    public void updateGravity(double extrp, int desiredFps, Force... forces)
    {
        if (status.isState(EntityState.WALK))
        {
            super.updateGravity(extrp, desiredFps, forces);
        }
    }

    @Override
    protected void handleActions(double extrp)
    {
        if (status.isState(EntityState.WALK))
        {
            final int y = getLocationIntY();
            if (y > getPositionMax())
            {
                teleportY(getPositionMax());
                check();
            }
            if (y < getPositionMin())
            {
                teleportY(getPositionMin());
                check();
            }
        }
        super.handleActions(extrp);
    }

    @Override
    protected void updateStates()
    {
        super.updateStates();
        if (!hit.isStarted())
        {
            status.setState(EntityState.WALK);
        }
        else
        {
            if (hit.elapsed(3000))
            {
                status.setState(EntityState.WALK);
                hit.stop();
            }
            else
            {
                status.setState(EntityState.TURN);
            }
        }
    }

    @Override
    public void hitBy(Entity entity)
    {
        if (entity instanceof EntityMover && entity.status.isState(ValdynState.ATTACK_FALL))
        {
            ((EntityMover) entity).forceJump();
            if (!hit.isStarted())
            {
                hit.start();
            }
        }
    }

    @Override
    protected void checkPatrolEnd()
    {
        // Nothing to do
    }
}
