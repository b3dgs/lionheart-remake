/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;
import com.b3dgs.lionheart.object.feature.Stats;

/**
 * Represents the HUD with player stats display.
 */
public final class Hud implements Updatable, Renderable
{
    private static final int HEALTH_MAX = 8;
    private static final int HEALTH_X = 1;
    private static final int HEALTH_Y = 1;

    private static final int TALISMENT_X = 57;
    private static final int TALISMENT_Y = 1;

    private static final int LIFE_X = 244;
    private static final int LIFE_Y = 1;

    private final SpriteTiled[] health = new SpriteTiled[HEALTH_MAX];
    private final SpriteTiled talisment;
    private final SpriteTiled life;
    private final SpriteDigit numberTalisment;
    private final SpriteDigit numberLife;

    private Stats stats;

    /**
     * Create HUD.
     */
    public Hud()
    {
        super();

        final Image healthSurface = Drawable.loadImage(Medias.create(Constant.FOLDER_SPRITES, "health.png"));
        healthSurface.load();
        healthSurface.prepare();

        for (int i = 0; i < HEALTH_MAX; i++)
        {
            health[i] = Drawable.loadSpriteTiled(healthSurface.getSurface(), 8, 8);
            health[i].setLocation(HEALTH_X + i % (HEALTH_MAX / 2) * HEALTH_MAX,
                                  HEALTH_Y + Math.floor(i / (HEALTH_MAX / 2.0)) * HEALTH_MAX);
            health[i].setTile(2);
        }

        final ImageBuffer hudSurface = Graphics.getImageBuffer(Medias.create(Constant.FOLDER_SPRITES, "hud.png"));
        hudSurface.prepare();

        talisment = Drawable.loadSpriteTiled(hudSurface, 16, 16);
        talisment.setLocation(TALISMENT_X, TALISMENT_Y + 2);
        talisment.setTile(0);

        life = Drawable.loadSpriteTiled(hudSurface, 16, 16);
        life.setLocation(LIFE_X, LIFE_Y);
        life.setTile(6);

        final ImageBuffer number = Graphics.getImageBuffer(Medias.create(Constant.FOLDER_SPRITES, "numbers.png"));
        number.prepare();

        numberTalisment = new SpriteDigit(number, 8, 16, 2);
        numberTalisment.setLocation(TALISMENT_X + talisment.getTileWidth(), TALISMENT_Y + 1);

        numberLife = new SpriteDigit(number, 8, 16, 2);
        numberLife.setLocation(LIFE_X + life.getTileWidth() + 2, LIFE_Y + 1);
    }

    /**
     * Set the featurable to display.
     * 
     * @param featurable The featurable reference.
     */
    public void setFeaturable(Featurable featurable)
    {
        stats = featurable.getFeature(Stats.class);
    }

    @Override
    public void update(double extrp)
    {
        for (int i = 0; i < HEALTH_MAX; i++)
        {
            if (i < stats.getHealth())
            {
                health[i].setTile(0);
            }
            else if (i < stats.getHealthMax())
            {
                health[i].setTile(1);
            }
            else
            {
                health[i].setTile(2);
            }
        }

        numberTalisment.setValue(stats.getTalisment());
        numberLife.setValue(stats.getLife());
    }

    @Override
    public void render(Graphic g)
    {
        for (final SpriteTiled element : health)
        {
            element.render(g);
        }

        talisment.render(g);
        numberTalisment.render(g);

        life.render(g);
        numberLife.render(g);
    }
}
