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
package com.b3dgs.lionheart.entity.monster;

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionheart.ThemeSwamp;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.EntityMonster;
import com.b3dgs.lionheart.entity.SetupEntity;
import com.b3dgs.lionheart.launcher.FactoryLauncher;
import com.b3dgs.lionheart.launcher.LauncherBullet;

/**
 * Flower implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Flower
        extends EntityMonster
        implements ThemeSwamp
{
    /** Launcher bullet. */
    private final LauncherBullet launcher;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Flower(SetupEntity setup)
    {
        super(setup);
        final FactoryLauncher factoryLauncher = setup.level.factoryLauncher;
        launcher = factoryLauncher.create(LauncherBullet.class);
        launcher.setOwner(this);
        launcher.setRate(2000);
    }

    /*
     * EntityMonster
     */

    @Override
    protected void update(Entity entity)
    {
        super.update(entity);
        final int frame = (getLocationIntX() - entity.getLocationIntX() + 12) / 16;
        if (frame > 0)
        {
            setFrame(5 - UtilMath.fixBetween(frame, 1, 4));
        }
        else
        {
            setFrame(5 + UtilMath.fixBetween(-frame, 0, 3));
        }
        final int f = getFrame();
        if (f == 1)
        {
            launcher.setOffsetX(-30);
        }
        else if (f == 2)
        {
            launcher.setOffsetX(-28);
        }
        else if (f == 3)
        {
            launcher.setOffsetX(-22);
        }
        else if (f == 4)
        {
            launcher.setOffsetX(-17);
        }
        else if (f == 5)
        {
            launcher.setOffsetX(-12);
        }
        else if (f == 6)
        {
            launcher.setOffsetX(-6);
        }
        else if (f == 7)
        {
            launcher.setOffsetX(-2);
        }
        else if (f == 8)
        {
            launcher.setOffsetX(0);
        }
        launcher.setOffsetY(6);
        launcher.launch(entity);
    }
}
