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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
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
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.object.Editable;
import com.b3dgs.lionheart.object.XmlLoader;
import com.b3dgs.lionheart.object.XmlSaver;
import com.b3dgs.lionheart.object.state.StateCrouch;

/**
 * Rotating feature implementation.
 * <ol>
 * <li>Turn around fixed reference.</li>
 * </ol>
 */
@FeatureInterface
public final class Rotating extends FeatureModel
                            implements XmlLoader, XmlSaver, Editable<RotatingConfig>, Routine, Recyclable
{
    private final Spawner spawner = services.get(Spawner.class);
    private final Viewer viewer = services.get(Viewer.class);

    private final Transformable transformable;

    private final List<Transformable> rings = new ArrayList<>();
    private final StateHandler target;

    private RotatingConfig config;
    private int count;
    private double angleStart;
    private double angle;
    private double angleAcc;
    private int angleBack;
    private double max;
    private double side;
    private boolean collide;
    private Transformable platform;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Rotating(Services services, Setup setup, Transformable transformable)
    {
        super(services, setup);

        this.transformable = transformable;

        final Optional<Trackable> trackable = services.getOptional(Trackable.class);
        if (trackable.isPresent())
        {
            target = trackable.get().getFeature(StateHandler.class);
        }
        else
        {
            target = null;
        }
        if (setup.hasNode(RotatingConfig.NODE_ROTATING))
        {
            config = new RotatingConfig(setup.getRoot());
        }
    }

    /**
     * Called on collide.
     * 
     * @param with The collision with.
     * @param by The collision by.
     */
    private void onCollide(Collision with, Collision by)
    {
        if (with.getName().startsWith(CollisionName.GROUND) && by.getName().startsWith(CollisionName.LEG))
        {
            collide = true;
        }
    }

    @Override
    public void setConfig(RotatingConfig config)
    {
        this.config = config;
    }

    @Override
    public RotatingConfig getConfig()
    {
        return config;
    }

    @Override
    public void load(XmlReader root)
    {
        if (root.hasNode(RotatingConfig.NODE_ROTATING))
        {
            config = new RotatingConfig(root);
        }
        rings.stream().map(r -> r.getFeature(Identifiable.class)).forEach(Identifiable::destroy);
        rings.clear();

        if (config.getSpeed() < 0)
        {
            angleStart = Constant.ANGLE_MAX / 2 + config.getAmplitude();
        }
        else
        {
            angleStart = Constant.ANGLE_MAX / 2 - config.getAmplitude();
        }
        angle = angleStart + config.getOffset();
        angleBack = config.getBack();
        side = config.getSpeed();

        if (!Settings.isEditor())
        {
            final String folder = setup.getMedia().getParentPath().replace(Folder.ENTITY, Folder.LIMB);
            for (int i = 0; i < config.getLength(); i++)
            {
                rings.add(spawner.spawn(Medias.create(folder, config.getRing()), transformable)
                                 .getFeature(Transformable.class));
            }
            platform = spawner.spawn(Medias.create(folder, config.getExtremity()), transformable)
                              .getFeature(Transformable.class);

            if (config.isControlled())
            {
                final Collidable platformCollidable = platform.getFeature(Collidable.class);
                platformCollidable.addListener((c, w, b) -> onCollide(w, b));
            }

            rings.add(platform);
            count = rings.size();
        }
    }

    @Override
    public void save(Xml root)
    {
        config.save(root);
    }

    @Override
    public void update(double extrp)
    {
        for (int i = 0; i < count; i++)
        {
            rings.get(i)
                 .setLocation(transformable.getX() + (i + 0.5) * UtilMath.cos(angle + 90) * 16,
                              transformable.getY() + (i + 0.5) * UtilMath.sin(angle + 90) * 16);
        }

        if (config.getAmplitude() > 0)
        {
            if (config.isControlled())
            {
                if (platform.getOldY() > platform.getY())
                {
                    if (collide && target.isState(StateCrouch.class))
                    {
                        max += 0.045 * extrp;
                    }
                }
                else
                {
                    max -= 0.02 * extrp;
                }
                max = UtilMath.clamp(max, 0.95, 6.5);

                if (angle > Constant.ANGLE_MAX / 2 + config.getAmplitude())
                {
                    side = -config.getSpeed();
                }
                else if (angle < Constant.ANGLE_MAX / 2 - config.getAmplitude())
                {
                    side = config.getSpeed();
                }
                angleAcc += side * extrp;
            }
            else
            {
                max = 3.5;
                if (Math.abs(angleStart - angle) > config.getAmplitude())
                {
                    angleAcc -= config.getSpeed() * extrp;
                }
                else
                {
                    angleAcc += config.getSpeed() * extrp;
                }
            }

            angleAcc = UtilMath.clamp(angleAcc, -max, max);
            angle = UtilMath.wrapAngleDouble(angle + angleAcc * extrp);
        }
        else
        {
            angle = UtilMath.wrapAngleDouble(angle + config.getSpeed() * extrp);
        }

        if (angleBack > -1 && config.getSpeed() > 0 ? angle > angleBack : angle < angleBack)
        {
            angle -= angleAcc * extrp;
            angleAcc = -angleAcc;
            if (viewer.isViewable(transformable, 0, 0))
            {
                Sfx.SCENERY_ROTATINGPLATFORM.play();
            }
        }
        collide = false;
    }

    @Override
    public void recycle()
    {
        angle = Constant.ANGLE_MAX / 2;
        angleAcc = 0.0;
        max = 0.95;
    }
}
