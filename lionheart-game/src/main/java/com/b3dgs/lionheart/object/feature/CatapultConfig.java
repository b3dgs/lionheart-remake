/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Catapult configuration.
 */
public final class CatapultConfig implements XmlSaver
{
    /** Pillar node name. */
    public static final String NODE_CATAPULT = "catapult";
    /** Horizontal speed attribute name. */
    public static final String ATT_VX = "vx";
    /** Vertical speed attribute name. */
    public static final String ATT_VY = "vy";

    /** Horizontal speed. */
    private final double vx;
    /** Vertical speed. */
    private final double vy;

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public CatapultConfig(XmlReader root)
    {
        super();

        final XmlReader node = root.getChild(NODE_CATAPULT);
        vx = node.readDouble(ATT_VX);
        vy = node.readDouble(ATT_VY);
    }

    /**
     * Get the horizontal speed.
     * 
     * @return The horizontal speed.
     */
    public double getVx()
    {
        return vx;
    }

    /**
     * Get the vertical speed.
     * 
     * @return The vertical speed.
     */
    public double getVy()
    {
        return vy;
    }

    @Override
    public void save(Xml root)
    {
        final Xml node = root.createChild(NODE_CATAPULT);
        node.writeDouble(ATT_VX, vx);
        node.writeDouble(ATT_VY, vy);
    }
}
