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

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionheart.CategoryType;
import com.b3dgs.lionheart.ThemeType;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.EntityCollisionTileCategory;
import com.b3dgs.lionheart.entity.FactoryEntity;
import com.b3dgs.lionheart.entity.HandlerEntity;
import com.b3dgs.lionheart.entity.monster.EntityMonster;

/**
 * Nest implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Nest
        extends EntityMonster
{
    /** Class media. */
    public static final Media MEDIA = Entity.getConfig(CategoryType.MONSTER, ThemeType.SWAMP, Nest.class);

    /** Fly timer. */
    private final Timing timer;

    /** Entity factory. */
    private FactoryEntity factory;
    /** Entity handler. */
    private HandlerEntity handler;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Nest(SetupSurfaceRasteredGame setup)
    {
        super(setup);
        timer = new Timing();
        timer.start();
    }

    /*
     * EntityMonster
     */

    @Override
    public void prepare(ContextGame context)
    {
        super.prepare(context);
        factory = context.getService(FactoryEntity.class);
        handler = context.getService(HandlerEntity.class);
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);
        if (!isDead() && timer.elapsed(3000))
        {
            final Fly entity = factory.create(Fly.MEDIA);
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
