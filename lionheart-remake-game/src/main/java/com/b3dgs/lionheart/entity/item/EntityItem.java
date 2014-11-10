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
package com.b3dgs.lionheart.entity.item;

import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionengine.game.configurer.ConfigAnimations;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionheart.effect.FactoryEffect;
import com.b3dgs.lionheart.effect.Taken;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.player.Valdyn;
import com.b3dgs.lionheart.map.Map;
import com.b3dgs.lionheart.purview.Takeable;

/**
 * Abstract implementation of an item.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class EntityItem
        extends Entity
        implements Takeable
{
    /** Effect factory. */
    private FactoryEffect factoryEffect;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public EntityItem(SetupSurfaceRasteredGame setup)
    {
        super(setup);
        final Configurer configurer = setup.getConfigurer();
        final ConfigAnimations configAnimations = ConfigAnimations.create(configurer);
        play(configAnimations.getAnimation(status.getState().getAnimationName()));
    }

    /*
     * Entity
     */

    @Override
    public void prepare(ContextGame context)
    {
        super.prepare(context);
        factoryEffect = context.getService(FactoryEffect.class);
    }

    @Override
    public void checkCollision(Valdyn player)
    {
        if (player.collide(this))
        {
            kill();
            onTaken(player.stats);
        }
    }

    @Override
    protected void updateStates()
    {
        // Nothing to do
    }

    @Override
    protected void updateDead()
    {
        factoryEffect.startEffect(Taken.MEDIA, getDeathLocation(), -getWidth() / 2, 0);
        destroy();
    }

    @Override
    protected void updateCollisions(Map map)
    {
        // Nothing to do
    }
}
