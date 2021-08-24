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
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.FeatureProvider;
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
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Block feature implementation.
 * <ol>
 * <li>Check if collided by {@link Anim#ATTACK}.</li>
 * <li>Spawn destroy effect and destroy.</li>
 * </ol>
 */
@FeatureInterface
public final class Block extends FeatureModel implements CollidableListener, Recyclable
{
    private final MapTile map = services.get(MapTile.class);
    private final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
    private final CollidableListener take;

    private CollidableListener current;

    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Collidable collidable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Block(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);

        final Spawner spawner = services.get(Spawner.class);

        take = (collidable, with, by) ->
        {
            if (by.getName().startsWith(Anim.ATTACK))
            {
                final Tile tile = map.getTile(transformable, 0, 0);
                if (CollisionName.BLOCK.equals(mapGroup.getGroup(tile)))
                {
                    map.removeTile(tile.getInTileX(), tile.getInTileY());

                    spawner.spawn(Medias.create(Folder.EFFECT, "ancienttown", "ExplodeBlock.xml"),
                                  tile.getX(),
                                  tile.getY());
                    Sfx.MONSTER_HURT.play();
                }
                identifiable.destroy();
                current = CollidableListenerVoid.getInstance();
            }
        };
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        collidable.setCollisionVisibility(Constant.DEBUG_COLLISIONS);
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
