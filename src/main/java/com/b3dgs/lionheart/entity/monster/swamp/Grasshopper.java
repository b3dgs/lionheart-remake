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
package com.b3dgs.lionheart.entity.monster.swamp;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionheart.CategoryType;
import com.b3dgs.lionheart.ThemeType;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.monster.EntityMonster;

/**
 * Grasshopper implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Grasshopper
        extends EntityMonster
{
    /** Class media. */
    public static final Media MEDIA = Entity.getConfig(CategoryType.MONSTER, ThemeType.SWAMP, Grasshopper.class);

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Grasshopper(SetupSurfaceRasteredGame setup)
    {
        super(setup);
    }
}
