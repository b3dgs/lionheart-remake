/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.Collection;

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
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Liana feature implementation.
 * <ol>
 * <li>Check if collided by {@link Anim#ATTACK}.</li>
 * <li>Spawn destroy effect, destroy and resolve neighbor.</li>
 * </ol>
 */
@FeatureInterface
public final class Liana extends FeatureModel implements CollidableListener, Recyclable
{
    private static final int TILE_VOID = 912;

    /**
     * Get top tile.
     * 
     * @param tile The current tile.
     * @return The top tile number.
     */
    private static int getTop(Tile tile)
    {
        if (tile == null)
        {
            return 809;
        }
        switch (tile.getNumber())
        {
            case 812:
                return 809;
            case 818:
                return 809;
            case 817:
            case 821:
                return 815;
            case 811:
            case 814:
                return 808;
            case 813:
            case 820:
            case 838:
                return 810;
            default:
                return 809;
        }
    }

    /**
     * Get bottom tile.
     * 
     * @param tile The current tile.
     * @return The bottom tile number.
     */
    private static int getBottom(Tile tile)
    {
        if (tile == null)
        {
            return 835;
        }
        switch (tile.getNumber())
        {
            case 813:
                return 835;
            case 807:
            case 817:
                return 816;
            case 811:
            case 814:
                return 819;
            case 820:
                return 836;
            case 818:
            case 821:
                return 838;
            case 804:
                return 816;
            default:
                return 835;
        }
    }

    /**
     * Get ground tile.
     * 
     * @param number The tile number.
     * @return The ground tile number.
     */
    private static int getGround(int number)
    {
        if (number == 845)
        {
            return 843;
        }
        return 842;
    }

    private final MapTile map = services.get(MapTile.class);
    private final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
    private final MapTileCollision mapCollision = map.getFeature(MapTileCollision.class);
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
    public Liana(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);

        final Spawner spawner = services.get(Spawner.class);

        take = (collidable, with, by) ->
        {
            if (by.getName().startsWith(Anim.ATTACK))
            {
                final Tile tile = map.getTile(transformable, 0, 0);
                if (CollisionName.LIANA_TOP.equals(mapGroup.getGroup(tile)))
                {
                    final Tile top = map.getTile(tile.getInTileX(), tile.getInTileY() + 1);
                    final Tile top2 = map.getTile(tile.getInTileX(), tile.getInTileY() + 2);
                    final Tile bottom = map.getTile(tile.getInTileX(), tile.getInTileY() - 1);
                    final Tile bottom2 = map.getTile(tile.getInTileX(), tile.getInTileY() - 2);
                    final Collection<Tile> toUpdate = new ArrayList<>();
                    final boolean ground;
                    int number;

                    if (CollisionName.LIANA_TOP.equals(mapGroup.getGroup(top))
                        && bottom != null
                        && bottom.getNumber() != TILE_VOID
                        && !CollisionName.LIANA_TOP.equals(mapGroup.getGroup(bottom)))
                    {
                        map.setTile(top.getInTileX(), top.getInTileY(), getVoid(top));
                        mapGroup.changeGroup(top, null);
                        mapCollision.updateCollisions(top);
                        ground = true;
                    }
                    else
                    {
                        ground = false;
                    }

                    number = tile.getNumber();
                    map.setTile(tile.getInTileX(), tile.getInTileY(), getVoid(tile));
                    mapGroup.changeGroup(tile, null);
                    mapCollision.updateCollisions(tile);

                    if (CollisionName.LIANA_TOP.equals(mapGroup.getGroup(top))
                        && !CollisionName.LIANA_TOP.equals(mapGroup.getGroup(top2)))
                    {
                        map.setTile(top.getInTileX(), top.getInTileY(), getVoid(top));
                        mapGroup.changeGroup(top, null);
                        mapCollision.updateCollisions(top);
                    }
                    else
                    {
                        toUpdate.add(tile);
                    }
                    if (CollisionName.LIANA_TOP.equals(mapGroup.getGroup(bottom))
                        && bottom != null
                        && bottom2 != null
                        && !CollisionName.LIANA_TOP.equals(mapGroup.getGroup(bottom2)))
                    {
                        if (bottom2.getNumber() == TILE_VOID)
                        {
                            map.setTile(bottom.getInTileX(), bottom.getInTileY(), getVoid(bottom));
                            mapGroup.changeGroup(bottom, null);
                            mapCollision.updateCollisions(bottom);
                        }
                        else if (top != null
                                 && top.getNumber() != 907
                                 && top.getNumber() != 908
                                 && top.getNumber() != 702)
                        {
                            map.setTile(bottom.getInTileX(), bottom.getInTileY(), getGround(bottom.getNumber()));
                            mapGroup.changeGroup(bottom, CollisionName.GROUND);
                            mapCollision.updateCollisions(bottom);
                        }
                    }
                    else
                    {
                        toUpdate.add(tile);
                    }

                    for (final Tile current : toUpdate)
                    {
                        final Tile t = map.getTile(current.getInTileX(), current.getInTileY() + 1);
                        final Tile b = map.getTile(current.getInTileX(), current.getInTileY() - 1);

                        if (CollisionName.LIANA_TOP.equals(mapGroup.getGroup(t)))
                        {
                            map.setTile(t.getInTileX(),
                                        t.getInTileY(),
                                        getBottom(map.getTile(current.getInTileX(), current.getInTileY() + 2)));
                            mapCollision.updateCollisions(t);
                        }
                        if (CollisionName.LIANA_TOP.equals(mapGroup.getGroup(b))
                            && b.getNumber() != 708
                            && b.getNumber() != 709)
                        {
                            map.setTile(b.getInTileX(),
                                        b.getInTileY(),
                                        getTop(map.getTile(current.getInTileX(), current.getInTileY() - 2)));
                            mapCollision.updateCollisions(b);
                        }
                    }
                    toUpdate.clear();

                    if (ground && top != null && top.getNumber() != 6)
                    {
                        map.setTile(tile.getInTileX(), tile.getInTileY(), getGround(number));
                        mapGroup.changeGroup(tile, CollisionName.GROUND);
                        mapCollision.updateCollisions(tile);
                    }

                    spawner.spawn(Medias.create(Folder.EFFECT, "swamp", "ExplodeLiana.xml"), tile.getX(), tile.getY());
                    Sfx.MONSTER_HURT.play();
                }
                identifiable.destroy();
                current = CollidableListenerVoid.getInstance();
            }
        };
    }

    /**
     * Get void tile.
     * 
     * @param tile The current tile.
     * @return The void tile number.
     */
    private int getVoid(Tile tile)
    {
        final Tile top = map.getTile(tile.getInTileX(), tile.getInTileY() + 1);
        final Tile top2 = map.getTile(tile.getInTileX(), tile.getInTileY() + 2);

        // Case for ground liana hole
        if (top != null && top2 != null)
        {
            // Bottom part
            if (top.getNumber() == 909 && top2.getNumber() == 908
                || (top.getNumber() == TILE_VOID || top.getNumber() == 6) && top2.getNumber() == 701)
            {
                return 5;
            }
            else if (top.getNumber() == 6 && (top2.getNumber() == 702 || top2.getNumber() == 907)
                     || top.getNumber() == 5 && (top2.getNumber() == 907 || top2.getNumber() == 908))
            {
                return 8;
            }

            // Upper part
            if (top2.getNumber() == 545
                || top2.getNumber() == 547
                || top2.getNumber() == 842
                || top2.getNumber() == 843
                || top2.getNumber() == 98
                || top2.getNumber() == 101)
            {
                if (top.getNumber() == 907 || top.getNumber() == 701 || top.getNumber() == 702)
                {
                    return 6;
                }
                else if (top.getNumber() == 908)
                {
                    return 909;
                }
            }
        }
        // Case for other liana
        return TILE_VOID;
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
