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
package com.b3dgs.lionheart;

import java.util.function.IntSupplier;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.io.DeviceController;

/**
 * Base scene implementation.
 */
public class AppInfo implements Updatable, Renderable
{
    private static final String NAME = Constant.PROGRAM_NAME
                                       + com.b3dgs.lionengine.Constant.SPACE
                                       + Constant.PROGRAM_VERSION;
    private static final String ENGINE = com.b3dgs.lionengine.Constant.ENGINE_NAME
                                         + com.b3dgs.lionengine.Constant.SPACE
                                         + com.b3dgs.lionengine.Constant.ENGINE_VERSION;

    /**
     * Set text data.
     * 
     * @param text The text object.
     * @param value The text value.
     * @param align The align used.
     */
    private static void setText(Text text, String value, Align align)
    {
        text.setAlign(align);
        text.setText(value);
        text.setColor(ColorRgba.GRAY_LIGHT);
    }

    private final Text textName;
    private final Text textFps;
    private final Text textEngine;
    private final DeviceController device;
    private final IntSupplier fps;

    private boolean show;
    private int fpsOld;

    /**
     * Create info.
     * 
     * @param fps The fps supplier.
     * @param services The services reference.
     */
    public AppInfo(IntSupplier fps, Services services)
    {
        super();

        this.fps = fps;

        final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);

        final int size = Math.max(9,
                                  9 * (int) Math.floor(source.getHeight() / (double) Constant.RESOLUTION.getHeight()));
        textName = Graphics.createText(size);
        textFps = Graphics.createText(size);
        textEngine = Graphics.createText(size);

        setText(textEngine, ENGINE, Align.LEFT);
        setText(textFps, String.valueOf(source.getRate()), Align.CENTER);
        setText(textName, NAME, Align.RIGHT);

        onResolutionChanged(source.getWidth(), source.getHeight());

        device = services.get(DeviceController.class);
    }

    /**
     * Called on resolution changed.
     * 
     * @param width The new width.
     * @param height The new height.
     */
    public final void onResolutionChanged(int width, int height)
    {
        textEngine.setLocation(0, height - textEngine.getSize());
        textFps.setLocation(width / 2, height - textFps.getSize());
        textName.setLocation(width, height - textName.getSize());
    }

    @Override
    public void update(double extrp)
    {
        show = device.isFired(DeviceMapping.TAB);

        final int current = fps.getAsInt();
        if (current != fpsOld)
        {
            fpsOld = current;
            textFps.setText(String.valueOf(current));
        }
    }

    @Override
    public void render(Graphic g)
    {
        if (show)
        {
            textEngine.render(g);
            textFps.render(g);
            textName.render(g);
        }
    }
}
