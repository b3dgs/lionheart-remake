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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.object.Editable;
import com.b3dgs.lionheart.object.XmlLoader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Shooter feature implementation.
 * <ol>
 * <li>Fire on delay.</li>
 * </ol>
 */
@FeatureInterface
public final class Shooter extends FeatureModel
                           implements XmlLoader, XmlSaver, Editable<ShooterConfig>, Routine, Recyclable
{
    private final Tick tick = new Tick();
    private final Animation idle;
    private final Animation attack;

    private final Trackable target = services.getOptional(Trackable.class).orElse(null);

    private ShooterConfig def;
    private ShooterConfig config;
    private Updatable updater;
    private boolean enabled;

    @FeatureGet private Launcher launcher;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Stats stats;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Shooter(Services services, Setup setup)
    {
        super(services, setup);

        if (setup.hasNode(ShooterConfig.NODE_SHOOTER))
        {
            def = new ShooterConfig(setup.getRoot());
        }

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation("patrol");
        attack = config.getAnimation(Anim.ATTACK);
    }

    @Override
    public ShooterConfig getConfig()
    {
        return config;
    }

    @Override
    public void setConfig(ShooterConfig config)
    {
        this.config = config;
    }

    /**
     * Load configuration.
     * 
     * @param config The configuration to load.
     */
    public void load(ShooterConfig config)
    {
        this.config = config;
        updater = this::updatePrepare;
        tick.restart();
        tick.set(config.getFireDelay() / 2);
    }

    /**
     * Set fire enabled flag.
     * 
     * @param enabled <code>true</code> if enabled, <code>false</code> else.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Update prepare fire, fire on delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updatePrepare(double extrp)
    {
        tick.update(extrp);
        if (enabled
            && tick.elapsed(config.getFireDelay())
            && (animatable.is(AnimState.FINISHED)
                || animatable.is(AnimState.STOPPED)
                || animatable.getFrameAnim() == 1))
        {
            updater = this::updateFire;
            if (config.getAnim() > 0)
            {
                animatable.play(attack);
            }
        }
    }

    /**
     * Update fire.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFire(double extrp)
    {
        if (config.getAnim() == 0 || animatable.getFrameAnim() == config.getAnim())
        {
            if (config.getTrack())
            {
                launcher.fire(new Force(0.25, 0.0), target);
            }
            else
            {
                launcher.fire();
            }
            if (config.getAnim() > 0)
            {
                updater = this::updateCheckAnimEnd;
            }
            else
            {
                if (config.getFiredDelay() > 0)
                {
                    updater = this::updateFired;
                }
                else
                {
                    updater = this::updatePrepare;
                }
                tick.restart();
            }
        }
    }

    /**
     * Update check anim end.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateCheckAnimEnd(double extrp)
    {
        if (animatable.is(AnimState.FINISHED))
        {
            if (config.getFiredDelay() > 0)
            {
                updater = this::updateFired;
            }
            else
            {
                animatable.play(idle);
                updater = this::updatePrepare;
            }
            tick.restart();
        }
    }

    /**
     * Update after fired, delay before prepare.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFired(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsed(config.getFiredDelay()))
        {
            animatable.play(idle);
            updater = this::updatePrepare;
            tick.restart();
        }
    }

    @Override
    public void load(XmlReader root)
    {
        if (root.hasNode(ShooterConfig.NODE_SHOOTER))
        {
            load(new ShooterConfig(root));
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
        if (stats.getHealth() > 0)
        {
            updater.update(extrp);
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        final MapTile map = services.get(MapTile.class);
        launcher.addListener(l ->
        {
            if (Settings.getInstance().getRasterObject())
            {
                l.ifIs(Rasterable.class,
                       r -> r.getMedia().ifPresent(media -> r.setRaster(true, media, map.getTileHeight())));
            }

            if (config != null && !config.getTrack())
            {
                final Force direction = l.getDirection();
                if (direction != null)
                {
                    final double dx = direction.getDirectionHorizontal();
                    final double dy = direction.getDirectionVertical();
                    direction.setDirection(dx * config.getSvx(), dy * config.getSvy());
                    direction.setDestination(dx * config.getDvx().orElse(config.getSvx()),
                                             dy * config.getDvy().orElse(config.getSvy()));
                }
            }
        });
    }

    @Override
    public void recycle()
    {
        config = null;
        updater = UpdatableVoid.getInstance();
        enabled = true;
        if (def != null)
        {
            load(def);
        }
    }
}
