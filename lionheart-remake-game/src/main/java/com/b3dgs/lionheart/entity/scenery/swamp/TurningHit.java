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

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionheart.CategoryType;
import com.b3dgs.lionheart.ThemeType;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.EntityMover;
import com.b3dgs.lionheart.entity.EntityState;
import com.b3dgs.lionheart.entity.player.Valdyn;
import com.b3dgs.lionheart.entity.player.ValdynState;
import com.b3dgs.lionheart.entity.scenery.EntitySceneryTurning;
import com.b3dgs.lionheart.purview.Hitable;

/**
 * Turning hit scenery implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class TurningHit
        extends EntitySceneryTurning
        implements Hitable
{
    /** Class media. */
    public static final Media MEDIA = Entity.getConfig(CategoryType.SCENERY, ThemeType.SWAMP, TurningHit.class);

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public TurningHit(SetupSurfaceRasteredGame setup)
    {
        super(setup);
        setShakeCounter(5);
    }

    /*
     * EntityTurning
     */

    @Override
    public void checkCollision(Valdyn player)
    {
        super.checkCollision(player);
        if (player.getCollisionAttack().collide(this))
        {
            hitBy(player);
        }
    }

    @Override
    public void hitBy(Entity entity)
    {
        if (entity instanceof EntityMover && entity.status.isState(ValdynState.ATTACK_FALL))
        {
            ((EntityMover) entity).forceJump();
        }
        if (getShakeCounter() == 6)
        {
            setShakeCounter(7);
        }
    }

    @Override
    protected void updateStates()
    {
        // Turning
        if (getShakeCounter() == 5)
        {
            status.setState(EntityState.TURN);
            setShakeCounter(6);
            resetEffectStart();
        }
        // Detect end turning
        if (getShakeCounter() == 7)
        {
            resetEffectStart();
            if (getFrameAnim() == 1 || getFrameAnim() == 16)
            {
                reset();
            }
        }
        super.updateStates();
    }
}
