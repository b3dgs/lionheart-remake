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
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionengine.game.configurer.ConfigAnimations;
import com.b3dgs.lionengine.game.configurer.ConfigCollisions;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.platform.entity.EntityPlatformRastered;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionheart.AppLionheart;
import com.b3dgs.lionheart.CategoryType;
import com.b3dgs.lionheart.ThemeType;
import com.b3dgs.lionheart.entity.player.Valdyn;
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
    private final HashMap<State, Animation> animations;
    /** Collisions data. */
    private final HashMap<Enum<?>, Collision> collisions;
    /** Dead timer. */
    private final Timing timerDie;
    /** Media reference. */
    private final Media media;
    /** Die location. */
    private final Coord dieLocation;
    /** Map reference. */
    private Map map;
    /** Mouse over state. */
    private boolean over;
    /** Selected state. */
    private boolean selected;
    /** Dead step. */
    private int stepDie;
    /** Directions used. */
    private Direction[] directions;
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
        directions = new Direction[0];
        final Configurer configurer = setup.getConfigurer();
        final ConfigAnimations configAnimations = ConfigAnimations.create(configurer);
        loadCollisions(configurer, EntityCollision.values());
        loadAnimations(configAnimations, EntityState.values());
        setFrame(configAnimations.getAnimation(EntityState.IDLE.getAnimationName()).getFirst());
    }

    /**
     * Check the collision with player.
     * 
     * @param entity The player to check collision with.
     */
    public abstract void checkCollision(Valdyn entity);

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
     * @param map The map reference.
     * @see EntityCollisionTile
     */
    protected abstract void updateCollisions(Map map);

    /**
     * Get a collision data from its key.
     * 
     * @param key The collision key.
     * @return The collision data.
     */
    public final Collision getCollisionData(Enum<?> key)
    {
        return collisions.get(key);
    }

    /**
     * Get an animation data from its key.
     * 
     * @param state The animation state.
     * @return The animation data.
     */
    public final Animation getAnimationData(State state)
    {
        return animations.get(state);
    }

    /**
     * Get the death time elapsed.
     * 
     * @return The death time elapsed.
     */
    public final long getDeathTime()
    {
        return timerDie.elapsed();
    }

    /**
     * Get the configuration file.
     * 
     * @return The configuration file.
     */
    public final Media getConfig()
    {
        return media;
    }

    /**
     * Set selection state.
     * 
     * @param selected The selected state.
     */
    public final void setSelection(boolean selected)
    {
        this.selected = selected;
    }

    /**
     * Set over flag.
     * 
     * @param over The over flag.
     */
    public final void setOver(boolean over)
    {
        this.over = over;
    }

    /**
     * Set a collision data from its key.
     * 
     * @param key The collision key.
     */
    public final void setCollision(Enum<?> key)
    {
        setCollision(collisions.get(key));
    }

    /**
     * Check if is over.
     * 
     * @return <code>true</code> if over, <code>false</code> else.
     */
    public final boolean isOver()
    {
        return over;
    }

    /**
     * Check if is selected.
     * 
     * @return <code>true</code> if selected, <code>false</code> else.
     */
    public final boolean isSelected()
    {
        return selected;
    }

    /**
     * Check if entity is dead.
     * 
     * @return <code>true</code> if dead, <code>false</code> else.
     */
    public final boolean isDead()
    {
        return dead;
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
     * @param configAnimations The configurer reference.
     */
    protected final void loadAnimations(ConfigAnimations configAnimations, State[] states)
    {
        for (final State state : states)
        {
            try
            {
                animations.put(state, configAnimations.getAnimation(state.getAnimationName()));
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
        final ConfigCollisions configCollisions = ConfigCollisions.create(configurer);
        for (final Enum<?> collision : values)
        {
            try
            {
                collisions.put(collision, configCollisions.getCollision(collision.toString()));
            }
            catch (final LionEngineException exception)
            {
                continue;
            }
        }
        setCollision(EntityCollision.DEFAULT);
    }

    /**
     * Get the death location.
     * 
     * @return The death location.
     */
    protected final Coord getDeathLocation()
    {
        return dieLocation;
    }

    /**
     * Get the current die step.
     * 
     * @return The die step.
     */
    protected final int getStepDie()
    {
        return stepDie;
    }

    /**
     * Reset the step die to 1.
     */
    protected final void resetStepDie()
    {
        stepDie = 1;
    }

    /**
     * Set forces involved in gravity and movement.
     * 
     * @param directions The directions list.
     */
    protected final void setDirections(Direction... directions)
    {
        this.directions = directions;
    }

    /**
     * Check if the death time is elapsed.
     * 
     * @param time The time to check.
     * @return <code>true</code> if elapsed, <code>false</code> else.
     */
    protected final boolean isDeathTimeElapsed(long time)
    {
        return timerDie.elapsed(time);
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
        updateGravity(extrp, desiredFps, directions);
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
            updateCollisions(map);
        }
    }

    @Override
    protected void handleAnimations(double extrp)
    {
        if (status.stateChanged())
        {
            play(getAnimationData(status.getState()));
        }
        updateAnimation(extrp);
    }
}
