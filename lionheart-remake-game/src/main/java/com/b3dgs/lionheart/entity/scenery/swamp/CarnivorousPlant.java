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
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionengine.game.purview.Collidable;
import com.b3dgs.lionengine.game.purview.model.CollidableModel;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionheart.CategoryType;
import com.b3dgs.lionheart.ThemeType;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.EntityCollision;
import com.b3dgs.lionheart.entity.EntityCollisionTile;
import com.b3dgs.lionheart.entity.EntityState;
import com.b3dgs.lionheart.entity.player.Valdyn;
import com.b3dgs.lionheart.entity.scenery.EntityScenery;

/**
 * Carnivorous plant implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class CarnivorousPlant
        extends EntityScenery
{
    /** Class media. */
    public static final Media MEDIA = Entity.getConfig(CategoryType.SCENERY, ThemeType.SWAMP, CarnivorousPlant.class);

    /** Minimum wait time. */
    private static final int WAIT_TIME_MIN = 500;
    /** Maximum random wait time. */
    private static final int WAIT_TIME_RAND_MAX = 2000;

    /** Bite collision. */
    private final Collidable bite;
    /** Bite anim timer. */
    private final Timing timer;
    /** Next time to wait. */
    private long next;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public CarnivorousPlant(SetupSurfaceRasteredGame setup)
    {
        super(setup);
        timer = new Timing();
        bite = new CollidableModel(this);
        bite.setCollision(collisions.get(EntityCollision.BITE));
        next = UtilRandom.getRandomInteger(2000) + 500;
        timer.start();
    }

    /*
     * EntityScenery
     */

    @Override
    public void updateCollision()
    {
        super.updateCollision();
        bite.updateCollision();
    }

    @Override
    public void hitThat(Entity entity)
    {
        if (entity instanceof Valdyn)
        {
            final Valdyn valdyn = (Valdyn) entity;
            if (bite.collide(valdyn.getCollisionLeg()))
            {
                entity.applyVerticalCollision(Double.valueOf(bite.getCollisionBounds().getMaxY()));
                entity.kill();
            }
            final Rectangle coll = getCollisionBounds();
            final Rectangle leg = valdyn.getCollisionLeg().getCollisionBounds();
            final EntityCollisionTile collision = valdyn.status.getCollision();
            final double xMin = leg.getMaxX();
            final double xMax = leg.getMinX();

            if (xMin > coll.getMinX() && xMin < coll.getMinX() + coll.getWidth() / 2)
            {
                valdyn.checkCollisionHorizontal(Double.valueOf(coll.getMinX() + (valdyn.getLocationX() - xMin + 1)),
                        collision);
            }
            else if (xMax <= coll.getMaxX() && xMax > coll.getMaxX() - coll.getWidth() / 2)
            {
                valdyn.checkCollisionHorizontal(Double.valueOf(coll.getMaxX() + (valdyn.getLocationX() - xMax - 1)),
                        collision);
            }
        }
    }

    /*
     * EntityScenery
     */

    @Override
    protected void updateStates()
    {
        super.updateStates();
        if (status.isState(EntityState.WALK) && getAnimState() == AnimState.FINISHED)
        {
            status.setState(EntityState.IDLE);
            stopAnimation();
            next = UtilRandom.getRandomInteger(WAIT_TIME_RAND_MAX) + WAIT_TIME_MIN;
            timer.restart();
        }
        if (timer.elapsed(next) && status.isState(EntityState.IDLE))
        {
            status.setState(EntityState.WALK);
        }
    }

    @Override
    protected void onCollide(Entity entity)
    {
        // Nothing to do
    }

    @Override
    protected void onLostCollision()
    {
        // Nothing to do
    }
}
