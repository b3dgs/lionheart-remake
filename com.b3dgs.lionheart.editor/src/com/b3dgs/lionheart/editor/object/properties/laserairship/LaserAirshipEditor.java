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
package com.b3dgs.lionheart.editor.object.properties.laserairship;

import java.util.Optional;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.editor.widget.TextWidget;
import com.b3dgs.lionheart.editor.object.properties.EditorAbstract;
import com.b3dgs.lionheart.object.feature.LaserAirshipConfig;

/**
 * Editor dialog.
 */
public class LaserAirshipEditor extends EditorAbstract<LaserAirshipConfig>
{
    /** Dialog icon. */
    public static final Image ICON = UtilIcon.get("properties", "laserairship.png");
    private static final String VALIDATOR = InputValidator.INTEGER_POSITIVE_STRICT_MATCH;

    private TextWidget fire;
    private TextWidget stay;

    /**
     * Create editor.
     * 
     * @param parent The parent reference.
     * @param config The configuration reference.
     */
    public LaserAirshipEditor(Composite parent, LaserAirshipConfig config)
    {
        super(parent, Messages.Title, ICON, config);
    }

    @Override
    protected void createFields(Composite parent, LaserAirshipConfig config)
    {
        fire = new TextWidget(parent, UtilConversion.toTitleCase(LaserAirshipConfig.ATT_FIRE_DELAY), VALIDATOR, true);
        stay = new TextWidget(parent, UtilConversion.toTitleCase(LaserAirshipConfig.ATT_STAY_DELAY), VALIDATOR, true);

        fire.set(config.getFireDelay());
        stay.set(config.getStayDelay());
    }

    @Override
    protected void onExit()
    {
        output = Optional.of(new LaserAirshipConfig(fire.getValue().orElse(0), stay.getValue().orElse(0)));
    }
}
