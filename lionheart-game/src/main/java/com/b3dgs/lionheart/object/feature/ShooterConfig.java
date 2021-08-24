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

import java.util.OptionalDouble;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
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
    private final OptionalDouble dvx;
    /** Fire vertical force destination. */
    private final OptionalDouble dvy;
    /** Fire track flag. */
    private final boolean track;

    /**
     * Create blank configuration.
     */
    public ShooterConfig()
    {
        this(0, 0, Animation.MINIMUM_FRAME, 0.0, 0.0, OptionalDouble.empty(), OptionalDouble.empty(), false);
    }

    /**
     * Create configuration.
     * 
     * @param fireDelay The fire delay.
     * @param firedDelay The fired delay.
     * @param anim The animation frame.
     * @param svx The starting horizontal.
     * @param svy The starting vertical.
     * @param dvx The ending horizontal.
     * @param dvy The ending vertical.
     * @param track The track flag.
     */
    public ShooterConfig(int fireDelay,
                         int firedDelay,
                         int anim,
                         double svx,
                         double svy,
                         OptionalDouble dvx,
                         OptionalDouble dvy,
                         boolean track)
    {
        super();

        this.fireDelay = fireDelay;
        this.firedDelay = firedDelay;
        this.anim = anim;
        this.svx = svx;
        this.svy = svy;
        this.dvx = dvx;
        this.dvy = dvy;
        this.track = track;
    }

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
        fireDelay = node.getInteger(ATT_FIRE_DELAY);
        firedDelay = node.getInteger(ATT_FIRED_DELAY);
        anim = node.getInteger(0, ATT_ANIM);
        svx = node.getDouble(ATT_SVX);
        svy = node.getDouble(ATT_SVY);
        dvx = node.getDoubleOptional(ATT_DVX);
        dvy = node.getDoubleOptional(ATT_DVY);
        track = node.getBoolean(false, ATT_TRACK);
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
    public OptionalDouble getDvx()
    {
        return dvx;
    }

    /**
     * Get the fire vertical speed destination.
     * 
     * @return The fire vertical speed destination.
     */
    public OptionalDouble getDvy()
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
        dvx.ifPresent(v -> node.writeDouble(ATT_DVX, v));
        dvy.ifPresent(v -> node.writeDouble(ATT_DVY, v));
        if (track)
        {
            node.writeBoolean(ATT_TRACK, track);
        }
    }

    private static void add(StringBuilder builder, String name, int value)
    {
        builder.append(name).append(Constant.DOUBLE_DOT).append(value).append(Constant.SPACE);
    }

    private static void add(StringBuilder builder, String name, double value)
    {
        builder.append(name).append(Constant.DOUBLE_DOT).append(value).append(Constant.SPACE);
    }

    private static void add(StringBuilder builder, String name, boolean value)
    {
        builder.append(name).append(Constant.DOUBLE_DOT).append(value).append(Constant.SPACE);
    }

    private static void add(StringBuilder builder, String name, OptionalDouble value)
    {
        value.ifPresent(v -> builder.append(name).append(Constant.DOUBLE_DOT).append(v).append(Constant.SPACE));
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Shooter [");
        add(builder, ATT_FIRE_DELAY, fireDelay);
        add(builder, ATT_FIRED_DELAY, firedDelay);
        add(builder, ATT_ANIM, anim);
        add(builder, ATT_SVX, svx);
        add(builder, ATT_SVY, svy);
        add(builder, ATT_DVX, dvx);
        add(builder, ATT_DVY, dvy);
        add(builder, ATT_TRACK, track);
        builder.append("]");
        return builder.toString();
    }
}
