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
import com.b3dgs.lionengine.AnimatorStateListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.landscape.ForegroundWater;

/**
 * Norka walk feature implementation.
 * <ol>
 * <li>Move down and transform.</li>
 * </ol>
 */
@FeatureInterface
public final class Norka extends FeatureModel implements Recyclable
{
    private final Identifiable[] pillar = new Identifiable[4];
    private final Animation idle;

    private final Spawner spawner = services.get(Spawner.class);

    private final ForegroundWater water = services.get(ForegroundWater.class);

    private Identifiable flyer;
    private Identifiable daemon;
    private boolean exit;

    @FeatureGet private Animatable animatable;
    @FeatureGet private Identifiable identifiable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Norka(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation(Anim.IDLE);
    }

    private void spawnPillar()
    {
        for (int i = 0; i < pillar.length; i++)
        {
            pillar[i] = spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "Pillar.xml"), 88 + i * 80, 86.4)
                               .getFeature(Identifiable.class);
            pillar[i].getFeature(Pillar.class).load(new PillarConfig(100 + i * 100));
        }
    }

    private void spawnFlyer()
    {
        flyer = spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "Boss1.xml"), 208, 400)
                       .getFeature(Identifiable.class);
        flyer.addListener(id -> onFlyerDeath());
    }

    private void onFlyerDeath()
    {
        daemon.destroy();

        for (final Identifiable element : pillar)
        {
            element.getFeature(Pillar.class).close();
        }
        water.setRaiseMax(-1);
    }

    private void spawnDaemon()
    {
        daemon = spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "Boss2a.xml"), 208, 176)
                        .getFeature(Identifiable.class);

        daemon.addListener(id -> spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "Boss2b.xml"), 208, 177)
                                        .getFeature(Identifiable.class)
                                        .addListener(i -> onDaemonDeath()));

    }

    private void onDaemonDeath()
    {
        animatable.play(idle);
        animatable.setFrame(idle.getLast());
        animatable.setAnimSpeed(-idle.getSpeed());
        exit = true;
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        animatable.addListener((AnimatorStateListener) s ->
        {
            if (exit && s == AnimState.FINISHED)
            {
                exit = false;
                spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "NorkaWalk.xml"), 208, 112);
                identifiable.destroy();
            }
        });
    }

    @Override
    public void recycle()
    {
        exit = false;
        spawnPillar();
        spawnFlyer();
        spawnDaemon();
        animatable.play(idle);
    }
}
