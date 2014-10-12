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
package com.b3dgs.lionheart;

import java.io.IOException;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionheart.effect.HandlerEffect;
import com.b3dgs.lionheart.entity.FactoryEntity;
import com.b3dgs.lionheart.entity.HandlerEntity;
import com.b3dgs.lionheart.entity.player.StatsRenderer;
import com.b3dgs.lionheart.entity.player.Valdyn;
import com.b3dgs.lionheart.landscape.FactoryLandscape;
import com.b3dgs.lionheart.landscape.Landscape;
import com.b3dgs.lionheart.launcher.FactoryLauncher;
import com.b3dgs.lionheart.map.Map;
import com.b3dgs.lionheart.projectile.FactoryProjectile;
import com.b3dgs.lionheart.projectile.HandlerProjectile;

/**
 * Represents the game layer, handling the landscape, the map, and the entities.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class World
        extends WorldGame
{
    /** Level reference. */
    final Level level;
    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Map reference. */
    private final Map map;
    /** Background factory. */
    private final FactoryLandscape factoryLandscape;
    /** Entity factory. */
    private final FactoryEntity factoryEntity;
    /** Handler entity. */
    private final HandlerEntity handlerEntity;
    /** Handler effect. */
    private final HandlerEffect handlerEffect;
    /** Handler projectile reference. */
    public final HandlerProjectile handlerProjectile;
    /** Factory projectile reference. */
    public final FactoryProjectile factoryProjectile;
    /** Factory launcher. */
    public final FactoryLauncher factoryLauncher;
    /** Stats renderer. */
    private final StatsRenderer statsRenderer;
    /** Landscape reference. */
    private Landscape landscape;
    /** Player reference. */
    private Valdyn player;
    /** Game over. */
    private boolean gameOver;

    /**
     * Constructor.
     * 
     * @param config The config reference.
     * @param keyboard The keyboard reference.
     */
    World(Config config, Keyboard keyboard)
    {
        super(config);

        this.keyboard = keyboard;
        final double scaleH = width / (double) Scene.SCENE_DISPLAY.getWidth();
        final double scaleV = height / (double) Scene.SCENE_DISPLAY.getHeight();

        camera = new CameraPlatform(width, height);
        map = new Map();
        factoryLandscape = new FactoryLandscape(source, scaleH, scaleV, false);
        factoryEntity = new FactoryEntity();
        handlerEntity = new HandlerEntity(camera, factoryEntity);
        handlerProjectile = new HandlerProjectile(camera, handlerEntity);
        factoryProjectile = new FactoryProjectile();
        factoryLauncher = new FactoryLauncher();
        level = new Level(camera, map, factoryEntity, handlerEntity);
        statsRenderer = new StatsRenderer(width);
        handlerEffect = level.handlerEffect;

        createContextsAndPreparators();
    }

    /**
     * Create the contexts and assign them.
     */
    private void createContextsAndPreparators()
    {
        final ContextGame contextEntity = new ContextGame();
        contextEntity.addService(camera);
        contextEntity.addService(map);
        contextEntity.addService(Integer.valueOf(source.getRate()));

        final ContextGame contextLauncher = new ContextGame();
        contextLauncher.addService(factoryProjectile);
        contextLauncher.addService(handlerProjectile);

        factoryEntity.setContext(contextEntity);
        factoryLauncher.setContext(contextLauncher);
    }

    /**
     * Called when the resolution changed.
     * 
     * @param width The new width.
     * @param height The new height.
     */
    public void setScreenSize(int width, int height)
    {
        landscape.setScreenSize(width, height);
        camera.setView(0, 0, width, height);
        camera.setScreenSize(width, height);
        statsRenderer.setScreenWidth(width);
    }

    /**
     * Get the game over state.
     * 
     * @return <code>true</code> if game is over, <code>false</code> else.
     */
    public boolean isGameOver()
    {
        return gameOver;
    }

    /*
     * WorldGame
     */

    @Override
    public void update(double extrp)
    {
        player.updateControl(keyboard);
        player.update(extrp);
        handlerEntity.update(extrp);
        handlerEffect.update(extrp);
        handlerProjectile.update(extrp);
        camera.follow(player);
        landscape.update(extrp, camera);
        if (player.isDestroyed() || level.checkEnd(player))
        {
            gameOver = true;
        }
    }

    @Override
    public void render(Graphic g)
    {
        landscape.renderBackground(g);
        map.render(g, camera);
        handlerEntity.render(g);
        handlerProjectile.render(g);
        handlerEffect.render(g);
        player.render(g, camera);
        if (AppLionheart.SHOW_COLLISIONS)
        {
            player.renderCollision(g, camera);
        }
        landscape.renderForeground(g);
        statsRenderer.render(g, player.stats);
        if (player.isDead() && player.getDeathTime() > 1500)
        {
            g.setColor(ColorRgba.BLACK);
            g.drawRect(0, 0, width, height, true);
        }
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        level.save(file);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        level.load(file);
        player = factoryEntity.create(Valdyn.MEDIA);
        handlerEntity.setPlayer(player);
        landscape = factoryLandscape.createLandscape(level.getLandscape());
        player.setLandscape(landscape);
        statsRenderer.load();
        camera.setIntervals(32, 0);
        handlerEntity.update(1.0);
        handlerEntity.prepare();
        player.setCheckpoints(level.worldData.getCheckpoints());
        player.respawn(level.worldData.getStartX(), level.worldData.getStartY());
        // map.createCollisionDraw(TileCollision.class);
    }
}
