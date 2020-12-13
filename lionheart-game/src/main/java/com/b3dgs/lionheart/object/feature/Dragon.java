/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionheart.Sfx;

/**
 * Dragon feature implementation.
 * <ol>
 * <li>Point player.</li>
 * <li>Raise on close distance.</li>
 * <li>Throw line during delay.</li>
 * <li>Retract on distance or hit.</li>
 * </ol>
 */
@FeatureInterface
public final class Dragon extends FeatureModel implements Routine, Recyclable
{
    private static final int TONGUE_COUNT = 7;
    private static final int TONGUE_OFFSET_X = 8;
    private static final int TONGUE_OFFSET_Y = 23;
    private static final int TONGUE_RETRACT_TICK_DELAY = 3;
    private static final int THROW_DISTANCE = 160;

    private final List<Launchable> tongue = new ArrayList<>();
    private final Tick tick = new Tick();
    private final MapTile map = services.get(MapTile.class);
    private final Transformable track;
    private final Animation raise;
    private final Animation open;
    private final Animation close;
    private final Animation hide;
    private Updatable current;
    private boolean fired;
    private boolean hurt;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Stats stats;
    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private Launcher launcher;
    @FeatureGet private Rasterable rasterable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Dragon(Services services, Setup setup)
    {
        super(services, setup);

        track = services.get(SwordShade.class).getFeature(Transformable.class);

        final AnimationConfig config = AnimationConfig.imports(setup);
        raise = config.getAnimation("raise");
        open = config.getAnimation("open");
        close = config.getAnimation("close");
        hide = config.getAnimation("hide");
    }

    /**
     * Update check distance.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateCheck(double extrp)
    {
        if (hurt)
        {
            hurt = UtilMath.getDistance(track, transformable) < THROW_DISTANCE * 2;
        }
        else if (animatable.is(AnimState.FINISHED) && UtilMath.getDistance(track, transformable) < THROW_DISTANCE)
        {
            animatable.play(raise);
            if (transformable.getX() > track.getX())
            {
                mirrorable.mirror(Mirror.HORIZONTAL);
            }
            else
            {
                mirrorable.mirror(Mirror.NONE);
            }
            current = this::updateRaise;
        }
    }

    /**
     * Update raise animation.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateRaise(double extrp)
    {
        if (animatable.is(AnimState.FINISHED))
        {
            animatable.play(open);
            fired = false;
            current = this::updateThrow;
        }
    }

    /**
     * Update throw.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateThrow(double extrp)
    {
        if (animatable.is(AnimState.FINISHED))
        {
            if (!fired)
            {
                launcher.fire();
                Sfx.SCENERY_DRAGON.play();
                fired = true;
            }
            if (tongue.size() == TONGUE_COUNT && !tick.isStarted())
            {
                if (isTongueHit())
                {
                    hurt = true;
                    triggerRetractTongue(true);
                    tick.start();
                }
                if (UtilMath.getDistance(track, transformable) > THROW_DISTANCE
                    || mirrorable.is(Mirror.NONE) && transformable.getX() > track.getX()
                    || mirrorable.is(Mirror.HORIZONTAL) && transformable.getX() < track.getX())
                {
                    triggerRetractTongue(false);
                    tick.start();
                }
            }
            tick.update(extrp);
            if (tick.isStarted() && tongue.isEmpty())
            {
                final int frame = animatable.getFrame();
                animatable.play(close);
                animatable.setFrame(frame);
                tick.stop();
                current = this::updateClose;
            }
        }
    }

    /**
     * Check if tongue hit.
     * 
     * @return <code>true</code> if hit, <code>false</code> else.
     */
    private boolean isTongueHit()
    {
        final int n = tongue.size();
        for (int i = 0; i < n; i++)
        {
            if (tongue.get(i).getFeature(Stats.class).getHealth() == 0)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Trigger tongue retract.
     * 
     * @param explode <code>true</code> to show explodes, <code>false</code> else.
     */
    private void triggerRetractTongue(boolean explode)
    {
        final int n = tongue.size();
        for (int i = 0; i < n; i++)
        {
            final int id = n - i - 1;
            tick.addAction(() ->
            {
                if (explode)
                {
                    tongue.get(id).getFeature(Hurtable.class).kill();
                }
                tongue.get(id).getFeature(Identifiable.class).destroy();
                if (id == 0)
                {
                    tongue.clear();
                }
            }, TONGUE_RETRACT_TICK_DELAY * i);
        }
    }

    /**
     * Update close.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateClose(double extrp)
    {
        if (animatable.is(AnimState.FINISHED))
        {
            final int frame = animatable.getFrame();
            animatable.play(hide);
            animatable.setFrame(frame);
            current = this::updateCheck;
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        launcher.setOffset(TONGUE_OFFSET_X, TONGUE_OFFSET_Y);
        launcher.addListener(tongue::add);
        launcher.addListener(l -> l.ifIs(Rasterable.class,
                                         r -> r.setRaster(false, rasterable.getMedia().get(), map.getTileHeight())));
    }

    @Override
    public void update(double extrp)
    {
        current.update(extrp);
    }

    @Override
    public void recycle()
    {
        current = this::updateCheck;
        tongue.clear();
    }
}
