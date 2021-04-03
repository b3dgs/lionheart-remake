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
package com.b3dgs.lionheart.object.state.attack;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.state.StateCrouch;
import com.b3dgs.lionheart.object.state.StateFall;
import com.b3dgs.lionheart.object.state.StateJump;

/**
 * Fall attack state implementation.
 */
public final class StateAttackFall extends State
{
    private static final double BOUNCE_SPEED = 0.02;
    private static final double BOUNCE_LIANA = 3.0;

    private final MapTile map = model.getMap();
    private final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
    private final MapTileCollision mapCollision = map.getFeature(MapTileCollision.class);
    private final Spawner spawner = model.getSpawner();

    private final AtomicBoolean collideSword = new AtomicBoolean();

    private double bounce;
    private double bounceAcc;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateAttackFall(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateCrouch.class, () -> !steep.is() && collideY.get() && isGoDown());
        addTransition(StateJump.class, () -> collideSword.get() && jump.getDirectionVertical() > 0);
        addTransition(StateFall.class,
                      () -> steep.is()
                            || !isGoDown() && collideY.get()
                            || !isFire() && Double.compare(jump.getDirectionVertical(), 0.0) <= 0);
    }

    /**
     * Perform jump on hit.
     * 
     * @param vy The bounce vertical.
     */
    private void jumpHit(double vy)
    {
        jump.setDirection(new Force(0, vy));
        jump.setDirectionMaximum(new Force(0, vy));

        body.resetGravity();
        collideSword.set(true);
        bounce = 0.0;
        bounceAcc = BOUNCE_SPEED;
    }

    @Override
    protected void onCollideLeg(CollisionResult result, CollisionCategory category)
    {
        super.onCollideLeg(result, category);

        final Tile tile = result.getTile();
        final Tile liana = map.getTile(tile.getInTileX(), tile.getInTileY() - 1);
        if (mapGroup.getGroup(liana).equals(CollisionName.LIANA_FULL))
        {
            map.setTile(liana.getInTileX(), liana.getInTileY(), liana.getNumber() + 206);
            mapCollision.updateCollisions(tile);
            jumpHit(BOUNCE_LIANA);
            spawner.spawn(Medias.create(Folder.EFFECT, "swamp", "ExplodeLiana.xml"), liana.getX(), liana.getY());
            Sfx.MONSTER_HURT.play();
        }
    }

    @Override
    protected void onCollided(Collidable collidable, Collision with, Collision by)
    {
        super.onCollided(collidable, with, by);

        if (!collideSword.get()
            && by.getName().startsWith(CollisionName.BODY)
            && with.getName().startsWith(Anim.ATTACK_FALL))
        {
            final double vy = UtilMath.clamp(bounce, Constant.JUMP_MIN, Constant.JUMP_HIT);
            jumpHit(vy);
        }
    }

    @Override
    public void enter()
    {
        super.enter();

        collideSword.set(false);
        bounce = 0.0;
        bounceAcc = BOUNCE_SPEED;
    }

    @Override
    public void update(double extrp)
    {
        if (transformable.getY() < transformable.getOldY())
        {
            bounce += bounceAcc;
            bounceAcc += BOUNCE_SPEED;
        }

        if (Double.compare(jump.getDirectionVertical(), 0.0) > 0)
        {
            body.resetGravity();
        }

        if (isGoHorizontal())
        {
            movement.setVelocity(Constant.WALK_VELOCITY_MAX);
        }
        else
        {
            movement.setVelocity(0.07);
        }
        movement.setDestination(device.getHorizontalDirection() * Constant.WALK_SPEED, 0.0);
    }
}
