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
import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.AnimatorFrameListener;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.intro.Intro;

/**
 * Extro part 4 implementation.
 */
public final class Part4
{
    private static final Animation GLOW = new Animation(Animation.DEFAULT_NAME, 1, 4, 0.15, true, true);

    private final Sprite credits = Drawable.loadSprite(Medias.create(Folder.EXTRO, "part4", "credits.png"));
    private final Sprite[] pics = new Sprite[2];
    private final SpriteFont font = Drawable.loadSpriteFont(Medias.create(Folder.SPRITES, "font.png"),
                                                            Medias.create(Folder.SPRITES, "fontdata.xml"),
                                                            12,
                                                            12);
    private final SpriteAnimated amulet = Drawable.loadSpriteAnimated(Medias.create(Folder.EXTRO,
                                                                                    "part4",
                                                                                    "amulet.png"),
                                                                      2,
                                                                      2);
    private double alphaBack;
    private final boolean alternative;
    private int glowed;

    /**
     * Constructor.
     * 
     * @param alternative The alternative ending flag.
     */
    public Part4(boolean alternative)
    {
        super();

        this.alternative = alternative;
    }

    /**
     * Load part.
     */
    public void load()
    {
        credits.load();
        credits.prepare();
        credits.setOrigin(Origin.CENTER_TOP);

        for (int i = 0; i < pics.length; i++)
        {
            pics[i] = Drawable.loadSprite(Medias.create(Folder.EXTRO, "part4", "pic" + i + ".png"));
            pics[i].load();
            pics[i].prepare();
        }

        pics[0].setLocation(102, 20);
        pics[1].setLocation(198, 70);

        font.load();
        font.prepare();

        amulet.load();
        amulet.prepare();
        amulet.setLocation(165, 160);
        amulet.addListener((AnimatorFrameListener) f ->
        {
            if (f == 1 && amulet.getAnimState() != AnimState.STOPPED)
            {
                glowed++;
                if (glowed > 5)
                {
                    amulet.stop();
                    amulet.setFrame(1);
                    glowed = 0;
                }
            }
        });
    }

    /**
     * Update part.
     * 
     * @param seek The current seek.
     * @param extrp The extrapolation value.
     */
    public void update(long seek, double extrp)
    {
        if (seek > 86000 && seek < 90000)
        {
            alphaBack += 6.0;
        }
        else if (seek > 133000)
        {
            alphaBack -= 6.0;
        }
        alphaBack = UtilMath.clamp(alphaBack, 0.0, 255.0);

        if (alternative)
        {
            amulet.update(extrp);
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
    public void render(int width, int height, long seek, Graphic g)
    {
        g.clear(0, 0, width, height);

        // Render histories
        if (seek >= 86000)
        {
            credits.setLocation(width / 2, 0);
            credits.render(g);

            if (alternative)
            {
                amulet.render(g);

                if (seek > 109600)
                {
                    g.setColor(Intro.ALPHAS_BLACK[128]);
                    g.drawRect(0, 0, width, height, true);
                    pics[0].render(g);
                }
                if (seek > 118000)
                {
                    pics[1].render(g);
                }
            }
        }

        // Render texts
        if (seek >= 89000 && seek < 109600)
        {
            font.draw(g,
                      88,
                      30,
                      Align.LEFT,
                      "The kingdom was saved. But%what did that mean to Valdyn !%Ilene was gone forever.");

            if (alternative && seek > 103000)
            {
                font.draw(g, 88, 82, Align.LEFT, "Wait! What's this !");

                if (amulet.getAnimState() == AnimState.STOPPED)
                {
                    amulet.play(GLOW);
                }
            }
        }
        else if (seek > 109600 && seek < 118000)
        {
            font.draw(g,
                      88,
                      180,
                      Align.LEFT,
                      "Valdyn stared at the amulet he had%found in the hidden cave. It glowed%with an eerie light!");
        }
        else if (seek > 118000 && seek < 135000)
        {
            font.draw(g, 88, 180, Align.LEFT, "With trembling hands, he put the%amulet around Ilene's petrified%neck.");
        }

        // Render fade in
        if (alphaBack < 255)
        {
            g.setColor(Intro.ALPHAS_BLACK[255 - (int) alphaBack]);
            g.drawRect(0, 0, width, height, true);
        }
    }
}
