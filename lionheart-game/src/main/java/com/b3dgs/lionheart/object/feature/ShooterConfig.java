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
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Shooter configuration.
 */
public final class ShooterConfig implements XmlSaver
{
    /** Shooter node name. */
    public static final String NODE_SHOOTER = "shooter";
    /** Fire delay attribute name. */
    public static final String ATT_FIRE_DELAY = "fireDelay";
    /** Fired delay attribute name. */
    public static final String ATT_FIRED_DELAY = "firedDelay";
    /** Fire anim attribute name. */
    public static final String ATT_ANIM = "anim";
    /** Fire horizontal force start attribute name. */
    public static final String ATT_SVX = "svx";
    /** Fire vertical force start attribute name. */
    public static final String ATT_SVY = "svy";
    /** Fire horizontal force destination attribute name. */
    public static final String ATT_DVX = "dvx";
    /** Fire vertical force destination attribute name. */
    public static final String ATT_DVY = "dvy";
    /** Fire track attribute name. */
    public static final String ATT_TRACK = "track";

    /** Fire delay. */
    private final int fireDelay;
    /** Fired delay. */
    private final int firedDelay;
    /** Fire anim flag. */
    private final int anim;
    /** Fire horizontal force start. */
    private final double svx;
    /** Fire vertical force start. */
    private final double svy;
    /** Fire horizontal force destination. */
    private final double dvx;
    /** Fire vertical force destination. */
    private final double dvy;
    /** Fire track flag. */
    private final boolean track;

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be null).
     */
    public ShooterConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        final XmlReader node = root.getChild(NODE_SHOOTER);
        fireDelay = node.readInteger(ATT_FIRE_DELAY);
        firedDelay = node.readInteger(ATT_FIRED_DELAY);
        anim = node.readInteger(0, ATT_ANIM);
        svx = node.readDouble(ATT_SVX);
        svy = node.readDouble(ATT_SVY);
        dvx = node.readDouble(svx, ATT_DVX);
        dvy = node.readDouble(svy, ATT_DVY);
        track = node.readBoolean(false, ATT_TRACK);
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
     * Get the fire anim number.
     * 
     * @return The fire anim number.
     */
    public int getAnim()
    {
        return anim;
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

    /**
     * Get the fire track flag.
     * 
     * @return The fire track flag.
     */
    public boolean getTrack()
    {
        return track;
    }

    @Override
    public void save(Xml root)
    {
        final Xml node = root.createChild(NODE_SHOOTER);
        node.writeInteger(ATT_FIRE_DELAY, fireDelay);
        node.writeInteger(ATT_FIRED_DELAY, firedDelay);
        node.writeInteger(ATT_ANIM, anim);
        node.writeDouble(ATT_SVX, svx);
        node.writeDouble(ATT_SVY, svy);
        node.writeDouble(ATT_DVX, dvx);
        node.writeDouble(ATT_DVY, dvy);
        node.writeBoolean(ATT_TRACK, track);
    }
}
