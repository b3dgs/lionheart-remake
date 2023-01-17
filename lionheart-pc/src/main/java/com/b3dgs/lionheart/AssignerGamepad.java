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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Verbose;

/**
 * Gamepad assigner.
 */
public class AssignerGamepad extends AssignController
{
    private static final String LABEL_BUTTON = "Button ";
    private static final AtomicReference<ActionGetter> ACTION = new AtomicReference<>();

    private final AtomicBoolean running = new AtomicBoolean(true);
    private final CountDownLatch latch = new CountDownLatch(1);

    /**
     * Create assigner.
     * 
     * @param gamepad The gamepad reference.
     * @param id The id.
     */
    public AssignerGamepad(Gamepad gamepad, int id)
    {
        super(ACTION::set, b -> LABEL_BUTTON + b);

        new Thread(() ->
        {
            while (running.get())
            {
                gamepad.update(1.0);
                final Integer last = gamepad.getCurrent(id).getPushed();
                if (last != null && ACTION.get() != null)
                {
                    ACTION.get().assign(last.intValue());
                }
                try
                {
                    Thread.sleep(Constant.HUNDRED);
                }
                catch (final InterruptedException exception)
                {
                    Thread.currentThread().interrupt();
                    Verbose.exception(exception);
                    running.set(false);
                }
                latch.countDown();
            }
        }, getName()).start();
    }

    @Override
    public void stop()
    {
        running.set(false);
        try
        {
            latch.await(Constant.THOUSAND, TimeUnit.MILLISECONDS);
        }
        catch (final InterruptedException exception)
        {
            Thread.currentThread().interrupt();
            Verbose.exception(exception);
        }
    }

    @Override
    public String getName()
    {
        return Gamepad.class.getSimpleName();
    }
}
