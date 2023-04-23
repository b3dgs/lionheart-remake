/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.editor.object.properties.geyzer;

import java.util.Optional;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.editor.widget.TextWidget;
import com.b3dgs.lionheart.editor.object.properties.EditorAbstract;
import com.b3dgs.lionheart.object.feature.GeyzerConfig;

/**
 * Editor dialog.
 */
public class GeyzerEditor extends EditorAbstract<GeyzerConfig>
{
    /** Dialog icon. */
    public static final Image ICON = UtilIcon.get("properties", "geyzer.png");
    private static final String VALIDATOR = InputValidator.INTEGER_POSITIVE_STRICT_MATCH;

    private TextWidget first;
    private TextWidget start;
    private TextWidget down;
    private TextWidget height;

    /**
     * Create editor.
     * 
     * @param parent The parent reference.
     * @param config The configuration reference.
     */
    public GeyzerEditor(Composite parent, GeyzerConfig config)
    {
        super(parent, Messages.Title, ICON, config);
    }

    @Override
    protected void createFields(Composite parent, GeyzerConfig config)
    {
        first = new TextWidget(parent, UtilConversion.toTitleCase(GeyzerConfig.ATT_FIRST_DELAY_MS), VALIDATOR, true);
        start = new TextWidget(parent, UtilConversion.toTitleCase(GeyzerConfig.ATT_START_DELAY_MS), VALIDATOR, true);
        down = new TextWidget(parent, UtilConversion.toTitleCase(GeyzerConfig.ATT_DOWN_DELAY_MS), VALIDATOR, true);
        height = new TextWidget(parent, UtilConversion.toTitleCase(GeyzerConfig.ATT_HEIGHT), VALIDATOR, true);

        first.set(config.getDelayFirst());
        start.set(config.getDelayStart());
        down.set(config.getDelayDown());
        height.set(config.getHeight());
    }

    @Override
    protected void onExit()
    {
        output = Optional.of(new GeyzerConfig(first.getValue().orElse(0),
                                              start.getValue().orElse(0),
                                              down.getValue().orElse(0),
                                              height.getValue().orElse(0)));
    }
}
