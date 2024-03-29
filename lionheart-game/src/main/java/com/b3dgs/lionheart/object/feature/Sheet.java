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
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionheart.object.Editable;
import com.b3dgs.lionheart.object.XmlLoader;
import com.b3dgs.lionheart.object.XmlSaver;
import com.b3dgs.lionheart.object.feature.Glue.GlueListener;

/**
 * Sheet feature implementation.
 * <p>
 * Add support to sheet movement on collide, applying a curve effect to represent impact.
 * </p>
 */
@FeatureInterface
public final class Sheet extends FeatureModel
                         implements XmlLoader, XmlSaver, Editable<SheetConfig>, RoutineUpdate, Recyclable
{
    private static final double CURVE_FORCE = 7.0;
    private static final double CURVE_SPEED = 8.0;
    private static final int HIDE_RANGE = 48;

    private final Transformable transformable;

    private Trackable target;

    private SheetConfig config;
    private boolean start;
    private boolean done;
    private double curve;
    private boolean abord;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @param glue The glue feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Sheet(Services services, Setup setup, Transformable transformable, Glue glue)
    {
        super(services, setup);

        this.transformable = transformable;

        load(setup.getRoot());

        glue.addListener(new GlueListener()
        {
            @Override
            public void notifyStart(Transformable transformable)
            {
                glue.setTransformY(() -> UtilMath.sin(curve) * CURVE_FORCE);
                start = true;
                done = false;
                glue.setGlue(true);
                abord = false;
            }

            @Override
            public void notifyEnd(Transformable transformable)
            {
                glue.setGlue(false);
                abord = true;
            }
        });
    }

    @Override
    public void setConfig(SheetConfig config)
    {
        this.config = config;
    }

    @Override
    public SheetConfig getConfig()
    {
        return config;
    }

    @Override
    public void update(double extrp)
    {
        if (start)
        {
            if (!done)
            {
                if (abord && Double.compare(curve, 90) < 0)
                {
                    curve -= CURVE_SPEED * extrp;
                }
                else
                {
                    curve += CURVE_SPEED * extrp;
                }
            }
            if (curve < 0 || curve > 180)
            {
                curve = 0.0;
                done = true;
            }
        }

        if (target != null
            && config.getHide()
            && Math.abs(transformable.getX() - target.getX()) < HIDE_RANGE
            && Math.abs(transformable.getY() - target.getY()) < HIDE_RANGE * 1.3)
        {
            ifIs(Rasterable.class, r -> r.setVisibility(true));
            target = null;
        }
    }

    @Override
    public void load(XmlReader root)
    {
        if (root.hasNode(SheetConfig.NODE_SHEET))
        {
            config = new SheetConfig(root);

            if (config.getHide())
            {
                ifIs(Rasterable.class, r -> r.setVisibility(false));
                target = services.getOptional(Trackable.class).orElse(null);
            }
        }
    }

    @Override
    public void save(Xml root)
    {
        config.save(root);
    }

    @Override
    public void recycle()
    {
        start = false;
        done = false;
        curve = 0.0;
        abord = false;
    }
}
