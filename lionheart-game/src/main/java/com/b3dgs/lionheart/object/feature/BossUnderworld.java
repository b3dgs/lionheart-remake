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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
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
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.MusicPlayer;
import com.b3dgs.lionheart.RasterType;
import com.b3dgs.lionheart.ScreenShaker;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.WorldType;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.landscape.ForegroundWater;

/**
 * Boss Underworld feature implementation.
 * <ol>
 * <li>Spawn from hole.</li>
 * <li>Track player horizontally.</li>
 * <li>Attack player.</li>
 * <li>Move down.</li>
 * <li>Spawn turtles.</li>
 * <li>Rise.</li>
 * </ol>
 */
@FeatureInterface
public final class BossUnderworld extends FeatureModel implements Routine, Recyclable
{
    private static final int SPAWN_DELAY_MS = 6_000;
    private static final int ATTACK_DELAY_MS = 10_000;
    private static final int DOWN_DELAY_MS = 3_000;

    private static final double RAISE_SPEED = 0.5;
    private static final double RAISE_MAX = -8;
    private static final double RAISE_MIN = -80;
    private static final double EFFECT_SPEED = 4.0;
    private static final double EFFECT_AMPLITUDE = 4.0;

    private final Trackable target = services.get(Trackable.class);
    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final MusicPlayer music = services.get(MusicPlayer.class);
    private final ScreenShaker shaker = services.get(ScreenShaker.class);
    private final Spawner spawner = services.get(Spawner.class);
    private final ForegroundWater water = services.get(ForegroundWater.class);

    private final Tick tick = new Tick();
    private final Animation idle;

    private Updatable updater;
    private boolean attack;
    private double effectY;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Launcher launcher;
    @FeatureGet private Rasterable rasterable;
    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Stats stats;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BossUnderworld(Services services, Setup setup)
    {
        super(services, setup);

        idle = AnimationConfig.imports(setup).getAnimation(Anim.IDLE);
    }

    /**
     * Update spawn delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateSpawn(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), SPAWN_DELAY_MS))
        {
            updater = this::updateRaise;
            shaker.start();
            attack = false;
        }
    }

    /**
     * Update raise phase until max.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateRaise(double extrp)
    {
        transformable.moveLocationY(extrp, RAISE_SPEED);

        if (transformable.getY() > RAISE_MAX)
        {
            transformable.teleportY(RAISE_MAX);
            updater = this::updateAttack;

            launcher.setLevel(attack ? 2 : 1);
            launcher.fire();

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
        transformable.setLocationY(RAISE_MAX + UtilMath.sin(effectY) * EFFECT_AMPLITUDE);
        effectY = UtilMath.wrapAngleDouble(effectY + EFFECT_SPEED);

        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), ATTACK_DELAY_MS))
        {
            updater = this::updateMoveDown;
            shaker.start();
        }
    }

    /**
     * Update move down phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateMoveDown(double extrp)
    {
        transformable.moveLocationY(extrp, -RAISE_SPEED);

        if (transformable.getY() < RAISE_MIN)
        {
            transformable.teleportY(RAISE_MIN);
            updater = this::updateAttackDown;

            launcher.setLevel(0);
            launcher.fire();

            tick.restart();
        }
    }

    /**
     * Update attack once moved down.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateAttackDown(double extrp)
    {
        tick.update(extrp);

        if (tick.elapsedTime(source.getRate(), DOWN_DELAY_MS))
        {
            updater = this::updateRaise;
            attack = !attack;
            shaker.start();

            tick.restart();
        }
    }

    /**
     * Update mirror depending on player location.
     */
    private void updateMirror()
    {
        if (transformable.getX() > target.getX())
        {
            mirrorable.mirror(Mirror.NONE);
        }
        else
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        if (RasterType.CACHE == Settings.getInstance().getRaster())
        {
            launcher.addListener(l -> l.ifIs(Underwater.class, u -> u.loadRaster("raster/underworld/underworld/")));
        }
        identifiable.addListener(id -> music.playMusic(Music.BOSS_WIN));
        stats.addListener(new StatsListener()
        {
            @Override
            public void notifyNextSword(int level)
            {
                // Nothing to do
            }

            @Override
            public void notifyDead()
            {
                water.stopRaise();
                spawner.spawn(Medias.create(Folder.ENTITY, WorldType.UNDERWORLD.getFolder(), "Floater3.xml"), 208, 4);
                spawner.spawn(Medias.create(Folder.ENTITY, WorldType.UNDERWORLD.getFolder(), "Floater3.xml"), 240, 4);
            }
        });
    }

    @Override
    public void update(double extrp)
    {
        updater.update(extrp);

        updateMirror();
    }

    @Override
    public void recycle()
    {
        updater = this::updateSpawn;
        animatable.play(idle);
        effectY = 0.0;
        tick.restart();
    }
}
