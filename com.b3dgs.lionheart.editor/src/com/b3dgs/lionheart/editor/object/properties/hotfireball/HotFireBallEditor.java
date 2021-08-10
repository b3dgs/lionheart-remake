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
package com.b3dgs.lionheart.editor.object.properties.hotfireball;

import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.editor.dialog.EditorAbstract;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.editor.widget.TextWidget;
import com.b3dgs.lionheart.object.feature.HotFireBallConfig;

/**
 * Editor dialog.
 */
public class HotFireBallEditor extends EditorAbstract
{
    /** Dialog icon. */
    public static final Image ICON = UtilIcon.get("dialog", "patrol-edit.png");
    private static final String VALIDATOR = InputValidator.INTEGER_POSITIVE_STRICT_MATCH;

    private final HotFireBallConfig config;

    private TextWidget delay;
    private TextWidget count;
    private TextWidget level;
    private TextWidget vx;
    private TextWidget vy;

    private Optional<HotFireBallConfig> output = Optional.empty();

    /**
     * Create editor.
     * 
     * @param parent The parent reference.
     * @param config The configuration reference.
     */
    public HotFireBallEditor(Composite parent, HotFireBallConfig config)
    {
        super(parent, Messages.Title, ICON);

        this.config = config;
    }

    @Override
    protected void createContent(Composite parent)
    {
        final Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        delay = new TextWidget(content, UtilConversion.toTitleCase(HotFireBallConfig.ATT_DELAY), VALIDATOR, true);
        count = new TextWidget(content, UtilConversion.toTitleCase(HotFireBallConfig.ATT_COUNT), VALIDATOR, true);
        level = new TextWidget(content, UtilConversion.toTitleCase(HotFireBallConfig.ATT_LEVEL), VALIDATOR, true);
        vx = new TextWidget(content, UtilConversion.toTitleCase(HotFireBallConfig.ATT_VX), VALIDATOR, true);
        vy = new TextWidget(content, UtilConversion.toTitleCase(HotFireBallConfig.ATT_VY), VALIDATOR, true);

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
                                                   vx.getValue().orElse(0),
                                                   vy.getValue().orElse(0)));
    }

    /**
     * Get output.
     * 
     * @return The output.
     */
    public Optional<HotFireBallConfig> getOutput()
    {
        return output;
    }
}
