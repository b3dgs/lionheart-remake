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
package com.b3dgs.lionheart.entity.swamp;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.EntityCollisionTileCategory;
import com.b3dgs.lionheart.entity.EntityMonster;
import com.b3dgs.lionheart.entity.HandlerEntity;
import com.b3dgs.lionheart.entity.SetupEntity;

/**
 * Nest implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Nest
        extends EntityMonster
{
    /** Entity factory. */
    private final FactoryEntitySwamp factory;
    /** Entity handler. */
    private final HandlerEntity handler;
    /** Fly timer. */
    private final Timing timer;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Nest(SetupEntity setup)
    {
        super(setup);
        factory = (FactoryEntitySwamp) setup.level.factoryEntity;
        handler = setup.level.handlerEntity;
        timer = new Timing();
        timer.start();
    }

    /*
     * EntityMonster
     */

    @Override
    public void update(double extrp)
    {
        super.update(extrp);
        if (!isDead() && timer.elapsed(3000))
        {
            final Fly entity = factory.create(EntitySwampType.FLY);
            entity.teleport(getLocationX(), getLocationY() + 16);
            entity.setMovementForce(-1.0, 0.0);
            entity.setMovementSpeedMax(1.0);
            entity.prepare();
            handler.add(entity);
            timer.restart();
        }
        if (checkCollisionHorizontal(EntityCollisionTileCategory.GROUND_CENTER) != null)
        {
            super.kill();
        }
    }

    @Override
    protected void onHitBy(Entity entity)
    {
        kill();
    }

    @Override
    public void kill()
    {
        setMass(2.0);
        setGravityMax(3.0);
    }
}
