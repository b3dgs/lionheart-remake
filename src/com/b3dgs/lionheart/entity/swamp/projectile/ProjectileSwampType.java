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
package com.b3dgs.lionheart.entity.swamp.projectile;

import com.b3dgs.lionengine.game.ObjectTypeUtility;
import com.b3dgs.lionheart.entity.projectile.ProjectileType;

/**
 * List of projectile types.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum ProjectileSwampType implements ProjectileType<ProjectileSwampType>
{
    /** Bullet. */
    BULLET(Bullet.class);

    /** Target class. */
    private final Class<?> target;
    /** Path name. */
    private final String path;

    /**
     * Constructor.
     * 
     * @param target The class target.
     */
    private ProjectileSwampType(Class<?> target)
    {
        this.target = target;
        path = ObjectTypeUtility.getPathName(this);
    }

    /*
     * ProjectileType
     */

    @Override
    public Class<?> getTargetClass()
    {
        return target;
    }

    @Override
    public String getPathName()
    {
        return path;
    }

    @Override
    public ProjectileSwampType getType()
    {
        return this;
    }
}
