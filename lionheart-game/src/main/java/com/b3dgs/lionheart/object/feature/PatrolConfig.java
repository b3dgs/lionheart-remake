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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Patrol configuration.
 */
public final class PatrolConfig implements XmlSaver
{
    /** Config node name. */
    public static final String NODE_PATROL = "patrol";
    /** Horizontal speed attribute name. */
    public static final String ATT_VX = "sh";
    /** Vertical speed attribute name. */
    public static final String ATT_VY = "sv";
    /** Amplitude attribute name. */
    public static final String ATT_AMPLITUDE = "amplitude";
    /** Offset attribute name. */
    public static final String ATT_OFFSET = "offset";
    /** Mirror attribute name. */
    public static final String ATT_MIRROR = "mirror";
    /** Coll attribute name. */
    public static final String ATT_COLL = "coll";
    /** Proximity attribute name. */
    public static final String ATT_PROXIMITY = "proximity";
    /** Sight attribute name. */
    public static final String ATT_SIGHT = "sight";
    /** Vertical animation offset attribute name. */
    public static final String ATT_ANIMOFFSET = "animOffset";
    /** Patrol delay offset attribute name. */
    public static final String ATT_DELAY_MS = "delay";
    /** Curve attribute name. */
    public static final String ATT_CURVE = "curve";

    /**
     * Imports the config from root.
     * 
     * @param root The patrol node reference (must not be <code>null</code>).
     * @return The config data.
     * @throws LionEngineException If unable to read node.
     */
    public static List<PatrolConfig> imports(XmlReader root)
    {
        Check.notNull(root);

        final List<PatrolConfig> patrols = new ArrayList<>();
        for (final XmlReader node : root.getChildren(NODE_PATROL))
        {
            patrols.add(new PatrolConfig(node));
        }
        return patrols;
    }

    /** Horizontal speed. */
    private final OptionalDouble sh;
    /** Vertical speed. */
    private final OptionalDouble sv;
    /** Amplitude enabled. */
    private final OptionalInt amplitude;
    /** Offset value. */
    private final OptionalInt offset;
    /** Mirror vertical. */
    private final Optional<Boolean> mirror;
    /** Disable collision on turn. */
    private final Optional<Boolean> coll;
    /** Perform movement update on proximity. */
    private final OptionalInt proximity;
    /** Perform movement update on sight. */
    private final OptionalInt sight;
    /** Vertical animation offset. */
    private final OptionalInt animOffset;
    /** Patrol delay. */
    private final OptionalInt delay;
    /** Curve flag. */
    private final Optional<Boolean> curve;

    /**
     * Create blank config.
     */
    public PatrolConfig()
    {
        super();

        sh = OptionalDouble.empty();
        sv = OptionalDouble.empty();
        amplitude = OptionalInt.empty();
        offset = OptionalInt.empty();
        mirror = Optional.empty();
        coll = Optional.empty();
        proximity = OptionalInt.empty();
        sight = OptionalInt.empty();
        animOffset = OptionalInt.empty();
        delay = OptionalInt.empty();
        curve = Optional.empty();
    }

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be null).
     */
    public PatrolConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        sh = root.getDoubleOptional(ATT_VX);
        sv = root.getDoubleOptional(ATT_VY);
        amplitude = root.getIntegerOptional(ATT_AMPLITUDE);
        offset = root.getIntegerOptional(ATT_OFFSET);
        mirror = root.getBooleanOptional(ATT_MIRROR);
        coll = root.getBooleanOptional(ATT_COLL);
        proximity = root.getIntegerOptional(ATT_PROXIMITY);
        sight = root.getIntegerOptional(ATT_SIGHT);
        animOffset = root.getIntegerOptional(ATT_ANIMOFFSET);
        delay = root.getIntegerOptional(ATT_DELAY_MS);
        curve = root.getBooleanOptional(ATT_CURVE);
    }

    /**
     * Create config.
     * 
     * @param sh The horizontal speed.
     * @param sv The vertical speed.
     * @param amplitude The amplitude value.
     * @param offset The start offset.
     * @param mirror The mirror flag.
     * @param coll The collide turn flag.
     * @param proximity The proximity value.
     * @param sight The sight value.
     * @param animOffset The animation offset value.
     * @param delay The delay value.
     * @param curve The curve value.
     */
    public PatrolConfig(OptionalDouble sh,
                        OptionalDouble sv,
                        OptionalInt amplitude,
                        OptionalInt offset,
                        Optional<Boolean> mirror,
                        Optional<Boolean> coll,
                        OptionalInt proximity,
                        OptionalInt sight,
                        OptionalInt animOffset,
                        OptionalInt delay,
                        Optional<Boolean> curve)
    {
        super();

        this.sh = sh;
        this.sv = sv;
        this.amplitude = amplitude;
        this.offset = offset;
        this.mirror = mirror;
        this.coll = coll;
        this.proximity = proximity;
        this.sight = sight;
        this.animOffset = animOffset;
        this.delay = delay;
        this.curve = curve;
    }

    /**
     * Get the horizontal speed.
     * 
     * @return The horizontal speed.
     */
    public OptionalDouble getSh()
    {
        return sh;
    }

    /**
     * Get the vertical speed.
     * 
     * @return The vertical speed.
     */
    public OptionalDouble getSv()
    {
        return sv;
    }

    /**
     * Get the amplitude value.
     * 
     * @return The patrol maximum movement, 0 if no turn.
     */
    public OptionalInt getAmplitude()
    {
        return amplitude;
    }

    /**
     * Get the offset value.
     * 
     * @return The offset start.
     */
    public OptionalInt getOffset()
    {
        return offset;
    }

    /**
     * Check if mirror is enabled.
     * 
     * @return <code>true</code> to enable mirror, <code>false</code> to disable.
     */
    public Optional<Boolean> getMirror()
    {
        return mirror;
    }

    /**
     * Check if collision disabled on turn is enabled.
     * 
     * @return <code>true</code> if disable collision on turn, <code>false</code> else.
     */
    public Optional<Boolean> getColl()
    {
        return coll;
    }

    /**
     * Get the proximity value.
     * 
     * @return The proximity value.
     */
    public OptionalInt getProximity()
    {
        return proximity;
    }

    /**
     * Get the sight value.
     * 
     * @return The sight value.
     */
    public OptionalInt getSight()
    {
        return sight;
    }

    /**
     * Get the animation offset value.
     * 
     * @return The animation offset value.
     */
    public OptionalInt getAnimOffset()
    {
        return animOffset;
    }

    /**
     * Get the patrol delay.
     * 
     * @return The patrol delay.
     */
    public OptionalInt getDelay()
    {
        return delay;
    }

    /**
     * Check if curve movement is enabled.
     * 
     * @return <code>true</code> if curve movement enabled, <code>false</code> else.
     */
    public Optional<Boolean> getCurve()
    {
        return curve;
    }

    @Override
    public void save(Xml root)
    {
        final Xml node = root.createChild(NODE_PATROL);
        sh.ifPresent(v -> node.writeDouble(ATT_VX, v));
        sv.ifPresent(v -> node.writeDouble(ATT_VY, v));
        amplitude.ifPresent(v -> node.writeInteger(ATT_AMPLITUDE, v));
        offset.ifPresent(v -> node.writeInteger(ATT_OFFSET, v));

        final boolean m = mirror.orElse(Boolean.TRUE).booleanValue();
        if (!m)
        {
            node.writeBoolean(ATT_MIRROR, m);
        }
        coll.ifPresent(v -> node.writeBoolean(ATT_COLL, v.booleanValue()));
        proximity.ifPresent(v -> node.writeInteger(ATT_PROXIMITY, v));
        animOffset.ifPresent(v -> node.writeInteger(ATT_ANIMOFFSET, v));
        delay.ifPresent(v -> node.writeInteger(ATT_DELAY_MS, v));
        curve.ifPresent(v -> node.writeBoolean(ATT_CURVE, v.booleanValue()));
    }

    private static void add(StringBuilder builder, String name, OptionalInt value)
    {
        value.ifPresent(v -> builder.append(name).append(Constant.DOUBLE_DOT).append(v).append(Constant.SPACE));
    }

    private static void add(StringBuilder builder, String name, OptionalDouble value)
    {
        value.ifPresent(v -> builder.append(name).append(Constant.DOUBLE_DOT).append(v).append(Constant.SPACE));
    }

    private static void add(StringBuilder builder, String name, Optional<Boolean> value)
    {
        value.ifPresent(v -> builder.append(name).append(Constant.DOUBLE_DOT).append(v).append(Constant.SPACE));
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Patrol [ ");
        add(builder, ATT_VX, sh);
        add(builder, ATT_VY, sv);
        add(builder, ATT_AMPLITUDE, amplitude);
        add(builder, ATT_OFFSET, offset);
        add(builder, ATT_MIRROR, mirror);
        add(builder, ATT_COLL, coll);
        add(builder, ATT_PROXIMITY, proximity);
        add(builder, ATT_ANIMOFFSET, animOffset);
        add(builder, ATT_DELAY_MS, delay);
        add(builder, ATT_CURVE, curve);
        builder.append("]");
        return builder.toString();
    }
}
