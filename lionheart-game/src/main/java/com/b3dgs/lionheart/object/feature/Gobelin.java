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
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
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
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.RasterType;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;

/**
 * Gobelin feature implementation.
 * <ol>
 * <li>Move up.</li>
 * <li>Fire on up.</li>
 * <li>Move down.</li>
 * </ol>
 */
@FeatureInterface
public final class Gobelin extends FeatureModel implements Routine, Recyclable
{
    private static final int MOVE_UP_DELAY_MS = 800;

    private final Tick tick = new Tick();
    private final Animation idle;
    private final Animation attack;
    private final Animation fall;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final MapTile map = services.get(MapTile.class);

    private int phase;

    @FeatureGet private Animatable animatable;
    @FeatureGet private Launcher launcher;
    @FeatureGet private Rasterable rasterable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Gobelin(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation(Anim.IDLE);
        attack = config.getAnimation(Anim.ATTACK);
        fall = config.getAnimation(Anim.FALL);
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);

        if (phase == 0 && animatable.is(AnimState.FINISHED))
        {
            phase++;
            animatable.play(idle);
            launcher.fire();
            tick.restart();
        }
        if (phase == 1 && tick.elapsedTime(source.getRate(), MOVE_UP_DELAY_MS))
        {
            animatable.play(fall);
            phase++;
            tick.restart();
        }
        else if (phase == 2 && tick.elapsedTime(source.getRate(), MOVE_UP_DELAY_MS))
        {
            animatable.play(attack);
            Sfx.MONSTER_GOBELIN.play();
            phase = 0;
            tick.restart();
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        if (RasterType.CACHE == Settings.getInstance().getRaster())
        {
            launcher.addListener(l -> l.ifIs(Rasterable.class,
                                             r -> r.setRaster(true, rasterable.getMedia().get(), map.getTileHeight())));
        }
    }

    @Override
    public void recycle()
    {
        animatable.play(attack);
        phase = 0;
        tick.restart();
    }
}
