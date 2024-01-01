/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object;

import java.util.Optional;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.geom.Coord;

/**
 * Entity model configuration.
 */
public final class ModelConfig implements XmlSaver
{
    /** Config node name. */
    public static final String NODE_MODEL = "model";
    /** Mirror attribute name. */
    public static final String ATT_MIRROR = "mirror";
    /** Fall attribute name. */
    public static final String ATT_FALL = "fall";
    /** Next attribute name. */
    public static final String ATT_NEXT = "next";
    /** Spawn tile x attribute name. */
    public static final String ATT_SPAWN_TX = "stx";
    /** Spawn tile y attribute name. */
    public static final String ATT_SPAWN_TY = "sty";

    private final Optional<Boolean> mirror;
    private final Optional<Boolean> fall;
    private final Optional<String> next;
    private final Optional<Coord> nextSpawn;

    /**
     * Create blank configuration.
     */
    public ModelConfig()
    {
        super();

        mirror = Optional.empty();
        fall = Optional.empty();
        next = Optional.empty();
        nextSpawn = Optional.empty();
    }

    /**
     * Create configuration.
     * 
     * @param mirror The mirror flag.
     * @param fall The fall flag.
     * @param next The next stage.
     * @param nextSpawn The next spawn coord.
     */
    public ModelConfig(boolean mirror, boolean fall, Optional<String> next, Optional<Coord> nextSpawn)
    {
        super();

        this.mirror = Optional.of(Boolean.valueOf(mirror));
        this.fall = Optional.of(Boolean.valueOf(mirror));
        this.next = next;
        this.nextSpawn = nextSpawn;
    }

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be <code>null</code>).
     */
    public ModelConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        mirror = root.getBooleanOptional(ATT_MIRROR, NODE_MODEL);
        fall = root.getBooleanOptional(ATT_FALL, NODE_MODEL);
        next = root.getStringOptional(ATT_NEXT, NODE_MODEL);
        if (root.hasAttribute(ATT_SPAWN_TX, NODE_MODEL) && root.hasAttribute(ATT_SPAWN_TY, NODE_MODEL))
        {
            nextSpawn = Optional.of(new Coord(root.getDouble(ATT_SPAWN_TX, NODE_MODEL),
                                              root.getDouble(ATT_SPAWN_TY, NODE_MODEL)));
        }
        else
        {
            nextSpawn = Optional.empty();
        }
    }

    /**
     * Get the mirror flag.
     * 
     * @return The mirror flag.
     */
    public Optional<Boolean> getMirror()
    {
        return mirror;
    }

    /**
     * Get the fall flag.
     * 
     * @return The fall flag.
     */
    public Optional<Boolean> getFall()
    {
        return fall;
    }

    /**
     * Get next stage.
     * 
     * @return The next stage.
     */
    public Optional<String> getNext()
    {
        return next;
    }

    /**
     * Get next spawn.
     * 
     * @return The next spawn.
     */
    public Optional<Coord> getNextSpawn()
    {
        return nextSpawn;
    }

    @Override
    public void save(Xml root)
    {
        if (mirror.isPresent() || next.isPresent() || nextSpawn.isPresent())
        {
            final Xml node = root.createChild(NODE_MODEL);
            mirror.ifPresent(m ->
            {
                node.writeBoolean(ATT_MIRROR, m.booleanValue());
            });
            fall.ifPresent(m ->
            {
                node.writeBoolean(ATT_FALL, m.booleanValue());
            });
            next.ifPresent(n -> node.writeString(ATT_NEXT, n));
            nextSpawn.ifPresent(s ->
            {
                node.writeDouble(ATT_SPAWN_TX, s.getX());
                node.writeDouble(ATT_SPAWN_TY, s.getY());
            });
        }
    }

    private static void add(StringBuilder builder, String name, Optional<Boolean> value)
    {
        value.ifPresent(v -> builder.append(name).append(Constant.DOUBLE_DOT).append(v).append(Constant.SPACE));
    }

    private static void addStr(StringBuilder builder, String name, Optional<String> value)
    {
        value.ifPresent(v -> builder.append(name).append(Constant.DOUBLE_DOT).append(v).append(Constant.SPACE));
    }

    private static void add(StringBuilder builder, String name, double value)
    {
        builder.append(name).append(Constant.DOUBLE_DOT).append(value).append(Constant.SPACE);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Model [ ");
        add(builder, ATT_MIRROR, mirror);
        add(builder, ATT_FALL, fall);
        addStr(builder, ATT_NEXT, next);
        nextSpawn.ifPresent(n ->
        {
            add(builder, ATT_SPAWN_TX, n.getX());
            add(builder, ATT_SPAWN_TY, n.getY());
        });
        builder.append("]");
        return builder.toString();
    }
}
