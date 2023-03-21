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
package com.b3dgs.lionheart;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.b3dgs.lionengine.InputDeviceListener;
import com.b3dgs.lionengine.TickAction;
import com.b3dgs.lionengine.io.DevicePush;

/**
 * Device push mock.
 */
public final class DevicePushMock implements DevicePush
{
    /**
     * Add key simulation.
     * 
     * @param actions The actions.
     * @param push The device.
     * @param key The key.
     */
    public static void addSimu(List<TickAction> actions, DevicePushMock push, DeviceMapping key)
    {
        actions.add(() -> push.press(key));
        actions.add(() -> push.release(key));
    }

    private final Set<Integer> press = new HashSet<>();
    private Integer last;

    /**
     * Press index.
     * 
     * @param index The index reference.
     */
    public void press(DeviceMapping index)
    {
        press.clear();
        press.add(index.getIndex());
    }

    /**
     * Release index.
     * 
     * @param index The index reference.
     */
    public void release(DeviceMapping index)
    {
        press.remove(index.getIndex());
    }

    @Override
    public String getName()
    {
        return "mock";
    }

    @Override
    public boolean isPushedOnce(Integer index)
    {
        if (press.contains(index))
        {
            last = index;
            return true;
        }
        return false;
    }

    @Override
    public boolean isPushed(Integer index)
    {
        if (press.contains(index))
        {
            last = index;
            return true;
        }
        return false;
    }

    @Override
    public boolean isPushed()
    {
        return !press.isEmpty();
    }

    @Override
    public Integer getPushed()
    {
        return last;
    }

    @Override
    public void addListener(InputDeviceListener listener)
    {
        // Mock
    }

    @Override
    public void removeListener(InputDeviceListener listener)
    {
        // Mock
    }
}
