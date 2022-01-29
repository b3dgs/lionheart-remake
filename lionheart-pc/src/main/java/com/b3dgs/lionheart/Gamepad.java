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

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.libsdl.SDL_Error;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.UtilStream;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.io.DevicePush;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import uk.co.electronstudio.sdl2gdx.SDL2ControllerManager;

/**
 * Gamepad device.
 */
public class Gamepad implements DevicePush
{
    /**
     * Get instance or <code>null</code>.
     * 
     * @return The available instance.
     */
    private static SDL2ControllerManager getFailsafe()
    {
        try
        {
            return new SDL2ControllerManager();
        }
        catch (final Exception | Error exception) // CHECKSTYLE IGNORE LINE: IllegalCatch|TrailingComment
        {
            Verbose.exception(exception);
            return null;
        }
    }

    /**
     * Load library.
     */
    static
    {
        try
        {
            if (System.getProperty("os.name", Constant.EMPTY_STRING).toLowerCase(Locale.ENGLISH).contains("win"))
            {
                final String arch;
                if (com.sun.jna.Native.POINTER_SIZE == 4)
                {
                    arch = "32";
                }
                else
                {
                    arch = "64";
                }
                System.load(UtilStream.getCopy(Medias.create(arch, "libusb-1.0.dll")).getAbsolutePath());
            }
        }
        catch (final Throwable throwable) // CHECKSTYLE IGNORE LINE: IllegalCatch|TrailingComment
        {
            Verbose.exception(throwable);
        }
    }

    /** Current manager, or <code>null</code>. */
    private final SDL2ControllerManager manager = getFailsafe();
    /** Controllers mapping by name and index. */
    private final Map<String, Integer> controllers = new ConcurrentHashMap<>();
    /** Press flags. */
    private final Map<Integer, Set<Integer>> press = new ConcurrentHashMap<>();
    /** Pressed flags. */
    private final Map<Integer, Set<Integer>> pressed = new ConcurrentHashMap<>();
    /** Last press number. */
    private final Map<Integer, Integer> last = new ConcurrentHashMap<>();
    /** Check timing. */
    private final Timing timing = new Timing();

    /** Active index. */
    private Integer controller;

    /**
     * Create.
     */
    public Gamepad()
    {
        super();

        if (manager != null)
        {
            manager.addListener(new ControllerListener()
            {
                @Override
                public void connected(Controller controller)
                {
                    final Integer index = controllers.get(controller.getName());
                    if (index != null)
                    {
                        init(index);
                    }
                }

                @Override
                public void disconnected(Controller controller)
                {
                    final Integer index = controllers.get(controller.getName());
                    if (index != null)
                    {
                        clean(index);
                    }
                }

                @Override
                public boolean buttonDown(Controller controller, int buttonCode)
                {
                    final Integer index = controllers.get(controller.getName());
                    if (index != null)
                    {
                        final Integer code = Integer.valueOf(buttonCode);
                        last.put(index, code);
                        press.get(index).add(code);
                    }
                    return false;
                }

                @Override
                public boolean buttonUp(Controller controller, int buttonCode)
                {
                    final Integer index = controllers.get(controller.getName());
                    if (index != null)
                    {
                        final Integer code = Integer.valueOf(buttonCode);
                        last.remove(index, code);
                        press.get(index).remove(code);
                    }
                    return false;
                }

                @Override
                public boolean xSliderMoved(Controller controller, int sliderCode, boolean value)
                {
                    return false;
                }

                @Override
                public boolean ySliderMoved(Controller controller, int sliderCode, boolean value)
                {
                    return false;
                }

                @Override
                public boolean povMoved(Controller controller, int povCode, PovDirection value)
                {
                    return false;
                }

                @Override
                public boolean axisMoved(Controller controller, int axisCode, float value)
                {
                    final Integer index = controllers.get(controller.getName());
                    if (index != null)
                    {
                        final Set<Integer> codes = press.get(index);
                        if (axisCode % 2 == 0)
                        {
                            if (value > 0.5)
                            {
                                codes.add(Integer.valueOf(14));
                                codes.remove(Integer.valueOf(13));
                            }
                            else if (value < -0.5)
                            {
                                codes.add(Integer.valueOf(13));
                                codes.remove(Integer.valueOf(14));
                            }
                            else
                            {
                                codes.remove(Integer.valueOf(13));
                                codes.remove(Integer.valueOf(14));
                            }
                        }
                        else if (axisCode % 2 == 1)
                        {
                            if (value < -0.5)
                            {
                                codes.add(Integer.valueOf(11));
                                codes.remove(Integer.valueOf(12));
                            }
                            else if (value > 0.5)
                            {
                                codes.add(Integer.valueOf(12));
                                codes.remove(Integer.valueOf(11));
                            }
                            else
                            {
                                codes.remove(Integer.valueOf(11));
                                codes.remove(Integer.valueOf(12));
                            }
                        }
                    }
                    return false;
                }

                @Override
                public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value)
                {
                    return false;
                }
            });
        }

        findDevices();
        timing.start();
    }

    /**
     * Select active device.
     * 
     * @param index The device index.
     */
    public void select(int index)
    {
        if (manager != null)
        {
            controller = Integer.valueOf(index);
        }
    }

    /**
     * Find available devices.
     * 
     * @return The devices mapping by name and index.
     */
    public final Map<String, Integer> findDevices()
    {
        if (manager != null)
        {
            try
            {
                manager.pollState();

                controllers.values().forEach(this::clean);

                final Array<Controller> array = manager.getControllers();
                controllers.clear();
                for (int i = 0; i < array.size; i++)
                {
                    final Integer index = Integer.valueOf(i);
                    controllers.put(array.get(i).getName(), index);
                    init(index);

                    if (controller == null)
                    {
                        select(i);
                    }
                }
                return controllers;
            }
            catch (final SDL_Error error)
            {
                Verbose.exception(error);
            }
        }
        return Collections.emptyMap();
    }

    /**
     * Init controller structure.
     * 
     * @param index The controller index.
     */
    private void init(Integer index)
    {
        press.put(index, new HashSet<>());
        pressed.put(index, new HashSet<>());
    }

    /**
     * Clean controller structure.
     * 
     * @param index The controller index.
     */
    private void clean(Integer index)
    {
        last.remove(index);
        Optional.ofNullable(press.get(index)).ifPresent(Set::clear);
        Optional.ofNullable(pressed.get(index)).ifPresent(Set::clear);
    }

    /**
     * Check if contains index in controller map.
     * 
     * @param map The map used.
     * @param index The index to check.
     * @return <code>true</code> if contained, <code>false</code> else.
     */
    private boolean contains(Map<Integer, Set<Integer>> map, Integer index)
    {
        final Set<Integer> data = map.get(controller);
        if (data != null)
        {
            return data.contains(index);
        }
        return false;
    }

    @Override
    public void update(double extrp)
    {
        if (manager != null)
        {
            if (!last.isEmpty() || timing.elapsed(Constant.ONE_SECOND_IN_MILLI))
            {
                try
                {
                    manager.pollState();
                }
                catch (final SDL_Error error)
                {
                    if (!last.isEmpty())
                    {
                        Verbose.exception(error);
                        timing.restart();
                    }
                }
            }
        }
    }

    @Override
    public boolean isPushed()
    {
        if (controller != null)
        {
            return !press.get(controller).isEmpty();
        }
        return false;
    }

    @Override
    public Integer getPushed()
    {
        if (controller != null)
        {
            return last.get(controller);
        }
        return null;
    }

    @Override
    public boolean isPushed(Integer index)
    {
        if (controller != null)
        {
            return contains(press, index);
        }
        return false;
    }

    @Override
    public boolean isPushedOnce(Integer index)
    {
        if (controller != null)
        {
            if (contains(press, index) && !contains(pressed, index))
            {
                pressed.get(controller).add(index);
                return true;
            }
        }
        return false;
    }

    @Override
    public void close()
    {
        if (manager != null)
        {
            manager.close();
        }
    }

    @Override
    public String getName()
    {
        return Gamepad.class.getSimpleName();
    }
}
