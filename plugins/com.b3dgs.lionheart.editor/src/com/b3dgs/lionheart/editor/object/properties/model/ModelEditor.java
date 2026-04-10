/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.editor.object.properties.model;

import java.util.Optional;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.control.UtilButton;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.editor.widget.TextWidget;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionheart.editor.object.properties.EditorAbstract;
import com.b3dgs.lionheart.object.ModelConfig;

/**
 * Editor dialog.
 */
public class ModelEditor extends EditorAbstract<ModelConfig>
{
    /** Dialog icon. */
    public static final Image ICON = UtilIcon.get("properties", "model.png");

    private static final String PATH_MATCH = "[a-zA-z0-9-/\\.]+";
    private static final String VALIDATOR_DOUBLE = InputValidator.DOUBLE_MATCH;

    private Button mirror;
    private Button fall;
    private TextWidget next;
    private TextWidget stx;
    private TextWidget sty;

    /**
     * Create editor.
     * 
     * @param parent The parent reference.
     * @param config The configuration reference.
     */
    public ModelEditor(Composite parent, ModelConfig config)
    {
        super(parent, Messages.Title, ICON, config);
    }

    @Override
    protected void createFields(Composite parent, ModelConfig config)
    {
        mirror = UtilButton.createCheck(UtilConversion.toTitleCase(ModelConfig.ATT_MIRROR), parent);
        fall = UtilButton.createCheck(UtilConversion.toTitleCase(ModelConfig.ATT_FALL), parent);
        next = new TextWidget(parent, UtilConversion.toTitleCase(ModelConfig.ATT_NEXT), PATH_MATCH, true, true);
        stx = new TextWidget(parent, UtilConversion.toTitleCase(ModelConfig.ATT_SPAWN_TX), VALIDATOR_DOUBLE, true);
        sty = new TextWidget(parent, UtilConversion.toTitleCase(ModelConfig.ATT_SPAWN_TY), VALIDATOR_DOUBLE, true);

        mirror.setSelection(config.getMirror().orElse(Boolean.FALSE).booleanValue());
        fall.setSelection(config.getFall().orElse(Boolean.TRUE).booleanValue());
        config.getNext().ifPresent(next::set);
        config.getNextSpawn().ifPresent(c ->
        {
            stx.set(c.getX());
            sty.set(c.getY());
        });
    }

    @Override
    protected void onExit()
    {
        final Optional<Coord> spawn;
        if (stx.getValueDouble().isPresent() && sty.getValueDouble().isPresent())
        {
            spawn = Optional.of(new Coord(stx.getValueDouble().getAsDouble(), sty.getValueDouble().getAsDouble()));
        }
        else
        {
            spawn = Optional.empty();
        }
        output = Optional.of(new ModelConfig(mirror.getSelection(), fall.getSelection(), next.getValueText(), spawn));
    }
}
