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
package com.b3dgs.lionheart.editor.checkpoint;

import java.util.Optional;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.editor.widget.TextWidget;
import com.b3dgs.lionheart.Checkpoint;
import com.b3dgs.lionheart.StageConfig;
import com.b3dgs.lionheart.editor.object.properties.EditorAbstract;

/**
 * Patrol editor dialog.
 */
public class CheckpointEditor extends EditorAbstract<Checkpoint>
{
    /** Dialog icon. */
    public static final Image ICON = UtilIcon.get("dialog", "patrol-edit.png");
    private static final String VALIDATOR_DOUBLE = InputValidator.DOUBLE_MATCH;

    private final Checkpoint config;

    private TextWidget tx;
    private TextWidget ty;

    /**
     * Create a patrol editor.
     * 
     * @param parent The parent reference.
     * @param config The patrol configuration reference.
     */
    public CheckpointEditor(Composite parent, Checkpoint config)
    {
        super(parent, Messages.Title, ICON);

        this.config = config;
    }

    @Override
    protected void createFields(Composite parent)
    {
        tx = new TextWidget(parent, UtilConversion.toTitleCase(StageConfig.ATT_CHECKPOINT_TX), VALIDATOR_DOUBLE, true);
        ty = new TextWidget(parent, UtilConversion.toTitleCase(StageConfig.ATT_CHECKPOINT_TY), VALIDATOR_DOUBLE, true);

        tx.set(config.getTx());
        ty.set(config.getTy());
    }

    @Override
    protected void onExit()
    {
        output = Optional.of(new Checkpoint(tx.getValueDouble().orElse(0.0),
                                            ty.getValueDouble().orElse(0.0),
                                            Optional.empty(),
                                            Optional.empty()));
    }
}
