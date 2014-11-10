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
package com.b3dgs.lionheart.purview.ascend;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.game.purview.Collidable;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionheart.entity.EntityCollisionTile;
import com.b3dgs.lionheart.entity.EntityMover;
import com.b3dgs.lionheart.purview.Borderer;

/**
 * Ascendable model implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class AscendableModel
        implements AscendableServices
{
    /** Ascendable user. */
    private final AscendableUsedServices user;
    /** Border timer. */
    private final Timing borderTimer;
    /** Collide state. */
    private boolean collide;
    /** Collide old state. */
    private boolean collideOld;

    /**
     * Create the model.
     * 
     * @param user The ascendable user.
     */
    public AscendableModel(AscendableUsedServices user)
    {
        this.user = user;
        borderTimer = new Timing();
    }

    /**
     * Update the entity extremity position.
     * 
     * @param borderer The entity reference.
     */
    private void updateExtremity(Borderer borderer)
    {
        final int width = borderer.getWidth() / 2;
        final Rectangle collision = user.getCollisionBounds();
        if (borderer.getLocationX() < collision.getMinX() - width + Borderer.TILE_EXTREMITY_WIDTH * 2)
        {
            checkExtremity(borderer, true);
        }
        else if (borderer.getLocationX() > collision.getMaxX() + width - Borderer.TILE_EXTREMITY_WIDTH * 2)
        {
            checkExtremity(borderer, false);
        }
        else
        {
            borderTimer.stop();
        }
    }

    /**
     * Check the extremity timer and apply it.
     * 
     * @param borderer The borderer reference.
     * @param mirror The mirror to apply.
     */
    private void checkExtremity(Borderer borderer, boolean mirror)
    {
        if (!borderTimer.isStarted())
        {
            borderTimer.start();
        }
        if (borderTimer.elapsed(250))
        {
            borderer.updateExtremity(mirror);
        }
    }

    /*
     * Ascendable
     */

    @Override
    public void checkAscendBy(EntityMover entity, Collidable collidable)
    {
        collideOld = collide;
        collide = false;
        if (collidable.collide(user) && !entity.isJumping() && entity.getLocationY() > user.getLocationY())
        {
            user.onAscendingBy(entity);
            entity.checkCollisionVertical(Double.valueOf(user.getLocationY() + user.getCollisionData().getOffsetY()),
                    EntityCollisionTile.GROUND);
            if (entity instanceof Borderer)
            {
                updateExtremity((Borderer) entity);
            }
            collide = true;
        }
        if (!collide && collideOld)
        {
            user.onDescended(entity);
            collideOld = false;
        }
    }
}
