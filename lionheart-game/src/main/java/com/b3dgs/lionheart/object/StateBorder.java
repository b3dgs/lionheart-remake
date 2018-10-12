/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;

/**
 * Idle state implementation.
 */
final class StateBorder extends State
{
    private final BorderDetection border = new BorderDetection();
    private final Mirrorable mirrorable;
    private final TileCollidable tileCollidable;
    private final Collidable collidable;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateBorder(EntityModel model, Animation animation)
    {
        super(model, animation);

        mirrorable = model.getFeature(Mirrorable.class);
        tileCollidable = model.getFeature(TileCollidable.class);
        collidable = model.getFeature(Collidable.class);

        addTransition(StateWalk.class, this::isGoingHorizontal);
        addTransition(StateCrouch.class, this::isGoingDown);
        addTransition(StateJump.class, this::isGoingUp);
        addTransition(StateAttackPrepare.class, control::isFireButton);
    }

    @Override
    public void enter()
    {
        super.enter();

        tileCollidable.addListener(border);
        collidable.addListener(border);
        border.reset();
    }

    @Override
    public void exit()
    {
        tileCollidable.removeListener(border);
        collidable.removeListener(border);
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        if (border.isLeft())
        {
            mirrorable.mirror(Mirror.NONE);
        }
        if (border.isRight())
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
    }
}
