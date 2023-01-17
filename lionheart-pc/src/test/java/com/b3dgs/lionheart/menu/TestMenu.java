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
package com.b3dgs.lionheart.menu;

import java.util.List;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.TickAction;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.io.DeviceActionModel;
import com.b3dgs.lionengine.io.DevicePush;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.GameConfig;

/**
 * Test menu implementation.
 */
public final class TestMenu extends Menu
{
    private final List<TickAction> actions;
    private int i;

    /**
     * Create the scene.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @param push The device reference.
     * @param actions The actions to add.
     * @throws LionEngineException If invalid argument.
     */
    TestMenu(Context context, DevicePush push, List<TickAction> actions)
    {
        super(context, new GameConfig());

        device.addFire(push, DeviceMapping.UP.getIndex(), new DeviceActionModel(DeviceMapping.UP.getIndex(), push));
        device.addFire(push, DeviceMapping.DOWN.getIndex(), new DeviceActionModel(DeviceMapping.DOWN.getIndex(), push));
        device.addFire(push,
                       DeviceMapping.CTRL_RIGHT.getIndex(),
                       new DeviceActionModel(DeviceMapping.CTRL_RIGHT.getIndex(), push));

        this.actions = actions;
        alphaSpeed = 255;
    }

    @Override
    public void update(double extrp)
    {
        actions.get(i).execute();
        i = UtilMath.clamp(i + 1, 0, actions.size() - 1);

        super.update(extrp);
    }
}
