/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.glfw.GLFWJoystickCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.InputDeviceListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilStream;

/**
 * Gamepad handler device.
 */
public class Gamepad implements InputDevice
{
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Gamepad.class);

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
            LOGGER.error("gamepad error", throwable);
        }
    }

    /** Push listener. */
    private final Map<Integer, ListenableModel<InputDeviceListener>> listeners = new ConcurrentHashMap<>();
    /** Id lists. */
    private final List<Integer> ids = new ArrayList<>();
    /** Controllers mapping by index. */
    private final Map<Integer, Integer> jidToIndex = new ConcurrentHashMap<>();
    /** Controllers mapping by name. */
    private final Map<Integer, String> jidToName = new ConcurrentHashMap<>();
    /** Press flags. */
    private final Map<Integer, Set<Integer>> press = new ConcurrentHashMap<>();
    /** Pressed flags. */
    private final Map<Integer, Set<Integer>> pressed = new ConcurrentHashMap<>();
    /** Last press number. */
    private final Map<Integer, Integer> last = new ConcurrentHashMap<>();
    /** Check timing. */
    private final Timing timing = new Timing();

    private final Map<Integer, byte[]> previousButtons = new HashMap<>();

    /**
     * Create.
     */
    public Gamepad()
    {
        super();

        if (!GLFW.glfwInit())
        {
            throw new LionEngineException("Unable to initialize !");
        }

        GLFW.glfwSetJoystickCallback(new GLFWJoystickCallback()
        {
            @Override
            public void invoke(int jid, int event)
            {
                final Integer id = Integer.valueOf(jid);
                if (event == GLFW.GLFW_CONNECTED)
                {
                    init(jid);
                }
                else if (event == GLFW.GLFW_DISCONNECTED)
                {
                    final Integer index = jidToIndex.get(id);
                    if (index != null)
                    {
                        clean(jid, index);
                    }
                }
            }
        });

        findDevices();
        timing.start();
    }

    /**
     * Find available devices.
     * 
     * @return The devices mapping by name and index.
     */
    public final Map<Integer, Integer> findDevices()
    {
        GLFW.glfwPollEvents();

        for (int jid = GLFW.GLFW_JOYSTICK_1; jid <= GLFW.GLFW_JOYSTICK_LAST; jid++)
        {
            if (GLFW.glfwJoystickPresent(jid))
            {
                init(jid);
            }
        }
        return jidToIndex;
    }

    /**
     * Init controller structure.
     * 
     * @param jid The internal id.
     */
    private void init(int jid)
    {
        final Integer key = Integer.valueOf(jid);

        int i;
        for (i = 0; i < jidToIndex.size(); i++)
        {
            if (!jidToIndex.containsKey(Integer.valueOf(i)))
            {
                break;
            }
        }
        final Integer index = Integer.valueOf(i);

        jidToIndex.put(key, index);
        jidToName.put(key, GLFW.glfwGetJoystickName(jid));

        previousButtons.put(key, new byte[GLFW.GLFW_JOYSTICK_LAST + 1]);

        listeners.put(index, new ListenableModel<>());
        press.put(index, new HashSet<>());
        pressed.put(index, new HashSet<>());

        ids.add(key);
    }

    /**
     * Clean controller structure.
     * 
     * @param jid The internal jid.
     * @param index The controller index.
     */
    private void clean(int jid, Integer index)
    {
        ids.remove(Integer.valueOf(jid));

        last.remove(index);
        Optional.ofNullable(press.get(index)).ifPresent(Set::clear);
        Optional.ofNullable(pressed.get(index)).ifPresent(Set::clear);

        previousButtons.remove(Integer.valueOf(jid));
    }

    @Override
    public void addListener(InputDeviceListener listener)
    {
        // Nothing to do
    }

    @Override
    public void removeListener(InputDeviceListener listener)
    {
        // Nothing to do
    }

    @Override
    public GamepadInstance getCurrent(int id)
    {
        final Integer i = Integer.valueOf(id);
        final Supplier<ListenableModel<InputDeviceListener>> listenersGet = () -> listeners.get(i);
        final Supplier<Set<Integer>> pressGet = () -> press.get(i);
        final Supplier<Set<Integer>> pressedGet = () -> pressed.get(i);
        final Supplier<Integer> lastGet = () -> last.get(i);
        final Updatable updater = id == 0 ? this::update : UpdatableVoid.getInstance();

        return new GamepadInstance(listenersGet, pressGet, pressedGet, lastGet, updater);
    }

    @Override
    public void update(double extrp)
    {
        GLFW.glfwPollEvents();

        final int n = ids.size();
        for (int idsIndex = 0; idsIndex < n; idsIndex++)
        {
            final int jid = ids.get(idsIndex).intValue();
            if (GLFW.glfwJoystickPresent(jid))
            {
                final Integer key = Integer.valueOf(jid);
                final Integer index = jidToIndex.get(key);

                if (GLFW.glfwJoystickIsGamepad(jid))
                {
                    final GLFWGamepadState state = GLFWGamepadState.create();
                    if (GLFW.glfwGetGamepadState(jid, state))
                    {
                        for (int i = 0; i <= GLFW.GLFW_GAMEPAD_BUTTON_LAST; i++)
                        {
                            if (state.buttons(i) != previousButtons.get(key)[i])
                            {
                                final Integer code = Integer.valueOf(i);
                                final boolean flag;
                                if (state.buttons(i) == GLFW.GLFW_PRESS)
                                {
                                    press.get(index).add(code);
                                    last.put(index, code);
                                    flag = true;
                                }
                                else
                                {
                                    press.get(index).remove(code);
                                    last.remove(index, code);
                                    flag = false;
                                }
                                final ListenableModel<InputDeviceListener> l = listeners.get(index);
                                final int count = l.size();
                                for (int j = 0; j < count; j++)
                                {
                                    l.get(j).onDeviceChanged(index, code, (char) code.intValue(), flag);
                                }
                                previousButtons.get(key)[i] = state.buttons(i);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void close()
    {
        jidToIndex.entrySet().forEach(e -> clean(e.getKey().intValue(), e.getValue()));
    }

    @Override
    public String getName()
    {
        return Gamepad.class.getSimpleName();
    }
}
