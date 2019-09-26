/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.android.object;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.android.Mouse;
import com.b3dgs.lionengine.geom.Area;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.io.InputDeviceControl;
import com.b3dgs.lionengine.io.InputDevicePointer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Represents a virtual button aimed to receive a click from a {@link InputDevicePointer} and generate a compatible
 * device for {@link InputDeviceControl}.
 */
public class VirtualDeviceButton implements InputDeviceControl, Updatable, Renderable
{
    /** CTRL key. */
    static final Integer CONTROL = Integer.valueOf(0);
    /** Arrow left key. */
    static final Integer LEFT = Integer.valueOf(1);
    /** Arrow right key. */
    static final Integer RIGHT = Integer.valueOf(2);
    /** Arrow down key. */
    static final Integer DOWN = Integer.valueOf(3);
    /** Arrow up key. */
    static final Integer UP = Integer.valueOf(4);
    /** No key code value. */
    private static final Integer NO_KEY_CODE = Integer.valueOf(-1);

    /** Text printer. */
    private final Text text = Graphics.createText(20);
    /** Buttons defined. */
    private final Map<Integer, Button> buttons = new HashMap<>();
    /** Pointer reference. */
    private final Mouse pointer;

    /** List of keys. */
    private final Collection<Integer> keys = new HashSet<>();
    /** Pressed states. */
    private final Collection<Integer> pressed = new HashSet<>();
    /** Last key code. */
    private Integer lastCode = NO_KEY_CODE;
    /** Last key name. */
    private String lastKeyName = Constant.EMPTY_STRING;
    /** Left key. */
    private Integer leftKey = LEFT;
    /** Right key. */
    private Integer rightKey = RIGHT;
    /** Up key. */
    private Integer upKey = UP;
    /** Down key. */
    private Integer downKey = DOWN;
    /** Fire key. */
    private Integer fireKey = CONTROL;

    /**
     * Create device.
     *
     * @param pointer The pointer reference.
     */
    public VirtualDeviceButton(Mouse pointer)
    {
        this.pointer = pointer;
    }

    /**
     * Add a button.
     *
     * @param area The area representation.
     * @param code The associated code.
     * @param label The associated label.
     */
    public void addButton(Area area, Integer code, String label)
    {
        final Button button = new Button(area, code, label);
        buttons.put(button.code, button);
    }

    /**
     * Check if the key is currently pressed.
     *
     * @param key The key to check.
     * @return <code>true</code> if pressed, <code>false</code> else.
     */
    public boolean isPressed(Integer key)
    {
        return keys.contains(key);
    }

    /**
     * Check if the key is currently pressed (not continuously).
     *
     * @param key The key to check.
     * @return <code>true</code> if pressed, <code>false</code> else.
     */
    public boolean isPressedOnce(Integer key)
    {
        if (keys.contains(key) && !pressed.contains(key))
        {
            pressed.add(key);
            return true;
        }
        return false;
    }

    /**
     * Get the current pressed key code.
     *
     * @return The pressed key code (-1 if key never pressed).
     */
    public Integer getKeyCode()
    {
        return lastCode;
    }

    /**
     * Get the current pressed key name.
     *
     * @return The pressed key name.
     */
    public String getKeyLabel()
    {
        return lastKeyName;
    }

    /**
     * Called on button pressed.
     *
     * @param button The pressed button.
     */
    private void onPressed(Button button)
    {
        lastKeyName = button.label;
        lastCode = button.code;

        if (!keys.contains(lastCode))
        {
            keys.add(lastCode);
        }
    }

    /**
     * Called on button released.
     *
     * @param button The released button.
     */
    private void onReleased(Button button)
    {
        lastKeyName = Constant.EMPTY_STRING;
        lastCode = NO_KEY_CODE;

        keys.remove(button.code);
        pressed.remove(button.code);
    }

    /*
     * InputDeviceDirectional
     */

    @Override
    public void setHorizontalControlPositive(Integer code)
    {
        rightKey = code;
    }

    @Override
    public void setHorizontalControlNegative(Integer code)
    {
        leftKey = code;
    }

    @Override
    public void setVerticalControlPositive(Integer code)
    {
        upKey = code;
    }

    @Override
    public void setVerticalControlNegative(Integer code)
    {
        downKey = code;
    }

    @Override
    public Integer getHorizontalControlPositive()
    {
        return rightKey;
    }

    @Override
    public Integer getHorizontalControlNegative()
    {
        return leftKey;
    }

    @Override
    public Integer getVerticalControlPositive()
    {
        return upKey;
    }

    @Override
    public Integer getVerticalControlNegative()
    {
        return downKey;
    }

    @Override
    public double getHorizontalDirection()
    {
        final double direction;
        if (isPressed(leftKey))
        {
            direction = -1;
        }
        else if (isPressed(rightKey))
        {
            direction = 1;
        }
        else
        {
            direction = 0;
        }
        return direction;
    }

    @Override
    public double getVerticalDirection()
    {
        final int direction;
        if (isPressed(downKey))
        {
            direction = -1;
        }
        else if (isPressed(upKey))
        {
            direction = 1;
        }
        else
        {
            direction = 0;
        }
        return direction;
    }

    @Override
    public void setFireButton(Integer code)
    {
        fireKey = code;
    }

    @Override
    public boolean isUpButtonOnce()
    {
        return isPressedOnce(upKey);
    }

    @Override
    public boolean isDownButtonOnce()
    {
        return isPressedOnce(downKey);
    }

    @Override
    public boolean isLeftButtonOnce()
    {
        return isPressedOnce(leftKey);
    }

    @Override
    public boolean isRightButtonOnce()
    {
        return isPressedOnce(rightKey);
    }

    @Override
    public boolean isFireButton()
    {
        return isPressed(fireKey);
    }

    @Override
    public boolean isFireButtonOnce()
    {
        return isPressedOnce(fireKey);
    }

    /*
     * Updatable
     */

    @Override
    public void update(double extrp)
    {
        for (final Button button : buttons.values())
        {
            boolean found = false;
            for (int i = 1; i < 3; i++)
            {
                if (pointer.hasClicked(i) && button.area.contains(pointer.getX(i), pointer.getY(i)))
                {
                    if (!button.pressed)
                    {
                        onPressed(button);
                        button.pressed = true;
                    }
                    found = true;
                    break;
                }
            }
            if (!found && button.pressed)
            {
                onReleased(button);
                button.pressed = false;
            }
        }
    }

    /*
     * Renderable
     */

    @Override
    public void render(Graphic g)
    {
        g.setColor(ColorRgba.GREEN);
        for (final Button button : buttons.values())
        {
            g.drawRect((int) button.area.getX(),
                       (int) button.area.getY(),
                       button.area.getWidth(),
                       button.area.getHeight(),
                       false);
        }
        for (final Button button : buttons.values())
        {
            text.draw(g,
                      (int) button.area.getX() + button.area.getWidth() / 2 - 1,
                      (int) button.area.getY() + 3,
                      Align.CENTER,
                      button.label);
        }
    }

    /**
     * Button representation.
     */
    private static class Button
    {
        /** Area reference. */
        private final Area area;
        /** Code reference. */
        private final Integer code;
        /** Label reference. */
        private final String label;
        /** Pressed flag. */
        private boolean pressed;

        /**
         * Create button.
         *
         * @param area The area representation.
         * @param code The associated code.
         * @param label The associated label.
         */
        Button(Area area, Integer code, String label)
        {
            super();

            this.area = area;
            this.code = code;
            this.label = label;
        }
    }
}
