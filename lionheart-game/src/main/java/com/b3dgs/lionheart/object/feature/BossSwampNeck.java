/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.Constant;

/**
 * Boss Swamp 1 bowl feature implementation.
 * <ol>
 * <li>Move horizontally.</li>
 * </ol>
 */
@FeatureInterface
public final class BossSwampNeck extends FeatureModel implements RoutineUpdate, CollidableListener, Recyclable
{
    private static final int OFFSET_X = -26;
    private static final int OFFSET_Y = 87;
    private static final int HIT_DELAY_MS = 30;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);

    private final Transformable transformable;
    private final Animatable animatable;
    private final Rasterable rasterable;
    private final Collidable collidable;
    private final Identifiable identifiable;

    private final Tick tick = new Tick();

    private CollidableListener listener;
    private int frame;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @param animatable The animatable feature.
     * @param rasterable The rasterable feature.
     * @param collidable The collidable feature.
     * @param identifiable The identifiable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public BossSwampNeck(Services services,
                         Setup setup,
                         Transformable transformable,
                         Animatable animatable,
                         Rasterable rasterable,
                         Collidable collidable,
                         Identifiable identifiable)
    {
        super(services, setup);

        this.transformable = transformable;
        this.animatable = animatable;
        this.rasterable = rasterable;
        this.collidable = collidable;
        this.identifiable = identifiable;

        collidable.setCollisionVisibility(Constant.DEBUG_COLLISIONS);
    }

    /**
     * Set location.
     * 
     * @param localizable The location.
     */
    public void setLocation(Localizable localizable)
    {
        transformable.setLocation(localizable.getX() + OFFSET_X, localizable.getY() + OFFSET_Y);
    }

    /**
     * Set enabled flag.
     * 
     * @param listener The collidable listener.
     */
    public void setEnabled(CollidableListener listener)
    {
        this.listener = listener;
        rasterable.setVisibility(listener != null);
        collidable.setEnabled(listener != null);
    }

    /**
     * Set the frame offset.
     * 
     * @param offset The frame offset.
     */
    public void setFrameOffset(int offset)
    {
        rasterable.setAnimOffset(UtilMath.clamp(offset, 0, 2) * 2);
    }

    /**
     * Destroy.
     */
    public void destroy()
    {
        identifiable.destroy();
    }

    @Override
    public void update(double extrp)
    {
        if (collidable.isEnabled())
        {
            tick.update(extrp);
            if (tick.elapsedTime(source.getRate(), HIT_DELAY_MS))
            {
                frame++;
                if (frame > 2)
                {
                    frame = 1;
                }
                rasterable.setVisibility(true);
                animatable.setFrame(frame);
                tick.restart();
            }
            transformable.check(true);
        }
        else
        {
            rasterable.setVisibility(false);
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (listener != null)
        {
            listener.notifyCollided(collidable, with, by);
        }
    }

    @Override
    public void recycle()
    {
        frame = 1;
        collidable.setEnabled(true);
        rasterable.setVisibility(true);
        animatable.setFrame(frame);
        tick.restart();
    }
}
