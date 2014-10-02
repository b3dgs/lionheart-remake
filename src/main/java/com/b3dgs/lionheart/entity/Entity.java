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
package com.b3dgs.lionheart.entity;

import java.io.IOException;
import java.util.HashMap;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.Collision;
import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.platform.entity.EntityPlatformRastered;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionheart.AppLionheart;
import com.b3dgs.lionheart.CategoryType;
import com.b3dgs.lionheart.ThemeType;
import com.b3dgs.lionheart.map.Map;

/**
 * Abstract entity base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Entity
        extends EntityPlatformRastered
{
    /**
     * Get an entity configuration file.
     * 
     * @param category The category type.
     * @param theme The theme type.
     * @param type The config associated class.
     * @return The media config.
     */
    protected static Media getConfig(CategoryType category, ThemeType theme, Class<? extends Entity> type)
    {
        return Core.MEDIA.create(AppLionheart.ENTITIES_DIR, category.getPath(), theme.getPath(), type.getSimpleName()
                + "." + FactoryObjectGame.FILE_DATA_EXTENSION);
    }

    /** Entity status. */
    public final EntityStatus status;
    /** Animations list. */
    protected final HashMap<State, Animation> animations;
    /** Collisions data. */
    protected final HashMap<Enum<?>, Collision> collisions;
    /** Dead timer. */
    protected final Timing timerDie;
    /** Media reference. */
    private final Media media;
    /** Forces used. */
    private final Force[] forces;
    /** Map reference. */
    protected Map map;
    /** Mouse over state. */
    protected boolean over;
    /** Selected state. */
    protected boolean selected;
    /** Dead step. */
    protected int stepDie;
    /** Die location. */
    protected Coord dieLocation;
    /** Desired fps value. */
    private int desiredFps;
    /** Dead flag. */
    private boolean dead;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    protected Entity(SetupSurfaceRasteredGame setup)
    {
        super(setup, Map.TILE_HEIGHT);
        media = setup.getConfigFile();
        status = new EntityStatus();
        animations = new HashMap<>(4);
        collisions = new HashMap<>(4);
        timerDie = new Timing();
        dieLocation = Geom.createCoord();
        forces = new Force[0];
        final Configurer configurer = setup.getConfigurer();
        loadCollisions(configurer, EntityCollision.values());
        loadAnimations(configurer, EntityState.values());
        setFrame(configurer.getAnimation(EntityState.IDLE.getAnimationName()).getFirst());
    }

    /**
     * Called when this is hit by another entity.
     * 
     * @param entity The entity hit.
     */
    public abstract void hitBy(Entity entity);

    /**
     * Called when this hit that.
     * 
     * @param entity The entity hit.
     */
    public abstract void hitThat(Entity entity);

    /**
     * Update entity states.
     * 
     * @see EntityState
     */
    protected abstract void updateStates();

    /**
     * Update the entity in dead case.
     */
    protected abstract void updateDead();

    /**
     * Update the collisions detection.
     * 
     * @see EntityCollisionTile
     */
    protected abstract void updateCollisions();

    /**
     * Update the animations handling.
     * 
     * @param extrp The Extrapolation value.
     */
    protected abstract void updateAnimations(double extrp);

    /**
     * Called when all entities are loaded.
     */
    public void prepare()
    {
        // Nothing by default
    }

    /**
     * Kill entity.
     */
    public void kill()
    {
        dead = true;
        dieLocation.set(getLocationX(), getLocationY());
        stepDie = 0;
        timerDie.restart();
    }

    /**
     * Respawn entity.
     */
    public void respawn()
    {
        dead = false;
        resetGravity();
        mirror(false);
        updateMirror();
        status.setCollision(EntityCollisionTile.GROUND);
        status.backupCollision();
    }

    /**
     * Save entity.
     * 
     * @param file The file output.
     * @throws IOException If error.
     */
    public void save(FileWriting file) throws IOException
    {
        file.writeShort((short) getLocationIntX());
        file.writeShort((short) getLocationIntY());
    }

    /**
     * Load entity.
     * 
     * @param file The file input.
     * @throws IOException If error.
     */
    public void load(FileReading file) throws IOException
    {
        final int tx = file.readShort();
        final int ty = file.readShort();
        teleport(tx, ty);
    }

    /**
     * Set selection state.
     * 
     * @param selected The selected state.
     */
    public void setSelection(boolean selected)
    {
        this.selected = selected;
    }

    /**
     * Set over flag.
     * 
     * @param over The over flag.
     */
    public void setOver(boolean over)
    {
        this.over = over;
    }

    /**
     * Get the configuration file.
     * 
     * @return The configuration file.
     */
    public Media getConfig()
    {
        return media;
    }

    /**
     * Check if is over.
     * 
     * @return <code>true</code> if over, <code>false</code> else.
     */
    public boolean isOver()
    {
        return over;
    }

    /**
     * Check if is selected.
     * 
     * @return <code>true</code> if selected, <code>false</code> else.
     */
    public boolean isSelected()
    {
        return selected;
    }

    /**
     * Check if entity is dead.
     * 
     * @return <code>true</code> if dead, <code>false</code> else.
     */
    public boolean isDead()
    {
        return dead;
    }

    /**
     * Called when entity has been updated.
     */
    public void onUpdated()
    {
        // Nothing by default
    }

    /**
     * Load all existing animations defined in the config file.
     * 
     * @param states The states to load.
     * @param configurer The configurer reference.
     */
    protected final void loadAnimations(Configurer configurer, State[] states)
    {
        for (final State state : states)
        {
            try
            {
                animations.put(state, configurer.getAnimation(state.getAnimationName()));
            }
            catch (final LionEngineException exception)
            {
                continue;
            }
        }
    }

    /**
     * Load all collisions data.
     * 
     * @param values The collisions list.
     * @param configurer The configurer reference.
     */
    protected final void loadCollisions(Configurer configurer, Enum<?>[] values)
    {
        for (final Enum<?> collision : values)
        {
            try
            {
                collisions.put(collision, configurer.getCollision(collision.toString()));
            }
            catch (final LionEngineException exception)
            {
                continue;
            }
        }
        setCollision(collisions.get(EntityCollision.DEFAULT));
    }

    /**
     * Get forces involved in gravity and movement. Return empty array by default.
     * 
     * @return The forces list.
     */
    protected Force[] getForces()
    {
        return forces;
    }

    /*
     * EntityPlatform
     */

    @Override
    public void prepare(ContextGame context)
    {
        map = context.getService(Map.class);
        desiredFps = context.getService(Integer.class).intValue();
    }

    @Override
    protected void handleActions(double extrp)
    {
        status.backupState();
        updateStates();
    }

    @Override
    protected void handleMovements(double extrp)
    {
        updateGravity(extrp, desiredFps, getForces());
        updateMirror();
        if (dead)
        {
            updateDead();
        }
    }

    @Override
    protected void handleCollisions(double extrp)
    {
        status.backupCollision();
        if (!isDead())
        {
            updateCollisions();
        }
    }

    @Override
    protected void handleAnimations(double extrp)
    {
        updateAnimations(extrp);
        if (status.stateChanged())
        {
            play(animations.get(status.getState()));
        }
        updateAnimation(extrp);
    }
}
