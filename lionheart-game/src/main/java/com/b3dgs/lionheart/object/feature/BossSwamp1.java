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

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Layerable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Boss Swamp 1 feature implementation.
 * <ol>
 * <li>Fly vertical up on spawn.</li>
 * <li>Track player horizontally.</li>
 * <li>Spawn bowls on proximity.</li>
 * <li>Destroy bowls on hit and fly away.</li>
 * <li>Spawn BossSwamp2 on exited screen.</li>
 * </ol>
 */
@FeatureInterface
public final class BossSwamp1 extends FeatureModel implements Routine, Recyclable
{
    private static final int MAX_Y = 220;
    private static final int TOP_Y = 230;
    private static final int MAX_AWAY_Y = 388;
    private static final double MOVE_X = 0.9;
    private static final int BOWL_MARGIN = 48;
    private static final int PALLET_OFFSET = 2;

    private final List<Launchable> bowls = new ArrayList<>();
    private final Transformable player = services.get(SwordShade.class).getFeature(Transformable.class);
    private final Spawner spawner = services.get(Spawner.class);
    private final Animation idle;

    private boolean moved;
    private double moveX;
    private double moveY;
    private boolean fired;
    private int hit;

    @FeatureGet private Animatable animatable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Launcher launcher;
    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Rasterable rasterable;
    @FeatureGet private Stats stats;
    @FeatureGet private BossSwampEffect effect;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BossSwamp1(Services services, Setup setup)
    {
        super(services, setup);

        idle = AnimationConfig.imports(setup).getAnimation(Anim.IDLE);
    }

    /**
     * Follow player on horizontal axis.
     */
    private void followHorizontal()
    {
        if (transformable.getX() < player.getX() - BossSwampEffect.EFFECT_MARGIN)
        {
            moveX = MOVE_X;
            effect.setEffectX(BossSwampEffect.EFFECT_SPEED);
        }
        else if (transformable.getX() > player.getX() + BossSwampEffect.EFFECT_MARGIN)
        {
            moveX = -MOVE_X;
            effect.setEffectX(-BossSwampEffect.EFFECT_SPEED);
        }
        else
        {
            moveX = 0.0;
        }
    }

    /**
     * Move up until player height.
     */
    private void movePlayerHeight()
    {
        if (transformable.getY() > TOP_Y)
        {
            moveY = -1.0;
        }
        else if (transformable.getY() > MAX_Y + BossSwampEffect.EFFECT_MARGIN)
        {
            moveY = 0.0;
            if (!moved)
            {
                moved = true;
            }
            else
            {
                effect.setEffectY(-BossSwampEffect.EFFECT_SPEED);
            }
        }
        else if (transformable.getY() < MAX_Y - BossSwampEffect.EFFECT_MARGIN)
        {
            if (!moved)
            {
                moveY = 1.0;
            }
            else
            {
                effect.setEffectY(BossSwampEffect.EFFECT_SPEED);
            }
        }
    }

    /**
     * Update bowls movement.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateBowls(double extrp)
    {
        if (fired)
        {
            final int n = bowls.size();
            int hitCount = 0;
            for (int i = 0; i < n; i++)
            {
                final Transformable trans = bowls.get(i).getFeature(Transformable.class);
                final BossSwampBowl bowl = trans.getFeature(BossSwampBowl.class);
                bowl.setFrameOffset(stats.getHealthMax() - stats.getHealth());
                final double x = transformable.getX()
                                 + Math.sin(-bowl.getEffect() + i * 0.9) * (i + 1) * (i + 1) * 0.25;

                if (hit == 1 || bowl.isHit())
                {
                    bowl.hit();
                    trans.moveLocationY(extrp, 3.0);
                    trans.teleportX(x);
                    hit = 1;
                    if (trans.getY() > transformable.getY() + trans.getHeight())
                    {
                        trans.getFeature(Identifiable.class).destroy();
                        hitCount++;
                    }
                    if (hitCount == n)
                    {
                        hit = 2;
                    }
                }
                else
                {
                    trans.teleport(x, transformable.getY() - i * 12 + 16);
                }
            }
        }
        if (!fired
            && UtilMath.isBetween(transformable.getX() - player.getX(), -BOWL_MARGIN, BOWL_MARGIN)
            && UtilMath.isBetween(transformable.getY(),
                                  MAX_Y - BossSwampEffect.EFFECT_MARGIN,
                                  MAX_Y + BossSwampEffect.EFFECT_MARGIN))
        {
            launcher.fire();
            fired = true;
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        launcher.setOffset(0, 0);
        launcher.addListener(bowl ->
        {
            bowl.getFeature(Layerable.class).setLayer(Integer.valueOf(2), Integer.valueOf(2 + bowls.size()));
            bowls.add(bowl);
            Sfx.BOSS1_BOWL.play();
        });
    }

    @Override
    public void update(double extrp)
    {
        if (hit == 2)
        {
            moveY = 3.0;
            if (transformable.getY() > MAX_AWAY_Y)
            {
                identifiable.destroy();
                spawner.spawn(Medias.create(Folder.BOSS, "swamp", "Boss2.xml"),
                              transformable.getX(),
                              transformable.getY())
                       .getFeature(Stats.class)
                       .applyDamages(stats.getHealthMax() - stats.getHealth());
            }
        }
        else
        {
            effect.update(extrp);

            followHorizontal();
            movePlayerHeight();

            updateBowls(extrp);
        }

        rasterable.setAnimOffset(UtilMath.clamp(stats.getHealthMax() - stats.getHealth(), 0, 2) * PALLET_OFFSET);

        transformable.moveLocation(extrp, moveX, moveY);
    }

    @Override
    public void recycle()
    {
        moveX = 0.0;
        moveY = 0.0;
        moved = false;
        fired = false;
        hit = 0;
        bowls.clear();
        animatable.play(idle);
    }
}
