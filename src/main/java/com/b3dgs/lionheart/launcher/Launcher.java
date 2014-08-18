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
package com.b3dgs.lionheart.launcher;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.projectile.LauncherProjectileGame;
import com.b3dgs.lionheart.AppLionheart;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.projectile.Projectile;

/**
 * Bullet launcher.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Launcher
        extends LauncherProjectileGame<Entity, Entity, Projectile>
{
    /**
     * Get a launcher configuration file.
     * 
     * @param type The config associated class.
     * @return The media config.
     */
    public static Media getConfig(Class<? extends Launcher> type)
    {
        return Core.MEDIA.create(AppLionheart.LAUNCHERS_DIR, type.getSimpleName() + "."
                + FactoryObjectGame.FILE_DATA_EXTENSION);
    }

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Launcher(SetupGame setup)
    {
        super(setup);
    }

    /*
     * LauncherProjectile
     */

    @Override
    protected void prepareProjectile(ContextGame context)
    {
        // Nothing to do
    }

    @Override
    protected void launchProjectile(Entity owner)
    {
        // Nothing to do
    }
}
