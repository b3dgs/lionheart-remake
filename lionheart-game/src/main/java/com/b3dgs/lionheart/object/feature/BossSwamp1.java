/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.Optional;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Layerable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.object.EntityModel;

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
public final class BossSwamp1 extends FeatureModel implements RoutineUpdate, Recyclable
{
    private static final int MAX_Y = 220;
    private static final int TOP_Y = 230;
    private static final int MAX_AWAY_Y = 388;
    private static final double MOVE_X = 1.1;
    private static final int BOWL_MARGIN = 48;

    private final Trackable target = services.get(Trackable.class);
    private final Spawner spawner = services.get(Spawner.class);

    private final EntityModel model;
    private final Animatable animatable;
    private final Transformable transformable;
    private final Launcher launcher;
    private final Identifiable identifiable;
    private final Rasterable rasterable;
    private final Stats stats;
    private final BossSwampEffect effect;

    private final List<Launchable> bowls = new ArrayList<>();
    private final Animation idle;

    private boolean moved;
    private double moveX;
    private double moveY;
    private boolean fired;
    private int hit;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param model The model feature.
     * @param animatable The animatable feature.
     * @param transformable The transformable feature.
     * @param launcher The launcher feature.
     * @param identifiable The identifiable feature.
     * @param rasterable The rasterable feature.
     * @param stats The stats feature.
     * @param effect The effect feature.
     * @throws LionEngineException If invalid arguments.
     */
    public BossSwamp1(Services services,
                      Setup setup,
                      EntityModel model,
                      Animatable animatable,
                      Transformable transformable,
                      Launcher launcher,
                      Identifiable identifiable,
                      Rasterable rasterable,
                      Stats stats,
                      BossSwampEffect effect)
    {
        super(services, setup);

        this.model = model;
        this.animatable = animatable;
        this.transformable = transformable;
        this.launcher = launcher;
        this.identifiable = identifiable;
        this.rasterable = rasterable;
        this.stats = stats;
        this.effect = effect;

        idle = AnimationConfig.imports(setup).getAnimation(Anim.IDLE);

        launcher.setOffset(0, 0);
        launcher.addListener(bowl ->
        {
            bowl.getFeature(Layerable.class).setLayer(Integer.valueOf(2), Integer.valueOf(2 + bowls.size()));
            bowls.add(bowl);
            Sfx.BOSS1_BOWL.play();
        });
    }

    /**
     * Follow player on horizontal axis.
     */
    private void followHorizontal()
    {
        if (transformable.getX() < target.getX() - BossSwampEffect.EFFECT_MARGIN)
        {
            moveX = MOVE_X;
            effect.setEffectX(BossSwampEffect.EFFECT_SPEED);
        }
        else if (transformable.getX() > target.getX() + BossSwampEffect.EFFECT_MARGIN)
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
            final List<Launchable> toRemove = new ArrayList<>(0);
            for (int i = 0; i < n; i++)
            {
                final Transformable trans = bowls.get(i).getFeature(Transformable.class);
                final BossSwampBowl bowl = trans.getFeature(BossSwampBowl.class);
                bowl.setFrameOffset(getFrameOffset());
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
                        toRemove.add(bowls.get(i));
                    }
                }
                else
                {
                    trans.teleport(x, transformable.getY() - i * 12 + 16);
                }
                trans.updateAfter();
            }
            for (int i = 0; i < toRemove.size(); i++)
            {
                bowls.remove(toRemove.get(i));
            }
            toRemove.clear();
            if (bowls.isEmpty())
            {
                hit = 2;
            }
        }
        if (!fired
            && UtilMath.isBetween(transformable.getX() - target.getX(), -BOWL_MARGIN, BOWL_MARGIN)
            && UtilMath.isBetween(transformable.getY(),
                                  MAX_Y - BossSwampEffect.EFFECT_MARGIN,
                                  MAX_Y + BossSwampEffect.EFFECT_MARGIN))
        {
            launcher.fire();
            fired = true;
        }
    }

    /**
     * Get frame offset based on health.
     * 
     * @return The frame offset.
     */
    private int getFrameOffset()
    {
        return (stats.getHealthMax() - stats.getHealth()) / 2;
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
                final Featurable boss2 = spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "Boss2.xml"),
                                                       transformable.getX(),
                                                       transformable.getY());
                boss2.getFeature(EntityModel.class).setNext(model.getConfig().getNext(), Optional.empty());
                boss2.getFeature(Stats.class).applyDamages(stats.getHealthMax() - stats.getHealth());
            }
        }
        else
        {
            effect.update(extrp);

            followHorizontal();
            movePlayerHeight();

            updateBowls(extrp);
        }

        rasterable.setAnimOffset(UtilMath.clamp(getFrameOffset(), 0, 2) * 2);

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
