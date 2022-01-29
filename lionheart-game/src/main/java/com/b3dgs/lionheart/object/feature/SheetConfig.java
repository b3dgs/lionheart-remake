/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * Sheet configuration.
 */
public final class SheetConfig implements XmlSaver
{
    /** Sheet node name. */
    public static final String NODE_SHEET = "sheet";
    /** Hide attribute name. */
    public static final String ATT_HIDE = "hide";

    /** Hide flag. */
    private final boolean hide;

    /**
     * Create blank configuration.
     */
    public SheetConfig()
    {
        this(false);
    }

    /**
     * Create configuration.
     * 
     * @param hide The hide flag.
     */
    public SheetConfig(boolean hide)
    {
        super();

        this.hide = hide;
    }

    /**
     * Create config.
     * 
     * @param root The root configuration (must not be null).
     */
    public SheetConfig(XmlReader root)
    {
        super();

        Check.notNull(root);

        hide = root.getBoolean(false, ATT_HIDE, NODE_SHEET);
    }

    /**
     * Get the hide flag.
     * 
     * @return The hide flag.
     */
    public boolean getHide()
    {
        return hide;
    }

    @Override
    public void save(Xml root)
    {
        if (hide)
        {
            final Xml node = root.createChild(NODE_SHEET);
            node.writeBoolean(ATT_HIDE, hide);
        }
    }

    private static void add(StringBuilder builder, String name, boolean value)
    {
        builder.append(name).append(Constant.DOUBLE_DOT).append(value).append(Constant.SPACE);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Sheet [ ");
        add(builder, ATT_HIDE, hide);
        builder.append("]");
        return builder.toString();
    }
}
