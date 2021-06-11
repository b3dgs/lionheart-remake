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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListenerVoid;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionheart.LoadNextStage;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.MusicPlayer;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;

/**
 * Takeable feature implementation.
 * <ol>
 * <li>Listen to {@link CollidableListener}.</li>
 * <li>Check if collided by {@link Anim#BODY}.</li>
 * <li>Apply taken stats, spawn effect, destroy.</li>
 * </ol>
 */
@FeatureInterface
public final class Takeable extends FeatureModel implements CollidableListener, Recyclable
{
    private static final int AMULET_TICK = 520;

    private final MusicPlayer player = services.get(MusicPlayer.class);
    private final LoadNextStage stageLoader = services.get(LoadNextStage.class);
    private final CollidableListener take;

    private CollidableListener current;

    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private EntityModel model;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Takeable(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);

        final Spawner spawner = services.get(Spawner.class);
        final TakeableConfig config = TakeableConfig.imports(setup);

        take = (collidable, with, by) ->
        {
            if (with.getName().startsWith(CollisionName.TAKE) && by.getName().startsWith(CollisionName.BODY))
            {
                if (config.isAmulet())
                {
                    player.playMusic(Music.SECRET_WIN);
                    model.getNext()
                         .ifPresent(next -> stageLoader.loadNextStage(next, AMULET_TICK, model.getNextSpawn()));
                }
                else
                {
                    config.getSfx().play();
                }
                collidable.getFeature(Stats.class).apply(config);
                spawner.spawn(config.getEffect(), transformable);
                identifiable.destroy();
                current = CollidableListenerVoid.getInstance();
            }
        };
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        current.notifyCollided(collidable, with, by);
    }

    @Override
    public void recycle()
    {
        current = take;
    }
}
