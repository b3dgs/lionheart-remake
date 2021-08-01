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
package com.b3dgs.lionheart.editor.object.properties;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.editor.properties.PropertiesModel;
import com.b3dgs.lionheart.object.feature.PatrolConfig;

/**
 * Test the properties node existence.
 */
public final class PropertiesPatrolTester extends PropertyTester
{
    /** Can enable patrol. */
    private static final String PROPERTY_PATROL_ENABLE = "enablePatrol";
    /** Can edit patrol. */
    private static final String PROPERTY_PATROL_EDIT = "editPatrol";
    /** Can disable patrol. */
    private static final String PROPERTY_PATROL_DISABLE = "disablePatrol";

    /**
     * Check result depending of selection.
     * 
     * @param model The properties model.
     * @param data The selection reference.
     * @param property The property to check.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    private static boolean check(PropertiesModel model, Object data, String property)
    {
        final boolean result;
        if (PROPERTY_PATROL_ENABLE.equals(property))
        {
            result = !model.hasProperty(PatrolConfig.NODE_PATROL);
        }
        else if (PatrolConfig.NODE_PATROL.equals(data)
                 && (PROPERTY_PATROL_EDIT.equals(property) || PROPERTY_PATROL_DISABLE.equals(property)))
        {
            result = model.hasProperty(PatrolConfig.NODE_PATROL);
        }
        else
        {
            result = false;
        }
        return result;
    }

    /**
     * Create tester.
     */
    public PropertiesPatrolTester()
    {
        super();
    }

    /*
     * PropertyTester
     */

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        final PropertiesModel model = PropertiesModel.INSTANCE;
        if (!model.isEmpty())
        {
            final Object data = model.getSelectedData();
            return check(model, data, property);
        }
        return false;
    }
}
