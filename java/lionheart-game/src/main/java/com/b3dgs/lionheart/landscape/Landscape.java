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
package com.b3dgs.lionheart.landscape;

import com.b3dgs.lionengine.game.background.Background;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Represents a landscape by containing a background and a foreground.
 */
public class Landscape
{
    private final Background background;
    private final Foreground foreground;

    /**
     * Constructor.
     * 
     * @param background The background element.
     * @param foreground The foreground element.
     */
    public Landscape(Background background, Foreground foreground)
    {
        super();

        this.background = background;
        this.foreground = foreground;
    }

    /**
     * Update the landscape.
     * 
     * @param extrp The extrapolation value.
     * @param camera The camera reference.
     */
    public void update(double extrp, Camera camera)
    {
        background.update(extrp, camera.getMovementHorizontal(), camera.getX(), camera.getY());
        foreground.update(extrp, camera.getMovementHorizontal(), camera.getX(), camera.getY());
        foreground.update(extrp);
    }

    /**
     * Render the background.
     * 
     * @param g The graphic output.
     */
    public void renderBackground(Graphic g)
    {
        background.render(g);
        foreground.renderBack(g);
    }

    /**
     * Render the foreground.
     * 
     * @param g The graphic output.
     */
    public void renderForeground(Graphic g)
    {
        foreground.renderFront(g);
    }

    /**
     * Called when the resolution changed.
     * 
     * @param width The new width.
     * @param height The new height.
     */
    public void setScreenSize(int width, int height)
    {
        background.setScreenSize(width, height);
        foreground.setScreenSize(width, height);
    }

    /**
     * Reset foreground.
     */
    public void reset()
    {
        foreground.reset();
    }

    /**
     * Set enabled flag.
     * 
     * @param enabled <code>true</code> to enable, <code>false</code> to disable.
     */
    public void setEnabled(boolean enabled)
    {
        foreground.setEnabled(enabled);
    }
}
