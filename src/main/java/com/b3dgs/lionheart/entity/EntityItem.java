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
package com.b3dgs.lionheart.entity;

import com.b3dgs.lionengine.game.configurable.Configurable;
import com.b3dgs.lionheart.effect.FactoryEffect;
import com.b3dgs.lionheart.effect.Taken;
import com.b3dgs.lionheart.entity.player.Valdyn;

/**
 * Abstract implementation of an item.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class EntityItem
        extends Entity
{
    /** Effect factory. */
    private final FactoryEffect factoryEffect;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public EntityItem(SetupEntity setup)
    {
        super(setup);
        factoryEffect = setup.level.factoryEffect;
        final Configurable configurable = setup.getConfigurable();
        play(configurable.getAnimation(status.getState().getAnimationName()));
    }

    /**
     * Called when the item is taken by the entity.
     * 
     * @param entity The entity.
     */
    protected abstract void onTaken(Valdyn entity);

    /*
     * Entity
     */

    @Override
    public void hitBy(Entity entity)
    {
        if (!isDead())
        {
            kill();
            onTaken((Valdyn) entity);
        }
    }

    @Override
    public void hitThat(Entity entity)
    {
        // Nothing to do
    }

    @Override
    protected void updateStates()
    {
        // Nothing to do
    }

    @Override
    protected void updateDead()
    {
        factoryEffect.startEffect(Taken.class, (int) dieLocation.getX() - getWidth() / 2, (int) dieLocation.getY());
        destroy();
    }

    @Override
    protected void updateCollisions()
    {
        // Nothing to do
    }

    @Override
    protected void updateAnimations(double extrp)
    {
        // Nothing to do
    }
}
