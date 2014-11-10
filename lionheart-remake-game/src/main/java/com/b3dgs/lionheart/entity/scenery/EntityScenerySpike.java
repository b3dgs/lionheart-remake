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
package com.b3dgs.lionheart.entity.scenery;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionheart.entity.EntityMover;
import com.b3dgs.lionheart.entity.EntityState;
import com.b3dgs.lionheart.entity.player.Valdyn;
import com.b3dgs.lionheart.purview.Hitable;
import com.b3dgs.lionheart.purview.Hurtable;

/**
 * Spike base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class EntityScenerySpike
        extends EntityScenery
        implements Hurtable
{
    /** Timer. */
    private final Timing timer;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public EntityScenerySpike(SetupSurfaceRasteredGame setup)
    {
        super(setup);
        timer = new Timing();
        timer.start();
    }

    /*
     * EntityScenery
     */

    @Override
    public void updateAnimation(double extrp)
    {
        if (status.stateChanged() && status.getState() == EntityState.TURN)
        {
            setFrame(getAnimationData(status.getState()).getLast());
        }
        super.updateAnimation(extrp);

    }

    @Override
    public void checkCollision(Valdyn entity)
    {
        if (collide(entity))
        {
            hitThat(entity);
        }
    }

    @Override
    public void hitThat(EntityMover entity)
    {
        final Animation anim = getAnimationData(EntityState.JUMP);
        final int frame = getFrame();
        if (entity instanceof Hitable && frame > anim.getFirst() && frame <= anim.getLast())
        {
            ((Hitable) entity).hitBy(this);
        }
    }

    @Override
    protected void updateStates()
    {
        if (status.isState(EntityState.IDLE))
        {
            status.setState(EntityState.WALK);
        }
        if (getAnimState() == AnimState.FINISHED)
        {
            if (timer.elapsed(1000) && status.isState(EntityState.WALK))
            {
                status.setState(EntityState.JUMP);
                timer.restart();
            }
            else if (timer.elapsed(1000) && status.isState(EntityState.JUMP))
            {
                status.setState(EntityState.TURN);
                timer.restart();
            }
            else if (timer.elapsed(1500) && status.isState(EntityState.TURN))
            {
                status.setState(EntityState.WALK);
                timer.restart();
            }
        }
    }
}
