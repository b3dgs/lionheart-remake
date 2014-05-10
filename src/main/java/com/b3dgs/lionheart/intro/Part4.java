/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteFont;

/**
 * Intro part 4 implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Part4
{
    /** Font. */
    private final SpriteFont font;
    /** Pictures. */
    private final Sprite[] history;
    /** Back alpha. */
    private double alphaBack;

    /**
     * Constructor.
     */
    public Part4()
    {
        font = Drawable.loadSpriteFont(Core.MEDIA.create("sprites", "font.png"),
                Core.MEDIA.create("sprites", "fontdata.xml"), 12, 12);
        history = new Sprite[4];
        for (int i = 0; i < history.length; i++)
        {
            history[i] = Drawable.loadSprite(Core.MEDIA.create("intro", "part4", "history" + i + ".png"));
            history[i].load(false);
        }
    }

    /**
     * Load part.
     */
    public void load()
    {
        font.load(false);
    }

    /**
     * Update part.
     * 
     * @param seek The current seek.
     * @param extrp The extrapolation value.
     */
    public void update(int seek, double extrp)
    {
        // First Fade in
        if (seek > 113500 && seek < 117000)
        {
            alphaBack += 3.0;
        }

        // First Fade out
        if (seek > 198500 && seek < 200000)
        {
            alphaBack -= 6.0;
        }
        alphaBack = UtilMath.fixBetween(alphaBack, 0.0, 255.0);
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

        // Render histories
        if (seek >= 113500)
        {
            history[0].render(g, 0, 0);
        }
        if (seek >= 129200)
        {
            history[1].render(g, 45, 20);
        }
        if (seek >= 154050)
        {
            history[2].render(g, 90, 40);
        }
        if (seek >= 178700)
        {
            history[3].render(g, 135, 60);
        }

        // Render texts
        if (seek >= 113500 && seek < 129200)
        {
            font.draw(
                    g,
                    2,
                    history[0].getHeight() + 2,
                    Align.LEFT,
                    "Valdyn crossed the border and flew into %Norka's country. Everything had gone %smoothly until now. %Then he noticed a shape on the horizon %which rapidly grew bigger. One of Norka's %air-ships!");
        }
        if (seek >= 129200 && seek < 154050)
        {
            font.draw(
                    g,
                    2,
                    history[1].getHeight() + 22,
                    Align.LEFT,
                    "Frantically, Valdyn urged the dragon %to fly faster. Maybe he could still escape! %But the airship grew ever larger. %It had neared to a distance of a few meters %when an explosion sounded and a rapidly %expanding net flew towards Valdyn and %the dragon!");
        }
        if (seek >= 154050 && seek < 178700)
        {
            font.draw(
                    g,
                    2,
                    history[2].getHeight() + 42,
                    Align.LEFT,
                    "They were caught by the net and were %swiftly hauled towards the ship. %Valdyn, who was having a hard time with %the panicking dragon, drew his sword and %hacked a way through the tough material. %Just before being drawn into the air-ship, %he jumped out and plummeted towards the %ground!");
        }
        if (seek >= 178700 && seek < 200000)
        {
            font.draw(
                    g,
                    2,
                    history[3].getHeight() + 62,
                    Align.LEFT,
                    "Due to his new powers, Valdyn had survived %the long fall without even a scratch. %He stood forlornly in the swamps and %watched the air-ship fly out of sight. %He cursed. His mission wouldn't be so %easy now.");
        }

        // Render fade in
        if (alphaBack < 255)
        {
            g.setColor(Intro.ALPHAS_BLACK[255 - (int) alphaBack]);
            g.drawRect(0, 0, width, height, true);
        }
    }
}
