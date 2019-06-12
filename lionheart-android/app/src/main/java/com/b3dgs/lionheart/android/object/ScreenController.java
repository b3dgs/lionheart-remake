/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
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
package com.b3dgs.lionheart.android.object;

import com.b3dgs.lionengine.android.Mouse;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.io.InputDeviceControl;

/**
 * Keyboard input controller.
 */
public final class ScreenController implements InputDeviceControl
{
    private final Mouse mouse;
    private final Camera camera;

    /**
     * Create controller.
     * 
     * @param services The services reference.
     */
    public ScreenController(Services services)
    {
        super();

        mouse = services.get(Mouse.class);
        camera = services.get(Camera.class);
    }

    @Override
    public void setHorizontalControlPositive(Integer code)
    {
        // Nothing to do
    }

    @Override
    public void setHorizontalControlNegative(Integer code)
    {
        // Nothing to do
    }

    @Override
    public void setVerticalControlPositive(Integer code)
    {
        // Nothing to do
    }

    @Override
    public void setVerticalControlNegative(Integer code)
    {
        // Nothing to do
    }

    @Override
    public Integer getHorizontalControlPositive()
    {
        return null;
    }

    @Override
    public Integer getHorizontalControlNegative()
    {
        return null;
    }

    @Override
    public Integer getVerticalControlPositive()
    {
        return null;
    }

    @Override
    public Integer getVerticalControlNegative()
    {
        return null;
    }

    @Override
    public double getHorizontalDirection()
    {
        if (mouse.getClick() > 0)
        {
            if (mouse.getOnWindowX() < camera.getWidth() / 2 - 32)
            {
                return -1;
            }
            else if (mouse.getOnWindowX() > camera.getWidth() / 2 + 32)
            {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public double getVerticalDirection()
    {
        if (mouse.getClick() > 0)
        {
            if (mouse.getOnWindowY() < camera.getHeight() / 2 - 16)
            {
                return 1;
            }
            else if (mouse.getOnWindowY() > camera.getHeight() / 2 + 16)
            {
                return -1;
            }
        }
        return 0;
    }

    @Override
    public void setFireButton(Integer code)
    {
        // Nothing to do
    }

    @Override
    public boolean isUpButtonOnce()
    {
        return getVerticalDirection() > 0;
    }

    @Override
    public boolean isDownButtonOnce()
    {
        return getVerticalDirection() < 0;
    }

    @Override
    public boolean isLeftButtonOnce()
    {
        return getHorizontalDirection() < 0;
    }

    @Override
    public boolean isRightButtonOnce()
    {
        return getHorizontalDirection() > 0;
    }

    @Override
    public boolean isFireButton()
    {
        return false;
    }

    @Override
    public boolean isFireButtonOnce()
    {
        return false;
    }
}
