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
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Spider configuration.
 */
public final class SpiderConfig implements XmlSaver
{
    /** Spider node name. */
    public static final String NODE_SPIDER = "spider";
    /** Follow attribute name. */
    public static final String ATT_FOLLOW = "follow";

    /** Follow flag. */
    private final boolean follow;

    /**
     * Create blank configuration.
     */
    public SpiderConfig()
    {
        this(false);
    }

    /**
     * Create configuration.
     * 
     * @param follow The follow flag.
     */
    public SpiderConfig(boolean follow)
    {
        super();

        this.follow = follow;
    }

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be null).
     */
    public SpiderConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        final XmlReader node = root.getChild(NODE_SPIDER);
        follow = node.getBoolean(true, ATT_FOLLOW);
    }

    /**
     * Get the follow flag.
     * 
     * @return The follow flag.
     */
    public boolean getFollow()
    {
        return follow;
    }

    @Override
    public void save(Xml root)
    {
        final Xml node = root.createChild(NODE_SPIDER);
        node.writeBoolean(ATT_FOLLOW, follow);
    }

    private static void add(StringBuilder builder, String name, boolean value)
    {
        builder.append(name).append(Constant.DOUBLE_DOT).append(value).append(Constant.SPACE);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Spider [");
        add(builder, ATT_FOLLOW, follow);
        builder.append("]");
        return builder.toString();
    }
}
