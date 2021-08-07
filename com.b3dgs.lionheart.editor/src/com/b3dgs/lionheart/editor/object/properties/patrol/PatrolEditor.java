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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.editor.dialog.EditorAbstract;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.control.UtilButton;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.editor.widget.TextWidget;
import com.b3dgs.lionheart.object.feature.PatrolConfig;

/**
 * Patrol editor dialog.
 */
public class PatrolEditor extends EditorAbstract
{
    /** Dialog icon. */
    public static final Image ICON = UtilIcon.get("dialog", "patrol-edit.png");
    private static final String VALIDATOR = InputValidator.INTEGER_POSITIVE_STRICT_MATCH;
    private static final String VALIDATOR_DOUBLE = InputValidator.DOUBLE_MATCH;

    private final PatrolConfig config;

    private TextWidget sh;
    private TextWidget sv;
    private TextWidget amplitude;
    private TextWidget offset;
    private Button mirror;
    private Button coll;
    private TextWidget proximity;
    private TextWidget animOffset;
    private TextWidget delay;
    private Button curve;

    private Optional<PatrolConfig> output = Optional.empty();

    /**
     * Create a patrol editor.
     * 
     * @param parent The parent reference.
     * @param config The patrol configuration reference.
     */
    public PatrolEditor(Composite parent, PatrolConfig config)
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

        sh = new TextWidget(content, UtilConversion.toTitleCase(PatrolConfig.ATT_VX), VALIDATOR_DOUBLE, true);
        sv = new TextWidget(content, UtilConversion.toTitleCase(PatrolConfig.ATT_VY), VALIDATOR_DOUBLE, true);
        amplitude = new TextWidget(content, UtilConversion.toTitleCase(PatrolConfig.ATT_AMPLITUDE), VALIDATOR, true);
        offset = new TextWidget(content, UtilConversion.toTitleCase(PatrolConfig.ATT_OFFSET), VALIDATOR, true);
        mirror = UtilButton.createCheck(UtilConversion.toTitleCase(PatrolConfig.ATT_MIRROR), content);
        coll = UtilButton.createCheck(UtilConversion.toTitleCase(PatrolConfig.ATT_COLL), content);
        proximity = new TextWidget(content, UtilConversion.toTitleCase(PatrolConfig.ATT_PROXIMITY), VALIDATOR, true);
        animOffset = new TextWidget(content, UtilConversion.toTitleCase(PatrolConfig.ATT_ANIMOFFSET), VALIDATOR, true);
        delay = new TextWidget(content, UtilConversion.toTitleCase(PatrolConfig.ATT_DELAY), VALIDATOR, true);
        curve = UtilButton.createCheck(UtilConversion.toTitleCase(PatrolConfig.ATT_CURVE), content);

        mirror.setOrientation(SWT.RIGHT_TO_LEFT);
        coll.setOrientation(SWT.RIGHT_TO_LEFT);
        curve.setOrientation(SWT.RIGHT_TO_LEFT);

        config.getSh().ifPresent(sh::set);
        config.getSv().ifPresent(sv::set);
        config.getAmplitude().ifPresent(amplitude::set);
        config.getOffset().ifPresent(offset::set);
        config.getMirror().ifPresent(b -> mirror.setSelection(b.booleanValue()));
        config.getColl().ifPresent(b -> coll.setSelection(b.booleanValue()));
        config.getProximity().ifPresent(proximity::set);
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
                                              get(mirror),
                                              get(coll),
                                              proximity.getValue(),
                                              animOffset.getValue(),
                                              delay.getValue(),
                                              get(curve)));
    }

    /**
     * Get output.
     * 
     * @return The output.
     */
    public Optional<PatrolConfig> getOutput()
    {
        return output;
    }
}
