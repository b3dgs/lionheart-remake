/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.rasterable.RasterableModel;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.EntityConfig;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.XmlLoader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Bounce bullet on hit ground.
 */
@FeatureInterface
public final class BulletBounceOnGround extends FeatureModel implements XmlLoader, XmlSaver, Routine, Recyclable,
                                        TileCollidableListener, CollidableListener
{
    private static final String NODE = "bulletBounceOnGround";
    private static final String ATT_SFX = "sfx";
    private static final String ATT_COUNT = "count";

    private static final double BOUNCE_MAX = 3.0;
    private static final int BOUNCE_DELAY_MS = 60;

    /**
     * Get horizontal side.
     * 
     * @param result The result reference.
     * @return The side value.
     */
    private static int getSideX(CollisionResult result)
    {
        final int sideX;
        if (result.contains(CollisionName.LEFT))
        {
            sideX = -1;
        }
        else if (result.contains(CollisionName.RIGHT))
        {
            sideX = 1;
        }
        else
        {
            sideX = 0;
        }
        return sideX;
    }

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Viewer viewer = services.get(Viewer.class);

    private final Body body;
    private final Launchable launchable;
    private final TileCollidable tileCollidable;
    private final Transformable transformable;
    private final Animatable animatable;
    private final EntityModel model;
    private final Hurtable hurtable;

    private final Tick tick = new Tick();
    private final Animation idle;
    private final Sfx sfx;
    private final int count;

    private Rasterable rasterable;
    private Force jump;
    private double bounceX;
    private int bounced;
    private double extrp = com.b3dgs.lionengine.Constant.EXTRP;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param body The body feature.
     * @param launchable The launchable feature.
     * @param tileCollidable The tile collidable feature.
     * @param transformable The transformable feature.
     * @param mirrorable The mirrorable feature.
     * @param animatable The animatable feature.
     * @param model The model feature.
     * @param hurtable The hurtable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public BulletBounceOnGround(Services services,
                                Setup setup,
                                Body body,
                                Launchable launchable,
                                TileCollidable tileCollidable,
                                Transformable transformable,
                                Mirrorable mirrorable,
                                Animatable animatable,
                                EntityModel model,
                                Hurtable hurtable)
    {
        super(services, setup);

        this.body = body;
        this.launchable = launchable;
        this.tileCollidable = tileCollidable;
        this.transformable = transformable;
        this.animatable = animatable;
        this.model = model;
        this.hurtable = hurtable;

        idle = AnimationConfig.imports(setup).getAnimation(Anim.IDLE);
        sfx = Sfx.valueOf(setup.getString(ATT_SFX, NODE));
        count = setup.getInteger(0, ATT_COUNT, NODE);

        jump = model.getJump();
        jump.setVelocity(0.12);
        jump.setSensibility(0.5);
        jump.setDestination(0.0, 0.0);
        jump.setDirection(0.0, 0.0);

        rasterable = new RasterableModel(services,
                                         new SetupSurfaceRastered(setup.getMedia()),
                                         transformable,
                                         mirrorable,
                                         animatable)
        {
            @Override
            public int getRasterIndex(double y)
            {
                return (int) (transformable.getHeight()
                              - UtilMath.clamp(transformable.getY()
                                               + UtilMath.getRounded(transformable.getHeight(), 16)
                                               - 32,
                                               0,
                                               transformable.getHeight()));
            }
        };
        rasterable.prepare(this);
    }

    /**
     * Load raster data.
     * 
     * @param raster The raster folder.
     */
    public void loadRaster(String raster)
    {
        rasterable.setRaster(false, Medias.create(raster, Constant.RASTER_FILE_WATER), transformable.getHeight());
    }

    /**
     * Load init move.
     * 
     * @param vx The horizontal direction.
     */
    public void load(double vx)
    {
        model.getMovement().setDirection(vx, 0.0);
        model.getMovement().setDestination(vx, 0.0);
    }

    @Override
    public void load(XmlReader root)
    {
        load(root.getDouble(0.0, EntityConfig.ATT_VX));
    }

    @Override
    public void save(Xml root)
    {
        final double vx = model.getMovement().getDirectionVertical();
        if (Double.compare(vx, 0.0) != 0)
        {
            root.writeDouble(EntityConfig.ATT_VX, model.getMovement().getDirectionVertical());
        }
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        if (result.contains(CollisionName.SPIKE))
        {
            hurtable.kill(true);
        }
        else if ((count == 0 || bounced < count)
                 && category.getName().contains(CollisionName.LEG)
                 && tick.elapsedTime(source.getRate(), BOUNCE_DELAY_MS))
        {
            if (result.containsY(CollisionName.GROUND)
                || result.containsY(CollisionName.SLOPE)
                || result.containsY(CollisionName.INCLINE)
                || result.containsY(CollisionName.BLOCK))
            {
                final double bounce = UtilMath.clamp(Math.abs(transformable.getOldY() - transformable.getY()) / extrp,
                                                     0.0,
                                                     BOUNCE_MAX);
                if (bounce > 0.5 && viewer.isViewable(transformable, 0, 0))
                {
                    sfx.play();
                }
                body.resetGravity();
                tick.restart();
                tileCollidable.apply(result);
                transformable.teleportY(transformable.getY() + 1);
                transformable.check(true);

                final int sideX = getSideX(result);
                if (result.containsY(CollisionName.SLOPE))
                {
                    bounceX += 0.6 * sideX;
                    jump.setDestination(bounceX, 0.0);
                }
                if (result.containsY(CollisionName.INCLINE))
                {
                    bounceX += 0.9 * sideX;
                    jump.setDestination(bounceX, 0.0);
                }
                bounceX = UtilMath.clamp(bounceX, -3, 3);
                jump.setDirection(bounceX, bounce);

                bounced++;

                if (count > 0 && bounced >= count)
                {
                    tileCollidable.setEnabled(false);
                }
            }
        }
        else if (category.getName().startsWith(CollisionName.KNEE))
        {
            final int side;
            if (transformable.getX() > transformable.getOldX())
            {
                side = -2;
            }
            else
            {
                side = 1;
            }
            tileCollidable.apply(result);
            transformable.teleportX(transformable.getX() + 1.0 * side);
            transformable.check(true);

            final Force direction = launchable.getDirection();
            final double vx = direction.getDirectionHorizontal();
            direction.setDirection(-vx, direction.getDirectionVertical());
            direction.setDestination(-vx, 0.0);
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (with.getName().startsWith(Anim.BODY) && by.getName().startsWith(Anim.ATTACK))
        {
            ifIs(Launchable.class, l -> l.getDirection().zeroHorizontal());
        }
    }

    @Override
    public void update(double extrp)
    {
        this.extrp = extrp;
        tick.update(extrp);
        rasterable.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        if (rasterable.getRasterIndex(0) > 0)
        {
            rasterable.render(g);
        }
    }

    @Override
    public void recycle()
    {
        animatable.play(idle);
        tick.restart();
        tick.set(BOUNCE_DELAY_MS);
        bounceX = 0.0;
        bounced = 0;
    }

}
