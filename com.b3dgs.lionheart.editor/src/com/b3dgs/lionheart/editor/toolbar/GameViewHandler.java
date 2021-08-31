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
package com.b3dgs.lionheart.editor.toolbar;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.updater.WorldZoomUpdater;
import com.b3dgs.lionengine.editor.world.view.WorldPart;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionheart.Constant;

/**
 * Game view handler.
 */
public final class GameViewHandler
{
    /**
     * Create handler.
     */
    public GameViewHandler()
    {
        super();
    }

    /**
     * Execute the handler.
     * 
     * @param partService The part service reference.
     */
    @Execute
    public void execute(EPartService partService)
    {
        final Services services = WorldModel.INSTANCE.getServices();
        final WorldPart part = services.get(WorldPart.class);

        final WorldZoomUpdater zoom = services.get(WorldZoomUpdater.class);
        final int percent = (int) (part.getView().height / (double) Constant.RESOLUTION_GAME.getHeight() * 100);
        zoom.setPercent(percent);

        part.update();

    }
}
