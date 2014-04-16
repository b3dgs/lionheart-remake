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
package com.b3dgs.lionheart.entity.launcher;

import com.b3dgs.lionengine.game.projectile.LauncherProjectileGame;
import com.b3dgs.lionheart.Foldered;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.projectile.Projectile;

/**
 * Bullet launcher.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Launcher
        extends LauncherProjectileGame<Entity, Entity, Projectile>
        implements Foldered
{
    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Launcher(SetupLauncher setup)
    {
        super(setup, setup.factory, setup.handler);
    }

    /*
     * LauncherProjectile
     */

    @Override
    protected void launchProjectile(Entity owner)
    {
        // Nothing to do
    }
}
