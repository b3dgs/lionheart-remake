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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.AnimationConfig;
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
import com.b3dgs.lionheart.constant.Anim;

/**
 * Boss Swamp 2 feature implementation.
 * <ol>
 * <li>Fly vertical down on spawn and track player horizontally.</li>
 * <li>Start spawn eggs on proximity.</li>
 * <li>Fly horizontal left while spawning eggs until border.</li>
 * <li>Eggs spawns little.</li>
 * <li>Fly horizontal back and track player.</li>
 * <li>Land on proximity.</li>
 * <li>Spawn 2 birds.</li>
 * <li>Take off slightly and show neck.</li>
 * <li>Fly away on neck hurt.</li>
 * <li>Spawn BossSwamp1 on exited screen.</li>
 * </ol>
 */
@FeatureInterface
public final class BossSwamp2 extends FeatureModel implements Routine, Recyclable
{
    private static final double MOVE_BACK_X = 0.8;
    private static final double MOVE_BACK_X_MARGIN = 64;
    private static final double MOVE_LEFT_X = -4.0;
    private static final double MIN_Y_MOVE_BACK = 200;

    private final Transformable player = services.get(SwordShade.class).getFeature(Transformable.class);
    private final Viewer viewer = services.get(Viewer.class);
    private final Animation idle;

    private double moveX;
    private double moveY;
    private boolean movedX;
    private boolean movedY;
    private int step;
    private double lastX;

    @FeatureGet private Animatable animatable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Launcher launcher;
    @FeatureGet private BossSwampEffect effect;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BossSwamp2(Services services, Setup setup)
    {
        super(services, setup);

        idle = AnimationConfig.imports(setup).getAnimation(Anim.IDLE);
    }

    /**
     * Follow player on horizontal axis.
     */
    private void followHorizontal()
    {
        if (transformable.getX() < player.getX() + transformable.getWidth() / 2 - MOVE_BACK_X_MARGIN)
        {
            if (!movedX)
            {
                moveX = MOVE_BACK_X;
            }
            effect.setEffectX(BossSwampEffect.EFFECT_SPEED);
        }
        else if (transformable.getX() > player.getX() + transformable.getWidth() / 2 + MOVE_BACK_X_MARGIN)
        {
            if (!movedX)
            {
                moveX = -MOVE_BACK_X;
            }
            effect.setEffectX(-BossSwampEffect.EFFECT_SPEED);
        }
        else
        {
            moveX = 0.0;
            movedX = true;
        }
    }

    /**
     * Move down until minimum height.
     */
    private void moveDown()
    {
        if (transformable.getY() < MIN_Y_MOVE_BACK - BossSwampEffect.EFFECT_MARGIN)
        {
            if (!movedY)
            {
                moveY = 0.0;
                movedY = true;
            }
            else
            {
                effect.setEffectY(BossSwampEffect.EFFECT_SPEED);
            }
        }
        else if (transformable.getY() > MIN_Y_MOVE_BACK + BossSwampEffect.EFFECT_MARGIN)
        {
            if (!movedY)
            {
                moveY = -1.0;
            }
            else
            {
                effect.setEffectY(-BossSwampEffect.EFFECT_SPEED);
            }
        }
    }

    /**
     * Move left straight.
     */
    private void moveLeft()
    {
        if (step == 1 && transformable.getX() > lastX - viewer.getWidth() * 2)
        {
            moveX = MOVE_LEFT_X;

            launcher.fire();
            step = 2;
        }
        else if (step == 2)
        {
            moveX = 0.0;
            step = 3;
        }
    }

    @Override
    public void update(double extrp)
    {
        effect.update(extrp);

        if (step == 0)
        {
            followHorizontal();
            moveDown();

            if (movedX && movedY)
            {
                lastX = transformable.getX();
                step = 1;
            }
        }
        else if (step == 1)
        {
            moveLeft();
        }

        transformable.moveLocation(extrp, moveX, moveY);
    }

    @Override
    public void recycle()
    {
        moveX = 0.0;
        moveY = 0.0;
        movedX = false;
        movedY = false;
        step = 0;
        animatable.stop();
        animatable.play(idle);
    }
}
