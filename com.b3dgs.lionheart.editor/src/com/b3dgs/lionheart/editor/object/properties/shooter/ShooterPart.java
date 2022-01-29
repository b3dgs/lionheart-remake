/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionheart.editor.object.properties.shooter;

import com.b3dgs.lionheart.editor.object.properties.PartAbstract;
import com.b3dgs.lionheart.object.feature.Shooter;
import com.b3dgs.lionheart.object.feature.ShooterConfig;

/**
 * Element properties part.
 */
public class ShooterPart extends PartAbstract<ShooterConfig, Shooter>
{
    /** Id. */
    public static final String ID = ID_PREFIX + "shooter";

    /**
     * Create part.
     */
    public ShooterPart()
    {
        super(ShooterConfig.class, Shooter.class, ShooterEditor.class);
    }
}
