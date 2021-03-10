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

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.intro.Intro;

/**
 * Extro part 3 implementation.
 */
public final class Part3
{
    private final Sprite[] pics = new Sprite[3];
    private final SpriteFont font = Drawable.loadSpriteFont(Medias.create(Folder.SPRITES, "font.png"),
                                                            Medias.create(Folder.SPRITES, "fontdata.xml"),
                                                            12,
                                                            12);
    private double alphaBack = 255;

    /**
     * Constructor.
     */
    public Part3()
    {
        super();

        for (int i = 0; i < pics.length; i++)
        {
            pics[i] = Drawable.loadSprite(Medias.create(Folder.EXTRO, "part3", "pic" + i + ".png"));
            pics[i].load();
        }
    }

    /**
     * Load part.
     */
    public void load()
    {
        font.load();
    }

    /**
     * Update part.
     * 
     * @param seek The current seek.
     * @param extrp The extrapolation value.
     */
    public void update(long seek, double extrp)
    {
        if (seek > 84000)
        {
            alphaBack -= 6.0;
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

        // Render histories
        if (seek >= 41000)
        {
            pics[0].setLocation(0, 0);
            pics[0].render(g);
        }
        if (seek >= 56000)
        {
            pics[1].setLocation(160, 14);
            pics[1].render(g);
        }
        if (seek >= 71000)
        {
            pics[2].setLocation(80, 29);
            pics[2].render(g);
        }

        // Render texts
        if (seek >= 41000 && seek < 56000)
        {
            font.draw(g,
                      1,
                      128,
                      Align.LEFT,
                      "In the temple, Valdyn took the Lionheart%and put it back in the shrine. As the%jewel returned to it's ancient resting%place, it glowed and sparkled as if to%express satisfaction.");
        }
        if (seek >= 56000 && seek < 71000)
        {
            font.draw(g,
                      1,
                      128,
                      Align.LEFT,
                      "'Our eternal thanks, Valdyn,' said the king%who had entered with two guards. 'The%realm may live in happiness once more.'%%But Valdyn did not feel happy.");
        }
        if (seek >= 71000)
        {
            font.draw(g,
                      1,
                      128,
                      Align.LEFT,
                      "Leaving the surprised king behind him,%Valdyn walked into the chamber where%Llene's petrified body stood.");
        }

        // Render fade in
        if (alphaBack < 255)
        {
            g.setColor(Intro.ALPHAS_BLACK[255 - (int) alphaBack]);
            g.drawRect(0, 0, width, height, true);
        }
    }
}
