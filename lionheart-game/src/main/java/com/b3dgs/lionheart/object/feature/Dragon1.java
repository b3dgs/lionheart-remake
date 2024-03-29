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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.object.Editable;
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
public final class Dragon1 extends FeatureModel
                           implements XmlLoader, XmlSaver, Editable<Dragon1Config>, RoutineUpdate, Recyclable
{
    private static final double SPEED_X = 1.45;
    private static final double SPEED_Y = 0.85;
    private static final int START_DELAY_MS = 2000;
    private static final int FIRED_DELAY_MS = 800;
    private static final int HEIGHT_LIMIT = 400;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);

    private final Animatable animatable;
    private final Launcher launcher;
    private final Transformable transformable;
    private final Identifiable identifiable;

    private final Tick tick = new Tick();
    private final Animation idle;

    private Updatable current;
    private Dragon1Config config;
    private int count;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param animatable The animatable feature.
     * @param launcher The launcher feature.
     * @param transformable The transformable feature.
     * @param identifiable The identifiable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Dragon1(Services services,
                   Setup setup,
                   Animatable animatable,
                   Launcher launcher,
                   Transformable transformable,
                   Identifiable identifiable)
    {
        super(services, setup);

        this.animatable = animatable;
        this.launcher = launcher;
        this.transformable = transformable;
        this.identifiable = identifiable;

        idle = AnimationConfig.imports(setup).getAnimation(Anim.IDLE);

        load(setup.getRoot());
    }

    /**
     * Update fire phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateStart(double extrp)
    {
        tick.start();

        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), START_DELAY_MS))
        {
            current = this::updateFire;
            tick.set(FIRED_DELAY_MS);
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
        if (tick.elapsedTime(source.getRate(), FIRED_DELAY_MS))
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
    public Dragon1Config getConfig()
    {
        return config;
    }

    @Override
    public void setConfig(Dragon1Config config)
    {
        this.config = config;
    }

    @Override
    public void load(XmlReader root)
    {
        if (root.hasNode(Dragon1Config.NODE_DRAGON1))
        {
            config = new Dragon1Config(root);
        }
        current = this::updateStart;
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
        animatable.play(idle);
        count = 0;
        tick.stop();
    }
}
