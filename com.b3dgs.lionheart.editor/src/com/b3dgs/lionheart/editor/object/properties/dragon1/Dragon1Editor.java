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
package com.b3dgs.lionheart.editor.object.properties.dragon1;

import java.util.Optional;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.editor.widget.TextWidget;
import com.b3dgs.lionheart.editor.object.properties.EditorAbstract;
import com.b3dgs.lionheart.object.feature.Dragon1Config;

/**
 * Editor dialog.
 */
public class Dragon1Editor extends EditorAbstract<Dragon1Config>
{
    /** Dialog icon. */
    public static final Image ICON = UtilIcon.get("properties", "dragon1.png");
    private static final String VALIDATOR = InputValidator.INTEGER_POSITIVE_STRICT_MATCH;

    private TextWidget firedCount;

    /**
     * Create editor.
     * 
     * @param parent The parent reference.
     * @param config The configuration reference.
     */
    public Dragon1Editor(Composite parent, Dragon1Config config)
    {
        super(parent, Messages.Title, ICON, config);
    }

    @Override
    protected void createFields(Composite parent, Dragon1Config config)
    {
        firedCount = new TextWidget(parent, UtilConversion.toTitleCase(Dragon1Config.ATT_FIRED_COUNT), VALIDATOR, true);

        firedCount.set(config.getFiredCount());
    }

    @Override
    protected void onExit()
    {
        output = Optional.of(new Dragon1Config(firedCount.getValue().orElse(0)));
    }
}
