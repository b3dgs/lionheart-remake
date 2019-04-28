/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Patrol configuration.
 */
public class PatrolConfig
{
    /** Config node name. */
    private static final String NODE_STATS = "patrol";
    /** Horizontal speed attribute name. */
    private static final String ATT_VX = "sh";
    /** Vertical speed attribute name. */
    private static final String ATT_VY = "sv";
    /** Turn attribute name. */
    private static final String ATT_TURN = "turn";

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

        final double sh = configurer.getDoubleDefault(0.0, ATT_VX, NODE_STATS);
        final double sv = configurer.getDoubleDefault(0.0, ATT_VY, NODE_STATS);
        final boolean turn = configurer.getBooleanDefault(true, ATT_TURN, NODE_STATS);

        return new PatrolConfig(sh, sv, turn);
    }

    /** Horizontal speed. */
    private final double sh;
    /** Vertical speed. */
    private final double sv;
    /** Turn enabled. */
    private final boolean turn;

    /**
     * Create config.
     * 
     * @param sh The horizontal speed.
     * @param sv The vertical speed.
     * @param turn The turn flag.
     */
    private PatrolConfig(double sh, double sv, boolean turn)
    {
        super();

        this.sh = sh;
        this.sv = sv;
        this.turn = turn;
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
     * Check if turn is enabled.
     * 
     * @return <code>true</code> to enable turn, <code>false</code> to continue straight on.
     */
    public boolean hasTurn()
    {
        return turn;
    }
}
