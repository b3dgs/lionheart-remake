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

import java.nio.ByteBuffer;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.networkable.Networkable;
import com.b3dgs.lionengine.game.feature.networkable.Syncable;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.network.Packet;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.object.Editable;
import com.b3dgs.lionheart.object.XmlLoader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Spike feature implementation.
 * <p>
 * Add support to spike movement, rising from ground with collision and return back to the ground.
 * </p>
 */
@FeatureInterface
public final class Spike extends FeatureModel
                         implements XmlLoader, XmlSaver, Editable<SpikeConfig>, Routine, Recyclable, Syncable
{
    private static final int PHASE1_DELAY_MS = 500;
    private static final int PHASE2_DELAY_MS = 500;
    private static final int PHASE3_DELAY_MS = 100;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Viewer viewer = services.get(Viewer.class);

    private final Animatable animatable;
    private final Transformable transformable;
    private final Networkable networkable;

    private final Tick tick = new Tick();
    private final Tick delay = new Tick();
    private final Animation rise;
    private final Animation attack;
    private final Animation hide;

    private SpikeConfig config;
    private Updatable updater;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param animatable The animatable feature.
     * @param transformable The transformable feature.
     * @param networkable The networkable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Spike(Services services,
                 Setup setup,
                 Animatable animatable,
                 Transformable transformable,
                 Networkable networkable)
    {
        super(services, setup);

        this.animatable = animatable;
        this.transformable = transformable;
        this.networkable = networkable;

        final AnimationConfig config = AnimationConfig.imports(setup);
        rise = config.getAnimation("phase1");
        attack = config.getAnimation(Anim.ATTACK);
        hide = config.getAnimation("phase3");

        load(setup.getRoot());
    }

    /**
     * Update prepare attack phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updatePrepareAttack(double extrp)
    {
        tick.start();

        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), PHASE1_DELAY_MS))
        {
            tick.restart();
            animatable.play(rise);
            updater = this::updateAttackPrepared;

            syncStart();
        }
    }

    /**
     * Update attack prepared phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateAttackPrepared(double extrp)
    {
        if (animatable.is(AnimState.FINISHED))
        {
            updater = this::updateAttack;
            tick.restart();
        }
    }

    /**
     * Update attack phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateAttack(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), PHASE2_DELAY_MS))
        {
            animatable.play(attack);
            if (viewer.isViewable(transformable, 0, 0))
            {
                Sfx.SCENERY_SPIKE.play();
            }
            updater = this::updateAttackFinished;
        }
    }

    /**
     * Update attack finished phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateAttackFinished(double extrp)
    {
        if (animatable.is(AnimState.FINISHED))
        {
            updater = this::updateMoveDown;
            tick.restart();
        }
    }

    /**
     * Update move down phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateMoveDown(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), PHASE3_DELAY_MS))
        {
            animatable.play(hide);
            animatable.setFrame(hide.getLast());
            updater = this::updateDone;
        }
    }

    /**
     * Update done phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateDone(double extrp)
    {
        if (animatable.is(AnimState.FINISHED))
        {
            updater = this::updatePrepareAttack;
            tick.restart();
        }
    }

    private void syncStart()
    {
        if (networkable.isOwner())
        {
            final ByteBuffer data = ByteBuffer.allocate(Integer.BYTES);
            data.putInt(getSyncId());
            networkable.send(data);
        }
    }

    @Override
    public SpikeConfig getConfig()
    {
        return config;
    }

    @Override
    public void setConfig(SpikeConfig config)
    {
        this.config = config;
    }

    @Override
    public void load(XmlReader root)
    {
        if (root.hasNode(SpikeConfig.NODE_SPIKE))
        {
            config = new SpikeConfig(root);

            config.getDelay().ifPresent(delayMs ->
            {
                updater = extrp ->
                {
                    delay.start();
                    delay.update(extrp);
                    if (delay.elapsedTime(source.getRate(), delayMs))
                    {
                        updater = this::updatePrepareAttack;
                    }
                };
            });
        }
    }

    @Override
    public void save(Xml root)
    {
        if (config != null)
        {
            config.save(root);
        }
    }

    @Override
    public void update(double extrp)
    {
        updater.update(extrp);
    }

    @Override
    public void onReceived(Packet packet)
    {
        tick.restart();
        animatable.play(rise);
        updater = this::updateAttackPrepared;
    }

    @Override
    public void recycle()
    {
        animatable.setFrame(rise.getFirst());
        updater = this::updatePrepareAttack;
    }
}
