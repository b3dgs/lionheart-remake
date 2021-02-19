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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Shooter configuration.
 */
public final class ShooterConfig
{
    /** Shooter node name. */
    public static final String NODE_SHOOTER = "shooter";
    /** Fire delay attribute name. */
    public static final String ATT_FIRE_DELAY = "fireDelay";
    /** Fired delay attribute name. */
    public static final String ATT_FIRED_DELAY = "firedDelay";
    /** Fire horizontal force start attribute name. */
    public static final String ATT_SVX = "svx";
    /** Fire vertical force start attribute name. */
    public static final String ATT_SVY = "svy";
    /** Fire horizontal force destination attribute name. */
    public static final String ATT_DVX = "dvx";
    /** Fire vertical force destination attribute name. */
    public static final String ATT_DVY = "dvy";

    /**
     * Imports the config from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static ShooterConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getChild(NODE_SHOOTER));
    }

    /**
     * Imports the config from root.
     * 
     * @param root The patrol node reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static ShooterConfig imports(XmlReader root)
    {
        Check.notNull(root);

        return new ShooterConfig(root);
    }

    /** Fire delay. */
    private final int fireDelay;
    /** Fired delay. */
    private final int firedDelay;
    /** Fire horizontal force start. */
    private final double svx;
    /** Fire vertical force start. */
    private final double svy;
    /** Fire horizontal force destination. */
    private final double dvx;
    /** Fire vertical force destination. */
    private final double dvy;

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be null).
     */
    private ShooterConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        fireDelay = root.readInteger(ATT_FIRE_DELAY);
        firedDelay = root.readInteger(ATT_FIRED_DELAY);
        svx = root.readDouble(ATT_SVX);
        svy = root.readDouble(ATT_SVY);
        dvx = root.readDouble(svx, ATT_DVX);
        dvy = root.readDouble(svy, ATT_DVY);
    }

    /**
     * Get the fire delay.
     * 
     * @return The fire delay.
     */
    public int getFireDelay()
    {
        return fireDelay;
    }

    /**
     * Get the fired delay.
     * 
     * @return The fired delay.
     */
    public int getFiredDelay()
    {
        return firedDelay;
    }

    /**
     * Get the fire horizontal speed start.
     * 
     * @return The fire horizontal speed start.
     */
    public double getSvx()
    {
        return svx;
    }

    /**
     * Get the fire vertical speed start.
     * 
     * @return The fire vertical speed start.
     */
    public double getSvy()
    {
        return svy;
    }

    /**
     * Get the fire horizontal speed destination.
     * 
     * @return The fire horizontal speed destination.
     */
    public double getDvx()
    {
        return dvx;
    }

    /**
     * Get the fire vertical speed destination.
     * 
     * @return The fire vertical speed destination.
     */
    public double getDvy()
    {
        return dvy;
    }
}
