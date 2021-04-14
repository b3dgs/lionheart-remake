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

import java.util.List;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resource;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.graphic.drawable.SpriteDigit;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.object.feature.Stats;

/**
 * Represents the player stats display.
 * <ul>
 * <li>Health (fill, lost)</li>
 * <li>Talisment (icon, count)</li>
 * <li>Life (icon, count)</li>
 * </ul>
 */
public final class Hud implements Resource, Updatable, Renderable
{
    private static final String IMG_HEART = "health.png";
    private static final String IMG_HUD = "hud.png";
    private static final String IMG_NUMBERS = "numbers.png";
    private static final List<String> TEXT = Util.readLines(Medias.create(Folder.TEXT, "hud.txt"));

    private static final int HEALTH_MAX = 8;
    private static final int HEALTH_X = 1;
    private static final int HEALTH_Y = 1;

    private static final int TALISMENT_Y = 1;

    private static final boolean SWORD_VISIBLE = false;
    private static final int SWORD_Y = 1;
    private static final int SWORD_TILE = 1;

    private static final int LIFE_Y = 1;

    private static final int PAUSE_FLICKER_TICK = 14;

    private final Image heartSurface = Drawable.loadImage(Medias.create(Folder.SPRITE, IMG_HEART));
    private final ImageBuffer hudSurface = Graphics.getImageBuffer(Medias.create(Folder.SPRITE, IMG_HUD));
    private final ImageBuffer number = Graphics.getImageBuffer(Medias.create(Folder.SPRITE, IMG_NUMBERS));
    private final Text text = Graphics.createText(Constant.FONT_DIALOG, 9, TextStyle.BOLD);

    private final SpriteTiled talisment = Drawable.loadSpriteTiled(hudSurface, 16, 16);
    private final SpriteTiled sword = Drawable.loadSpriteTiled(hudSurface, 16, 16);
    private final SpriteTiled life = Drawable.loadSpriteTiled(hudSurface, 16, 16);
    private final SpriteDigit numberTalisment = Drawable.loadSpriteDigit(number, 8, 16, 2);
    private final SpriteDigit numberLife = Drawable.loadSpriteDigit(number, 8, 16, 2);

    private final SpriteTiled[] hearts = new SpriteTiled[HEALTH_MAX];
    private final Tick tick = new Tick();
    private final Viewer viewer;

    private Stats stats;
    private boolean paused;
    private boolean exit;
    private boolean flicker;

    /**
     * Create hud.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public Hud(Services services)
    {
        super();

        viewer = services.get(Viewer.class);
    }

    /**
     * Called when the resolution changed.
     * 
     * @param width The new width.
     * @param height The new height.
     */
    public void setScreenSize(int width, int height)
    {
        life.setLocation(width - life.getTileWidth() - numberLife.getWidth() - 4, LIFE_Y);
        numberLife.setLocation(life.getX() + life.getTileWidth() + 2, LIFE_Y + 1);
    }

    /**
     * Set paused flag.
     * 
     * @param paused The paused flag.
     */
    public void setPaused(boolean paused)
    {
        this.paused = paused;
        exit = false;
    }

    /**
     * Set exit flag.
     * 
     * @param exit The exit flag.
     */
    public void setExit(boolean exit)
    {
        paused = exit;
        this.exit = exit;
    }

    /**
     * Set the featurable to display.
     * 
     * @param featurable The featurable reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public void setFeaturable(Featurable featurable)
    {
        if (featurable.hasFeature(Stats.class))
        {
            stats = featurable.getFeature(Stats.class);
        }
        else
        {
            stats = null;
        }
    }

    /**
     * Load health and set location.
     */
    private void loadHealth()
    {
        for (int i = 0; i < HEALTH_MAX; i++)
        {
            hearts[i] = Drawable.loadSpriteTiled(heartSurface.getSurface(), 8, 8);
            hearts[i].setTile(2);
            hearts[i].setLocation(HEALTH_X + i % (HEALTH_MAX / 2) * HEALTH_MAX,
                                  HEALTH_Y + Math.floor(i / (HEALTH_MAX / 2.0)) * HEALTH_MAX);
        }
    }

    /**
     * Load talisment location.
     */
    private void loadTalisment()
    {
        talisment.setTile(0);
        talisment.setLocation(viewer.getWidth() * 0.205, TALISMENT_Y + 2);
        numberTalisment.setLocation(talisment.getX() + talisment.getTileWidth(), TALISMENT_Y + 1);
    }

    /**
     * Load talisment location.
     */
    private void loadSword()
    {
        sword.setTile(SWORD_TILE);
        sword.setLocation(viewer.getWidth() * 0.5, SWORD_Y);
    }

    /**
     * Load life location.
     */
    private void loadLife()
    {
        life.setTile(6);
        life.setLocation(viewer.getWidth() - life.getTileWidth() - numberLife.getWidth() - 4, LIFE_Y);
        numberLife.setLocation(life.getX() + life.getTileWidth() + 2, LIFE_Y + 1);
    }

    /**
     * Update heart count with current health.
     */
    private void updateHeart()
    {
        for (int i = 0; i < HEALTH_MAX; i++)
        {
            if (i < stats.getHealth()) // Remaining hearts
            {
                hearts[i].setTile(0);
            }
            else if (i < stats.getHealthMax()) // Lost hearts
            {
                hearts[i].setTile(1);
            }
            else // Max hearts
            {
                hearts[i].setTile(2);
            }
        }
    }

    /**
     * Render pause exit.
     * 
     * @param g The graphic output.
     */
    private void renderPause(Graphic g)
    {
        if (exit)
        {
            text.draw(g, HEALTH_X, HEALTH_Y, TEXT.get(1));
            text.draw(g, HEALTH_X, HEALTH_Y + text.getSize(), TEXT.get(2));
        }
        else
        {
            text.draw(g, HEALTH_X, HEALTH_Y, TEXT.get(0));
        }
    }

    @Override
    public void load()
    {
        heartSurface.load();
        heartSurface.prepare();

        loadHealth();

        hudSurface.prepare();
        number.prepare();

        loadTalisment();
        loadSword();
        loadLife();

        tick.start();
    }

    @Override
    public boolean isLoaded()
    {
        return heartSurface.isLoaded() && talisment.isLoaded() && sword.isLoaded() && life.isLoaded();
    }

    @Override
    public void dispose()
    {
        heartSurface.dispose();
        hudSurface.dispose();
        number.dispose();
    }

    @Override
    public void update(double extrp)
    {
        if (stats != null)
        {
            updateHeart();

            numberTalisment.setValue(stats.getTalisment());
            if (SWORD_VISIBLE)
            {
                sword.setTile(SWORD_TILE + stats.getSword());
            }
            numberLife.setValue(stats.getLife());
        }

        if (paused)
        {
            tick.update(extrp);
            if (tick.elapsed(PAUSE_FLICKER_TICK))
            {
                flicker = !flicker;
                text.setColor(flicker ? ColorRgba.BLACK : ColorRgba.WHITE);
                tick.restart();
            }
        }
    }

    @Override
    public void render(Graphic g)
    {
        if (stats != null)
        {
            if (paused)
            {
                renderPause(g);
            }
            else
            {
                for (int i = 0; i < hearts.length; i++)
                {
                    hearts[i].render(g);
                }
            }

            talisment.render(g);
            numberTalisment.render(g);

            if (SWORD_VISIBLE)
            {
                sword.render(g);
            }

            life.render(g);
            numberLife.render(g);
        }
    }
}
