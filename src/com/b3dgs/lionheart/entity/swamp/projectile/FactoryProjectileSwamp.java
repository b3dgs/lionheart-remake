/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.entity.swamp.projectile;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionheart.AppLionheart;
import com.b3dgs.lionheart.WorldType;
import com.b3dgs.lionheart.entity.projectile.FactoryProjectile;

/**
 * Projectile swamp factory.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FactoryProjectileSwamp
        extends FactoryProjectile<ProjectileSwampType>
{
    /**
     * Constructor.
     */
    public FactoryProjectileSwamp()
    {
        super(ProjectileSwampType.class, Media.getPath(AppLionheart.PROJECTILES_DIR, WorldType.SWAMP.getPathName()));
    }

    /*
     * FactoryObjectGame
     */

    @Override
    protected SetupSurfaceGame createSetup(ProjectileSwampType type, Media config)
    {
        return new SetupSurfaceGame(config);
    }
}
