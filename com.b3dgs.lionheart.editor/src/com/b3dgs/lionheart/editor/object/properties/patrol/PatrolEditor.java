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
package com.b3dgs.lionheart.editor.object.properties.patrol;

import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.control.UtilButton;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.editor.widget.TextWidget;
import com.b3dgs.lionheart.editor.object.properties.EditorAbstract;
import com.b3dgs.lionheart.object.feature.PatrolConfig;

/**
 * Patrol editor dialog.
 */
public class PatrolEditor extends EditorAbstract<PatrolConfig>
{
    /** Dialog icon. */
    public static final Image ICON = UtilIcon.get("properties", "patrol.png");
    private static final String VALIDATOR = InputValidator.INTEGER_MATCH;
    private static final String VALIDATOR_DOUBLE = InputValidator.DOUBLE_MATCH;

    private TextWidget sh;
    private TextWidget sv;
    private TextWidget amplitude;
    private TextWidget offset;
    private Button mirror;
    private Button coll;
    private TextWidget proximity;
    private TextWidget sight;
    private TextWidget animOffset;
    private TextWidget delay;
    private Button curve;

    /**
     * Create a patrol editor.
     * 
     * @param parent The parent reference.
     * @param config The patrol configuration reference.
     */
    public PatrolEditor(Composite parent, PatrolConfig config)
    {
        super(parent, Messages.Title, ICON, config);
    }

    @Override
    protected void createFields(Composite parent, PatrolConfig config)
    {
        sh = new TextWidget(parent, UtilConversion.toTitleCase(PatrolConfig.ATT_VX), VALIDATOR_DOUBLE, true);
        sv = new TextWidget(parent, UtilConversion.toTitleCase(PatrolConfig.ATT_VY), VALIDATOR_DOUBLE, true);
        amplitude = new TextWidget(parent, UtilConversion.toTitleCase(PatrolConfig.ATT_AMPLITUDE), VALIDATOR, true);
        offset = new TextWidget(parent, UtilConversion.toTitleCase(PatrolConfig.ATT_OFFSET), VALIDATOR, true);
        mirror = UtilButton.createCheck(UtilConversion.toTitleCase(PatrolConfig.ATT_MIRROR), parent);
        coll = UtilButton.createCheck(UtilConversion.toTitleCase(PatrolConfig.ATT_COLL), parent);
        proximity = new TextWidget(parent, UtilConversion.toTitleCase(PatrolConfig.ATT_PROXIMITY), VALIDATOR, true);
        sight = new TextWidget(parent, UtilConversion.toTitleCase(PatrolConfig.ATT_SIGHT), VALIDATOR, true);
        animOffset = new TextWidget(parent, UtilConversion.toTitleCase(PatrolConfig.ATT_ANIMOFFSET), VALIDATOR, true);
        delay = new TextWidget(parent, UtilConversion.toTitleCase(PatrolConfig.ATT_DELAY_MS), VALIDATOR, true);
        curve = UtilButton.createCheck(UtilConversion.toTitleCase(PatrolConfig.ATT_CURVE), parent);

        mirror.setOrientation(SWT.RIGHT_TO_LEFT);
        coll.setOrientation(SWT.RIGHT_TO_LEFT);
        curve.setOrientation(SWT.RIGHT_TO_LEFT);

        sh.set(Constant.EMPTY_STRING);
        sv.set(Constant.EMPTY_STRING);
        amplitude.set(Constant.EMPTY_STRING);
        offset.set(Constant.EMPTY_STRING);
        mirror.setSelection(true);
        coll.setSelection(false);
        proximity.set(Constant.EMPTY_STRING);
        sight.set(Constant.EMPTY_STRING);
        animOffset.set(Constant.EMPTY_STRING);
        delay.set(Constant.EMPTY_STRING);
        curve.setSelection(false);

        config.getSh().ifPresent(sh::set);
        config.getSv().ifPresent(sv::set);
        config.getAmplitude().ifPresent(amplitude::set);
        config.getOffset().ifPresent(offset::set);
        mirror.setSelection(config.getMirror().orElse(Boolean.TRUE).booleanValue());
        config.getColl().ifPresent(b -> coll.setSelection(b.booleanValue()));
        config.getProximity().ifPresent(proximity::set);
        config.getSight().ifPresent(sight::set);
        config.getAnimOffset().ifPresent(animOffset::set);
        config.getDelay().ifPresent(delay::set);
        config.getCurve().ifPresent(b -> curve.setSelection(b.booleanValue()));
    }

    private static Optional<Boolean> get(Button button)
    {
        if (button.getSelection())
        {
            return Optional.of(Boolean.TRUE);
        }
        return Optional.empty();
    }

    @Override
    protected void onExit()
    {
        output = Optional.of(new PatrolConfig(sh.getValueDouble(),
                                              sv.getValueDouble(),
                                              amplitude.getValue(),
                                              offset.getValue(),
                                              mirror.getSelection() ? Optional.empty() : Optional.of(Boolean.FALSE),
                                              get(coll),
                                              proximity.getValue(),
                                              sight.getValue(),
                                              animOffset.getValue(),
                                              delay.getValue(),
                                              get(curve)));
    }
}
