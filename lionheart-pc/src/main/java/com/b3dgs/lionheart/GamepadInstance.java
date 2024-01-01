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
package com.b3dgs.lionheart;

import java.util.Set;
import java.util.function.Supplier;

import com.b3dgs.lionengine.InputDeviceListener;
import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.io.DevicePush;

/**
 * Gamepad device.
 */
public class GamepadInstance implements DevicePush
{
    /** Listener. */
    private final Supplier<ListenableModel<InputDeviceListener>> listeners;
    /** Press flags. */
    private final Supplier<Set<Integer>> press;
    /** Pressed flags. */
    private final Supplier<Set<Integer>> pressed;
    /** Last press number. */
    private final Supplier<Integer> last;
    /** Updater. */
    private final Updatable updater;

    /**
     * Create.
     * 
     * @param listeners The listener.
     * @param press The press.
     * @param pressed The pressed.
     * @param last The last.
     * @param updater The updater.
     */
    public GamepadInstance(Supplier<ListenableModel<InputDeviceListener>> listeners,
                           Supplier<Set<Integer>> press,
                           Supplier<Set<Integer>> pressed,
                           Supplier<Integer> last,
                           Updatable updater)
    {
        super();

        this.listeners = listeners;
        this.press = press;
        this.pressed = pressed;
        this.last = last;
        this.updater = updater;
    }

    @Override
    public void addListener(InputDeviceListener listener)
    {
        final Listenable<InputDeviceListener> l = listeners.get();
        if (l != null)
        {
            l.addListener(listener);
        }
    }

    @Override
    public void removeListener(InputDeviceListener listener)
    {
        final Listenable<InputDeviceListener> l = listeners.get();
        if (l != null)
        {
            l.removeListener(listener);
        }
    }

    @Override
    public void update(double extrp)
    {
        updater.update(extrp);
    }

    @Override
    public boolean isPushed()
    {
        final Set<Integer> p = press.get();
        if (p != null)
        {
            return !p.isEmpty();
        }
        return false;
    }

    @Override
    public Integer getPushed()
    {
        return last.get();
    }

    @Override
    public boolean isPushed(Integer index)
    {
        final Set<Integer> p = press.get();
        if (p != null)
        {
            return p.contains(index);
        }
        return false;
    }

    @Override
    public boolean isPushedOnce(Integer index)
    {
        final Set<Integer> p = press.get();
        final Set<Integer> ps = pressed.get();
        if (p != null && ps != null && p.contains(index) && !ps.contains(index))
        {
            ps.add(index);
            return true;
        }
        return false;
    }

    @Override
    public String getName()
    {
        return GamepadInstance.class.getSimpleName();
    }
}
