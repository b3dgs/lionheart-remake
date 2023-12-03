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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.RasterType;
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
    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Trackable target = services.getOptional(Trackable.class).orElse(null);
    private final Camera camera = services.get(Camera.class);

    private final Launcher launcher;
    private final Animatable animatable;
    private final Transformable transformable;
    private final Stats stats;

    private final Tick tick = new Tick();
    private final Animation idle;
    private final Animation attack;

    private ShooterConfig config;
    private Updatable updater;
    private boolean enabled;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param launcher The launcher feature.
     * @param animatable The animatable feature.
     * @param transformable The transformable feature.
     * @param stats The stats feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Shooter(Services services,
                   Setup setup,
                   Launcher launcher,
                   Animatable animatable,
                   Transformable transformable,
                   Stats stats)
    {
        super(services, setup);

        this.launcher = launcher;
        this.animatable = animatable;
        this.transformable = transformable;
        this.stats = stats;

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation("patrol");
        attack = config.getAnimation(Anim.ATTACK);

        load(setup.getRoot());

        final MapTile map = services.get(MapTile.class);
        launcher.addListener(l ->
        {
            if (RasterType.CACHE == Settings.getInstance().getRaster())
            {
                l.ifIs(Rasterable.class,
                       r -> r.getMedia().ifPresent(media -> r.setRaster(true, media, map.getTileHeight())));
            }

            if (!this.config.getTrack())
            {
                final Force direction = l.getDirection();
                if (direction != null)
                {
                    final double dx = direction.getDirectionHorizontal();
                    final double dy = direction.getDirectionVertical();
                    direction.setDirection(dx * this.config.getSvx(), dy * this.config.getSvy());
                    direction.setDestination(dx * this.config.getDvx().orElse(this.config.getSvx()),
                                             dy * this.config.getDvy().orElse(this.config.getSvy()));
                }
            }
        });
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
        tick.start();

        tick.update(extrp);
        if (enabled
            && tick.elapsedTime(source.getRate(), config.getFireDelay())
            && (animatable.is(AnimState.FINISHED)
                || animatable.is(AnimState.STOPPED)
                || animatable.getFrameAnim() == 1))
        {
            updater = this::updateFire;
            if (config.getAnim() != 0)
            {
                animatable.play(attack);
            }
            tick.stop();
        }
    }

    /**
     * Update fire.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFire(double extrp)
    {
        if (config.getAnim() < 1 || animatable.getFrameAnim() == config.getAnim())
        {
            if (config.getTrack())
            {
                launcher.fire(new Force(0.3, 0.0), target);
            }
            else
            {
                launcher.fire();
            }
            if (config.getAnim() != 0)
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
        if (animatable.is(AnimState.FINISHED) || config.getAnim() < 0)
        {
            if (config.getFiredDelay() > 0)
            {
                updater = this::updateFired;
            }
            else
            {
                if (config.getAnim() > 0)
                {
                    animatable.play(idle);
                }
                updater = this::updatePrepare;
            }
        }
    }

    /**
     * Update after fired, delay before prepare.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFired(double extrp)
    {
        tick.start();

        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), config.getFiredDelay()))
        {
            animatable.play(idle);
            updater = this::updatePrepare;
            tick.stop();
        }
    }

    @Override
    public void load(XmlReader root)
    {
        if (root.hasNode(ShooterConfig.NODE_SHOOTER))
        {
            config = new ShooterConfig(root);
            updater = this::updatePrepare;
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
        if (camera.isViewable(transformable, 0, 0) && stats.getHealth() > 0)
        {
            updater.update(extrp);
        }
    }

    @Override
    public void recycle()
    {
        enabled = true;
        tick.stop();
    }
}
