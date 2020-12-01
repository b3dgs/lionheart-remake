/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Patrol configuration.
 */
public final class PatrolConfig
{
    /** Config node name. */
    public static final String NODE_PATROL = "patrol";
    /** Horizontal speed attribute name. */
    private static final String ATT_VX = "sh";
    /** Vertical speed attribute name. */
    private static final String ATT_VY = "sv";
    /** Amplitude attribute name. */
    private static final String ATT_AMPLITUDE = "amplitude";
    /** Mirror attribute name. */
    private static final String ATT_MIRROR = "mirror";
    /** Coll attribute name. */
    private static final String ATT_COLL = "coll";

    /**
     * Imports the config from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static PatrolConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getChild(NODE_PATROL));
    }

    /**
     * Imports the config from root.
     * 
     * @param root The patrol node reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static PatrolConfig imports(XmlReader root)
    {
        Check.notNull(root);

        final double sh = root.readDouble(0.3, ATT_VX);
        final double sv = root.readDouble(0.0, ATT_VY);
        final int amplitude = root.readInteger(50, ATT_AMPLITUDE);
        final boolean mirror = root.readBoolean(false, ATT_MIRROR);
        final boolean coll = root.readBoolean(false, ATT_COLL);

        return new PatrolConfig(sh, sv, amplitude, mirror, coll);
    }

    /** Horizontal speed. */
    private final double sh;
    /** Vertical speed. */
    private final double sv;
    /** Amplitude enabled. */
    private final int amplitude;
    /** Mirror vertical. */
    private final boolean mirror;
    /** Disable collision on turn. */
    private final boolean coll;

    /**
     * Create config.
     * 
     * @param sh The horizontal speed.
     * @param sv The vertical speed.
     * @param amplitude The amplitude value.
     * @param mirror The mirror vertical flag.
     * @param coll The disable collision on turn flag.
     */
    private PatrolConfig(double sh, double sv, int amplitude, boolean mirror, boolean coll)
    {
        super();

        this.sh = sh;
        this.sv = sv;
        this.amplitude = amplitude;
        this.mirror = mirror;
        this.coll = coll;
    }

    /**
     * Get the horizontal speed.
     * 
     * @return The horizontal speed.
     */
    public double getSh()
    {
        return sh;
    }

    /**
     * Get the vertical speed.
     * 
     * @return The vertical speed.
     */
    public double getSv()
    {
        return sv;
    }

    /**
     * Get the amplitude value.
     * 
     * @return The patrol maximum movement, 0 if no turn.
     */
    public int getAmplitude()
    {
        return amplitude;
    }

    /**
     * Check if mirror is enabled.
     * 
     * @return <code>true</code> to enable mirror, <code>false</code> to disable.
     */
    public boolean hasMirror()
    {
        return mirror;
    }

    /**
     * Check if collision disabled on turn is enabled.
     * 
     * @return <code>true</code> if disable collision on turn, <code>false</code> else.
     */
    public boolean hasColl()
    {
        return coll;
    }
}
