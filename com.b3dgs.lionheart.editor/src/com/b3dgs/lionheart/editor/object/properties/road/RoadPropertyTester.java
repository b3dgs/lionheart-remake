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
package com.b3dgs.lionheart.editor.object.properties.road;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.editor.utility.UtilPart;

/**
 * Test if configuration is defined.
 */
public final class RoadPropertyTester extends PropertyTester
{
    /** Can enable property. */
    private static final String PROPERTY_ENABLE = "enable";
    /** Can disable property. */
    private static final String PROPERTY_DISABLE = "disable";

    /**
     * Create tester.
     */
    public RoadPropertyTester()
    {
        super();
    }

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        final RoadPart part = UtilPart.getPart(RoadPart.ID, RoadPart.class);
        final boolean result;
        if (PROPERTY_DISABLE.equals(property))
        {
            result = part.exists();
        }
        else if (PROPERTY_ENABLE.equals(property))
        {
            result = !part.exists();
        }
        else
        {
            result = false;
        }
        return result;
    }
}
