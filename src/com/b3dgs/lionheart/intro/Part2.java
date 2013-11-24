/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionheart.intro;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.core.UtilityMath;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteAnimated;

/**
 * Intro part 2 implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Part2
{
    /** Door. */
    private final SpriteAnimated door;
    /** Pillar. */
    private final Sprite[] pillar;
    /** Cave 1. */
    private final Sprite cave1;
    /** Z location. */
    private double z;
    /** Z accelerate. */
    private double accelerate;
    /** Z accelerate 2. */
    private double accelerate2;
    /** Alpha. */
    private double alpha;

    /**
     * Constructor.
     */
    public Part2()
    {
        door = Drawable.loadSpriteAnimated(UtilityMedia.get("intro", "part2", "door.png"), 3, 2);
        pillar = new Sprite[6];
        for (int i = 0; i < pillar.length; i++)
        {
            pillar[i] = Drawable.loadSprite(UtilityMedia.get("intro", "part2", "pillar.png"));
            pillar[i].load(false);
        }
        cave1 = Drawable.loadSprite(UtilityMedia.get("intro", "part2", "cave1.png"));
    }

    /**
     * Load part.
     */
    public void load()
    {
        door.load(false);
        cave1.load(false);
        door.play(Anim.createAnimation(1, 6, 0.15, false, false));
        z = 0;
        accelerate = 1.0;
        accelerate2 = 0.0000001;
    }

    /**
     * Update part.
     * 
     * @param seek The current seek.
     * @param extrp The extrapolation value.
     */
    public void update(int seek, double extrp)
    {
        door.updateAnimation(extrp);
        if (seek > 48700 && z < 125)
        {
            z += accelerate;
            accelerate += 0.015;
            door.scale((int) (100 + z));
            if (z > 125)
            {
                accelerate = 0.5;
            }
        }
        if (seek > 49500 && alpha < 255.0)
        {
            alpha += 2.0;
            UtilityMath.fixBetween(alpha, 0.0, 255.0);
        }
        if (z >= 125)
        {
            z += accelerate;
            accelerate += accelerate2;
            accelerate2 += 0.00005;
        }
    }

    /**
     * Render part.
     * 
     * @param width The width.
     * @param height The height.
     * @param seek The current seek.
     * @param g The graphic output.
     */
    public void render(int width, int height, int seek, Graphic g)
    {
        g.clear(0, 0, width, height);
        if (z < 125)
        {
            door.render(g, width / 2 - door.getFrameWidth() / 2, height / 2 - door.getFrameHeight() / 2);
        }
        if (z > 125)
        {
            final int newZ = (int) (z - 125);
            final int caveZ = UtilityMath.fixBetween(1 + (int) Math.sqrt(newZ * 2.5), 0, 100);
            if (caveZ < 100)
            {
                cave1.scale(caveZ);
            }
            cave1.render(g, width / 2 - cave1.getWidth() / 2, height / 2 - cave1.getHeight() / 2);
            for (int i = pillar.length - 1; i >= 0; i--)
            {
                final int offset;
                if (i % 2 == 1)
                {
                    offset = -40 + i * 3 - newZ / 2 / (i + 1);
                }
                else
                {
                    offset = 40 - i * 3 + newZ / 2 / (i + 1);
                }
                final int scale = newZ / 2 / (i + 1) - i * 5 + 30;
                pillar[i].scale(scale);
                pillar[i].render(g, width / 2 - pillar[i].getWidth() / 2 + offset, height / 2 - pillar[i].getHeight()
                        / 2);
            }
            if (alpha < 255)
            {
                g.setColor(Intro.ALPHAS_BLACK[255 - (int) alpha]);
                g.drawRect(0, 0, width, height, true);
            }
        }
    }
}
