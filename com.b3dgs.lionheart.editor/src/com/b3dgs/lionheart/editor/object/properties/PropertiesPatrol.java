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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.editor.properties.PropertiesProviderObject;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionheart.object.feature.PatrolConfig;

/**
 * Element properties part.
 */
public class PropertiesPatrol implements PropertiesProviderObject
{
    /** Animations icon. */
    private static final Image ICON_PATROL = UtilIcon.get("properties", "patrol.png");

    /**
     * Create the animations attribute.
     * 
     * @param properties The properties tree reference.
     */
    public static void createAttributeAnimations(Tree properties)
    {
        final TreeItem animationsItem = new TreeItem(properties, SWT.NONE);
        animationsItem.setText(Messages.Patrol);
        animationsItem.setData(PatrolConfig.NODE_PATROL);
        animationsItem.setImage(ICON_PATROL);
    }

    /**
     * Create properties.
     */
    public PropertiesPatrol()
    {
        super();
    }

    /*
     * PropertiesProvider
     */

    @Override
    public void setInput(Tree properties, Configurer configurer)
    {
        final Xml root = configurer.getRoot();
        if (root.hasChild(PatrolConfig.NODE_PATROL))
        {
            createAttributeAnimations(properties);
        }
    }

    @Override
    public boolean updateProperties(TreeItem item, Configurer configurer)
    {
        final Object data = item.getData();
        if (PatrolConfig.NODE_PATROL.equals(data))
        {
            // TODO final AnimationEditor animationEditor = new AnimationEditor(item.getParent(), configurer);
            // animationEditor.create();
            // animationEditor.openAndWait();
        }
        return false;
    }
}
