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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.XmlLoader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Dragon1 feature implementation.
 * <ol>
 * <li>Fire on delay.</li>
 * <li>Move up on fired count.</li>
 * </ol>
 */
@FeatureInterface
public final class Dragon1 extends FeatureModel implements XmlLoader, XmlSaver, Routine, Recyclable
{
    private static final double SPEED_X = 1.2;
    private static final double SPEED_Y = 0.7;
    private static final int START_DELAY_TICK = 90;
    private static final int FIRED_DELAY_TICK = 50;
    private static final int HEIGHT_LIMIT = 400;

    private final Tick tick = new Tick();
    private final Animation idle;

    private Updatable current;
    private Dragon1Config config;
    private int count;

    @FeatureGet private Animatable animatable;
    @FeatureGet private Launcher launcher;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Identifiable identifiable;
    @FeatureGet private EntityModel model;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Dragon1(Services services, Setup setup)
    {
        super(services, setup);

        idle = AnimationConfig.imports(setup).getAnimation(Anim.IDLE);
    }

    /**
     * Update fire phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateStart(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsed(START_DELAY_TICK))
        {
            current = this::updateFire;
            tick.set(FIRED_DELAY_TICK);
        }
    }

    /**
     * Update fire phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFire(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsed(FIRED_DELAY_TICK))
        {
            if (count < config.getFiredCount())
            {
                launcher.fire();
                count++;
                tick.restart();
            }
            else
            {
                current = this::updateMoveUp;
            }
        }
    }

    /**
     * Update move up phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateMoveUp(double extrp)
    {
        transformable.moveLocationY(extrp, SPEED_Y);
        if (transformable.getY() > HEIGHT_LIMIT)
        {
            identifiable.destroy();
        }
    }

    @Override
    public void load(XmlReader root)
    {
        config = new Dragon1Config(root);
        current = this::updateStart;
        tick.restart();
    }

    @Override
    public void save(Xml root)
    {
        config.save(root);
    }

    @Override
    public void update(double extrp)
    {
        transformable.moveLocationX(extrp, SPEED_X);
        current.update(extrp);
    }

    @Override
    public void recycle()
    {
        current = UpdatableVoid.getInstance();
        config = null;
        animatable.play(idle);
        count = 0;
        tick.restart();
    }
}
