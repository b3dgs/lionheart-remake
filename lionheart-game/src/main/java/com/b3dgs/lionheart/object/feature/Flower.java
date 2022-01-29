/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.AnimatorFrameListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilMath;
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
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.RasterType;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.object.state.StateDecay;

/**
 * Flower feature implementation.
 * <ol>
 * <li>Point player.</li>
 * <li>Throw projectile on delay.</li>
 * </ol>
 */
@FeatureInterface
public final class Flower extends FeatureModel implements Routine, Recyclable
{
    private static final int FIRE_DELAY_MS = 3300;
    private static final double FIRE_SPEED = 0.6;

    private final Tick tick = new Tick();
    private final Force direction = new Force();
    private final int halfFrames;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final MapTile map = services.get(MapTile.class);
    private final Trackable target = services.get(Trackable.class);

    private Updatable current;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Stats stats;
    @FeatureGet private Launcher launcher;
    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private Rasterable rasterable;
    @FeatureGet private Hurtable hurtable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Flower(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        halfFrames = config.getAnimation(Anim.IDLE).getFrames() / 2;
    }

    /**
     * Update on alive.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateAlive(double extrp)
    {
        if (stats.getHealth() > 0)
        {
            updateFrame();
            updateFire(extrp);
        }
        else
        {
            stateHandler.changeState(StateDecay.class);
            hurtable.kill(true);
            current = UpdatableVoid.getInstance();
        }
    }

    /**
     * Update frame depending of target location.
     */
    private void updateFrame()
    {
        final int margin = (int) Math.round(target.getX() - target.getWidth() / 4 - transformable.getX()) / 16;
        final int frame = UtilMath.clamp(margin, -halfFrames, halfFrames - 1) + halfFrames + 1;
        animatable.setFrame(frame);
        launcher.setOffset(frame * halfFrames - 17, launcher.getOffsetY());
        direction.setDirection((frame - halfFrames - 0.5) / 2.5 * FIRE_SPEED, -1.0 * FIRE_SPEED);
        direction.setDestination((frame - halfFrames - 0.5) / 2.5 * FIRE_SPEED, -1.0 * FIRE_SPEED);
    }

    /**
     * Update fire delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFire(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), FIRE_DELAY_MS))
        {
            launcher.fire(direction);
            Sfx.PROJECTILE_FLOWER.play();
            tick.restart();
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        animatable.addListener((AnimatorFrameListener) frame ->
        {
            if (frame < 4)
            {
                rasterable.setFrameOffsets(0, 0);
            }
            else if (frame < 7)
            {
                rasterable.setFrameOffsets(-11, 0);
            }
            else if (frame < 8)
            {
                rasterable.setFrameOffsets(-17, 0);
            }
            else if (frame < 9)
            {
                rasterable.setFrameOffsets(-19, 0);
            }
        });
        if (RasterType.CACHE == Settings.getInstance().getRaster())
        {
            launcher.addListener(l -> l.ifIs(Rasterable.class,
                                             r -> r.setRaster(true, rasterable.getMedia().get(), map.getTileHeight())));
        }
    }

    @Override
    public void update(double extrp)
    {
        current.update(extrp);
    }

    @Override
    public void recycle()
    {
        current = this::updateAlive;
        tick.restart();
    }
}
