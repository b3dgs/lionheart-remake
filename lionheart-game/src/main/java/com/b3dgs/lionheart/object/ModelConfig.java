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
package com.b3dgs.lionheart.object;

import java.util.Optional;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;

/**
 * Entity model configuration.
 */
public final class ModelConfig implements XmlSaver
{
    /** Config node name. */
    public static final String NODE_MODEL = "model";
    /** Mirror attribute name. */
    public static final String ATT_MIRROR = "mirror";

    /** Mirror flag. */
    private final Optional<Boolean> mirror;

    /**
     * Create blank configuration.
     */
    public ModelConfig()
    {
        super();

        mirror = Optional.empty();
    }

    /**
     * Create configuration.
     * 
     * @param mirror The mirror flag.
     */
    public ModelConfig(boolean mirror)
    {
        super();

        this.mirror = Optional.of(Boolean.valueOf(mirror));
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

    @Override
    public void save(Xml root)
    {
        mirror.ifPresent(m ->
        {
            final Xml node = root.createChild(NODE_MODEL);
            node.writeBoolean(ATT_MIRROR, m.booleanValue());
        });
    }

    private static void add(StringBuilder builder, String name, Optional<Boolean> value)
    {
        value.ifPresent(v -> builder.append(name).append(Constant.DOUBLE_DOT).append(v).append(Constant.SPACE));
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Model [ ");
        add(builder, ATT_MIRROR, mirror);
        builder.append("]");
        return builder.toString();
    }
}
