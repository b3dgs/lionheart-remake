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
import com.b3dgs.lionheart.entity.player.ValdynState;
import com.b3dgs.lionheart.entity.scenery.EntitySceneryTurning;

/**
 * Turning hit scenery implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class TurningHit
        extends EntitySceneryTurning
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
        shakeCounter = 5;
    }

    /*
     * EntityTurning
     */

    @Override
    public void hitBy(Entity entity)
    {
        super.hitBy(entity);
        if (entity instanceof EntityMover && entity.status.isState(ValdynState.ATTACK_FALL))
        {
            ((EntityMover) entity).forceJump();
        }
        if (shakeCounter == 6)
        {
            shakeCounter = 7;
        }
    }

    @Override
    protected void updateStates()
    {
        // Turning
        if (shakeCounter == 5)
        {
            status.setState(EntityState.TURN);
            shakeCounter = 6;
            effectStart = false;
        }
        // Detect end turning
        if (shakeCounter == 7)
        {
            effectStart = false;
            if (getFrameAnim() == 1 || getFrameAnim() == 16)
            {
                shakeCounter = 0;
                shake = false;
                status.setState(EntityState.IDLE);
                effectSide = 1;
                timerShake.start();
            }
        }
        super.updateStates();
    }
}
