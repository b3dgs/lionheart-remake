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
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.RenderableVoid;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.graphic.drawable.SpriteDigit;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
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
    private static final String TXT_FILE = "hud.txt";

    private static final int HEALTH_TILE_WIDTH = 8;
    private static final int HEALTH_TILE_HEIGHT = 8;
    private static final int HEALTH_MAX = com.b3dgs.lionheart.Constant.STATS_MAX_HEART;
    private static final int HEALTH_X = 1;
    private static final int HEALTH_Y = 1;

    private static final double TALISMENT_X_RATIO = 0.205;
    private static final int TALISMENT_Y = 1;
    private static final int TALISMENT_TILE = 0;

    private static final double AMULET_X_RATIO = 0.72;
    private static final int AMULET_Y = 1;
    private static final int AMULET_TILE = 1;

    private static final double SWORD_X_RATIO = 0.5;
    private static final int SWORD_Y = 1;
    private static final int SWORD_TILE = 2;

    private static final int LIFE_TILE = 6;
    private static final int LIFE_Y = 1;
    private static final int LIFE_X_BORDER = 4;

    private static final int PAUSE_FLICKER_DELAY_MS = 250;

    private final List<String> hud = Util.readLines(Medias.create(Folder.TEXT,
                                                                  Settings.getInstance().getLang(),
                                                                  TXT_FILE));

    private final Image heartSurface = Drawable.loadImage(Medias.create(Folder.SPRITE, IMG_HEART));
    private final ImageBuffer hudSurface = Graphics.getImageBuffer(Medias.create(Folder.SPRITE, IMG_HUD));
    private final ImageBuffer number = Graphics.getImageBuffer(Medias.create(Folder.SPRITE, IMG_NUMBERS));
    private final Text text = Graphics.createText(Constant.FONT_DIALOG, 9, TextStyle.BOLD);

    private final SpriteTiled talisment = Drawable.loadSpriteTiled(hudSurface, 16, 16);
    private final SpriteTiled amulet = Drawable.loadSpriteTiled(hudSurface, 16, 16);
    private final SpriteTiled sword = Drawable.loadSpriteTiled(hudSurface, 16, 16);
    private final SpriteTiled life = Drawable.loadSpriteTiled(hudSurface, 16, 16);
    private final SpriteDigit numberTalisment = Drawable.loadSpriteDigit(number, 8, 16, 2);
    private final SpriteDigit numberLife = Drawable.loadSpriteDigit(number, 8, 16, 2);

    private final SpriteTiled[] hearts = new SpriteTiled[HEALTH_MAX];
    private final Tick tick = new Tick();
    private final boolean swordVisible = Settings.getInstance().getHudSword();

    private final SourceResolutionProvider source;
    private final Viewer viewer;

    private Updatable updaterHud = UpdatableVoid.getInstance();
    private Updatable updaterPause = UpdatableVoid.getInstance();
    private Renderable rendererHud = RenderableVoid.getInstance();
    private Renderable rendererPause = this::renderHealth;
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

        source = services.get(SourceResolutionProvider.class);
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
        life.setLocation(width - life.getTileWidth() - numberLife.getWidth() - LIFE_X_BORDER, LIFE_Y);
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

        switchRendererPause();
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

        switchRendererPause();
    }

    /**
     * Set the featurable to display.
     * 
     * @param featurable The featurable reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public void setFeaturable(Featurable featurable)
    {
        if (Settings.getInstance().getHudVisible() && featurable.hasFeature(Stats.class))
        {
            stats = featurable.getFeature(Stats.class);
            updaterHud = this::updateHud;
            updaterPause = this::updatePause;
            rendererHud = this::renderHud;
        }
        else
        {
            stats = null;
            updaterHud = UpdatableVoid.getInstance();
            updaterPause = UpdatableVoid.getInstance();
            rendererHud = RenderableVoid.getInstance();
        }
    }

    /**
     * Load health and set location.
     */
    private void loadHealth()
    {
        for (int i = 0; i < HEALTH_MAX; i++)
        {
            hearts[i] = Drawable.loadSpriteTiled(heartSurface.getSurface(), HEALTH_TILE_WIDTH, HEALTH_TILE_HEIGHT);
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
        talisment.setTile(TALISMENT_TILE);
        talisment.setLocation(viewer.getWidth() * TALISMENT_X_RATIO, TALISMENT_Y + 2);
        numberTalisment.setLocation(talisment.getX() + talisment.getTileWidth(), TALISMENT_Y + 1);
    }

    /**
     * Load talisment location.
     */
    private void loadSword()
    {
        sword.setTile(SWORD_TILE);
        sword.setLocation(viewer.getWidth() * SWORD_X_RATIO, SWORD_Y);
    }

    /**
     * Load amulet location.
     */
    private void loadAmulet()
    {
        amulet.setTile(AMULET_TILE);
        amulet.setLocation(viewer.getWidth() * AMULET_X_RATIO, AMULET_Y);
    }

    /**
     * Load life location.
     */
    private void loadLife()
    {
        life.setTile(LIFE_TILE);
        life.setLocation(viewer.getWidth() - life.getTileWidth() - numberLife.getWidth() - LIFE_X_BORDER, LIFE_Y);
        numberLife.setLocation(life.getX() + life.getTileWidth() + 2, LIFE_Y + 1);
    }

    /**
     * Switch renderer pause.
     */
    private void switchRendererPause()
    {
        if (paused)
        {
            rendererPause = this::renderPause;
        }
        else
        {
            rendererPause = this::renderHealth;
        }
    }

    /**
     * Update hud.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateHud(double extrp)
    {
        updateHeart();

        numberTalisment.setValue(stats.getTalisment());
        if (swordVisible)
        {
            sword.setTile(SWORD_TILE + stats.getSword());
        }
        numberLife.setValue(stats.getLife());
    }

    /**
     * Update heart count with current health.
     */
    private void updateHeart()
    {
        for (int i = 0; i < HEALTH_MAX; i++)
        {
            if (i < stats.getHealth())
            {
                // Remaining hearts
                hearts[i].setTile(0);
            }
            else if (i < stats.getHealthMax())
            {
                // Lost hearts
                hearts[i].setTile(1);
            }
            else
            {
                // Max hearts
                hearts[i].setTile(2);
            }
        }
    }

    /**
     * Update pause.
     * 
     * @param extrp The extrapolation value.
     */
    private void updatePause(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), PAUSE_FLICKER_DELAY_MS))
        {
            flicker = !flicker;
            text.setColor(flicker ? ColorRgba.BLACK : ColorRgba.WHITE);
            tick.restart();
        }
    }

    /**
     * Render health.
     * 
     * @param g The graphic output.
     */
    private void renderHealth(Graphic g)
    {
        for (int i = 0; i < hearts.length; i++)
        {
            hearts[i].render(g);
        }
    }

    /**
     * Render hud.
     * 
     * @param g The graphic output.
     */
    private void renderHud(Graphic g)
    {
        rendererPause.render(g);

        talisment.render(g);
        numberTalisment.render(g);

        if (swordVisible)
        {
            sword.render(g);
        }
        if (Boolean.TRUE.equals(stats.hasAmulet()))
        {
            amulet.render(g);
        }

        life.render(g);
        numberLife.render(g);
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
            text.draw(g, HEALTH_X, HEALTH_Y, hud.get(1));
            text.draw(g, HEALTH_X, HEALTH_Y + text.getSize(), hud.get(2));
        }
        else
        {
            text.draw(g, HEALTH_X, HEALTH_Y, hud.get(0));
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
        loadAmulet();
        loadLife();

        tick.start();
    }

    @Override
    public boolean isLoaded()
    {
        return heartSurface.isLoaded() && talisment.isLoaded() && sword.isLoaded() && life.isLoaded();
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        updaterHud.update(extrp);
        updaterPause.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        rendererHud.render(g);
    }

    @Override
    public void dispose()
    {
        heartSurface.dispose();
        hudSurface.dispose();
        number.dispose();
    }
}
