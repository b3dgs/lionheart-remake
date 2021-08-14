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
package com.b3dgs.lionheart.editor.object.properties.rotating;

import java.util.Optional;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.control.UtilButton;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.editor.widget.TextWidget;
import com.b3dgs.lionheart.editor.object.properties.EditorAbstract;
import com.b3dgs.lionheart.object.feature.RotatingConfig;

/**
 * Editor dialog.
 */
public class RotatingEditor extends EditorAbstract<RotatingConfig>
{
    /** Dialog icon. */
    public static final Image ICON = UtilIcon.get("dialog", "patrol-edit.png");
    private static final String VALIDATOR = InputValidator.INTEGER_POSITIVE_STRICT_MATCH;
    private static final String VALIDATOR_DOUBLE = InputValidator.DOUBLE_MATCH;

    private final RotatingConfig config;

    private TextWidget length;
    private TextWidget speed;
    private TextWidget offset;
    private TextWidget amplitude;
    private Button controlled;
    private TextWidget back;

    /**
     * Create editor.
     * 
     * @param parent The parent reference.
     * @param config The configuration reference.
     */
    public RotatingEditor(Composite parent, RotatingConfig config)
    {
        super(parent, Messages.Title, ICON);

        this.config = config;
    }

    @Override
    protected void createFields(Composite parent)
    {
        length = new TextWidget(parent, UtilConversion.toTitleCase(RotatingConfig.ATT_LENGTH), VALIDATOR, true);
        speed = new TextWidget(parent, UtilConversion.toTitleCase(RotatingConfig.ATT_SPEED), VALIDATOR, true);
        offset = new TextWidget(parent, UtilConversion.toTitleCase(RotatingConfig.ATT_OFFSET), VALIDATOR_DOUBLE, true);
        amplitude = new TextWidget(parent,
                                   UtilConversion.toTitleCase(RotatingConfig.ATT_AMPLITUDE),
                                   VALIDATOR_DOUBLE,
                                   true);
        controlled = UtilButton.createCheck(UtilConversion.toTitleCase(RotatingConfig.ATT_CONTROLLED), parent);
        back = new TextWidget(parent, UtilConversion.toTitleCase(RotatingConfig.ATT_BACK), VALIDATOR_DOUBLE, true);

        length.set(config.getLength());
        speed.set(config.getSpeed());
        offset.set(config.getOffset());
        amplitude.set(config.getAmplitude());
        controlled.setSelection(config.isControlled());
        back.set(config.getBack());
    }

    @Override
    protected void onExit()
    {
        output = Optional.of(new RotatingConfig("",
                                                "",
                                                length.getValue().orElse(0),
                                                speed.getValueDouble().orElse(0.0),
                                                offset.getValue().orElse(0),
                                                amplitude.getValue().orElse(0),
                                                controlled.getSelection(),
                                                back.getValue().orElse(0)));
    }
}
