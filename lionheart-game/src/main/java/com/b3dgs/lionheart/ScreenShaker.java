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
package com.b3dgs.lionheart;

import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;

/**
 * Handle screen shake effect.
 */
public class ScreenShaker implements Updatable
{
    private static final int SHAKE_DELAY = 2;
    private static final int SHAKE_COUNT = 5;
    private static final int SHAKE_AMPLITUDE = 4;

    private final Tick tick = new Tick();
    private final Camera camera;

    private int shakeX;
    private int shakeY;
    private int shakeCount;

    /**
     * Create screen shaker.
     * 
     * @param services The services reference.
     */
    public ScreenShaker(Services services)
    {
        super();

        camera = services.get(Camera.class);
    }

    /**
     * Start shake effect.
     */
    public void start()
    {
        shakeX = 0;
        shakeY = 0;
        shakeCount = 0;
        tick.restart();
        tick.set(SHAKE_DELAY);
    }

    /**
     * Check if shaken.
     * 
     * @return <code>true</code> if shaken, <code>false</code> else.
     */
    public boolean hasShaken()
    {
        return shakeCount > SHAKE_COUNT;
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsed(SHAKE_DELAY))
        {
            if (shakeX == 0)
            {
                shakeX = SHAKE_AMPLITUDE;
                shakeY = -SHAKE_AMPLITUDE;
            }
            else
            {
                shakeX = 0;
                shakeY = 0;
            }
            shakeCount++;

            if (shakeCount > SHAKE_COUNT)
            {
                camera.setShake(0, 0);
                tick.stop();
            }
            else
            {
                camera.setShake(shakeX, shakeY);
                tick.restart();
            }
        }
    }
}
