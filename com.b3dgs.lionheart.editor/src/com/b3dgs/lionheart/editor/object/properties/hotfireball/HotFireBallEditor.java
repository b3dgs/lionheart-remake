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
package com.b3dgs.lionheart.editor.object.properties.hotfireball;

import java.util.Optional;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.editor.widget.TextWidget;
import com.b3dgs.lionheart.editor.object.properties.EditorAbstract;
import com.b3dgs.lionheart.object.feature.HotFireBallConfig;

/**
 * Editor dialog.
 */
public class HotFireBallEditor extends EditorAbstract<HotFireBallConfig>
{
    /** Dialog icon. */
    public static final Image ICON = UtilIcon.get("properties", "hotfireball.png");
    private static final String VALIDATOR = InputValidator.INTEGER_POSITIVE_STRICT_MATCH;
    private static final String VALIDATOR_DOUBLE = InputValidator.DOUBLE_MATCH;

    private TextWidget delay;
    private TextWidget count;
    private TextWidget level;
    private TextWidget vx;
    private TextWidget vy;

    /**
     * Create editor.
     * 
     * @param parent The parent reference.
     * @param config The configuration reference.
     */
    public HotFireBallEditor(Composite parent, HotFireBallConfig config)
    {
        super(parent, Messages.Title, ICON, config);
    }

    @Override
    protected void createFields(Composite parent, HotFireBallConfig config)
    {
        delay = new TextWidget(parent, UtilConversion.toTitleCase(HotFireBallConfig.ATT_DELAY_MS), VALIDATOR, true);
        count = new TextWidget(parent, UtilConversion.toTitleCase(HotFireBallConfig.ATT_COUNT), VALIDATOR, true);
        level = new TextWidget(parent, UtilConversion.toTitleCase(HotFireBallConfig.ATT_LEVEL), VALIDATOR, true);
        vx = new TextWidget(parent, UtilConversion.toTitleCase(HotFireBallConfig.ATT_VX), VALIDATOR_DOUBLE, true);
        vy = new TextWidget(parent, UtilConversion.toTitleCase(HotFireBallConfig.ATT_VY), VALIDATOR_DOUBLE, true);

        delay.set(config.getDelay());
        count.set(config.getCount());
        level.set(config.getLevel());
        vx.set(config.getVx());
        vy.set(config.getVy());
    }

    @Override
    protected void onExit()
    {
        output = Optional.of(new HotFireBallConfig(delay.getValue().orElse(0),
                                                   count.getValue().orElse(0),
                                                   level.getValue().orElse(0),
                                                   vx.getValueDouble().orElse(0),
                                                   vy.getValueDouble().orElse(0)));
    }
}
