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
package com.b3dgs.lionheart.extro;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.intro.Intro;

/**
 * Extro part 5 implementation.
 */
public final class Part5
{
    private final SpriteAnimated transform0 = Drawable.loadSpriteAnimated(Medias.create(Folder.EXTRO,
                                                                                        "part5",
                                                                                        "transform0.png"),
                                                                          2,
                                                                          1);
    private final SpriteAnimated transform1 = Drawable.loadSpriteAnimated(Medias.create(Folder.EXTRO,
                                                                                        "part5",
                                                                                        "transform1.png"),
                                                                          4,
                                                                          3);
    private final SpriteAnimated transform2 = Drawable.loadSpriteAnimated(Medias.create(Folder.EXTRO,
                                                                                        "part5",
                                                                                        "transform2.png"),
                                                                          10,
                                                                          2);
    private final SpriteAnimated transform3 = Drawable.loadSpriteAnimated(Medias.create(Folder.EXTRO,
                                                                                        "part5",
                                                                                        "transform3.png"),
                                                                          4,
                                                                          2);
    private double alphaBack;

    /**
     * Constructor.
     */
    public Part5()
    {
        super();
    }

    /**
     * Load part.
     */
    public void load()
    {
        transform0.load();
        transform0.prepare();
        transform0.setOrigin(Origin.CENTER_TOP);

        transform1.load();
        transform1.prepare();

        transform2.load();
        transform2.prepare();

        transform3.load();
        transform3.prepare();
    }

    /**
     * Update part.
     * 
     * @param seek The current seek.
     * @param extrp The extrapolation value.
     */
    public void update(long seek, double extrp)
    {
        if (seek > 135000 && seek < 138000)
        {
            alphaBack += 3.0;
        }
        else if (seek > 161000)
        {
            alphaBack -= 3.0;
        }
        alphaBack = UtilMath.clamp(alphaBack, 0.0, 255.0);
    }

    /**
     * Render part.
     * 
     * @param width The width.
     * @param height The height.
     * @param seek The current seek.
     * @param g The graphic output.
     */
    public void render(int width, int height, long seek, Graphic g)
    {
        g.clear(0, 0, width, height);

        transform0.setLocation(width / 2, 32);
        transform0.render(g);

        // Render fade in
        if (alphaBack < 255)
        {
            g.setColor(Intro.ALPHAS_BLACK[255 - (int) alphaBack]);
            g.drawRect(0, 0, width, height, true);
        }
    }
}
