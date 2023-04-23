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
import com.b3dgs.lionengine.AnimatorStateListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.RasterType;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.WorldType;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.landscape.ForegroundWater;

/**
 * Norka walk feature implementation.
 * <ol>
 * <li>Move down and transform.</li>
 * </ol>
 */
@FeatureInterface
public final class Norka extends FeatureModel implements Routine, Recyclable
{
    private static final int SPAWN_PILLAR_DELAY_MS = 1250;
    private static final int SPAWN_FLYER_DELAY_MS = 1500;

    private final Tick tick = new Tick();
    private final Identifiable[] pillar = new Identifiable[4];
    private final Animation idle;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Spawner spawner = services.get(Spawner.class);
    private final Trackable target = services.get(Trackable.class);
    private final Hurtable playerHurtable = target.getFeature(Hurtable.class);
    private final ForegroundWater water = services.get(ForegroundWater.class);

    private Identifiable flyer;
    private Identifiable daemon;
    private boolean exit;
    private Updatable phase;

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

        for (int i = 0; i < pillar.length; i++)
        {
            pillar[i] = spawner.spawn(Medias.create(Folder.ENTITY, WorldType.NORKA.getFolder(), "Pillar.xml"),
                                      88 + i * 80,
                                      0)
                               .getFeature(Identifiable.class);
            if (RasterType.CACHE == Settings.getInstance().getRaster())
            {
                pillar[i].getFeature(Underwater.class).loadRaster("raster/norka/norka/");
            }
            pillar[i].getFeature(Pillar.class).load(new PillarConfig(1650 + i * 1650));
        }
    }

    /**
     * Spawn pillar.
     * 
     * @param extrp The extrapolation value.
     */
    private void spawnPillar(double extrp)
    {
        tick.start();

        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), SPAWN_PILLAR_DELAY_MS))
        {
            phase = this::spawnFlyer;
            tick.stop();
        }
    }

    /**
     * Spawn flyer.
     * 
     * @param extrp The extrapolation value.
     */
    private void spawnFlyer(double extrp)
    {
        tick.start();

        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), SPAWN_FLYER_DELAY_MS))
        {
            flyer = spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "Boss1.xml"), 208, 400)
                           .getFeature(Identifiable.class);
            flyer.addListener(id -> onFlyerDeath());

            tick.stop();
            phase = UpdatableVoid.getInstance();
        }
    }

    /**
     * Called on flyer death.
     */
    private void onFlyerDeath()
    {
        daemon.destroy();

        for (final Identifiable element : pillar)
        {
            element.getFeature(Pillar.class).close();
        }
        water.setRaiseMax(-1);
    }

    /**
     * Spawn daemon.
     */
    private void spawnDaemon()
    {
        daemon = spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "Boss2a.xml"), 208, 176)
                        .getFeature(Identifiable.class);

        daemon.addListener(id -> spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "Boss2b.xml"), 208, 177)
                                        .getFeature(Identifiable.class)
                                        .addListener(i -> onDaemonDeath()));

    }

    /**
     * Called on daemon death.
     */
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
    public void update(double extrp)
    {
        phase.update(extrp);

        if (target.getY() < water.getTotalHeight() - 4 && !playerHurtable.isHurtingBody())
        {
            playerHurtable.hurtDamages();
        }
    }

    @Override
    public void recycle()
    {
        exit = false;
        phase = this::spawnPillar;
        spawnDaemon();
        animatable.play(idle);
        tick.stop();
    }
}
