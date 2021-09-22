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
package com.b3dgs.lionheart.editor.object.properties.shooter;

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
import com.b3dgs.lionheart.object.feature.ShooterConfig;

/**
 * Editor dialog.
 */
public class ShooterEditor extends EditorAbstract<ShooterConfig>
{
    /** Dialog icon. */
    public static final Image ICON = UtilIcon.get("properties", "shooter.png");
    private static final String VALIDATOR = InputValidator.INTEGER_POSITIVE_STRICT_MATCH;
    private static final String VALIDATOR_DOUBLE = InputValidator.DOUBLE_MATCH;

    private TextWidget fireDelay;
    private TextWidget firedDelay;
    private TextWidget anim;
    private TextWidget svx;
    private TextWidget svy;
    private TextWidget dvx;
    private TextWidget dvy;
    private Button track;

    /**
     * Create editor.
     * 
     * @param parent The parent reference.
     * @param config The configuration reference.
     */
    public ShooterEditor(Composite parent, ShooterConfig config)
    {
        super(parent, Messages.Title, ICON, config);
    }

    @Override
    protected void createFields(Composite parent, ShooterConfig config)
    {
        fireDelay = new TextWidget(parent,
                                   UtilConversion.toTitleCase(ShooterConfig.ATT_FIRE_DELAY_MS),
                                   VALIDATOR,
                                   true);
        firedDelay = new TextWidget(parent,
                                    UtilConversion.toTitleCase(ShooterConfig.ATT_FIRED_DELAY_MS),
                                    VALIDATOR,
                                    true);
        anim = new TextWidget(parent, UtilConversion.toTitleCase(ShooterConfig.ATT_ANIM), VALIDATOR, true);
        svx = new TextWidget(parent, UtilConversion.toTitleCase(ShooterConfig.ATT_SVX), VALIDATOR_DOUBLE, true);
        svy = new TextWidget(parent, UtilConversion.toTitleCase(ShooterConfig.ATT_SVY), VALIDATOR_DOUBLE, true);
        dvx = new TextWidget(parent, UtilConversion.toTitleCase(ShooterConfig.ATT_DVX), VALIDATOR_DOUBLE, true);
        dvy = new TextWidget(parent, UtilConversion.toTitleCase(ShooterConfig.ATT_DVY), VALIDATOR_DOUBLE, true);
        track = UtilButton.createCheck(UtilConversion.toTitleCase(ShooterConfig.ATT_TRACK), parent);

        fireDelay.set(config.getFireDelay());
        firedDelay.set(config.getFiredDelay());
        anim.set(config.getAnim());
        svx.set(config.getSvx());
        svy.set(config.getSvy());
        config.getDvx().ifPresent(v -> dvx.set(v));
        config.getDvy().ifPresent(v -> dvy.set(v));
        track.setSelection(config.getTrack());
    }

    @Override
    protected void onExit()
    {
        output = Optional.of(new ShooterConfig(fireDelay.getValue().orElse(0),
                                               firedDelay.getValue().orElse(0),
                                               anim.getValue().orElse(0),
                                               svx.getValueDouble().orElse(0.0),
                                               svy.getValueDouble().orElse(0.0),
                                               dvx.getValueDouble(),
                                               dvy.getValueDouble(),
                                               track.getSelection()));
    }
}
