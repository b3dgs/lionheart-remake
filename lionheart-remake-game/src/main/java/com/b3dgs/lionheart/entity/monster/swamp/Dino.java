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
package com.b3dgs.lionheart.entity.monster.swamp;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionheart.CategoryType;
import com.b3dgs.lionheart.ThemeType;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.EntityState;
import com.b3dgs.lionheart.entity.Patrol;
import com.b3dgs.lionheart.entity.PatrolSide;
import com.b3dgs.lionheart.entity.monster.EntityMonster;

/**
 * Dino monster implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Dino
        extends EntityMonster
{
    /** Class media. */
    public static final Media MEDIA = Entity.getConfig(CategoryType.MONSTER, ThemeType.SWAMP, Dino.class);

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Dino(SetupSurfaceRasteredGame setup)
    {
        super(setup);
        setFrameOffsets(0, -4);
        enableMovement(Patrol.HORIZONTAL);
    }

    /*
     * EntityMonster
     */

    @Override
    public boolean getMirror()
    {
        return !super.getMirror();
    }

    @Override
    protected void onHitBy(Entity entity)
    {
        super.onHitBy(entity);
        final int side;
        if (entity.getLocationX() - getLocationX() < 0)
        {
            side = PatrolSide.MIN.getMove();
        }
        else
        {
            side = PatrolSide.MAX.getMove();
        }
        movement.setForce(3.0 * side, 0.0);
    }

    @Override
    protected void updateStates()
    {
        if (isFalling() || timerHurt.isStarted())
        {
            status.setState(EntityState.FALL);
        }
        else
        {
            super.updateStates();
        }
    }
}
