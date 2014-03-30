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
package com.b3dgs.lionheart.map;

import java.util.EnumSet;
import java.util.Set;

import com.b3dgs.lionengine.game.platform.CollisionFunction;
import com.b3dgs.lionengine.game.platform.CollisionTile;
import com.b3dgs.lionengine.game.platform.CollisionTileModel;

/**
 * List of collision types.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum TileCollision implements CollisionTile
{
    /** None. */
    NONE(TileCollisionGroup.NONE),
    /** Ground. */
    GROUND(TileCollisionGroup.FLAT),
    /** Ground top. */
    GROUND_TOP(TileCollisionGroup.FLAT),
    /** Ground hoockable. */
    GROUND_HOOKABLE(TileCollisionGroup.FLAT),
    /** Ground with spike. */
    GROUND_SPIKE(TileCollisionGroup.FLAT),
    /** Ground slide link. */
    GROUND_SLIDE(TileCollisionGroup.FLAT),
    /** Slope right top \. */
    SLOPE_RIGHT_1(TileCollisionGroup.SLOPE, false),
    /** Slope right middle \. */
    SLOPE_RIGHT_2(TileCollisionGroup.SLOPE, false),
    /** Slope right bottom \. */
    SLOPE_RIGHT_3(TileCollisionGroup.SLOPE, false),
    /** Slope left top /. */
    SLOPE_LEFT_1(TileCollisionGroup.SLOPE, true),
    /** Slope left middle /. */
    SLOPE_LEFT_2(TileCollisionGroup.SLOPE, true),
    /** Slope left bottom /. */
    SLOPE_LEFT_3(TileCollisionGroup.SLOPE, true),
    /** Slide right top. */
    SLIDE_RIGHT_1(TileCollisionGroup.SLIDE, false),
    /** Slide right middle. */
    SLIDE_RIGHT_2(TileCollisionGroup.SLIDE, false),
    /** Slide right bottom. */
    SLIDE_RIGHT_3(TileCollisionGroup.SLIDE, false),
    /** Slide right ground slide. */
    SLIDE_RIGHT_GROUND_SLIDE(TileCollisionGroup.SLIDE, false),
    /** Slide left top. */
    SLIDE_LEFT_1(TileCollisionGroup.SLIDE, true),
    /** Slide middle top. */
    SLIDE_LEFT_2(TileCollisionGroup.SLIDE, true),
    /** Slide bottom top. */
    SLIDE_LEFT_3(TileCollisionGroup.SLIDE, true),
    /** Slide left ground slide. */
    SLIDE_LEFT_GROUND_SLIDE(TileCollisionGroup.SLIDE, true),
    /** Pillar vertical. */
    PILLAR_VERTICAL(TileCollisionGroup.PILLAR),
    /** Pillar horizontal. */
    PILLAR_HORIZONTAL(TileCollisionGroup.PILLAR),
    /** Ground steep right top. */
    STEEP_RIGHT_1(TileCollisionGroup.STEEP, false),
    /** Ground steep right bottom. */
    STEEP_RIGHT_2(TileCollisionGroup.STEEP, false),
    /** Ground steep left top. */
    STEEP_LEFT_1(TileCollisionGroup.STEEP, true),
    /** Ground steep left bottom. */
    STEEP_LEFT_2(TileCollisionGroup.STEEP, true),
    /** Liana horizontal. */
    LIANA_HORIZONTAL(TileCollisionGroup.LIANA_HORIZONTAL),
    /** Liana steep right top. */
    LIANA_STEEP_RIGHT_1(TileCollisionGroup.LIANA_STEEP, false),
    /** Liana steep right bottom. */
    LIANA_STEEP_RIGHT_2(TileCollisionGroup.LIANA_STEEP, false),
    /** Liana steep left top. */
    LIANA_STEEP_LEFT_1(TileCollisionGroup.LIANA_STEEP, true),
    /** Liana steep left bottom. */
    LIANA_STEEP_LEFT_2(TileCollisionGroup.LIANA_STEEP, true),
    /** Liana leaning right top. */
    LIANA_LEANING_RIGHT_1(TileCollisionGroup.LIANA_LEANING, false),
    /** Liana leaning right top. */
    LIANA_LEANING_RIGHT_2(TileCollisionGroup.LIANA_LEANING, false),
    /** Liana leaning right top. */
    LIANA_LEANING_RIGHT_3(TileCollisionGroup.LIANA_LEANING, false),
    /** Liana leaning left top. */
    LIANA_LEANING_LEFT_1(TileCollisionGroup.LIANA_LEANING, true),
    /** Liana leaning left top. */
    LIANA_LEANING_LEFT_2(TileCollisionGroup.LIANA_LEANING, true),
    /** Liana leaning left top. */
    LIANA_LEANING_LEFT_3(TileCollisionGroup.LIANA_LEANING, true);

    /** Vertical collisions list. */
    public static final EnumSet<TileCollision> COLLISION_VERTICAL = EnumSet.noneOf(TileCollision.class);
    /** Horizontal collisions list. */
    public static final EnumSet<TileCollision> COLLISION_HORIZONTAL = EnumSet.noneOf(TileCollision.class);
    /** Vertical collisions list. */
    public static final EnumSet<TileCollision> COLLISION_LIANA_STEEP = EnumSet.noneOf(TileCollision.class);
    /** Vertical collisions list. */
    public static final EnumSet<TileCollision> COLLISION_LIANA_LEANING = EnumSet.noneOf(TileCollision.class);
    /** Vertical collisions list. */
    public static final EnumSet<TileCollision> COLLISION_GROUND_HOOCKABLE = EnumSet.noneOf(TileCollision.class);

    /**
     * Static init.
     */
    static
    {
        TileCollision.COLLISION_VERTICAL.add(TileCollision.GROUND);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.GROUND_TOP);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.GROUND_SPIKE);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.GROUND_HOOKABLE);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.GROUND_SLIDE);

        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLOPE_LEFT_1);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLOPE_LEFT_2);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLOPE_LEFT_3);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLOPE_RIGHT_1);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLOPE_RIGHT_2);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLOPE_RIGHT_3);

        TileCollision.COLLISION_VERTICAL.add(TileCollision.PILLAR_HORIZONTAL);

        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLIDE_LEFT_1);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLIDE_LEFT_2);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLIDE_LEFT_3);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLIDE_LEFT_GROUND_SLIDE);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLIDE_RIGHT_1);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLIDE_RIGHT_2);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLIDE_RIGHT_3);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLIDE_RIGHT_GROUND_SLIDE);

        TileCollision.COLLISION_VERTICAL.add(TileCollision.STEEP_RIGHT_1);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.STEEP_RIGHT_2);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.STEEP_LEFT_1);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.STEEP_LEFT_2);

        TileCollision.COLLISION_LIANA_STEEP.add(TileCollision.LIANA_HORIZONTAL);
        TileCollision.COLLISION_LIANA_STEEP.add(TileCollision.LIANA_STEEP_RIGHT_1);
        TileCollision.COLLISION_LIANA_STEEP.add(TileCollision.LIANA_STEEP_RIGHT_2);
        TileCollision.COLLISION_LIANA_STEEP.add(TileCollision.LIANA_STEEP_LEFT_1);
        TileCollision.COLLISION_LIANA_STEEP.add(TileCollision.LIANA_STEEP_LEFT_2);

        TileCollision.COLLISION_GROUND_HOOCKABLE.add(TileCollision.GROUND_HOOKABLE);

        TileCollision.COLLISION_LIANA_LEANING.add(TileCollision.LIANA_LEANING_LEFT_1);
        TileCollision.COLLISION_LIANA_LEANING.add(TileCollision.LIANA_LEANING_LEFT_2);
        TileCollision.COLLISION_LIANA_LEANING.add(TileCollision.LIANA_LEANING_LEFT_3);
        TileCollision.COLLISION_LIANA_LEANING.add(TileCollision.LIANA_LEANING_RIGHT_1);
        TileCollision.COLLISION_LIANA_LEANING.add(TileCollision.LIANA_LEANING_RIGHT_2);
        TileCollision.COLLISION_LIANA_LEANING.add(TileCollision.LIANA_LEANING_RIGHT_3);

        TileCollision.COLLISION_HORIZONTAL.add(TileCollision.GROUND_SPIKE);
        TileCollision.COLLISION_HORIZONTAL.add(TileCollision.PILLAR_VERTICAL);
        TileCollision.COLLISION_HORIZONTAL.add(TileCollision.GROUND_SLIDE);
    }

    /** Group. */
    private final TileCollisionGroup group;
    /** Left flag. */
    private final boolean left;

    /**
     * Constructor.
     * 
     * @param group The collision group.
     */
    private TileCollision(TileCollisionGroup group)
    {
        this(group, false);
    }

    /**
     * Constructor.
     * 
     * @param group The collision group.
     * @param left The side (true = left, false = right).
     */
    private TileCollision(TileCollisionGroup group, boolean left)
    {
        this.group = group;
        this.left = left;
    }

    /**
     * Get the tile collision group.
     * 
     * @return The tile collision group.
     */
    public TileCollisionGroup getGroup()
    {
        return group;
    }

    /**
     * Get the left right flag (not used for flat collision group).
     * 
     * @return <code>true</code> if left, <code>false</code> if right.
     */
    public boolean isLeft()
    {
        return left;
    }

    /** Model. */
    private final CollisionTileModel model = new CollisionTileModel();

    /*
     * CollisionTile
     */

    @Override
    public void addCollisionFunction(CollisionFunction function)
    {
        model.addCollisionFunction(function);
    }

    @Override
    public void removeCollisionFunction(CollisionFunction function)
    {
        model.removeCollisionFunction(function);
    }

    @Override
    public Set<CollisionFunction> getCollisionFunctions()
    {
        return model.getCollisionFunctions();
    }
}
