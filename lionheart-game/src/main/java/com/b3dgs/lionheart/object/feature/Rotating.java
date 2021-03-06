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

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.object.state.StateCrouch;

/**
 * Rotating feature implementation.
 * <ol>
 * <li>Turn around fixed reference.</li>
 * </ol>
 */
@FeatureInterface
public final class Rotating extends FeatureModel implements Routine, Recyclable
{
    private final List<Transformable> rings = new ArrayList<>();
    private final Spawner spawner = services.get(Spawner.class);
    private final StateHandler player = services.get(SwordShade.class).getFeature(StateHandler.class);
    private final Tick tick = new Tick();

    private RotatingConfig config;
    private int count;
    private double angleStart;
    private double angle;
    private double angleAcc;
    private double max;
    private double side;
    private boolean collide;
    private Transformable platform;

    @FeatureGet private Transformable transformable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Rotating(Services services, Setup setup)
    {
        super(services, setup);
    }

    /**
     * Load configuration.
     * 
     * @param config The configuration to load.
     */
    public void load(RotatingConfig config)
    {
        rings.stream().map(r -> r.getFeature(Identifiable.class)).forEach(Identifiable::destroy);
        rings.clear();

        this.config = config;
        if (config.getSpeed() < 0)
        {
            angleStart = Constant.ANGLE_MAX / 2 + config.getAmplitude();
        }
        else
        {
            angleStart = Constant.ANGLE_MAX / 2 - config.getAmplitude();
        }
        angle = angleStart + config.getOffset();
        side = config.getSpeed();

        for (int i = 0; i < config.getLength(); i++)
        {
            rings.add(spawner.spawn(Medias.create(config.getRing()), transformable).getFeature(Transformable.class));
        }
        platform = spawner.spawn(Medias.create(config.getExtremity()), transformable).getFeature(Transformable.class);
        if (config.getAmplitude() > 0)
        {
            platform.getFeature(TileCollidable.class).addListener((result, category) ->
            {
                if (tick.elapsed(10))
                {
                    angle -= angleAcc;
                    angleAcc = -angleAcc;
                    Sfx.SCENERY_ROTATINGPLATFORM.play();
                    tick.restart();
                }
            });
        }

        if (config.isControlled())
        {
            final Collidable platformCollidable = platform.getFeature(Collidable.class);
            platformCollidable.addListener((c, w, b) -> onCollide());
        }

        rings.add(platform);
        count = rings.size();
    }

    /**
     * Called on collide.
     */
    private void onCollide()
    {
        collide = true;
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);

        for (int i = 0; i < count; i++)
        {
            rings.get(i)
                 .setLocation(transformable.getX() + (i + 0.5) * UtilMath.cos(angle + 90) * 16,
                              transformable.getY() + (i + 0.5) * UtilMath.sin(angle + 90) * 16);
        }

        if (config != null)
        {
            if (config.getAmplitude() > 0)
            {
                if (config.isControlled())
                {
                    if (collide && player.isState(StateCrouch.class))
                    {
                        if (platform.getOldY() > platform.getY())
                        {
                            max += 0.03;
                        }
                        else
                        {
                            max -= 0.05;
                        }
                    }
                    else
                    {
                        max -= 0.001;
                    }
                    max = UtilMath.clamp(max, 0.8, 5.5);

                    if (angle > Constant.ANGLE_MAX / 2 + config.getAmplitude())
                    {
                        side = -config.getSpeed();
                    }
                    else if (angle < Constant.ANGLE_MAX / 2 - config.getAmplitude())
                    {
                        side = config.getSpeed();
                    }
                    angleAcc += side;
                }
                else
                {
                    max = 3.5;
                    if (Math.abs(angleStart - angle) > config.getAmplitude())
                    {
                        angleAcc -= config.getSpeed();
                    }
                    else
                    {
                        angleAcc += config.getSpeed();
                    }
                }

                angleAcc = UtilMath.clamp(angleAcc, -max, max);
                angle = UtilMath.wrapAngleDouble(angle + angleAcc);
            }
            else
            {
                angle = UtilMath.wrapAngleDouble(angle + config.getSpeed());
            }
        }

        collide = false;
    }

    @Override
    public void recycle()
    {
        angle = Constant.ANGLE_MAX / 2;
        angleAcc = 0.0;
        max = 0.8;
        tick.restart();
        tick.set(10);
    }
}
