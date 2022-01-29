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
package com.b3dgs.lionheart.editor.stage;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.editor.utility.control.UtilButton;
import com.b3dgs.lionengine.editor.utility.dialog.UtilDialog;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.editor.widget.BrowseWidget;
import com.b3dgs.lionengine.editor.widget.ComboWidget;
import com.b3dgs.lionengine.editor.widget.TextWidget;
import com.b3dgs.lionheart.StageConfig;
import com.b3dgs.lionheart.editor.Activator;
import com.b3dgs.lionheart.landscape.BackgroundType;
import com.b3dgs.lionheart.landscape.ForegroundConfig;
import com.b3dgs.lionheart.landscape.ForegroundType;

/**
 * Element properties part.
 */
public class StagePart
{
    /** Id. */
    public static final String ID = Activator.PLUGIN_ID + ".part.stage";

    private BrowseWidget pic;
    private BrowseWidget text;
    private BrowseWidget music;
    private BrowseWidget map;
    private BrowseWidget raster;
    private ComboWidget<BackgroundType> background;
    private ComboWidget<ForegroundType> foreground;
    private TextWidget depth;
    private TextWidget offset;
    private TextWidget speed;
    private TextWidget raise;
    private Button effect;

    /**
     * Create part.
     */
    public StagePart()
    {
        super();
    }

    /**
     * Create the composite.
     * 
     * @param parent The parent reference.
     * @param menuService The menu service reference.
     */
    @PostConstruct
    public void createComposite(Composite parent, EMenuService menuService)
    {
        final Composite naration = new Composite(parent, SWT.NONE);
        naration.setLayout(new GridLayout(2, true));
        naration.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        pic = new BrowseWidget(naration, Messages.Picture, UtilDialog.getImageFilter(), true);
        text = new BrowseWidget(naration, Messages.Text, new String[]
        {
            "txt"
        }, true);

        final Composite data = new Composite(parent, SWT.NONE);
        data.setLayout(new GridLayout(2, true));
        data.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        music = new BrowseWidget(data, Messages.Music, new String[]
        {
            "sc68"
        }, true);
        map = new BrowseWidget(data, Messages.Map, UtilDialog.getImageFilter(), true);

        final Composite back = new Composite(parent, SWT.NONE);
        back.setLayout(new GridLayout(2, false));
        back.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        raster = new BrowseWidget(back, Messages.Raster);
        background = new ComboWidget<>(back, Messages.Background, false, BackgroundType.values());

        final Composite front = new Composite(parent, SWT.NONE);
        front.setLayout(new GridLayout(6, false));
        front.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        foreground = new ComboWidget<>(front, Messages.Foreground, false, ForegroundType.values());
        depth = new TextWidget(front, Messages.Depth, InputValidator.INTEGER_MATCH);
        offset = new TextWidget(front, Messages.Offset, InputValidator.INTEGER_MATCH);
        speed = new TextWidget(front, Messages.Speed, InputValidator.DOUBLE_MATCH);
        raise = new TextWidget(front, Messages.Raise, InputValidator.INTEGER_MATCH);
        effect = UtilButton.createCheck(Messages.Effect, front);
    }

    /**
     * Load stage data.
     * 
     * @param stage The stage to load.
     */
    public void load(StageConfig stage)
    {
        pic.setLocation(null);
        text.setLocation(null);
        raster.setLocation(null);
        depth.set(Constant.EMPTY_STRING);
        offset.set(Constant.EMPTY_STRING);
        speed.set(Constant.EMPTY_STRING);

        stage.getPic().ifPresent(p -> pic.setLocation(p.getPath()));
        stage.getText().ifPresent(t -> text.setLocation(t));
        music.setLocation(stage.getMusic().getPath());
        map.setLocation(stage.getMapFile().getPath());
        stage.getRasterFolder().ifPresent(r -> raster.setLocation(r));
        background.setValue(stage.getBackground());

        final ForegroundConfig foregroundConfig = stage.getForeground();
        foreground.setValue(foregroundConfig.getType());
        foregroundConfig.getWaterDepth().ifPresent(depth::set);
        foregroundConfig.getWaterOffset().ifPresent(offset::set);
        foregroundConfig.getWaterSpeed().ifPresent(speed::set);
        raise.set(foregroundConfig.getWaterRaise());
        effect.setSelection(foregroundConfig.getWaterEffect());
    }

    /**
     * Save stage data.
     * 
     * @param root The root reference.
     */
    public void save(Xml root)
    {
        if (pic.getMedia() != null)
        {
            root.writeString(StageConfig.ATT_STAGE_PIC, pic.getMedia().getPath());
        }
        if (text.getMedia() != null)
        {
            root.writeString(StageConfig.ATT_STAGE_TEXT, text.getMedia().getPath());
        }
        if (music.getMedia() != null)
        {
            root.createChild(StageConfig.NODE_MUSIC).writeString(StageConfig.ATT_FILE, music.getMedia().getPath());
        }
        if (map.getMedia() != null)
        {
            root.createChild(StageConfig.NODE_MAP).writeString(StageConfig.ATT_FILE, map.getMedia().getPath());
        }
        if (raster.getMedia() != null)
        {
            root.createChild(StageConfig.NODE_RASTER)
                .writeString(StageConfig.ATT_RASTER_FOLDER, raster.getMedia().getPath());
        }
        root.createChild(StageConfig.NODE_BACKGROUND).writeEnum(StageConfig.ATT_BACKGROUND_TYPE, background.getValue());

        final Xml foregroundNode = root.createChild(ForegroundConfig.NODE_FOREGROUND);
        foregroundNode.writeEnum(ForegroundConfig.ATT_FOREGROUND_TYPE, foreground.getValue());
        depth.getValue().ifPresent(v ->
        {
            if (v != 0)
            {
                foregroundNode.writeInteger(ForegroundConfig.ATT_WATER_DEPTH, v);
            }
        });
        offset.getValue().ifPresent(v ->
        {
            if (v != 0)
            {
                foregroundNode.writeInteger(ForegroundConfig.ATT_WATER_OFFSET, v);
            }
        });
        speed.getValueDouble().ifPresent(v ->
        {
            if (Double.compare(v, 0.0) != 0)
            {
                foregroundNode.writeDouble(ForegroundConfig.ATT_WATER_SPEED, v);
            }
        });
        raise.getValue().ifPresent(v ->
        {
            if (v != 0)
            {
                foregroundNode.writeInteger(ForegroundConfig.ATT_WATER_RAISE, v);
            }
        });
        if (!effect.getSelection())
        {
            foregroundNode.writeBoolean(ForegroundConfig.ATT_WATER_EFFECT, effect.getSelection());
        }
    }
}
